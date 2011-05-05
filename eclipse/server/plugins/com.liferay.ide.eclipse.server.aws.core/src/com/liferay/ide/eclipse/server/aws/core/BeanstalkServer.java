package com.liferay.ide.eclipse.server.aws.core;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.server.core.ILiferayServer;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatPlugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.equinox.security.storage.ISecurePreferences;
import org.eclipse.equinox.security.storage.SecurePreferencesFactory;
import org.eclipse.equinox.security.storage.StorageException;
import org.eclipse.jst.server.core.FacetUtil;
import org.eclipse.jst.server.core.IWebModule;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.internal.Server;
import org.eclipse.wst.server.core.internal.ServerWorkingCopy;
import org.eclipse.wst.server.core.model.ServerDelegate;


public class BeanstalkServer extends ServerDelegate
	implements IBeanstalkServer, IBeanstalkServerWorkingCopy, ILiferayServer, IServerListener {

	public static final String ATTR_Beanstalk_SERVER_MODULE_IDS_LIST = "webspere-server-module-ids-list";
	public static final String LIFERAY_PORTAL_ENTERPRISE_EDITION = "Liferay Portal Enterprise Edition";
	public static final String PREF_DEFAULT_SERVER_HOSTNAME_PREFIX = "default.server.hostname.";

	protected URL liferayPortalHomeUrl;
	protected ISecurePreferences securePreferencesNode;

	public BeanstalkServer() {
		super();
	}

	@Override
	public IStatus canModifyModules(IModule[] add, IModule[] remove) {
		// check to see if we can add this to Beanstalk
		if (!(CoreUtil.isNullOrEmpty(add))) {
			for (IModule addModule : add) {
				IProject addModuleProject = addModule.getProject();

				if (!ProjectUtil.isLiferayProject(addModuleProject)) {
					return AWSCorePlugin.createErrorStatus("Cannot add non Liferay plugin project module.");
				}

				IStatus facetStatus = FacetUtil.verifyFacets(addModuleProject, getServer());

				if (facetStatus != null && !facetStatus.isOK()) {
					return facetStatus;
				}

				// make sure that EXT is disabled for now see STUDIO-20
				if (ProjectUtil.isExtProject(addModuleProject)) {
					return AWSCorePlugin.createErrorStatus("Ext plugin deployment to Beanstalk is not supported at this time.");
				}
			}

		}
		else if (!(CoreUtil.isNullOrEmpty(remove))) {
			for (IModule removeModule : remove) {
				IProject removeModuleProject = removeModule.getProject();

				if (removeModuleProject == null) {
					// something has happened to the project lets just remove it
					return Status.OK_STATUS;
				}

				// if (!ProjectUtil.isLiferayProject(removeModuleProject)) {
				// return BeanstalkCore.createErrorStatus("Cannot remove non Liferay plugin project module.");
				// }

				IStatus facetStatus = FacetUtil.verifyFacets(removeModuleProject, getServer());

				if (facetStatus != null && !facetStatus.isOK()) {
					return facetStatus;
				}
			}
		}

		// ignore remove we can always try to remove;

		return Status.OK_STATUS;
	}

	@Override
	public IModule[] getChildModules(IModule[] module) {
		if (CoreUtil.isNullOrEmpty(module)) {
			return null;
		}

		List<IModule> childModules = new ArrayList<IModule>();

		for (IModule m : module) {
			IProject project = m.getProject();

			if (ProjectUtil.isLiferayProject(project)) {
				IWebModule webModule = (IWebModule) m.loadAdapter(IWebModule.class, null);
				if (webModule != null) {
					IModule[] webModules = webModule.getModules();

					if (!(CoreUtil.isNullOrEmpty(webModules))) {
						for (IModule childWebModule : webModules) {
							childModules.add(childWebModule);
						}
					}
				}
			}
		}

		return childModules.toArray(new IModule[0]);
	}

	public String getHost() {
		return getServer().getHost();
	}

	public String getId() {
		return getServer().getId();
	}

	public URL getModuleRootURL(IModule module) {
		return getPortalHomeUrl();
	}

	public String getPassword() {
		return getSecurePreference(IBeanstalkServer.ATTR_PASSWORD);
	}

	/*
	 * (non-Javadoc)
	 * @see com.liferay.ide.eclipse.server.core.IPortalServer#getPortalHomeUrl()
	 */
	public URL getPortalHomeUrl() {
		if (liferayPortalHomeUrl == null) {
			// first try to get app name

				try {
				liferayPortalHomeUrl = new URL("http://" + getServer().getHost());
				}
				catch (MalformedURLException e) {
				}
		}

		return liferayPortalHomeUrl;
	}

	@Override
	public IModule[] getRootModules(IModule module) throws CoreException {

		IModule[] modules = new IModule[] { module };

		IStatus status = canModifyModules(modules, null);

		if (status == null || !status.isOK()) {
			throw new CoreException(status);
		}

		return modules;
	}

	public IRuntime getRuntime() {
		return getServer().getRuntime();
	}

	public String getUsername() {
		return getAttribute(ATTR_USERNAME, "");
	}

	public URL getWebServicesListURL() {
		try {
			return new URL(getPortalHomeUrl(), "/tunnel-web/axis");
		}
		catch (MalformedURLException e) {
			LiferayTomcatPlugin.logError("Unable to get web services list URL", e);
		}

		return null;
	}

	public boolean isLocal() {
		// local mode is not yet supported
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void modifyModules(IModule[] add, IModule[] remove, IProgressMonitor monitor) throws CoreException {

		List<String> moduleIds =
			(List<String>) this.getAttribute(ATTR_Beanstalk_SERVER_MODULE_IDS_LIST, (List<String>) null);

		if (!(CoreUtil.isNullOrEmpty(add))) {
			if (moduleIds == null) {
				moduleIds = new ArrayList<String>(add.length);
			}

			for (IModule addModule : add) {
				if (moduleIds.contains(addModule.getId()) == false) {
					moduleIds.add(addModule.getId());
				}
			}
		}

		if (!(CoreUtil.isNullOrEmpty(remove)) && !(CoreUtil.isNullOrEmpty(moduleIds))) {
			for (IModule removeModule : remove) {
				moduleIds.remove(removeModule.getId());
			}
		}

		if (moduleIds != null) {
			setAttribute(ATTR_Beanstalk_SERVER_MODULE_IDS_LIST, moduleIds);
		}
	}

	public void serverChanged(ServerEvent event) {
		if (event.getKind() == ServerEvent.SERVER_CHANGE) {
			liferayPortalHomeUrl = null;
		}
	}

	@Override
	public void setDefaults(IProgressMonitor monitor) {
		super.setDefaults(monitor);
		getServerWorkingCopy().setAttribute(Server.PROP_AUTO_PUBLISH_SETTING, Server.AUTO_PUBLISH_DISABLE);
	}

	public void setPassword(String pw) {
		// remove the cached URL
		this.liferayPortalHomeUrl = null;

		String oldPw = getSecurePreference("password");
		setSecurePreference("password", pw, true);

		((ServerWorkingCopy) getServerWorkingCopy()).firePropertyChangeEvent("password", oldPw, pw);
	}


	public void setUsername(String username) {
		// remove the cached URL
		this.liferayPortalHomeUrl = null;

		setAttribute(ATTR_USERNAME, username);
	}

	/*
	 * (non-Javadoc)
	 * @see
	 * com.liferay.studio.eclipse.server.ee.Beanstalk.core.IBeanstalkServerWorkingCopy#validate(org.eclipse.core.runtime
	 * .IProgressMonitor)
	 */
	public IStatus validate(IProgressMonitor monitor) {
		IStatus status = null;

		return Status.OK_STATUS;
	}

	protected IStatus checkForLiferayPortalVersion(URL portalUrl, String version) {
		IStatus retval = null;

		try {
			Map<String, List<String>> fieldsMap = portalUrl.openConnection().getHeaderFields();
			if (fieldsMap != null) {
				List<String> portalField = fieldsMap.get("Liferay-Portal");

				if (portalField != null) {
					String portalVersion = portalField.get(0);

					if (portalVersion != null && portalVersion.contains(version)) {
						return Status.OK_STATUS;
					}
				}
			}
		}
		catch (IOException e) {
		}

		return retval;
	}



	protected String getSecurePreference(String key) {
		String retval = null;

		try {
			retval = getSecurePreferencesNode().get(key, "");
		}
		catch (StorageException e) {
			AWSCorePlugin.logError("Unable to retrieve " + key + " from secure pref store.", e);
		}

		return retval;
	}

	protected ISecurePreferences getSecurePreferencesNode() {
		if (securePreferencesNode == null) {
			ISecurePreferences root = SecurePreferencesFactory.getDefault();
			securePreferencesNode = root.node("liferay/studio/Beanstalk/" + this.getServer().getId());
		}

		return securePreferencesNode;
	}



	@Override
	protected void initialize() {
		super.initialize();

		getServer().addServerListener(this);
	}

	protected void setSecurePreference(String key, String value, boolean encrypt) {
		try {
			getSecurePreferencesNode().put(key, value, encrypt);
		}
		catch (StorageException e) {
			AWSCorePlugin.logError("Unable to put " + key + " to secure pref store.", e);
		}
	}

}
