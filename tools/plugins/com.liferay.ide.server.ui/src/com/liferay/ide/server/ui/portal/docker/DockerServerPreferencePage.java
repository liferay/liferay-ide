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

package com.liferay.ide.server.ui.portal.docker;

import java.io.BufferedReader;
import java.io.StringReader;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.Stream;

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IPreferencesService;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.json.JSONArray;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.docker.IDockerServer;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Ethan Sun
 * @author Simon Jiang
 */
public class DockerServerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public static final Object[] NO_ELEMENTS = new Object[0];
	
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();

		String configurations = _preferencesService.getString(
				LiferayServerUI.PLUGIN_ID, IDockerServer.DOCKER_REGISTRY_INFO, "", null);
			
		_configurations =  _getDockerRegistryConfigurations(configurations);
	}

	
	@Override
	public boolean performOk() {
		_saveDockerConfigurations();
		_saveDockerConnection();
		
		return true;
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);

		composite.setLayout(new GridLayout());

		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, true);

		composite.setLayoutData(layoutData);
		
		Group registryGroup = new Group(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL );

		registryGroup.setText("Docker Registry Host Information");

		registryGroup.setLayout(new GridLayout(2, false));

		registryGroup.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));
		
		_createDockerRegistryView(registryGroup);
		
		_createDockerConnection(composite);

		return composite;
	}

	
	private void _createDockerConnection(Composite parent) {
		final Composite composite = SWTUtil.createComposite(parent, 2, 2, GridData.FILL_BOTH);
		
		Label registryNamelabel = new Label(composite, SWT.LEFT);

		registryNamelabel.setText("Registrl Name");
		registryNamelabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		Text _repoName = new Text(composite, SWT.HORIZONTAL | SWT.SINGLE);

		_repoName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

	}

	private void handleRegistryViewSelectionChangedEvent(SelectionChangedEvent event) {
		if (_registryUrlViewer.equals(event.getSource())) {
			final ISelection element = event.getSelection();

			if (element instanceof DockerRegistryConfiguration) {
				_registryUrlViewer.setGrayed(element, false);
			}

			_startCheckThread();
		}
	}
	

	private void handleRegistryViewCheckStateChangedEvent(CheckStateChangedEvent event) {
		if (_registryUrlViewer.equals(event.getSource())) {
			final Object element = event.getElement();

			if (element instanceof DockerRegistryConfiguration) {
				_registryUrlViewer.setGrayed(element, false);
			}

			DockerRegistryConfiguration registryConfiguration = (DockerRegistryConfiguration)element;
			
			registryConfiguration.setActivity(!registryConfiguration.isActivity());

			_startCheckThread();
		}
	}

	
	private void _createDockerRegistryView(Composite parent) {
		final Composite composite = SWTUtil.createComposite(parent, 2, 2, GridData.FILL_BOTH);
		
		String[] titles = {"Enabled", "Registr Repo Name", "RegistrlUrl"};

		int[] bounds = {35, 100, 450};

		_registryUrlViewer = CheckboxTableViewer.newCheckList(composite, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL | SWT.SINGLE | SWT.FULL_SELECTION | SWT.CHECK);
				
		final Table table = _registryUrlViewer.getTable();

		final GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 4);

		tableData.heightHint = 225;
		
		tableData.widthHint = 400;
		
		table.setLayoutData(tableData);

		table.setLinesVisible(true);

		table.setHeaderVisible(true);
		
		_registryUrlViewer.addCheckStateListener(
			new ICheckStateListener() {

				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					handleRegistryViewCheckStateChangedEvent(event);
				}

			});

		_registryUrlViewer.addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				handleRegistryViewSelectionChangedEvent(event);
				
			}
			
		});
		
		_registryUrlViewer.setContentProvider(new CheckboxContentProvider());
		
		_createRegistryTableViewerColumn(
				titles[0], bounds[0], _registryUrlViewer,null);
		

		_createRegistryTableViewerColumn(titles[1], bounds[1], _registryUrlViewer, 
				element -> {
					DockerRegistryConfiguration registryConfiguration = (DockerRegistryConfiguration)element;

					return registryConfiguration.getName();
				});

		
		_createRegistryTableViewerColumn(titles[2], bounds[2], _registryUrlViewer,
				element -> {
					DockerRegistryConfiguration registryConfiguration = (DockerRegistryConfiguration)element;

					return registryConfiguration.getRegitstryUrl();
				});

		_createButton(
				composite, "Add...", "Add new private registry configuration.", event -> _handleAddRegistryEvent(), true);

		_editButton = _createButton(
				composite, "Edit...", "Edit private registry configuration.", event -> _handleEditRegistryEvent(), false);
		
		_removeButton = _createButton(
				composite, "Remove...", "Remove private registry configuration.", event -> _handleRemoveRegistryEvent(), false);
		
		_startCheckThread();
	}
	
	private void _handleAddRegistryEvent() {
		DockerServerConfigurationDialog dialog = new DockerServerConfigurationDialog(
				DockerServerPreferencePage.this.getShell(), null);

			if (dialog.open() == Window.OK) {
				DockerRegistryConfiguration configurationData = dialog.getConfigurationData();

				if ((configurationData != null) && !_configurations.contains(configurationData)) {
					_configurations.add(configurationData);

					_startCheckThread();
				}
			}
	}
	
	private void _handleEditRegistryEvent() {
		ISelection selection = _registryUrlViewer.getSelection();
		
		if (selection != null) {
			IStructuredSelection currentSelection = (IStructuredSelection)selection;

			DockerRegistryConfiguration oldConfiguration = (DockerRegistryConfiguration)currentSelection.getFirstElement();
		
			DockerServerConfigurationDialog dialog = new DockerServerConfigurationDialog(
					DockerServerPreferencePage.this.getShell(), oldConfiguration);
	
				if (dialog.open() == Window.OK) {
					_startCheckThread();
				}
		}
	}
	
	private void _handleRemoveRegistryEvent() {
		ISelection selection = _registryUrlViewer.getSelection();
		
		if (selection != null) {
			IStructuredSelection currentSelection = (IStructuredSelection)selection;

			DockerRegistryConfiguration oldConfiguration = (DockerRegistryConfiguration)currentSelection.getFirstElement();
			
			_configurations.remove(oldConfiguration);

			_startCheckThread();
		}
	}
	
	
	private void checkAndUpdateRegistryView() {
		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					_registryUrlViewer.setInput(_configurations.toArray(new DockerRegistryConfiguration[0]));
					
					Stream<DockerRegistryConfiguration> configurationStream = _configurations.stream();
					
					configurationStream.filter(
						configuration -> configuration.isActivity()
					).forEach(
						configuration -> _registryUrlViewer.setChecked(configuration, true)
					);
					
					ISelection selection = _registryUrlViewer.getSelection();
					
					if (Objects.isNull(selection) || selection.isEmpty()) {
						_editButton.setEnabled(false);
						_removeButton.setEnabled(false);
					}
					else {
						_editButton.setEnabled(true);
						_removeButton.setEnabled(true);
					}
				}

			});
	}
	
	private void _startCheckThread() {
		final Thread t = new Thread() {

			@Override
			public void run() {
				checkAndUpdateRegistryView();
			}

		};

		t.start();
	}
	
	private void _createRegistryTableViewerColumn(String title, int bound, TableViewer viewer, Function<Object, String> textProvider) {
		TableViewerColumn tableViewerColumn = new TableViewerColumn(viewer, SWT.NONE);

		TableColumn tableColumn = tableViewerColumn.getColumn();

		tableColumn.setText(title);
		
		if (bound > -1) {
			tableColumn.setWidth(bound);
		}
		tableColumn.setResizable(true);
		tableColumn.setMoveable(true);
		tableColumn.pack();

		tableViewerColumn.setLabelProvider(
			new ColumnLabelProvider() {

				@Override
				public String getText(Object element) {
					if (textProvider == null) {
						return null;
					}

					return textProvider.apply(element);
				}

				@Override
				public void update(ViewerCell viewerCell) {
					super.update(viewerCell);

					Table table = _registryUrlViewer.getTable();

					TableColumn tableColumn = table.getColumn(1);

					tableColumn.pack();
				}
				
			});
	}
	
	private static Button _createButton(Composite composite, String text, String tooltip, Listener listener, boolean enabled) {
		Button button = new Button(composite, SWT.PUSH | SWT.BORDER);

		button.addListener(SWT.Selection, listener);
		button.setEnabled(enabled);
		button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		button.setText(text);
		button.setToolTipText(tooltip);

		return button;
	}

	private void _saveDockerConfigurations()  {
		try {
			JSONArray jsonArray = new JSONArray(_configurations);

			_prefstore.put(IDockerServer.DOCKER_REGISTRY_INFO, jsonArray.toString());

			_prefstore.flush();
		}
		catch(Exception exception) {
			LiferayServerUI.logError(exception);
		}
	}

	private void _saveDockerConnection(){
		try {
		_prefstore.put(IDockerServer.DOCKER_DAEMON_CONNECTION, _connection);

		_prefstore.flush();
		}
		catch(Exception exception) {
			LiferayServerUI.logError(exception);
		}		
	}

	private List<DockerRegistryConfiguration> _configurations;
	private Button _editButton;
	private Button _removeButton;
	private String _connection = "";
	private IPreferencesService _preferencesService = Platform.getPreferencesService();
	private IEclipsePreferences _prefstore = InstanceScope.INSTANCE.getNode(LiferayServerUI.PLUGIN_ID);
	private CheckboxTableViewer _registryUrlViewer;
	
	
	public List<DockerRegistryConfiguration> _getDockerRegistryConfigurations(String inputString) {
		try (JsonReader jsonReader = new JsonReader(new BufferedReader(new StringReader(inputString)))) {
			Gson gson = new Gson();

			TypeToken<List<DockerRegistryConfiguration>> typeToken = new TypeToken<List<DockerRegistryConfiguration>>() {
				private static final long serialVersionUID = 1L;				
			};
			
			Optional<List<DockerRegistryConfiguration>> dockerConfigurationListOptional  = Optional.ofNullable(gson.fromJson(jsonReader, typeToken.getType()));
			
			return dockerConfigurationListOptional.orElse(new CopyOnWriteArrayList<>());
		}
		catch (Exception ce) {
			ProjectCore.logError("Cannot Find Product Info", ce);
		}

		return Collections.emptyList();
	}
	
	private class CheckboxContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof DockerRegistryConfiguration[]) {
				return (DockerRegistryConfiguration[])inputElement;
			}

			return new Object[] {inputElement};
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

}