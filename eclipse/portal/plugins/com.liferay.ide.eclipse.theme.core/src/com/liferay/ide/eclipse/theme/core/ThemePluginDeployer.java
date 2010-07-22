package com.liferay.ide.eclipse.theme.core;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.server.core.AbstractPluginDeployer;
import com.liferay.ide.eclipse.server.core.IPortalConstants;
import com.liferay.ide.eclipse.server.core.IPortalRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;
import com.liferay.ide.eclipse.theme.core.facet.ThemePluginFacetInstall;
import com.liferay.ide.eclipse.theme.core.operation.ThemeDescriptorHelper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Greg Amerson
 */
public class ThemePluginDeployer extends AbstractPluginDeployer {

	public ThemePluginDeployer() {
	}

	@Override
	public boolean prePublishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) {
		boolean publish = true;

		if ((kind != IServer.PUBLISH_FULL && kind != IServer.PUBLISH_INCREMENTAL) || moduleTree == null) {
			return publish;
		}

		if (deltaKind != ServerBehaviourDelegate.REMOVED) {
			try {
				addThemeModule(moduleTree[0]);
			}
			catch (Exception e) {
				ThemeCore.logError("Unable to pre-publish module.", e);
			}
		}

		return publish;
	}

	protected void addThemeModule(IModule module)
		throws CoreException {

		SDK sdk = null;
		IProject project = module.getProject();

		try {
			sdk = ProjectUtil.getSDK(project, ThemePluginFacetInstall.LIFERAY_THEME_PLUGIN_FACET);
		}
		catch (BackingStoreException e) {
			throw new CoreException(ThemeCore.createErrorStatus(e));
		}

		if (sdk == null) {
			throw new CoreException(
				ThemeCore.createErrorStatus("No SDK for project configured. Could not deploy theme module"));
		}

		IRuntime runtime = ServerUtil.getRuntime(project);

		String appServerDir = runtime.getLocation().toOSString();
		Map<String, String> properties = new HashMap<String, String>();
		properties.put("app.server.type", "tomcat");
		properties.put("app.server.dir", appServerDir);
		properties.put("app.server.deploy.dir", appServerDir + "/webapps");
		properties.put("app.server.lib.global.dir", appServerDir + "/lib/ext");
		properties.put("app.server.portal.dir", appServerDir + "/webapps/ROOT");

		IStatus status = sdk.compileThemePlugin(project, properties);

		if (!status.isOK()) {
			throw new CoreException(status);
		}

		IFolder docroot = ProjectUtil.getDocroot(project);

		IFile lookAndFeelFile = docroot.getFile("WEB-INF/" + IPortalConstants.LIFERAY_LOOK_AND_FEEL_XML_FILE);

		if (!lookAndFeelFile.exists()) {
			String id = project.getName().replaceAll(ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX, "");
			IFile propsFile = docroot.getFile("WEB-INF/" + ILiferayConstants.LIFERAY_PLUGIN_PACKAGE_PROPERTIES_FILE);
			String name = id;
			if (propsFile.exists()) {
				Properties props = new Properties();
				try {
					props.load(propsFile.getContents());
					String nameValue = props.getProperty("name");
					if (!CoreUtil.isNullOrEmpty(nameValue)) {
						name = nameValue;
					}
				}
				catch (IOException e) {
					ThemeCore.logError("Unable to load plugin package properties.", e);
				}
			}

			IPortalRuntime portalRuntime = ServerUtil.getPortalRuntime(runtime);
			if (portalRuntime != null) {
				ThemeDescriptorHelper.createDefaultFile(lookAndFeelFile, portalRuntime.getVersion() + "+", id, name);
			}
		}

		if (docroot != null && docroot.exists()) {
			docroot.refreshLocal(IResource.DEPTH_INFINITE, null);
		}

	}
}
