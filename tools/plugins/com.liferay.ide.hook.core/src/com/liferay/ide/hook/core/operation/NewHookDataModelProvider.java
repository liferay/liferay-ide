/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */

package com.liferay.ide.hook.core.operation;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.hook.core.HookCore;
import com.liferay.ide.hook.core.dd.HookDescriptorHelper;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditOperationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;

import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 * @author Charles Wu
 */
@SuppressWarnings({"restriction", "unchecked", "rawtypes"})
public class NewHookDataModelProvider
	extends ArtifactEditOperationDataModelProvider implements INewHookDataModelProperties {

	public NewHookDataModelProvider() {
	}

	@Override
	public IDataModelOperation getDefaultOperation() {
		return new AddHookOperation(getDataModel());
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (CUSTOM_JSPS_FOLDER.equals(propertyName)) {

			// check to see if there is an existing hook descriptor and read
			// custom_jsps out of that

			IProject targetProject = getTargetProject();

			IWebProject webproject = LiferayCore.create(IWebProject.class, targetProject);

			if (webproject != null) {
				IFolder defaultWebappRootFolder = webproject.getDefaultDocrootFolder();

				IPath fullPath = defaultWebappRootFolder.getFullPath();

				String defaultWebappRootPath = fullPath.toPortableString();

				if (targetProject != null) {
					HookDescriptorHelper hookDescriptorHelper = new HookDescriptorHelper(targetProject);

					String customJspFolder = hookDescriptorHelper.getCustomJSPFolder(getDataModel());

					if (customJspFolder != null) {

						// folder should be relative to the web app folder root
						// IDE-110 IDE-648

						IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

						if (defaultDocroot != null) {
							IPath defaultDocrootPath = defaultDocroot.getFullPath();

							IPath path = defaultDocrootPath.append(customJspFolder);

							String containerFullPath = path.toPortableString();

							int index = containerFullPath.indexOf(defaultWebappRootPath);

							if (index != -1) {
								int beginIndex = index + defaultWebappRootPath.length();

								containerFullPath = containerFullPath.substring(beginIndex);
							}

							return containerFullPath;
						}
					}

					if (defaultWebappRootFolder != null) {
						return "/META-INF/custom_jsps";
					}
				}
			}
		}
		else if (PORTAL_PROPERTIES_FILE.equals(propertyName)) {
			IProject targetProject = getTargetProject();

			List<IFolder> sources = CoreUtil.getSourceFolders(JavaCore.create(targetProject));

			if ((targetProject != null) && ListUtil.isNotEmpty(sources)) {
				IFolder source = sources.get(0);

				IPath sourcePath = source.getFullPath();

				sourcePath = sourcePath.append("portal.properties");

				return sourcePath.toPortableString();
			}
		}
		else if (CONTENT_FOLDER.equals(propertyName)) {
			IProject targetProject = getTargetProject();

			List<IFolder> sources = CoreUtil.getSourceFolders(JavaCore.create(targetProject));

			if ((targetProject != null) && !sources.isEmpty()) {
				IFolder source = sources.get(0);

				IPath sourcePath = source.getFullPath();

				sourcePath = sourcePath.append("content");

				return sourcePath.toPortableString();
			}
		}
		else if (SELECTED_PROJECT.equals(propertyName)) {
			IProject targetProject = getTargetProject();

			if (targetProject != null) {
				return targetProject.getName();
			}
		}
		else if (WEB_ROOT_FOLDER.equals(propertyName)) {
			IProject targetProject = getTargetProject();

			IWebProject webproject = LiferayCore.create(IWebProject.class, targetProject);

			if ((targetProject != null) && (webproject != null)) {
				IFolder folder = webproject.getDefaultDocrootFolder();

				return folder.getName();
			}
		}
		else if (propertyName.equals(INewJavaClassDataModelProperties.PROJECT)) {
			return getTargetProject();
		}
		else if (propertyName.equals(INewJavaClassDataModelProperties.SOURCE_FOLDER)) {
			IFolder sourceFolder = getDefaultJavaSourceFolder();

			if (FileUtil.exists(sourceFolder)) {
				IPath fullPath = sourceFolder.getFullPath();

				return fullPath.toPortableString();
			}
		}
		else if (propertyName.equals(INewJavaClassDataModelProperties.JAVA_SOURCE_FOLDER)) {
			return getJavaSourceFolder();
		}
		else if (propertyName.equals(INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT)) {
			return getJavaPackageFragmentRoot();
		}
		else if (CUSTOM_JSPS_FILES_CREATED.equals(propertyName)) {
			return new HashSet<>();
		}
		else if (LANGUAGE_PROPERTIES_FILES_CREATED.equals(propertyName)) {
			return new HashSet<>();
		}
		else if (DISABLE_CUSTOM_JSP_FOLDER_VALIDATION.equals(propertyName)) {
			return true;
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		propertyNames.add(CREATE_CUSTOM_JSPS);
		propertyNames.add(CREATE_PORTAL_PROPERTIES);
		propertyNames.add(CREATE_SERVICES);
		propertyNames.add(CREATE_LANGUAGE_PROPERTIES);
		propertyNames.add(CUSTOM_JSPS_FOLDER);
		propertyNames.add(CUSTOM_JSPS_ITEMS);
		propertyNames.add(CUSTOM_JSPS_FILES_CREATED);
		propertyNames.add(PORTAL_PROPERTIES_FILE);
		propertyNames.add(PORTAL_PROPERTIES_ACTION_ITEMS);
		propertyNames.add(PORTAL_PROPERTIES_OVERRIDE_ITEMS);
		propertyNames.add(SERVICES_ITEMS);
		propertyNames.add(CONTENT_FOLDER);
		propertyNames.add(LANGUAGE_PROPERTIES_ITEMS);
		propertyNames.add(LANGUAGE_PROPERTIES_FILES_CREATED);
		propertyNames.add(SELECTED_PROJECT);
		propertyNames.add(INewJavaClassDataModelProperties.SOURCE_FOLDER);
		propertyNames.add(WEB_ROOT_FOLDER);
		propertyNames.add(INewJavaClassDataModelProperties.JAVA_SOURCE_FOLDER);
		propertyNames.add(INewJavaClassDataModelProperties.JAVA_PACKAGE_FRAGMENT_ROOT);
		propertyNames.add(DISABLE_CUSTOM_JSP_FOLDER_VALIDATION);

		return propertyNames;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public IStatus validate(String propertyName) {
		boolean is70 = false;

		try {
			SDK sdk = SDKUtil.getWorkspaceSDK();

			if (sdk != null) {
				Version version = Version.parseVersion(sdk.getVersion());
				Version sdk70 = ILiferayConstants.V700;

				if (CoreUtil.compareVersions(version, sdk70) >= 0) {
					is70 = true;
				}
			}
		}
		catch (CoreException ce) {
		}

		if (CUSTOM_JSPS_FOLDER.equals(propertyName) && getBooleanProperty(CREATE_CUSTOM_JSPS)) {
			String jspFolder = getStringProperty(CUSTOM_JSPS_FOLDER);

			if (CoreUtil.isNullOrEmpty(jspFolder)) {
				return HookCore.createErrorStatus(Msgs.customJSPsFolderNotConfigured);
			}

			IProject project = getTargetProject();

			IWebProject webproject = LiferayCore.create(IWebProject.class, project);

			if (webproject != null) {
				IFolder defaultWebappRootFolder = webproject.getDefaultDocrootFolder();

				IPath defaultWebappRootFolderPath = defaultWebappRootFolder.getFullPath();

				IPath path = defaultWebappRootFolderPath.append(jspFolder);

				String jspFolderPath = path.toPortableString();

				IWorkspace workspace = CoreUtil.getWorkspace();

				IStatus validateStatus = workspace.validatePath(jspFolderPath, IResource.FOLDER);

				if (!validateStatus.isOK()) {
					return HookCore.createErrorStatus(validateStatus.getMessage());
				}
			}
		}
		else if (CUSTOM_JSPS_ITEMS.equals(propertyName) && getBooleanProperty(CREATE_CUSTOM_JSPS)) {
			Object jspItems = getProperty(CUSTOM_JSPS_ITEMS);

			if (jspItems instanceof List) {
				List<String[]> jspItemsList = (List<String[]>)jspItems;

				if (!jspItemsList.isEmpty()) {
					String[][] jspArrays = jspItemsList.toArray(new String[0][]);

					for (int i = 0; i < jspArrays.length; i++) {
						String[] comp1 = jspArrays[i];

						for (int j = i + 1; j < jspArrays.length; j++) {
							String[] comp2 = jspArrays[j];

							if (comp1[0].equals(comp2[0])) {
								return HookCore.createWarnStatus("Should not add same jsp file.");
							}
						}
					}

					return Status.OK_STATUS;
				}
			}

			return HookCore.createErrorStatus(Msgs.specifyOneJSP);
		}
		else if (PORTAL_PROPERTIES_FILE.equals(propertyName) && getBooleanProperty(CREATE_PORTAL_PROPERTIES)) {
			String portalPropertiesFile = getStringProperty(PORTAL_PROPERTIES_FILE);

			if (CoreUtil.isNullOrEmpty(portalPropertiesFile)) {
				return HookCore.createErrorStatus(Msgs.portalPropertiesFileNotConfigured);
			}

			IPath portalPropertiesPath = Path.fromPortableString(portalPropertiesFile);

			List<IFolder> sources = CoreUtil.getSourceFolders(JavaCore.create(getTargetProject()));

			if (portalPropertiesFile.startsWith("/")) {
				for (IFolder sourceFolder : sources) {
					IPath sourceFolderPath = sourceFolder.getFullPath();

					if (sourceFolderPath.isPrefixOf(portalPropertiesPath)) {
						return Status.OK_STATUS;
					}
				}

				return HookCore.createErrorStatus(Msgs.pathUnderSourceFolder);
			}

			return HookCore.createWarnStatus(Msgs.appendedToDefaultLocation);
		}
		else if (PORTAL_PROPERTIES_ACTION_ITEMS.equals(propertyName) && getBooleanProperty(CREATE_PORTAL_PROPERTIES)) {

			// if we have valid actions items or property overrides then we
			// don't need an error

			IStatus actionItemsStatus = validateListItems(PORTAL_PROPERTIES_ACTION_ITEMS);

			IStatus propertyOverridesStatus = validateListItems(PORTAL_PROPERTIES_OVERRIDE_ITEMS);

			if (actionItemsStatus.isOK() || propertyOverridesStatus.isOK()) {
				return Status.OK_STATUS;
			}

			return HookCore.createErrorStatus(Msgs.specifyOneEventActionProperty);
		}
		else if (SERVICES_ITEMS.equals(propertyName) && getBooleanProperty(CREATE_SERVICES)) {
			IStatus itemsStatus = validateListItems(SERVICES_ITEMS);

			if (itemsStatus.isOK()) {
				return Status.OK_STATUS;
			}

			return HookCore.createErrorStatus(Msgs.specifyOneService);
		}
		else if (CONTENT_FOLDER.equals(propertyName) && getBooleanProperty(CREATE_LANGUAGE_PROPERTIES)) {
			String contentFolder = getStringProperty(CONTENT_FOLDER);

			if (CoreUtil.isNullOrEmpty(contentFolder)) {
				return HookCore.createErrorStatus(Msgs.contentFolderNotConfigured);
			}

			IPath contentPath = Path.fromPortableString(contentFolder);

			List<IFolder> sources = CoreUtil.getSourceFolders(JavaCore.create(getTargetProject()));

			if (contentFolder.startsWith("/")) {
				for (IFolder sourceFolder : sources) {
					IPath sourcePath = sourceFolder.getFullPath();

					if (sourcePath.isPrefixOf(contentPath)) {
						return Status.OK_STATUS;
					}
				}

				return HookCore.createErrorStatus(Msgs.pathUnderSourceFolder);
			}

			return HookCore.createWarnStatus(Msgs.appendedToDefaultLocation);
		}
		else if (LANGUAGE_PROPERTIES_ITEMS.equals(propertyName) && getBooleanProperty(CREATE_LANGUAGE_PROPERTIES)) {
			Object propertiesItems = getProperty(LANGUAGE_PROPERTIES_ITEMS);

			if (propertiesItems instanceof List) {
				List jsps = (List)propertiesItems;

				if (ListUtil.isNotEmpty(jsps)) {
					return Status.OK_STATUS;
				}
			}

			return HookCore.createErrorStatus(Msgs.specifyOneLanguagePropertyFile);
		}
		else if (is70 && getBooleanProperty(CREATE_LANGUAGE_PROPERTIES)) {
			return HookCore.createErrorStatus(Msgs.noSupportInSDK70);
		}

		return super.validate(propertyName);
	}

	@SuppressWarnings("deprecation")
	protected IFolder getDefaultJavaSourceFolder() {
		IProject project = getTargetProject();

		if (project == null) {
			return null;
		}

		IPackageFragmentRoot[] sources = J2EEProjectUtilities.getSourceContainers(project);

		// Try and return the first source folder

		if (ListUtil.isNotEmpty(sources)) {
			try {
				return (IFolder)sources[0].getCorrespondingResource();
			}
			catch (Exception e) {
				return null;
			}
		}
		else {
			ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

			IFolder[] sourceFolders = liferayProject.getSourceFolders();

			if (ListUtil.isNotEmpty(sourceFolders)) {
				return sourceFolders[0];
			}
		}

		return null;
	}

	/**
	 * Subclasses may extend this method to perform their own retrieval mechanism.
	 * This implementation simply returns the JDT package fragment root for the
	 * selected source folder. This method may return null.
	 *
	 * @see IJavaProject#getPackageFragmentRoot(IResource)
	 * @return IPackageFragmentRoot
	 */
	protected IPackageFragmentRoot getJavaPackageFragmentRoot() {
		IProject project = getTargetProject();

		if (project != null) {
			IJavaProject aJavaProject = JavaCore.create(project);

			// Return the source folder for the java project of the selected
			// project

			if (aJavaProject != null) {
				IFolder sourcefolder = (IFolder)getProperty(INewJavaClassDataModelProperties.JAVA_SOURCE_FOLDER);

				if (sourcefolder != null) {
					return aJavaProject.getPackageFragmentRoot(sourcefolder);
				}

				ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, project);

				IFolder[] sourceFolders = liferayProject.getSourceFolders();

				if (ListUtil.isNotEmpty(sourceFolders)) {
					sourcefolder = sourceFolders[0];

					return aJavaProject.getPackageFragmentRoot(sourcefolder);
				}
			}
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	protected final IFolder getJavaSourceFolder() {
		IPackageFragmentRoot[] sources = J2EEProjectUtilities.getSourceContainers(getTargetProject());

		// Ensure there is valid source folder(s)

		if (ListUtil.isEmpty(sources)) {
			return null;
		}

		String folderFullPath = getStringProperty(INewJavaClassDataModelProperties.SOURCE_FOLDER);

		// Get the source folder whose path matches the source folder name value
		// in the data model

		for (IPackageFragmentRoot source : sources) {
			IPath path = source.getPath();

			if (path.equals(new Path(folderFullPath))) {
				try {
					return (IFolder)source.getCorrespondingResource();
				}
				catch (Exception e) {
					break;
				}
			}
		}

		return null;
	}

	protected IStatus validateListItems(String propertyName) {
		Object items = getProperty(propertyName);

		if (items instanceof List) {
			List itemsList = (List)items;

			if (ListUtil.isNotEmpty(itemsList)) {
				return Status.OK_STATUS;
			}
		}

		return HookCore.createErrorStatus(Msgs.specifyOneItem);
	}

	private static class Msgs extends NLS {

		public static String appendedToDefaultLocation;
		public static String contentFolderNotConfigured;
		public static String customJSPsFolderNotConfigured;
		public static String noSupportInSDK70;
		public static String pathUnderSourceFolder;
		public static String portalPropertiesFileNotConfigured;
		public static String specifyOneEventActionProperty;
		public static String specifyOneItem;
		public static String specifyOneJSP;
		public static String specifyOneLanguagePropertyFile;
		public static String specifyOneService;

		static {
			initializeMessages(NewHookDataModelProvider.class.getName(), Msgs.class);
		}

	}

}