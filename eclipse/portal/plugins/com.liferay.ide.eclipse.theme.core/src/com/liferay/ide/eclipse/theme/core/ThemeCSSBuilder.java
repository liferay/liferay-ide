
package com.liferay.ide.eclipse.theme.core;

import com.liferay.ide.eclipse.core.ILiferayConstants;
import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;
import com.liferay.ide.eclipse.sdk.ISDKConstants;
import com.liferay.ide.eclipse.sdk.SDK;
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
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IRuntime;
import org.osgi.service.prefs.BackingStoreException;

@SuppressWarnings("rawtypes")
public class ThemeCSSBuilder extends IncrementalProjectBuilder {

	public static final String ID = "com.liferay.ide.eclipse.theme.core.cssBuilder";

	@Override
	protected IProject[] build(int kind, Map args, IProgressMonitor monitor) {
		if (kind == IncrementalProjectBuilder.FULL_BUILD) {
			fullBuild(args, monitor);
		}
		else {
			IResourceDelta delta = getDelta(getProject());
			if (delta == null) {
				fullBuild(args, monitor);
			}
			else {
				incrementalBuild(delta, monitor);
			}
		}
		return null;
	}

	protected void incrementalBuild(IResourceDelta delta, IProgressMonitor monitor) {
		int deltaKind = delta.getKind();
		
		if (deltaKind == IResourceDelta.REMOVED || deltaKind == IResourceDelta.REMOVED_PHANTOM) {
			return;
		}
		
		final boolean[] buildCSS = new boolean[1];
		
		try {
			delta.accept(new IResourceDeltaVisitor() {
				
				private IFolder docroot = null;

				public boolean visit(IResourceDelta delta) {
					IPath fullResourcePath = delta.getResource().getFullPath();
					
					for (String segment : fullResourcePath.segments()) {
						if ("_diffs".equals(segment)) {
							if (docroot == null) {
								docroot = ProjectUtil.getDocroot(getProject());
							}
							
							IFolder diffs = docroot.getFolder("_diffs");
							
							if (diffs.exists() && diffs.getFullPath().isPrefixOf(fullResourcePath)) {
								buildCSS[0] = true;
								
								return false;
							}
						}
					}
					
					return true; // visit children too
				}
			});
		}
		catch (CoreException e) {
			e.printStackTrace();
		}
		
		if (buildCSS[0]) {
			try {
				cssBuild(getProject());
			}
			catch (CoreException e) {
				ThemeCore.logError("Error in Theme CSS Builder", e);
			}
		}
	}

	protected void fullBuild(Map args, IProgressMonitor monitor) {
		System.out.println("full build");

		try {
			if (shouldFullBuild(args)) {
				cssBuild(getProject(args));
			}
		}
		catch (Exception e) {
			ThemeCore.logError("Full build failed for Theme CSS Builder", e);
		}
	}

	protected IProject getProject(Map args) {
		return this.getProject();
	}

	protected boolean shouldFullBuild(Map args)
		throws CoreException {
		// check to see if there is any files in the _diffs folder
		IFolder docroot = ProjectUtil.getDocroot(getProject());

		if (docroot != null) {
			IFolder diffs = docroot.getFolder("_diffs");

			if (diffs.exists()) {
				IResource[] diffMembers = diffs.members();

				if (!CoreUtil.isNullOrEmpty(diffMembers)) {
					return true;
				}
			}
		}

		return false;
	}

	protected IStatus cssBuild(IProject project)
		throws CoreException {

		SDK sdk = null;

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

		return status;
	}
}
