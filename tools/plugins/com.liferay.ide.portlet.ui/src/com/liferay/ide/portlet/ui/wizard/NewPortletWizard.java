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

package com.liferay.ide.portlet.ui.wizard;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.operation.NewPortletClassDataModelProvider;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.portlet.ui.template.PortletTemplateContextTypeIds;
import com.liferay.ide.project.core.IPluginWizardFragmentProperties;
import com.liferay.ide.project.ui.wizard.IPluginWizardFragment;
import com.liferay.ide.project.ui.wizard.ValidProjectChecker;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import java.util.Hashtable;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.corext.fix.CleanUpConstants;
import org.eclipse.jdt.internal.corext.refactoring.RefactoringExecutionStarter;
import org.eclipse.jdt.internal.ui.fix.ImportsCleanUp;
import org.eclipse.jdt.ui.cleanup.CleanUpOptions;
import org.eclipse.jdt.ui.cleanup.ICleanUp;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebArtifactWizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.internal.operation.IArtifactEditOperationDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class NewPortletWizard
	extends NewWebArtifactWizard implements INewPortletClassDataModelProperties, IPluginWizardFragment {

	public static final String ID = "com.liferay.ide.eclipse.portlet.ui.wizard.portlet";

	public NewPortletWizard() {
		this((IDataModel)null);

		setupWizard();
	}

	public NewPortletWizard(IDataModel model) {
		super(model);

		setDefaultPageImageDescriptor(getImage());
		setupWizard();
	}

	public NewPortletWizard(IProject project) {
		this((IDataModel)null);

		initialProject = project;
		setupWizard();
	}

	public IDataModelProvider getDataModelProvider() {
		return getDefaultProvider();
	}

	public String getTitle() {
		return Msgs.newLiferayPortlet;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		Object selected = selection.getFirstElement();

		if (selected instanceof IProject) {
			IProject project = ((IProject)selected).getProject();

			getDataModel().setStringProperty(
				IArtifactEditOperationDataModelProperties.COMPONENT_NAME, project.getName());
		}

		super.init(workbench, selection);

		ValidProjectChecker checker = new ValidProjectChecker(ID);

		checker.checkValidProjectTypes();
	}

	public void initFragmentDataModel(IDataModel parentDataModel, String projectName) {
		getDataModel().setBooleanProperty(IPluginWizardFragmentProperties.REMOVE_EXISTING_ARTIFACTS, true);
		getDataModel().setProperty(
			IPluginWizardFragmentProperties.FACET_RUNTIME,
			parentDataModel.getProperty(IFacetProjectCreationDataModelProperties.FACET_RUNTIME));
		getDataModel().setStringProperty(IArtifactEditOperationDataModelProperties.PROJECT_NAME, projectName);
	}

	public void setFragment(boolean fragment) {
		this.fragment = fragment;
	}

	public void setHostPage(IWizardPage hostPage) {
		this.hostPage = hostPage;
	}

	@Override
	protected void doAddPages() {
		addPage(
			new NewPortletClassWizardPage(
				getDataModel(), "pageOne", Msgs.createPortletClass, getDefaultPageTitle(), fragment,
				initialProject != null));
		addPage(
			new NewPortletOptionsWizardPage(
				getDataModel(), "pageTwo", Msgs.specifyPortletDeployment, getDefaultPageTitle(), fragment));
		addPage(
			new NewLiferayPortletWizardPage(
				getDataModel(), "pageThree", Msgs.specifyLiferayPortletDeployment, getDefaultPageTitle(), fragment));
		addPage(
			new NewPortletClassOptionsWizardPage(
				getDataModel(), "pageFour", Msgs.specifyModifiersInterfacesMethodStubs, getDefaultPageTitle(),
				fragment));
	}

	protected String getDefaultPageTitle() {
		return Msgs.createLiferayPortlet;
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		PortletUIPlugin plugin = PortletUIPlugin.getDefault();

		TemplateStore templateStore = plugin.getTemplateStore();

		ContextTypeRegistry contextTypeRegistry = plugin.getTemplateContextRegistry();

		TemplateContextType contextType = contextTypeRegistry.getContextType(PortletTemplateContextTypeIds.NEW);

		return new NewPortletClassDataModelProvider(fragment, initialProject) {

			@Override
			public IDataModelOperation getDefaultOperation() {
				return new AddPortletOperation(model, templateStore, contextType);
			}

		};
	}

	protected ImageDescriptor getImage() {
		return PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID, "/icons/wizban/portlet_wiz.png");
	}

	@Override
	protected void openJavaClass() {
		IProject project = CoreUtil.getProject(getDataModel().getStringProperty(PROJECT_NAME));

		if (getDataModel().getBooleanProperty(USE_DEFAULT_PORTLET_CLASS)) {
			try {
				String jspsFolder = getDataModel().getStringProperty(CREATE_JSPS_FOLDER);

				// IDE-110 IDE-648

				IWebProject webproject = LiferayCore.create(IWebProject.class, project);

				if ((webproject != null) && (webproject.getDefaultDocrootFolder() != null)) {
					IFolder defaultDocroot = webproject.getDefaultDocrootFolder();

					IFile viewFile = defaultDocroot.getFile(new Path(jspsFolder + "/view.jsp"));

					if (viewFile.exists()) {
						IWorkbenchPage page = UIUtil.getActivePage();

						IDE.openEditor(page, viewFile, true);

						return;
					}
				}
			}
			catch (Exception e) {

				// eat this exception this is just best effort

			}
		}
		else {
			Map<String, String> settings = new Hashtable<>();

			settings.put(CleanUpConstants.ORGANIZE_IMPORTS, CleanUpOptions.TRUE);

			ImportsCleanUp importsCleanUp = new ImportsCleanUp(settings);

			ICleanUp[] cleanUps = {importsCleanUp};

			IJavaProject javaProject = JavaCore.create(project);

			try {
				IType type = javaProject.findType(
					getDataModel().getStringProperty(INewJavaClassDataModelProperties.QUALIFIED_CLASS_NAME));

				ICompilationUnit cu = (ICompilationUnit)type.getParent();

				ICompilationUnit[] units = {cu};

				RefactoringExecutionStarter.startCleanupRefactoring(
					units, cleanUps, false, getShell(), false, "organize imports");
			}
			catch (Exception e) {
			}

			super.openJavaClass();
		}
	}

	@Override
	protected void postPerformFinish() throws InvocationTargetException {
		openJavaClass();
	}

	@Override
	protected boolean prePerformFinish() {
		if (fragment) {

			// if this is added to plugin wizard as fragment we don't want this to execute
			// performFinish

			return false;
		}
		else {
			return true;
		}
	}

	protected void setupWizard() {
		setNeedsProgressMonitor(true);
	}

	protected boolean fragment;
	protected IWizardPage hostPage;
	protected IProject initialProject;

	private static class Msgs extends NLS {

		public static String createLiferayPortlet;
		public static String createPortletClass;
		public static String newLiferayPortlet;
		public static String specifyLiferayPortletDeployment;
		public static String specifyModifiersInterfacesMethodStubs;
		public static String specifyPortletDeployment;

		static {
			initializeMessages(NewPortletWizard.class.getName(), Msgs.class);
		}

	}

}