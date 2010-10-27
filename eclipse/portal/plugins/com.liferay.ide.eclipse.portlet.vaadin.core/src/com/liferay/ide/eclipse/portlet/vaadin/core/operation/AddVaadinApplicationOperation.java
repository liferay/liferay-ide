
package com.liferay.ide.eclipse.portlet.vaadin.core.operation;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.eclipse.portlet.vaadin.core.dd.VaadinPortletDescriptorHelper;
import com.liferay.ide.eclipse.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jem.util.emf.workbench.ProjectUtilities;
import org.eclipse.jst.j2ee.internal.common.operations.AddJavaEEArtifactOperation;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaEEArtifactClassOperation;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Henri Sara - borrows from AddPortletOperation by Greg Amerson
 */
@SuppressWarnings("restriction")
public class AddVaadinApplicationOperation extends AddJavaEEArtifactOperation
	implements INewPortletClassDataModelProperties, IPluginWizardFragmentProperties,
	INewVaadinPortletClassDataModelProperties {

	protected IVirtualFolder docroot;

	public AddVaadinApplicationOperation(IDataModel dataModel) {
		super(dataModel);

		this.docroot = ComponentCore.createComponent(getTargetProject()).getRootFolder();
	}

	@Override
	public IStatus doExecute(IProgressMonitor monitor, IAdaptable info)
		throws ExecutionException {

		IStatus status = super.doExecute(monitor, info);

		if (!status.isOK()) {
			return status;
		}

		if (getDataModel().getBooleanProperty(CREATE_RESOURCE_BUNDLE_FILE)) {
			try {
				createEmptyFileInDefaultSourceFolder(getDataModel().getStringProperty(CREATE_RESOURCE_BUNDLE_FILE_PATH));
			}
			catch (CoreException e) {
				status = PortletCore.createErrorStatus(e);
			}
		}

		try {
			String cssFilePath = getDataModel().getStringProperty(CSS_FILE);

			if (!CoreUtil.isNullOrEmpty(cssFilePath)) {
				createEmptyFileInDocroot(cssFilePath);
			}

			String javascriptFilePath = getDataModel().getStringProperty(JAVASCRIPT_FILE);

			if (!CoreUtil.isNullOrEmpty(javascriptFilePath)) {
				createEmptyFileInDocroot(javascriptFilePath);
			}
		}
		catch (Exception ex) {
			status = PortletCore.createErrorStatus(ex);
		}

		return status;
	}

	@Override
	public IStatus execute(final IProgressMonitor monitor, final IAdaptable info)
		throws ExecutionException {

		IStatus status = doExecute(monitor, info);

		if (!status.isOK()) {
			return status;
		}

		generateMetaData(getDataModel());

		return Status.OK_STATUS;
	}

	protected void createEmptyFileInDefaultSourceFolder(String filePath)
		throws CoreException {

		IFolder[] sourceFolders = ProjectUtil.getSourceFolders(getTargetProject());

		if (sourceFolders != null && sourceFolders.length > 0) {
			IFile projectFile = sourceFolders[0].getFile(filePath);

			if (!projectFile.exists()) {
				IFolder parent = (IFolder) projectFile.getParent();

				CoreUtil.prepareFolder(parent);

				projectFile.create(new ByteArrayInputStream(new byte[0]), IResource.FORCE, null);
			}
		}
	}

	protected void createEmptyFileInDocroot(String filePath)
		throws CoreException {

		IFile projectFile = getProjectFile(filePath);

		if (!projectFile.exists()) {
			IFolder parent = (IFolder) projectFile.getParent();

			CoreUtil.prepareFolder(parent);

			projectFile.create(new ByteArrayInputStream(new byte[0]), IResource.FORCE, null);
		}

	}

	protected IStatus generateMetaData(IDataModel aModel) {
		if (ProjectUtil.isPortletProject(getTargetProject())) {
			VaadinPortletDescriptorHelper portletDescHelper = new VaadinPortletDescriptorHelper(getTargetProject());

			if (aModel.getBooleanProperty(REMOVE_EXISTING_ARTIFACTS)) {
				portletDescHelper.removeAllPortlets();
			}

			// also adds a dependency to vaadin.jar in
			// liferay-plugin-package.properties
			IStatus status = portletDescHelper.addNewVaadinPortlet(model);

			if (!status.isOK()) {
				PortletCore.getDefault().getLog().log(status);
			}

			return status;
		}
		return OK_STATUS;
	}

	protected NewJavaEEArtifactClassOperation getNewClassOperation() {
		return new NewVaadinApplicationClassOperation(getDataModel());
	}

	protected IFile getProjectFile(String filePath) {
		return this.docroot.getFile(filePath).getUnderlyingFile();
	}

	public IProject getTargetProject() {
		String projectName = model.getStringProperty(PROJECT_NAME);
		return ProjectUtilities.getProject(projectName);
	}

}
