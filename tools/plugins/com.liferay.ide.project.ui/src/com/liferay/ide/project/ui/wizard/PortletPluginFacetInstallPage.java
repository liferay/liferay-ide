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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.facet.PortletPluginFacetInstallDataModelProvider;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.ISharedImages;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;
import org.eclipse.wst.common.project.facet.ui.IFacetWizardPage;
import org.eclipse.wst.common.project.facet.ui.IWizardContext;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class PortletPluginFacetInstallPage
	extends DataModelWizardPage implements IFacetWizardPage, IPluginProjectDataModelProperties {

	public PortletPluginFacetInstallPage() {
		super(
			DataModelFactory.createDataModel(new PortletPluginFacetInstallDataModelProvider()),
			"portlet.plugin.facet.install.page");

		ProjectUI projectUI = ProjectUI.getDefault();

		Bundle bundle = projectUI.getBundle();

		setImageDescriptor(ImageDescriptor.createFromURL(bundle.getEntry("/icons/wizban/plugin_project.png")));

		setTitle(Msgs.modifyLiferayPluginProject);

		setMessage(Msgs.convertingProjectsNotAvailable, IMessageProvider.WARNING);
	}

	public void setConfig(Object config) {
		model.removeListener(this);

		synchHelper.dispose();

		model = (IDataModel)config;

		model.addListener(this);

		synchHelper = initializeSynchHelper(model);
	}

	public void setWizardContext(IWizardContext context) {
	}

	public void transferStateToConfig() {
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 2);

		Label label = SWTUtil.createLabel(topComposite, StringPool.EMPTY, 1);

		ISharedImages sharedImages = UIUtil.getSharedImages();

		label.setImage(sharedImages.getImage(ISharedImages.IMG_OBJS_INFO_TSK));

		label = SWTUtil.createLabel(topComposite, SWT.WRAP, Msgs.bestWayConvertProject, 1);

		GridData gd = new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1);

		gd.widthHint = 400;

		label.setLayoutData(gd);

		return topComposite;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {LIFERAY_SDK_NAME};
	}

	private static class Msgs extends NLS {

		public static String bestWayConvertProject;
		public static String convertingProjectsNotAvailable;
		public static String modifyLiferayPluginProject;

		static {
			initializeMessages(PortletPluginFacetInstallPage.class.getName(), Msgs.class);
		}

	}

}