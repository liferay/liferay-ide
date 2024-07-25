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

package com.liferay.ide.project.ui.springmvcportlet;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.springmvcportlet.NewSpringMVCPortletProjectOp;
import com.liferay.ide.project.core.springmvcportlet.SpringMVCPortletProjectConstants;
import com.liferay.ide.core.util.ReleaseUtil;
import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
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
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Text;

/**
 * @author Simon Jiang
 */
public class SpringMVCPortletConfigurationPresentationPart
	extends FormComponentPart implements SapphireContentAccessor {

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

								NewSpringMVCPortletProjectOp op = _op();

								String version = get(op.getLiferayVersion());

								_frameworkDependenciesCombo.removeAll();

								switch (version) {
									case "7.0":
										_frameworkDependenciesCombo.setItems(
											SpringMVCPortletProjectConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);

										break;
									case "7.1":
									case "7.2":
										if (value.equals(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK[0])) {
											_frameworkDependenciesCombo.setItems(
												SpringMVCPortletProjectConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
										}
										else {
											_frameworkDependenciesCombo.setItems(
												SpringMVCPortletProjectConstants.SPRING_FRAMEWORK_DEPENDENCIES);
										}

										break;
									case "7.3":
									case "7.4":
										_frameworkDependenciesCombo.setItems(
											SpringMVCPortletProjectConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);

										break;
								}

								_frameworkDependenciesCombo.select(0);

								op.setFramework(value);
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

				SWTUtil.createVerticalSpacer(parent, 2, 2);

				SWTUtil.createSeparator(parent, 2);

				SWTUtil.createVerticalSpacer(parent, 2, 2);

				SWTUtil.createLabel(parent, "Package Name:", 1);

				Display pageDdisplay = parent.getDisplay();

				_packageNameText = SWTUtil.createSingleText(parent, 1);

				_packageNameText.setForeground(pageDdisplay.getSystemColor(SWT.COLOR_DARK_GRAY));

				_packageNameText.setText(get(_op().getProjectName()));

				_packageNameText.addModifyListener(
					new ModifyListener() {

						@Override
						public void modifyText(ModifyEvent e) {
							if (_packageNameText.equals(e.getSource())) {
								String value = _packageNameText.getText();

								_op().setPackageName(value);
							}
						}

					});

				_intializeSpringConfigurationData(get(_op().getLiferayVersion()));

				_liferayVersionListener = new FilteredListener<ValuePropertyContentEvent>() {

					@Override
					protected void handleTypedEvent(final ValuePropertyContentEvent valueChangeEvent) {
						PropertyDef eventDef = SapphireUtil.getPropertyDef(valueChangeEvent);

						if (eventDef.equals(NewSpringMVCPortletProjectOp.PROP_LIFERAY_VERSION)) {
							String beforeVersion = valueChangeEvent.before();
							String afterVersion = valueChangeEvent.after();

							if ((beforeVersion == null) || !beforeVersion.equals(afterVersion)) {
								_intializeSpringConfigurationData(afterVersion);
							}
						}
					}

				};

				Value<Object> liferayVersionProperty = _op().property(
					NewSpringMVCPortletProjectOp.PROP_LIFERAY_VERSION);

				liferayVersionProperty.attach(_liferayVersionListener);
			}

		};
	}

	@Override
	public void dispose() {
		if (_liferayVersionListener != null) {
			Value<Object> liferayVersion = _op().property(NewSpringMVCPortletProjectOp.PROP_LIFERAY_VERSION);

			liferayVersion.detach(_liferayVersionListener);
		}

		if (_projectNameListener != null) {
			Value<Object> projectName = _op().property(NewSpringMVCPortletProjectOp.PROP_PROJECT_NAME);

			projectName.detach(_projectNameListener);
		}

		super.dispose();
	}

	@Override
	protected void init() {
		super.init();

		String liferayVersion = get(_op().getLiferayVersion());

		if (liferayVersion.equals(ReleaseUtil.getProductGroupVersions()[0])) {
			_op().setFramework(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK[1]);
		}
		else {
			_op().setFramework(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK[0]);
		}

		_op().setFrameworkDependencies(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
		_op().setViewType(SpringMVCPortletProjectConstants.SPRING_VIEW_TYPE[0]);

		_projectNameListener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				PropertyDef eventDef = SapphireUtil.getPropertyDef(event);

				if (eventDef.equals(NewSpringMVCPortletProjectOp.PROP_PROJECT_NAME)) {
					String packageName = _getPackageName(get(_op().getProjectName()));

					if (packageName != null) {
						if (_packageNameText == null) {
							_op().setPackageName(packageName);
						}
						else {
							_packageNameText.setText(packageName);
						}
					}
				}
			}

		};

		Value<Object> projectNameProperty = _op().property(NewSpringMVCPortletProjectOp.PROP_PROJECT_NAME);

		projectNameProperty.attach(_projectNameListener);
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

	private String _getPackageName(String projectName) {
		if (projectName != null) {
			String packageNameText = projectName.replace('-', '.');

			return packageNameText.replace(' ', '.');
		}

		return null;
	}

	private void _intializeSpringConfigurationData(String liferayVersion) {
		_clearSpringConfigurationData();

		if (liferayVersion.equals(ReleaseUtil.getProductGroupVersions()[0])) {
			_frameworkCombo.setItems(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK[1]);
			_frameworkDependenciesCombo.setItems(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK_DEPENDENCIES[0]);
		}
		else {
			_frameworkCombo.setItems(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK);
			_frameworkDependenciesCombo.setItems(SpringMVCPortletProjectConstants.SPRING_FRAMEWORK_DEPENDENCIES);
		}

		_viewTypeCombo.setItems(SpringMVCPortletProjectConstants.SPRING_VIEW_TYPE);

		_frameworkCombo.select(0);
		_frameworkDependenciesCombo.select(0);
		_viewTypeCombo.select(0);
	}

	private NewSpringMVCPortletProjectOp _op() {
		return getLocalModelElement().nearest(NewSpringMVCPortletProjectOp.class);
	}

	private Combo _frameworkCombo;
	private Combo _frameworkDependenciesCombo;
	private FilteredListener<ValuePropertyContentEvent> _liferayVersionListener;
	private Text _packageNameText;
	private FilteredListener<PropertyContentEvent> _projectNameListener;
	private Combo _viewTypeCombo;

}