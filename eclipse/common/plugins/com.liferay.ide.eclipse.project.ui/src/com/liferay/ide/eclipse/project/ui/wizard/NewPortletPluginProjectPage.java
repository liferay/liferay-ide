/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.project.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.project.core.IPortletFramework;
import com.liferay.ide.eclipse.project.core.ProjectCorePlugin;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.ui.IPortletFrameworkDelegate;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.ui.util.SWTUtil;
import com.liferay.ide.eclipse.ui.util.UIUtil;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jst.j2ee.internal.wizard.J2EEComponentFacetCreationWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.PlatformUI;
import org.eclipse.wst.common.componentcore.internal.util.IModuleConstants;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class NewPortletPluginProjectPage extends J2EEComponentFacetCreationWizardPage
	implements IPluginProjectDataModelProperties {

	protected Button[] frameworkButtons;

	protected Composite[] optionsComposites;

	protected Button pluginFragmentButton;

	public NewPortletPluginProjectPage(NewPluginProjectWizard wizard, IDataModel model) {
		super(model, "advanced.page");

		setWizard(wizard);
		setImageDescriptor(wizard.getDefaultPageImageDescriptor());
		setTitle("Liferay Plug-in Project");
		setDescription("Choose from various template types depending on which technology is most appropriate.");
	}

	@Override
	public boolean canFlipToNextPage() {
		return getModel().getBooleanProperty(PLUGIN_FRAGMENT_ENABLED);
	}

	// protected void createPluginFragmentButton(Composite parent) {
	// pluginFragmentButton = SWTUtil.createCheckButton(parent, "", null, false, 1);
	// ((GridData) pluginFragmentButton.getLayoutData()).horizontalIndent = 8;
	// ((GridData) pluginFragmentButton.getLayoutData()).verticalIndent = 8;
	// this.synchHelper.synchCheckbox(pluginFragmentButton, PLUGIN_FRAGMENT_ENABLED, null);
	// if (!CoreUtil.isNullOrEmpty(getModel().getStringProperty(PLUGIN_FRAGMENT_BUTTON_LABEL))) {
	// pluginFragmentButton.setText(getModel().getStringProperty(PLUGIN_FRAGMENT_BUTTON_LABEL));
	// }
	// else {
	// pluginFragmentButton.setVisible(false);
	// }
	//
	// getModel().addListener(new IDataModelListener() {
	//
	// public void propertyChanged(DataModelEvent event) {
	// if (PLUGIN_FRAGMENT_BUTTON_LABEL.equals(event.getPropertyName())) {
	// String buttonText = getModel().getStringProperty(PLUGIN_FRAGMENT_BUTTON_LABEL);
	// pluginFragmentButton.setText(buttonText);
	// pluginFragmentButton.setVisible(!CoreUtil.isNullOrEmpty(buttonText));
	// }
	// }
	// });
	// }

	public IPortletFramework getSelectedPortletFramework() {
		IPortletFramework retval = null;

		if (!CoreUtil.isNullOrEmpty(frameworkButtons)) {
			for (Button templateButton : frameworkButtons) {
				if (templateButton.getSelection()) {
					retval = (IPortletFramework) templateButton.getData();
				}
			}
		}

		return retval;
	}

	protected void createFrameworkGroup(Composite parent) {
		Group group = SWTUtil.createGroup(parent, "Select portlet framework", 2);
		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		layoutData.widthHint = 200;// make sure the width doesn't grow the dialog
		group.setLayoutData(layoutData);
		((GridLayout) group.getLayout()).verticalSpacing = 10;

		IPortletFramework[] portletFrameworks = ProjectCorePlugin.getPortletFrameworks();

		if (!CoreUtil.isNullOrEmpty(portletFrameworks)) {
			List<Button> buttons = new ArrayList<Button>();

			for (final IPortletFramework framework : portletFrameworks) {
				final IPortletFrameworkDelegate delegate =
					ProjectUIPlugin.getPortletFrameworkDelegate(framework.getId());

				String iconUrl = null;
				String bundleId = null;

				if (delegate != null) {
					iconUrl = delegate.getIconUrl();
					bundleId = delegate.getBundleId();
				}
				else {
					iconUrl = "icons/e16/jsp-template.png";
					bundleId = ProjectUIPlugin.PLUGIN_ID;
				}

				Button templateButton =
					SWTUtil.createRadioButton(
						group, framework.getDisplayName(),
						UIUtil.getPluginImageDescriptor(bundleId, iconUrl).createImage(), false, 1);
				templateButton.setData(framework);
				((GridData) templateButton.getLayoutData()).verticalAlignment = SWT.TOP;

				templateButton.addSelectionListener(new SelectionAdapter() {

					@Override
					public void widgetSelected(SelectionEvent e) {
						getDataModel().setProperty(PORTLET_FRAMEWORK, framework);
						getDataModel().setBooleanProperty(PLUGIN_FRAGMENT_ENABLED, delegate.isFragmentEnabled());
					}

				});

				buttons.add(templateButton);

				final URL helpUrl = framework.getHelpUrl();
				Link descriptionWithLink =
					SWTUtil.createLink(group, SWT.WRAP, framework.getDescription() +
						(helpUrl != null ? " <a>Learn more....</a>" : ""), 1);
				descriptionWithLink.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));

				if (helpUrl != null) {
					descriptionWithLink.addSelectionListener(new SelectionAdapter() {

						@Override
						public void widgetSelected(SelectionEvent e) {
							try {
								PlatformUI.getWorkbench().getBrowserSupport().getExternalBrowser().openURL(helpUrl);
							}
							catch (Exception e1) {
								ProjectUIPlugin.logError("Could not open external browser", e1);
							}
						}

					});
				}

			}

			frameworkButtons = buttons.toArray(new Button[0]);
			frameworkButtons[0].setSelection(true);
		}
	}

	protected void createTemplateOptionsGroup(Composite parent) {
		if (CoreUtil.isNullOrEmpty(frameworkButtons)) {
			return;
		}

		final Composite stackComposite = new Composite(parent, SWT.NONE);
		stackComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

		final StackLayout optionsLayout = new StackLayout();
		stackComposite.setLayout(optionsLayout);

		List<Composite> composites = new ArrayList<Composite>();

		for (Button templateButton : frameworkButtons) {
			IPortletFramework template = (IPortletFramework) templateButton.getData();
			IPortletFrameworkDelegate delegate = ProjectUIPlugin.getPortletFrameworkDelegate(template.getId());

			final Composite[] optionsComposite = new Composite[1];

			if (delegate != null) {
				optionsComposite[0] = delegate.createNewProjectOptionsComposite(stackComposite, getDataModel());
			}

			templateButton.addSelectionListener(new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					optionsLayout.topControl = optionsComposite[0];
					stackComposite.layout();
				}

			});

			if (optionsComposite[0] == null) {
				optionsComposite[0] = SWTUtil.createComposite(stackComposite, 1, 1, SWT.FILL);
			}

			composites.add(optionsComposite[0]);
		}

		optionsComposites = composites.toArray(new Composite[0]);

		optionsLayout.topControl = optionsComposites[0];
		stackComposite.layout();
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite top = new Composite(parent, SWT.NONE);

		top.setLayout(new GridLayout());
		top.setLayoutData(new GridData(GridData.FILL_BOTH));

		createFrameworkGroup(top);
		// createPluginFragmentButton(top);
		createTemplateOptionsGroup(top);

		return top;
	}

	protected IDataModel getModel() {
		return model;
	}

	@Override
	protected String getModuleFacetID() {
		return IModuleConstants.JST_WEB_MODULE;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return new String[] {
			PLUGIN_FRAGMENT_ENABLED, PORTLET_FRAMEWORK
		};
	}

}
