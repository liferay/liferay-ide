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

package com.liferay.ide.project.ui.spring;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.WorkspaceConstants;
import com.liferay.ide.project.core.spring.NewLiferaySpringProjectOp;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.sapphire.ui.forms.FormComponentPart;
import org.eclipse.sapphire.ui.forms.swt.FormComponentPresentation;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Simon Jiang
 */
public class LiferaySpringConfigurationPresentationPart extends FormComponentPart implements SapphireContentAccessor {

	@Override
	public FormComponentPresentation createPresentation(SwtPresentation parent, Composite composite) {
		return new FormComponentPresentation(this, parent, composite) {

			@Override
			public void render() {
				final Composite parent = SWTUtil.createComposite(composite(), 2, 2, GridData.FILL_BOTH);

				SWTUtil.createLabel(parent, "Framework:", 1);

				_frameworkCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

				_frameworkCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				_frameworkCombo.addModifyListener(
					new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							if (_frameworkCombo.equals(e.getSource())) {
								String value = _frameworkCombo.getText();

								if (value.equals(WorkspaceConstants.SPRING_FRAMEWORK[0])) {
									_frameworkDependenciesCombo.removeAll();
									_frameworkDependenciesCombo.setItems(
										WorkspaceConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
								}
								else if (value.equals(WorkspaceConstants.SPRING_FRAMEWORK[1])) {
									_frameworkDependenciesCombo.removeAll();

									String version = get(_op().getLiferayVersion());

									if (version.equals(WorkspaceConstants.LIFERAY_VERSIONS[1]) ||
										version.equals(WorkspaceConstants.LIFERAY_VERSIONS[2])) {

										_frameworkDependenciesCombo.setItems(
											WorkspaceConstants.SPRING_FRAMEWORK_DEPENDENCIES);
									}
									else {
										_frameworkDependenciesCombo.setItems(
											WorkspaceConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
									}
								}

								_frameworkDependenciesCombo.select(0);

								_op().setFramework(value);
							}
						}

					});

				SWTUtil.createLabel(parent, "Framework Dependencies:", 1);

				_frameworkDependenciesCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

				_frameworkDependenciesCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				_frameworkDependenciesCombo.addModifyListener(
					new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							if (_frameworkDependenciesCombo.equals(e.getSource())) {
								String value = _frameworkDependenciesCombo.getText();

								_op().setFrameworkDependencies(value);
							}
						}

					});

				SWTUtil.createLabel(parent, "View Type:", 1);

				_viewTypeCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

				_viewTypeCombo.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

				_viewTypeCombo.addModifyListener(
					new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							if (_viewTypeCombo.equals(e.getSource())) {
								String value = _viewTypeCombo.getText();

								_op().setViewType(value);
							}
						}

					});

				_intializeSpringConfigurationData(get(_op().getLiferayVersion()));

				_listener = new FilteredListener<ValuePropertyContentEvent>() {

					@Override
					protected void handleTypedEvent(final ValuePropertyContentEvent valueChangeEvent) {
						PropertyDef eventDef = SapphireUtil.getPropertyDef(valueChangeEvent);

						if (eventDef.equals(NewLiferaySpringProjectOp.PROP_LIFERAY_VERSION)) {
							String beforeVersion = valueChangeEvent.before();
							String afterVersion = valueChangeEvent.after();

							if ((beforeVersion == null) || !beforeVersion.equals(afterVersion)) {
								_intializeSpringConfigurationData(afterVersion);
							}
						}
					}

				};

				Value<Object> liferayVersionProperty = _op().property(NewLiferaySpringProjectOp.PROP_LIFERAY_VERSION);

				liferayVersionProperty.attach(_listener);
			}

		};
	}

	@Override
	public void dispose() {
		if (_listener != null) {
			Value<Object> liferayVersion = _op().property(NewLiferaySpringProjectOp.PROP_LIFERAY_VERSION);

			liferayVersion.detach(_listener);
		}

		super.dispose();
	}

	@Override
	protected void init() {
		super.init();

		String liferayVersion = get(_op().getLiferayVersion());

		if (liferayVersion.equals(WorkspaceConstants.LIFERAY_VERSIONS[0])) {
			_op().setFramework(WorkspaceConstants.SPRING_FRAMEWORK[1]);
			_op().setFrameworkDependencies(WorkspaceConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
			_op().setViewType(WorkspaceConstants.SPRING_VIEW_TYPE[0]);
		}
		else {
			_op().setFramework(WorkspaceConstants.SPRING_FRAMEWORK[0]);
			_op().setFrameworkDependencies(WorkspaceConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
			_op().setViewType(WorkspaceConstants.SPRING_VIEW_TYPE[0]);
		}
	}

	private void _clearSpringConfigurationData() {
		if (_frameworkCombo != null) {
			_frameworkCombo.removeAll();
		}

		if (_frameworkCombo != null) {
			_frameworkDependenciesCombo.removeAll();
		}

		if (_frameworkCombo != null) {
			_viewTypeCombo.removeAll();
		}
	}

	private void _intializeSpringConfigurationData(String liferayVersion) {
		_clearSpringConfigurationData();

		if (liferayVersion.equals(WorkspaceConstants.LIFERAY_VERSIONS[0])) {
			_frameworkCombo.setItems(WorkspaceConstants.SPRING_FRAMEWORK[1]);
			_frameworkDependenciesCombo.setItems(WorkspaceConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
			_viewTypeCombo.setItems(WorkspaceConstants.SPRING_VIEW_TYPE);
		}
		else {
			_frameworkCombo.setItems(WorkspaceConstants.SPRING_FRAMEWORK);
			_frameworkDependenciesCombo.setItems(WorkspaceConstants.SPRING_FRAMEWORK_DEPENDENCIES);
			_viewTypeCombo.setItems(WorkspaceConstants.SPRING_VIEW_TYPE);
		}

		_frameworkCombo.select(0);
		_frameworkDependenciesCombo.select(0);
		_viewTypeCombo.select(0);
	}

	private NewLiferaySpringProjectOp _op() {
		return getLocalModelElement().nearest(NewLiferaySpringProjectOp.class);
	}

	private Combo _frameworkCombo;
	private Combo _frameworkDependenciesCombo;
	private FilteredListener<ValuePropertyContentEvent> _listener;
	private Combo _viewTypeCombo;

}