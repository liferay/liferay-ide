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

package com.liferay.ide.gradle.ui.portal.docker;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.gradle.core.LiferayGradleDockerServer;
import com.liferay.ide.gradle.ui.LiferayGradleUI;

import java.io.File;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Stream;

import org.apache.commons.lang.SystemUtils;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.ITextListener;
import org.eclipse.jface.text.TextEvent;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnViewerToolTipSupport;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.Property;

import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Ethan Sun
 */
public class DockerServerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final Object[] NO_ELEMENTS = new Object[0];

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	public boolean performOk() {
		boolean ok = super.performOk();

		if (_contentIsDirty) {
			try {
				_saveDockerInfo();
			}
			catch (BackingStoreException e) {
				LiferayGradleUI.createErrorStatus(e.getMessage());

				ok = false;
			}
		}

		return ok;
	}

	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			updateApplyButton();
		}

		super.setVisible(visible);
	}

	@Override
	protected Control createContents(Composite parent) {
		_gc = new GC(parent);

		_gc.setFont(JFaceResources.getDialogFont());

		Composite panel = new Composite(parent, 0);

		panel.setLayout(new GridLayout());

		GridData layoutData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);

		panel.setLayoutData(layoutData);

		_createDockerRegistryUrlGroup(panel);

		_createDockerConnectionGroup(panel);

		_gc.dispose();

		return panel;
	}

	protected void loadDockerConfigurations() {
		String configurations = _preferencesService.getString(
			LiferayGradleUI.PLUGIN_ID, LiferayGradleDockerServer.DOCKER_REGISTRY_INFO, "", null);

		List<Properties> list = new ArrayList<>();

		if (CoreUtil.isNotNullOrEmpty(configurations)) {
			JSONArray jsonArray = new JSONArray(configurations);

			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject jsonObject = (JSONObject)jsonArray.get(i);

				list.add(Property.toProperties(jsonObject));
			}
		}

		if (!list.isEmpty()) {
			_configurations.addAll(list);
		}
	}

	@Override
	protected void performDefaults() {
		_contentIsDirty = true;

		_configurations.clear();

		Properties property = new Properties();

		property.setProperty(LiferayGradleDockerServer.PROP_REPO_NAME, LiferayGradleDockerServer.PROP_REPO_PORTAL);
		property.setProperty(
			LiferayGradleDockerServer.PROP_REGISTRY_URL, LiferayGradleDockerServer.PROP_REGISTRY_URL_PORTAL);
		property.setProperty(LiferayGradleDockerServer.PROP_STATE, "true");

		_configurations.add(property);

		property = new Properties();

		property.setProperty(LiferayGradleDockerServer.PROP_REPO_NAME, LiferayGradleDockerServer.PROP_REPO_DXP);
		property.setProperty(
			LiferayGradleDockerServer.PROP_REGISTRY_URL, LiferayGradleDockerServer.PROP_REGISTRY_URL_DXP);
		property.setProperty(LiferayGradleDockerServer.PROP_STATE, "false");

		_configurations.add(property);

		Stream<Properties> configurationsStream = _configurations.stream();

		_registryUrlViewer.setInput(_configurations);
		_registryUrlViewer.setCheckedElements(
			configurationsStream.filter(
				element -> Boolean.parseBoolean(element.getProperty(LiferayGradleDockerServer.PROP_STATE))
			).toArray());

		_registryUrlViewer.refresh();

		_connection = LiferayGradleDockerServer.getDefaultDockerUrl();

		if (_connectionViewer instanceof TextViewer) {
			TextViewer textViewer = (TextViewer)_connectionViewer;

			textViewer.setDocument(new Document(_connection));
		}
		else {
			String dockerDaemonDefaultHost = _connection.split(":")[1].substring(2);
			String dockerDaemonDefaultPort = _connection.split(":")[2];

			property = new Properties();

			property.setProperty("Host", dockerDaemonDefaultHost);
			property.setProperty("Port", dockerDaemonDefaultPort);

			_connectionViewer.setInput(property);
		}

		_connectionViewer.refresh();

		super.performDefaults();
	}

	@Override
	protected void updateApplyButton() {
		if (getApplyButton() != null) {
			getApplyButton().setEnabled(_contentIsDirty);
		}
	}

	protected void updateButtons(TableViewer viewer, Button addButton, Button editButton, Button removeButton) {
		if (viewer != null) {
			addButton.setEnabled(true);

			ISelection selection = viewer.getSelection();

			boolean hasSelection = false;

			if ((selection != null) && !selection.isEmpty()) {
				hasSelection = true;
			}

			int count = 0;

			if (selection instanceof IStructuredSelection) {
				IStructuredSelection structuredSelection = (IStructuredSelection)selection;

				count = structuredSelection.size();
			}

			editButton.setEnabled(hasSelection && (count == 1));
			removeButton.setEnabled(hasSelection && (count > 0));
		}
		else {
			addButton.setEnabled(false);
			editButton.setEnabled(false);
			removeButton.setEnabled(false);
		}
	}

	private void _configureViewerColumn(TableViewer connectionViewer, TableColumn column) {
		final TextCellEditor editor = new TextCellEditor(column.getParent());

		editor.setValidator(
			new ICellEditorValidator() {

				@Override
				public String isValid(Object value) {
					if ((value == null) || CoreUtil.empty(value.toString())) {
						return "Empty string is not allowed";
					}

					return null;
				}

			});
		editor.addListener(
			new ICellEditorListener() {

				@Override
				public void applyEditorValue() {
					setErrorMessage(null);
				}

				@Override
				public void cancelEditor() {
					setErrorMessage(null);
				}

				@Override
				public void editorValueChanged(boolean oldValidState, boolean newValidState) {
					setErrorMessage(editor.getErrorMessage());
				}

			});

		TableViewerColumn viewerColumn = new TableViewerColumn(connectionViewer, column);

		viewerColumn.setEditingSupport(
			new EditingSupport(connectionViewer) {

				@Override
				protected boolean canEdit(Object element) {
					return element instanceof Properties;
				}

				@Override
				protected CellEditor getCellEditor(Object element) {
					return editor;
				}

				@Override
				protected Object getValue(Object element) {
					Properties property = (Properties)element;

					return property.getProperty(column.getText());
				}

				@Override
				protected void setValue(Object element, Object newValue) {
					Properties entry = (Properties)element;

					String property = entry.getProperty(column.getText());

					if (!property.equals(newValue)) {
						entry.setProperty(column.getText(), (String)newValue);

						_markDirty();

						String host = entry.getProperty("Host");

						String port = entry.getProperty("Port");

						_connection = "tcp://" + host + ":" + port;

						connectionViewer.refresh();
					}
				}

			});
	}

	private void _createDockerConnectionGroup(Composite panel) {
		Group group = new Group(panel, SWT.NONE);

		group.setText("Docker Daemon Connection");
		group.setLayout(new GridLayout(2, false));
		group.setLayoutData(new GridData(SWT.BEGINNING, SWT.BEGINNING, false, false));

		if (SystemUtils.IS_OS_UNIX && new File("/var/run/docker.sock").exists()) {
			_connectionViewer = new TextViewer(group, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);

			TextViewer viewer = (TextViewer)_connectionViewer;

			GridDataFactory gridDataFactory = GridDataFactory.fillDefaults();

			gridDataFactory = gridDataFactory.hint(295, 12);

			gridDataFactory = gridDataFactory.grab(true, true);

			gridDataFactory.applyTo(viewer.getTextWidget());

			_connection = _preferencesService.getString(
				LiferayGradleUI.PLUGIN_ID, LiferayGradleDockerServer.DOCKER_DAEMON_CONNECTION, "", null);

			if (CoreUtil.empty(_connection)) {
				String dockerDaemonDefaultUrl = LiferayGradleDockerServer.getDefaultDockerUrl();

				viewer.setDocument(new Document(dockerDaemonDefaultUrl));
			}
			else {
				viewer.setDocument(new Document(_connection));
			}

			viewer.addTextListener(
				new ITextListener() {

					@Override
					public void textChanged(TextEvent event) {
						Object input = viewer.getInput();

						IDocument document = (IDocument)input;

						_connection = document.get();

						_markDirty();
					}

				});
		}
		else {
			_connectionViewer = new TableViewer(group, SWT.SINGLE | SWT.FULL_SELECTION | SWT.BORDER);

			TableViewer viewer = (TableViewer)_connectionViewer;

			Table table = viewer.getTable();

			GridDataFactory gridDataFactory = GridDataFactory.fillDefaults();

			gridDataFactory = gridDataFactory.hint(295, 35);
			gridDataFactory = gridDataFactory.grab(true, true);

			gridDataFactory.applyTo(table);

			table.setHeaderVisible(true);
			table.setLinesVisible(true);

			TableColumn host = new TableColumn(table, SWT.CENTER);

			host.setText("Host");
			host.setWidth(150);

			TableColumn port = new TableColumn(table, SWT.CENTER);

			port.setText("Port");
			port.setWidth(150);

			_configureViewerColumn(viewer, host);

			_configureViewerColumn(viewer, port);

			viewer.setContentProvider(
				new IStructuredContentProvider() {

					@Override
					public void dispose() {
					}

					@Override
					public Object[] getElements(Object inputElement) {
						if (inputElement instanceof Properties) {
							Properties property = (Properties)inputElement;

							if (!property.isEmpty()) {
								return new Object[] {inputElement};
							}
						}

						return NO_ELEMENTS;
					}

					@Override
					public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
					}

				});

			viewer.setLabelProvider(
				new ITableLabelProvider() {

					@Override
					public void addListener(ILabelProviderListener listener) {
					}

					@Override
					public void dispose() {
					}

					@Override
					public Image getColumnImage(Object element, int columnIndex) {
						return null;
					}

					@Override
					public String getColumnText(Object element, int columnIndex) {
						if (element instanceof Properties) {
							Properties property = (Properties)element;

							switch (columnIndex) {
								case 0:
									return property.getProperty("Host");
								case 1:
									return property.getProperty("Port");
							}
						}

						return null;
					}

					@Override
					public boolean isLabelProperty(Object element, String property) {
						return false;
					}

					@Override
					public void removeListener(ILabelProviderListener listener) {
					}

				});

			Properties property = new Properties();

			String connection = _preferencesService.getString(
				LiferayGradleUI.PLUGIN_ID, LiferayGradleDockerServer.DOCKER_DAEMON_CONNECTION, "", null);

			String dockerDaemonUrl = "";

			if (CoreUtil.empty(connection)) {
				dockerDaemonUrl = LiferayGradleDockerServer.getDefaultDockerUrl();
			}
			else {
				dockerDaemonUrl = connection;
			}

			String dockerDaemonHost = dockerDaemonUrl.split(":")[1].substring(2);
			String dockerDaemonPort = dockerDaemonUrl.split(":")[2];

			property.setProperty("Host", dockerDaemonHost);
			property.setProperty("Port", dockerDaemonPort);

			viewer.setInput(property);
		}
	}

	private void _createDockerRegistryUrlGroup(Composite panel) {
		Group group = new Group(panel, SWT.NONE);

		group.setText("Docker Registry Host Information");

		group.setLayout(new GridLayout(2, false));

		group.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		final Table table = new Table(
			group, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK);

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);

		layoutData.heightHint = Dialog.convertHeightInCharsToPixels(_gc.getFontMetrics(), 10);

		table.setLayoutData(layoutData);

		table.setLinesVisible(true);

		table.setHeaderVisible(true);

		_registryUrlViewer = new CheckboxTableViewer(table);

		TableViewerColumn enablementColumn = new TableViewerColumn(_registryUrlViewer, SWT.NONE);

		TableColumn column = enablementColumn.getColumn();

		column.setText("Enabled");

		column.setWidth(35);

		enablementColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					return null;
				}

			});

		TableViewerColumn repoNameColumn = new TableViewerColumn(_registryUrlViewer, SWT.NONE);

		column = repoNameColumn.getColumn();

		column.setText("Repo Name");
		column.setWidth(100);

		repoNameColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					Properties property = (Properties)element;

					return property.getProperty(LiferayGradleDockerServer.PROP_REPO_NAME);
				}

			});

		TableViewerColumn registryHostColumn = new TableViewerColumn(_registryUrlViewer, SWT.NONE);

		column = registryHostColumn.getColumn();

		column.setText("Docker Registry Host");
		column.setWidth(450);

		registryHostColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					Properties property = (Properties)element;

					return property.getProperty(LiferayGradleDockerServer.PROP_REGISTRY_URL);
				}

			});

		loadDockerConfigurations();

		_registryUrlViewer.setContentProvider(new ArrayContentProvider());

		_registryUrlViewer.setLabelProvider(
			new ITableLabelProvider() {

				@Override
				public void addListener(ILabelProviderListener listener) {
				}

				@Override
				public void dispose() {
				}

				@Override
				public Image getColumnImage(Object element, int columnIndex) {
					return null;
				}

				@Override
				public String getColumnText(Object element, int columnIndex) {
					if (element instanceof Properties) {
						Properties property = (Properties)element;

						switch (columnIndex) {
							case 1:
								return property.getProperty(LiferayGradleDockerServer.PROP_REPO_NAME);
							case 2:
								return property.getProperty(LiferayGradleDockerServer.PROP_REGISTRY_URL);
						}
					}

					return null;
				}

				@Override
				public boolean isLabelProperty(Object element, String property) {
					return false;
				}

				@Override
				public void removeListener(ILabelProviderListener listener) {
				}

			});

		_registryUrlViewer.addCheckStateListener(
			event -> {
				if (event.getElement() instanceof Properties) {
					Stream<Properties> configurationsStream = _configurations.stream();

					configurationsStream.filter(
						element -> Boolean.parseBoolean(element.getProperty(LiferayGradleDockerServer.PROP_STATE))
					).forEach(
						element -> element.setProperty(LiferayGradleDockerServer.PROP_STATE, "false")
					);

					Properties property = (Properties)event.getElement();

					property.setProperty(LiferayGradleDockerServer.PROP_STATE, String.valueOf(event.getChecked()));

					configurationsStream = _configurations.stream();

					Object[] checkedElements = configurationsStream.filter(
						element -> Boolean.parseBoolean(element.getProperty(LiferayGradleDockerServer.PROP_STATE))
					).toArray();

					_registryUrlViewer.setCheckedElements(checkedElements);

					_markDirty();
				}
			});

		ColumnViewerToolTipSupport.enableFor(_registryUrlViewer);

		Composite buttonsPanel = new Composite(group, SWT.NONE);

		GridLayout layout = new GridLayout();

		layout.marginHeight = 0;
		layout.marginWidth = 0;

		buttonsPanel.setLayout(layout);

		buttonsPanel.setLayoutData(new GridData(SWT.LEAD, SWT.BEGINNING, false, false));

		Button addButton = new Button(buttonsPanel, 8);

		addButton.setText("Add...");

		layoutData = new GridData(SWT.FILL, SWT.CENTER, false, false);

		layoutData.widthHint = Dialog.convertWidthInCharsToPixels(_gc.getFontMetrics(), 10);

		addButton.setLayoutData(layoutData);

		addButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					DockerServerConfigurationDialog dialog = new DockerServerConfigurationDialog(
						DockerServerPreferencePage.this.getShell(), false);

					if (dialog.open() == Window.OK) {
						Properties configurationData = dialog.getConfigurationData();

						if ((configurationData != null) && !_configurations.contains(configurationData)) {
							_configurations.removeIf(
								configuration -> {
									String repoName = configuration.getProperty(
										LiferayGradleDockerServer.PROP_REPO_NAME);

									return (repoName != null) &&
										   repoName.equals(
											   configurationData.getProperty(LiferayGradleDockerServer.PROP_REPO_NAME));
								});

							_configurations.add(configurationData);

							_markDirty();

							_registryUrlViewer.refresh();
						}
					}
				}

			});

		Button editButton = new Button(buttonsPanel, 8);

		editButton.setText("Edit...");

		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		layoutData.widthHint = Dialog.convertWidthInCharsToPixels(_gc.getFontMetrics(), 10);

		editButton.setLayoutData(layoutData);

		editButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ISelection s = _registryUrlViewer.getSelection();

					if ((s instanceof IStructuredSelection) && !s.isEmpty()) {
						IStructuredSelection selection = (IStructuredSelection)s;

						Object element = selection.getFirstElement();

						if (element instanceof Properties) {
							final Properties m = (Properties)element;
							DockerServerConfigurationDialog dialog = new DockerServerConfigurationDialog(
								DockerServerPreferencePage.this.getShell(), true);

							dialog.setConfigurationData(m);

							if (dialog.open() == Window.OK) {
								Properties configurationData = dialog.getConfigurationData();

								if (configurationData != null) {
									m.clear();

									m.putAll(configurationData);

									_markDirty();

									_registryUrlViewer.refresh();
								}
							}
						}
					}
				}

			});

		Button removeButton = new Button(buttonsPanel, 8);

		removeButton.setText("Remove");

		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		layoutData.widthHint = Dialog.convertWidthInCharsToPixels(_gc.getFontMetrics(), 10);

		removeButton.setLayoutData(layoutData);

		removeButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					ISelection selection = _registryUrlViewer.getSelection();

					if ((selection instanceof IStructuredSelection) && !selection.isEmpty()) {
						IStructuredSelection s = (IStructuredSelection)selection;

						Iterator<?> iterator = s.iterator();

						while (iterator.hasNext()) {
							Object element = iterator.next();

							if (element instanceof Properties) {
								Properties m = (Properties)element;

								_configurations.remove(m);
							}

							_markDirty();

							_registryUrlViewer.refresh();
						}
					}
				}

			});

		_registryUrlViewer.setInput(_configurations);

		Stream<Properties> configurationsStream = _configurations.stream();

		_registryUrlViewer.setCheckedElements(
			configurationsStream.filter(
				element -> Boolean.parseBoolean(element.getProperty(LiferayGradleDockerServer.PROP_STATE))
			).toArray());

		_registryUrlViewer.addPostSelectionChangedListener(
			event -> updateButtons(_registryUrlViewer, addButton, editButton, removeButton));

		updateButtons(_registryUrlViewer, addButton, editButton, removeButton);
	}

	private void _markDirty() {
		_contentIsDirty = true;

		updateApplyButton();
	}

	private void _saveDockerConfigurations() throws BackingStoreException {
		JSONArray jsonArray = new JSONArray();

		for (Properties configuration : _configurations) {
			JSONObject jsonObject = Property.toJSONObject(configuration);

			jsonArray.put(jsonObject);
		}

		_prefstore.put(LiferayGradleDockerServer.DOCKER_REGISTRY_INFO, jsonArray.toString());

		_prefstore.flush();
	}

	private void _saveDockerConnection() throws BackingStoreException {
		_prefstore.put(LiferayGradleDockerServer.DOCKER_DAEMON_CONNECTION, _connection);

		_prefstore.flush();
	}

	private void _saveDockerInfo() throws BackingStoreException {
		_saveDockerConfigurations();

		_saveDockerConnection();
	}

	private final List<Properties> _configurations = new CopyOnWriteArrayList<>();
	private String _connection = "";
	private Viewer _connectionViewer;
	private boolean _contentIsDirty;
	private GC _gc;
	private IPreferencesService _preferencesService = Platform.getPreferencesService();
	private IEclipsePreferences _prefstore = InstanceScope.INSTANCE.getNode(LiferayGradleUI.PLUGIN_ID);
	private CheckboxTableViewer _registryUrlViewer;

}