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

package com.liferay.ide.layouttpl.ui.wizard;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.core.operation.NewLayoutTplDataModelProvider;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.project.ui.wizard.ValidProjectChecker;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelOperation;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewLayoutTplWizard extends DataModelWizard implements INewWizard, INewLayoutTplDataModelProperties {

	public static final String ID = "com.liferay.ide.eclipse.layouttpl.ui.wizard.layouttemplate";

	public static final String LAYOUTTPL_LAYOUT_PAGE = "layoutTplLayoutPage";

	public static final String LAYOUTTPL_PAGE = "layoutTplPage";

	public static final String[] WIZARD_PAGES = {LAYOUTTPL_PAGE, LAYOUTTPL_LAYOUT_PAGE};

	public NewLayoutTplWizard() {
		this(null);
	}

	public NewLayoutTplWizard(IDataModel dataModel) {
		super(dataModel);

		setWindowTitle(Msgs.newLayoutTemplate);
		setDefaultPageImageDescriptor(getDefaultImageDescriptor());
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getDataModel();
		ValidProjectChecker checker = new ValidProjectChecker(ID);

		checker.checkValidProjectTypes();
	}

	@Override
	protected void doAddPages() {
		layoutTplPage = new NewLayoutTplWizardPage(getDataModel(), LAYOUTTPL_PAGE);

		addPage(layoutTplPage);

		layoutTplStartPage = new NewLayoutTplLayoutWizardPage(getDataModel(), LAYOUTTPL_LAYOUT_PAGE);

		addPage(layoutTplStartPage);
	}

	protected ImageDescriptor getDefaultImageDescriptor() {
		return LayoutTplUI.imageDescriptorFromPlugin(LayoutTplUI.PLUGIN_ID, "/icons/wizban/layout_template_wiz.png");
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		LayoutTplUI defaultUI = LayoutTplUI.getDefault();

		TemplateStore templateStore = defaultUI.getTemplateStore();

		ContextTypeRegistry contextTypeRegistry = defaultUI.getTemplateContextRegistry();

		TemplateContextType contextType = contextTypeRegistry.getContextType(LayoutTplTemplateContextTypeIds.NEW);

		return new NewLayoutTplDataModelProvider() {

			@Override
			public IDataModelOperation getDefaultOperation() {
				return new AddLayoutTplOperation(getDataModel(), templateStore, contextType);
			}

		};
	}

	protected void openEditor(IFile file) {
		if (file != null) {
			Display display = getShell().getDisplay();

			display.asyncExec(
				new Runnable() {

					public void run() {
						try {
							IWorkbench workBench = PlatformUI.getWorkbench();

							IWorkbenchWindow workBenchWindow = workBench.getActiveWorkbenchWindow();

							IWorkbenchPage page = workBenchWindow.getActivePage();

							IDE.openEditor(page, file, true);
						}
						catch (PartInitException pie) {
							LayoutTplUI.logError(pie);
						}
					}

				});
		}
	}

	protected void openWebFile(IFile file) {
		try {
			openEditor(file);
		}
		catch (Exception cantOpen) {
			LayoutTplUI.logError(cantOpen);
		}
	}

	@Override
	protected void postPerformFinish() throws InvocationTargetException {
		super.postPerformFinish();

		IFile layoutTplFile = (IFile)getDataModel().getProperty(LAYOUT_TPL_FILE_CREATED);

		if (FileUtil.exists(layoutTplFile)) {
			openWebFile(layoutTplFile);
		}
	}

	@Override
	protected boolean runForked() {
		return false;
	}

	protected NewLayoutTplWizardPage layoutTplPage;
	protected NewLayoutTplLayoutWizardPage layoutTplStartPage;

	private static class Msgs extends NLS {

		public static String newLayoutTemplate;

		static {
			initializeMessages(NewLayoutTplWizard.class.getName(), Msgs.class);
		}

	}

}