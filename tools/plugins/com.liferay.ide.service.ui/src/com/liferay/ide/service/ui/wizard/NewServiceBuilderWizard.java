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

package com.liferay.ide.service.ui.wizard;

import com.liferay.ide.project.ui.wizard.ValidProjectChecker;
import com.liferay.ide.service.core.operation.INewServiceBuilderDataModelProperties;
import com.liferay.ide.service.core.operation.NewServiceBuilderDataModelProvider;
import com.liferay.ide.service.ui.ServiceUI;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jst.servlet.ui.internal.wizard.NewWebArtifactWizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelProvider;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewServiceBuilderWizard
	extends NewWebArtifactWizard implements INewWizard, INewServiceBuilderDataModelProperties {

	public static final String ID = "com.liferay.ide.eclipse.portlet.ui.wizard.servicebuilder";

	public NewServiceBuilderWizard() {
		this(null);
	}

	public NewServiceBuilderWizard(IDataModel model) {
		super(model);

		setDefaultPageImageDescriptor(getImage());
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);

		ValidProjectChecker checker = new ValidProjectChecker(ID);

		checker.checkValidProjectTypes();
	}

	@Override
	protected void doAddPages() {
		addPage(
			new NewServiceBuilderWizardPage(
				getDataModel(), "pageOne", Msgs.newLiferayServiceBuilder, Msgs.createNewServiceBuilderXmlFile));
	}

	@Override
	protected IDataModelProvider getDefaultProvider() {
		return new NewServiceBuilderDataModelProvider();
	}

	protected ImageDescriptor getImage() {
		return ServiceUI.imageDescriptorFromPlugin(ServiceUI.PLUGIN_ID, "/icons/wizban/service_wiz.png");
	}

	@Override
	protected String getTitle() {
		return Msgs.newServiceBuilder;
	}

	@Override
	protected void postPerformFinish() throws InvocationTargetException {
		Object file = getDataModel().getProperty(CREATED_SERVICE_FILE);

		if (file instanceof IFile) {
			openEditor((IFile)file);
		}
	}

	private static class Msgs extends NLS {

		public static String createNewServiceBuilderXmlFile;
		public static String newLiferayServiceBuilder;
		public static String newServiceBuilder;

		static {
			initializeMessages(NewServiceBuilderWizard.class.getName(), Msgs.class);
		}

	}

}