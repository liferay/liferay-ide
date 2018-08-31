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

import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.common.frameworks.datamodel.DataModelEvent;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.datamodel.IDataModelListener;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;
import org.eclipse.wst.common.frameworks.internal.ui.NewProjectGroup;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewPluginProjectGroup extends NewProjectGroup implements IPluginProjectDataModelProperties {

	public NewPluginProjectGroup(Composite parent, IDataModel model, IDataModel nestedModel) {
		super(parent, model);

		this.model = model;
		this.nestedModel = nestedModel;
		synchHelper = new DataModelSynchHelper(model);
		nestedSynchHelper = new DataModelSynchHelper(nestedModel);

		createControl(parent);
	}

	@Override
	public void buildComposites(Composite parent) {
	}

	public void createControl(Composite parent) {
		createProjectNameGroup(parent);

		createDisplayNameGroup(parent);

		// for 1.0 we are not going to show location
		// createProjectLocationGroup(parent);

	}

	public void dispose() {
		model.removeListener(synchHelper);

		synchHelper.dispose();

		model = null;
	}

	protected void createDisplayNameGroup(Composite parent) {
		Font font = parent.getFont(); // project specification group

		Composite displayGroup = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();

		layout.numColumns = 2;

		displayGroup.setLayout(layout);

		displayGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label

		Label displayLabel = new Label(displayGroup, SWT.NONE);

		displayLabel.setFont(font);
		displayLabel.setText(Msgs.displayNameLabel);

		// new project name entry field

		displayNameField = new Text(displayGroup, SWT.BORDER);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.widthHint = SIZING_TEXT_FIELD_WIDTH;

		displayNameField.setLayoutData(data);

		displayNameField.setFont(font);

		synchHelper.synchText(displayNameField, DISPLAY_NAME, new Control[] {displayLabel});

		IDataModel dataModel = nestedSynchHelper.getDataModel();

		dataModel.addListener(
			new IDataModelListener() {

				public void propertyChanged(DataModelEvent event) {
					if (PROJECT_NAME.equals(event.getPropertyName())) {
						synchHelper.synchAllUIWithModel();
					}
				}

			});
	}

	protected void createProjectLocationGroup(Composite parent) {
		Group projectLocationGroup = new Group(parent, SWT.NONE);

		projectLocationGroup.setText(Msgs.location);

		projectLocationGroup.setLayout(new GridLayout(3, false));

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = 2;

		projectLocationGroup.setLayoutData(gd);

		sdkLocationButton = SWTUtil.createRadioButton(projectLocationGroup, Msgs.createNewProjectSDK, null, true, 3);

		this.synchHelper.synchRadio(sdkLocationButton, LIFERAY_USE_SDK_LOCATION, null);

		workspaceLocationButton = SWTUtil.createRadioButton(
			projectLocationGroup, Msgs.createNewProjectWorkspace, null, true, 3);

		this.nestedSynchHelper.synchRadio(workspaceLocationButton, USE_DEFAULT_LOCATION, null);

		customLocationButton = SWTUtil.createRadioButton(
			projectLocationGroup, Msgs.createNewProjectLabel, null, true, 3);

		this.synchHelper.synchRadio(customLocationButton, LIFERAY_USE_CUSTOM_LOCATION, null);

		createUserSpecifiedProjectLocationField(projectLocationGroup);
	}

	protected void createProjectNameGroup(Composite parent) {
		Font font = parent.getFont(); // project specification group

		Composite projectGroup = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout();

		layout.numColumns = 2;

		projectGroup.setLayout(layout);

		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		// new project label

		Label projectLabel = new Label(projectGroup, SWT.NONE);

		projectLabel.setFont(font);
		projectLabel.setText(Msgs.projectNameLabel);

		// new project name entry field

		projectNameField = new Text(projectGroup, SWT.BORDER);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.widthHint = SIZING_TEXT_FIELD_WIDTH;

		projectNameField.setLayoutData(data);

		projectNameField.setFont(font);

		nestedSynchHelper.synchText(projectNameField, PROJECT_NAME, new Control[] {projectLabel});
	}

	protected void createUserSpecifiedProjectLocationField(Composite projectGroup) {
		Font font = projectGroup.getFont();

		// location label

		final Label locationLabel = new Label(projectGroup, SWT.NONE);

		locationLabel.setFont(font);
		locationLabel.setText(Msgs.wizardNewProjectCreationPageLocationLabel);

		// project location entry field

		locationPathField = new Text(projectGroup, SWT.BORDER);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.widthHint = SIZING_TEXT_FIELD_WIDTH;

		locationPathField.setLayoutData(data);

		locationPathField.setFont(font);

		// browse button

		browseButton = new Button(projectGroup, SWT.PUSH);

		browseButton.setFont(font);
		browseButton.setText(Msgs.browse);
		browseButton.addSelectionListener(
			new SelectionAdapter() {

				public void widgetSelected(SelectionEvent event) {
					handleLocationBrowseButtonPressed();
				}

			});

		final IDataModel localModel = model;

		final IDataModel localNestedModel = nestedModel;

		LocationListener listener = new LocationListener(localModel, localNestedModel, locationLabel);

		listener.propertyChanged(new DataModelEvent(model, LIFERAY_USE_SDK_LOCATION, IDataModel.VALUE_CHG));

		locationPathField.addModifyListener(listener);

		model.addListener(listener);

		nestedModel.addListener(listener);
	}

	protected static final int SIZING_TEXT_FIELD_WIDTH = 305;

	protected Button customLocationButton;
	protected Text displayNameField;
	protected IDataModel model;
	protected IDataModel nestedModel;
	protected DataModelSynchHelper nestedSynchHelper;
	protected Button sdkLocationButton;
	protected DataModelSynchHelper synchHelper;
	protected Button workspaceLocationButton;

	private static class Msgs extends NLS {

		public static String browse;
		public static String createNewProjectLabel;
		public static String createNewProjectSDK;
		public static String createNewProjectWorkspace;
		public static String displayNameLabel;
		public static String location;
		public static String projectNameLabel;
		public static String wizardNewProjectCreationPageLocationLabel;

		static {
			initializeMessages(NewPluginProjectGroup.class.getName(), Msgs.class);
		}

	}

	private class LocationListener implements ModifyListener, IDataModelListener {

		public LocationListener(IDataModel localModel, IDataModel localNestedModel, Label locationLabel) {
			_localModel = localModel;
			_localNestedModel = localNestedModel;
			_locationLabel = locationLabel;
		}

		public void modifyText(ModifyEvent e) {
			if (!(_localModel.getBooleanProperty(LIFERAY_USE_SDK_LOCATION) ||
				  _localNestedModel.getBooleanProperty(USE_DEFAULT_LOCATION)) &&
				!_propertySet) {

				try {
					_typing = true;

					_localNestedModel.setProperty(USER_DEFINED_LOCATION, locationPathField.getText());
				}
				finally {
					_typing = false;
				}
			}
		}

		public void propertyChanged(DataModelEvent event) {
			boolean useDefault = _localNestedModel.getBooleanProperty(USE_DEFAULT_LOCATION);

			if (LIFERAY_USE_SDK_LOCATION.equals(event.getPropertyName()) ||
				USE_DEFAULT_LOCATION.equals(event.getPropertyName()) ||
				LIFERAY_USE_CUSTOM_LOCATION.equals(event.getPropertyName()) ||
				LIFERAY_SDK_NAME.equals(event.getPropertyName())) {

				boolean enableLocationField = _localModel.getBooleanProperty(LIFERAY_USE_CUSTOM_LOCATION);

				_locationLabel.setEnabled(enableLocationField);

				locationPathField.setEnabled(enableLocationField);

				browseButton.setEnabled(enableLocationField);

				_propertySet = true;

				if (useDefault) {
					locationPathField.setText(_localNestedModel.getStringProperty(DEFAULT_LOCATION));
				}
				else if (_localModel.getBooleanProperty(LIFERAY_USE_SDK_LOCATION)) {
					locationPathField.setText(_localNestedModel.getStringProperty(PROJECT_LOCATION));
				}
				else if (_localModel.getBooleanProperty(LIFERAY_USE_CUSTOM_LOCATION)) {
					locationPathField.setText(_localNestedModel.getStringProperty(USER_DEFINED_LOCATION));
				}

				_propertySet = false;
			}
			else if (!_typing) {
				if ((useDefault && DEFAULT_LOCATION.equals(event.getPropertyName())) ||
					(!useDefault &&
					 (USER_DEFINED_LOCATION.equals(event.getPropertyName()) ||
					  PROJECT_LOCATION.equals(event.getPropertyName())))) {

					_propertySet = true;

					locationPathField.setText((String)event.getProperty());

					_propertySet = false;
				}
			}
		}

		private IDataModel _localModel;
		private IDataModel _localNestedModel;
		private Label _locationLabel;
		private boolean _propertySet = false;
		private boolean _typing = false;

	}

}