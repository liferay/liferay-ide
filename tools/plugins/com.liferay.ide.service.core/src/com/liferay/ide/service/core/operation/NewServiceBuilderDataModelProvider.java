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

package com.liferay.ide.service.core.operation;

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.service.core.AddServiceBuilderOperation;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.util.ServiceUtil;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jem.workbench.utility.JemProjectUtilities;
import org.eclipse.jst.j2ee.internal.common.J2EECommonMessages;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.project.J2EEProjectUtilities;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditOperationDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.internal.plugin.WTPCommonPlugin;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
@SuppressWarnings({"restriction", "unchecked"})
public class NewServiceBuilderDataModelProvider
	extends ArtifactEditOperationDataModelProvider implements INewServiceBuilderDataModelProperties {

	public NewServiceBuilderDataModelProvider() {
	}

	@Override
	public IDataModelOperation getDefaultOperation() {
		return new AddServiceBuilderOperation(getDataModel());
	}

	@Override
	public Object getDefaultProperty(String propertyName) {
		if (SERVICE_FILE.equals(propertyName)) {
			return ILiferayConstants.SERVICE_XML_FILE;
		}
		else if (AUTHOR.equals(propertyName)) {
			return System.getProperty("user.name");
		}
		else if (JAVA_PACKAGE_FRAGMENT_ROOT.equals(propertyName)) {
			return getJavaPackageFragmentRoot();
		}
		else if (propertyName.equals(JAVA_SOURCE_FOLDER)) {
			return getJavaSourceFolder();
		}
		else if (propertyName.equals(SOURCE_FOLDER)) {
			IFolder sourceFolder = getDefaultJavaSourceFolder();

			if (FileUtil.exists(sourceFolder)) {
				return sourceFolder.getFullPath().toOSString();
			}
		}
		else if (INewJavaClassDataModelProperties.OPEN_IN_EDITOR.equals(propertyName)) {
			return true;
		}
		else if (propertyName.equals(USE_SAMPLE_TEMPLATE)) {
			return true;
		}

		return super.getDefaultProperty(propertyName);
	}

	@Override
	@SuppressWarnings("rawtypes")
	public Set getPropertyNames() {
		Set propertyNames = super.getPropertyNames();

		propertyNames.add(SERVICE_FILE);
		propertyNames.add(JAVA_PACKAGE_FRAGMENT_ROOT);
		propertyNames.add(JAVA_SOURCE_FOLDER);
		propertyNames.add(SOURCE_FOLDER);
		propertyNames.add(PACKAGE_PATH);
		propertyNames.add(NAMESPACE);
		propertyNames.add(AUTHOR);
		propertyNames.add(CREATED_SERVICE_FILE);
		propertyNames.add(INewJavaClassDataModelProperties.OPEN_IN_EDITOR);
		propertyNames.add(USE_SAMPLE_TEMPLATE);

		return propertyNames;
	}

	@Override
	public void init() {
		super.init();
	}

	@Override
	public boolean isPropertyEnabled(String propertyName) {
		if (SERVICE_FILE.equals(propertyName)) {
			return false;
		}

		return super.isPropertyEnabled(propertyName);
	}

	@Override
	public IStatus validate(String propertyName) {
		if (SERVICE_FILE.equals(propertyName)) {
			IFile serviceFile = getServiceFile();

			if (FileUtil.exists(serviceFile)) {
				return ServiceCore.createErrorStatus(Msgs.projectContainsServiceXmlFile);
			}
		}
		else if (PACKAGE_PATH.equals(propertyName)) {
			String packagePath = getStringProperty(propertyName);

			if (empty(packagePath)) {
				return ServiceCore.createErrorStatus(Msgs.packagePathNotEmpty);
			}

			if (!empty(packagePath)) {
				return _validateJavaPackage(packagePath);
			}
		}
		else if (NAMESPACE.equals(propertyName)) {
			String namespace = getStringProperty(propertyName);

			if (empty(namespace)) {
				return ServiceCore.createErrorStatus(Msgs.namespaceNotEmpty);
			}

			if (!ServiceUtil.isValidNamespace(namespace)) {
				return ServiceCore.createErrorStatus(Msgs.namespaceInvalid);
			}
		}

		return super.validate(propertyName);
	}

	/**
	 * Subclasses may extend this method to perform their own retrieval
	 * mechanism. This implementation simply returns the JDT package fragment
	 * root for the selected source folder. This method may return null.
	 *
	 * @see IJavaProject#getPackageFragmentRoot(IResource)
	 * @return IPackageFragmentRoot
	 */
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

		return null;
	}

	protected IPackageFragmentRoot getJavaPackageFragmentRoot() {
		IProject project = getTargetProject();

		if (project != null) {
			IJavaProject aJavaProject = JemProjectUtilities.getJavaProject(project);

			// Return the source folder for the java project of the selected project

			if (aJavaProject != null) {
				IFolder sourcefolder = (IFolder)getProperty(JAVA_SOURCE_FOLDER);

				if (sourcefolder != null) {
					return aJavaProject.getPackageFragmentRoot(sourcefolder);
				}
			}
		}

		return null;
	}

	@SuppressWarnings("deprecation")
	protected IFolder getJavaSourceFolder() {
		IPackageFragmentRoot[] sources = J2EEProjectUtilities.getSourceContainers(getTargetProject());

		// Ensure there is valid source folder(s)

		if (ListUtil.isEmpty(sources)) {
			return null;
		}

		String folderFullPath = getStringProperty(SOURCE_FOLDER);

		// Get the source folder whose path matches the source folder name value in the data model

		for (int i = 0; i < sources.length; i++) {
			if (sources[i].getPath().equals(new Path(folderFullPath))) {
				try {
					return (IFolder)sources[i].getCorrespondingResource();
				}
				catch (Exception e) {
					break;
				}
			}
		}

		return null;
	}

	protected IFile getServiceFile() {
		String serviceFileProperty = getStringProperty(SERVICE_FILE);
		IWebProject webproject = LiferayCore.create(IWebProject.class, getTargetProject());

		if (CoreUtil.isNullOrEmpty(serviceFileProperty) || (webproject == null)) {
			return null;
		}

		// IDE-110 IDE-648

		IResource serviceXmlResource = webproject.findDocrootResource(new Path("WEB-INF/" + serviceFileProperty));

		if (FileUtil.exists(serviceXmlResource) && (serviceXmlResource instanceof IFile)) {
			return (IFile)serviceXmlResource;
		}

		return null;
	}

	protected IWorkspaceRoot getWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	@SuppressWarnings("rawtypes")
	protected IStatus validateListItems(String propertyName) {
		Object items = getProperty(propertyName);

		if (items instanceof List) {
			List itemsList = (List)items;

			if (ListUtil.isEmpty(itemsList)) {
				return Status.OK_STATUS;
			}
		}

		return ServiceCore.createErrorStatus(Msgs.specifyOneItem);
	}

	private IStatus _validateJavaPackage(String packName) {
		if ((packName != null) && (packName.trim().length() > 0)) {

			// Use standard java conventions to validate the package name

			IStatus javaStatus = JavaConventions.validatePackageName(
				packName, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7);

			if (javaStatus.getSeverity() == IStatus.ERROR) {
				String msg = J2EECommonMessages.ERR_JAVA_PACAKGE_NAME_INVALID + javaStatus.getMessage();

				return WTPCommonPlugin.createErrorStatus(msg);
			}
			else if (javaStatus.getSeverity() == IStatus.WARNING) {
				String msg = J2EECommonMessages.ERR_JAVA_PACKAGE_NAME_WARNING + javaStatus.getMessage();

				return WTPCommonPlugin.createWarningStatus(msg);
			}
		}

		// java package name is valid

		return Status.OK_STATUS;
	}

	private static class Msgs extends NLS {

		public static String namespaceInvalid;
		public static String namespaceNotEmpty;
		public static String packagePathNotEmpty;
		public static String projectContainsServiceXmlFile;
		public static String specifyOneItem;

		static {
			initializeMessages(NewServiceBuilderDataModelProvider.class.getName(), Msgs.class);
		}

	}

}