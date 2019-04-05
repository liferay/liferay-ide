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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.util.LayoutTplUIUtil;
import com.liferay.ide.project.core.descriptor.LiferayDescriptorHelper;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelWizardPage;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewLayoutTplWizardPage extends LiferayDataModelWizardPage implements INewLayoutTplDataModelProperties {

	public NewLayoutTplWizardPage(IDataModel dataModel, String pageName) {
		super(
			dataModel, pageName, Msgs.createLayoutTemplate,
			LayoutTplUI.imageDescriptorFromPlugin(LayoutTplUI.PLUGIN_ID, "/icons/wizban/layout_template_wiz.png"));

		setDescription(Msgs.createLiferayLayoutTemplate);
	}

	protected void createProjectNameGroup(Composite parent) {
		projectNameLabel = new Label(parent, SWT.NONE);

		projectNameLabel.setText("Layout plugin project:");
		projectNameLabel.setLayoutData(new GridData());

		// set up project name entry field

		projectNameCombo = new Combo(parent, SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.widthHint = 300;
		data.horizontalSpan = 1;
		data.grabExcessHorizontalSpace = true;
		projectNameCombo.setLayoutData(data);

		synchHelper.synchCombo(projectNameCombo, PROJECT_NAME, null);

		String initialProjectName = initializeProjectList(projectNameCombo, model);

		if ((projectName == null) && (initialProjectName != null)) {
			projectName = initialProjectName;
		}
	}

	protected void createTemplateInfoGroup(Composite parent) {
		SWTUtil.createLabel(parent, SWT.RIGHT, Msgs.name, 1);

		name = SWTUtil.createText(parent, 1);

		synchHelper.synchText(name, LAYOUT_TEMPLATE_NAME, null);

		SWTUtil.createLabel(parent, StringPool.EMPTY, 1);

		SWTUtil.createLabel(parent, SWT.RIGHT, Msgs.id, 1);

		id = SWTUtil.createText(parent, 1);

		synchHelper.synchText(id, LAYOUT_TEMPLATE_ID, null);

		SWTUtil.createLabel(parent, StringPool.EMPTY, 1);

		SWTUtil.createLabel(parent, SWT.RIGHT, Msgs.templateFile, 1);

		templateFile = SWTUtil.createText(parent, 1);

		synchHelper.synchText(templateFile, LAYOUT_TEMPLATE_FILE, null);

		Button templateFileBrowse = SWTUtil.createPushButton(parent, Msgs.browse, null);

		templateFileBrowse.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleFileBrowseButton(
						NewLayoutTplWizardPage.this.templateFile, Msgs.templateFileSelection, Msgs.chooseTemplateFile);
				}

			});

		if (CoreUtil.isNotNullOrEmpty(projectName)) {
			IProject layoutProject = ProjectUtil.getProject(projectName);

			String pluginVersion = LiferayDescriptorHelper.getDescriptorVersion(layoutProject);

			if (pluginVersion.equals("6.2.0")) {
				SWTUtil.createLabel(parent, SWT.RIGHT, Msgs.wapTemplateFile, 1);

				wapTemplateFile = SWTUtil.createText(parent, 1);

				this.synchHelper.synchText(wapTemplateFile, LAYOUT_WAP_TEMPLATE_FILE, null);

				Button wapTemplateFileBrowse = SWTUtil.createPushButton(parent, Msgs.browse, null);

				wapTemplateFileBrowse.addSelectionListener(
					new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							handleFileBrowseButton(
								NewLayoutTplWizardPage.this.wapTemplateFile, Msgs.wapTemplateFileSelection,
								Msgs.chooseWAPTemplateFile);
						}

					});
			}
		}

		SWTUtil.createLabel(parent, SWT.RIGHT, Msgs.thumbnailFile, 1);

		thumbnailFile = SWTUtil.createText(parent, 1);

		synchHelper.synchText(thumbnailFile, LAYOUT_THUMBNAIL_FILE, null);

		Button thumbnailFileBrowse = SWTUtil.createPushButton(parent, Msgs.browse, null);

		thumbnailFileBrowse.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					handleFileBrowseButton(
						NewLayoutTplWizardPage.this.thumbnailFile, Msgs.wapTemplateFileSelection,
						Msgs.chooseThumbnailFile);
				}

			});

		IDataModel dataModel = synchHelper.getDataModel();

		dataModel.addListener(
			new IDataModelListener() {

				public void propertyChanged(DataModelEvent event) {
					if (LAYOUT_TEMPLATE_NAME.equals(event.getPropertyName()) ||
						LAYOUT_TEMPLATE_ID.equals(event.getPropertyName())) {

						synchHelper.synchAllUIWithModel();
					}
				}

			});
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		createProjectNameGroup(topComposite);

		SWTUtil.createSeparator(topComposite, 3);

		createTemplateInfoGroup(topComposite);

		return topComposite;
	}

	@Override
	protected void enter() {
		super.enter();

		validatePage(true);
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {
			PROJECT_NAME, LAYOUT_TEMPLATE_NAME, LAYOUT_TEMPLATE_ID, LAYOUT_TEMPLATE_FILE, LAYOUT_WAP_TEMPLATE_FILE,
			LAYOUT_THUMBNAIL_FILE
		};
	}

	@Override
	protected boolean isProjectValid(IProject project) {
		return LayoutTplUIUtil.isLayoutTplProject(project);
	}

	@Override
	protected boolean showValidationErrorsOnEnter() {
		return true;
	}

	protected Text id;
	protected Text name;
	protected String projectName;
	protected Combo projectNameCombo;
	protected Label projectNameLabel;
	protected Text templateFile;
	protected Text thumbnailFile;
	protected Text wapTemplateFile;

	private static class Msgs extends NLS {

		public static String browse;
		public static String chooseTemplateFile;
		public static String chooseThumbnailFile;
		public static String chooseWAPTemplateFile;
		public static String createLayoutTemplate;
		public static String createLiferayLayoutTemplate;
		public static String id;
		public static String name;
		public static String templateFile;
		public static String templateFileSelection;
		public static String thumbnailFile;
		public static String wapTemplateFile;
		public static String wapTemplateFileSelection;

		static {
			initializeMessages(NewLayoutTplWizardPage.class.getName(), Msgs.class);
		}

	}

}