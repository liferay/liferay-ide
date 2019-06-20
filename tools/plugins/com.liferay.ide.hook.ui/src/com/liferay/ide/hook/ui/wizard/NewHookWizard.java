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

package com.liferay.ide.hook.ui.wizard;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.hook.core.operation.INewHookDataModelProperties;
import com.liferay.ide.hook.core.operation.NewHookDataModelProvider;
import com.liferay.ide.hook.ui.HookUI;
import com.liferay.ide.project.ui.wizard.ValidProjectChecker;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import java.util.Iterator;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizard;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewHookWizard extends DataModelWizard implements INewHookDataModelProperties, INewWizard {

	public static final String CUSTOM_JSPS_PAGE = "customJSPsPage";

	public static final String ID = "com.liferay.ide.eclipse.portlet.ui.wizard.hook";

	public static final String LANGUAGE_PROPERTIES_PAGE = "languagePropertiesPage";

	public static final String[] PAGE_PROPERTIES = {
		CREATE_CUSTOM_JSPS, CREATE_PORTAL_PROPERTIES, CREATE_SERVICES, CREATE_LANGUAGE_PROPERTIES
	};

	public static final String PORTAL_PROPERTIES_PAGE = "portalPropertiesPage";

	public static final String SERVICES_PAGE = "servicesPage";

	public static final String TYPE_PAGE = "typePage";

	public static final String[] WIZARD_PAGES = {
		CUSTOM_JSPS_PAGE, PORTAL_PROPERTIES_PAGE, SERVICES_PAGE, LANGUAGE_PROPERTIES_PAGE
	};

	public NewHookWizard() {
		this(null);
	}

	public NewHookWizard(IDataModel dataModel) {
		super(dataModel);

		setWindowTitle(Msgs.newLiferayHook);

		setDefaultPageImageDescriptor(getDefaultImageDescriptor());
	}

	@Override
	public boolean canFinish() {
		boolean valid = getDataModel().isValid();

		if (!valid) {
			return false;
		}

		for (String type : PAGE_PROPERTIES) {
			boolean pageTypeChecked = getDataModel().getBooleanProperty(type);

			if (pageTypeChecked) {
				return true;
			}
		}

		return false;
	}

	@Override
	public String getNextPage(String currentPageName, String expectedNextPageName) {
		if (TYPE_PAGE.equals(expectedNextPageName)) {
			return TYPE_PAGE;
		}

		if (TYPE_PAGE.equals(currentPageName)) {
			for (int i = 0; i < PAGE_PROPERTIES.length; i++) {
				boolean nextPageType = getDataModel().getBooleanProperty(PAGE_PROPERTIES[i]);

				if (nextPageType) {
					return WIZARD_PAGES[i];
				}
			}
		}

		for (int i = 0; i < WIZARD_PAGES.length; i++) {
			if (WIZARD_PAGES[i].equals(currentPageName)) {
				for (int j = i + 1; j < WIZARD_PAGES.length; j++) {
					boolean nextPageType = getDataModel().getBooleanProperty(PAGE_PROPERTIES[j]);

					if (nextPageType) {
						return WIZARD_PAGES[j];
					}
				}
			}
		}

		return null;
	}

	@Override
	public String getPreviousPage(String currentPageName, String expectedPreviousPageName) {
		if (TYPE_PAGE.equals(expectedPreviousPageName)) {
			return TYPE_PAGE;
		}

		for (int i = 0; i < WIZARD_PAGES.length; i++) {
			if (WIZARD_PAGES[i].equals(currentPageName)) {
				for (int j = i - 1; j >= 0; j--) {
					boolean previousPageType = getDataModel().getBooleanProperty(PAGE_PROPERTIES[j]);

					if (previousPageType) {
						return WIZARD_PAGES[j];
					}
				}
			}
		}

		return super.getPreviousPage(currentPageName, expectedPreviousPageName);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
		getDataModel();
		ValidProjectChecker checker = new ValidProjectChecker(ID);

		checker.checkValidHookProjectTypes();
	}

	@Override
	protected void doAddPages() {
		hookTypePage = new NewHookTypeWizardPage(getDataModel(), TYPE_PAGE);

		addPage(hookTypePage);

		customJSPsHookPage = new NewCustomJSPsHookWizardPage(getDataModel(), CUSTOM_JSPS_PAGE);

		addPage(customJSPsHookPage);

		portalPropertiesPage = new NewPortalPropertiesHookWizardPage(getDataModel(), PORTAL_PROPERTIES_PAGE);

		addPage(portalPropertiesPage);

		servicesPage = new NewServicesHookWizardPage(getDataModel(), SERVICES_PAGE);

		addPage(servicesPage);

		languagePropertiesPage = new NewLanguagePropertiesHookWizardPage(getDataModel(), LANGUAGE_PROPERTIES_PAGE);

		addPage(languagePropertiesPage);
	}

	protected ImageDescriptor getDefaultImageDescriptor() {
		return HookUI.imageDescriptorFromPlugin(HookUI.PLUGIN_ID, "/icons/wizban/hook_wiz.png");
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		return new NewHookDataModelProvider();
	}

	protected void openEditor(IFile file) {
		if (file != null) {
			Display display = getShell().getDisplay();

			display.asyncExec(
				new Runnable() {

					public void run() {
						try {
							IWorkbenchPage page = UIUtil.getActivePage();

							IDE.openEditor(page, file, true);
						}
						catch (PartInitException pie) {
							HookUI.logError(pie);
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
			HookUI.logError(cantOpen);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void postPerformFinish() throws InvocationTargetException {
		super.postPerformFinish();

		Set<IFile> jspFiles = (Set<IFile>)getDataModel().getProperty(CUSTOM_JSPS_FILES_CREATED);

		if (ListUtil.isNotEmpty(jspFiles)) {
			Iterator<IFile> iterator = jspFiles.iterator();

			// just open the first one

			openWebFile(iterator.next());
		}

		Set<IFile> languagePropertiesFiles = (Set<IFile>)getDataModel().getProperty(LANGUAGE_PROPERTIES_FILES_CREATED);

		if (ListUtil.isNotEmpty(languagePropertiesFiles)) {
			Iterator<IFile> iterator = languagePropertiesFiles.iterator();

			// just openthe first one

			openWebFile(iterator.next());
		}
	}

	@Override
	protected boolean runForked() {
		return false;
	}

	protected NewCustomJSPsHookWizardPage customJSPsHookPage;
	protected NewHookTypeWizardPage hookTypePage;
	protected NewLanguagePropertiesHookWizardPage languagePropertiesPage;
	protected NewPortalPropertiesHookWizardPage portalPropertiesPage;
	protected NewServicesHookWizardPage servicesPage;

	private static class Msgs extends NLS {

		public static String newLiferayHook;

		static {
			initializeMessages(NewHookWizard.class.getName(), Msgs.class);
		}

	}

}