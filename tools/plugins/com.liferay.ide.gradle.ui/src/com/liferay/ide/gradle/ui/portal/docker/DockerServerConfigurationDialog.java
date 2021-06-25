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

import java.util.Properties;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.TrayDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Ethan Sun
 */
public class DockerServerConfigurationDialog extends TrayDialog {

	public DockerServerConfigurationDialog(Shell shell, boolean edit) {
		super(shell);

		_edit = edit;
	}

	public Properties getConfigurationData() {
		return _configurationData;
	}

	public void setConfigurationData(Properties data) {
		if (data == null) {
			_configurationData = null;
		}
		else {
			_configurationData = new Properties(data);
		}
	}

	public void setDialogTitle(String title) {
		Shell shell = getShell();

		if ((shell != null) && !shell.isDisposed()) {
			shell.setText(title);
		}
	}

	public void validate() {
		boolean valid = true;

		if ((_repoName != null) && !_repoName.isDisposed()) {
			valid = CoreUtil.isNotNullOrEmpty(_repoName.getText());
		}

		String value = null;

		if ((_registryUrl != null) && !_registryUrl.isDisposed()) {
			value = _registryUrl.getText();

			valid = CoreUtil.isNotNullOrEmpty(value);
		}

		Button okButton = getButton(0);

		if (okButton != null) {
			okButton.setEnabled(valid);
		}
	}

	protected void cancelPressed() {
		_configurationData = null;

		super.cancelPressed();
	}

	@Override
	protected final Control createDialogArea(Composite parent) {
		Control control = super.createDialogArea(parent);

		if (control instanceof Composite) {
			ScrolledComposite sc = new ScrolledComposite((Composite)control, SWT.V_SCROLL);

			GridLayout layout = new GridLayout(1, true);

			layout.marginHeight = 0;

			layout.marginWidth = 0;

			layout.verticalSpacing = 0;

			layout.horizontalSpacing = 0;

			sc.setLayout(layout);

			sc.setLayoutData(new GridData(GridData.FILL, GridData.FILL, true, true));

			sc.setExpandHorizontal(true);

			sc.setExpandVertical(true);

			Composite composite = new Composite(sc, SWT.NONE);

			composite.setLayout(new GridLayout());

			createDialogAreaContent(composite);

			sc.setContent(composite);

			sc.setMinSize(composite.computeSize(SWT.DEFAULT, SWT.DEFAULT));

			control = sc;
		}

		return control;
	}

	protected void createDialogAreaContent(Composite parent) {
		Assert.isNotNull(parent);

		setDialogTitle(_edit ? "_edit Docker Registry Url" : "Add Docker Registry Url");

		Composite panel = new Composite(parent, SWT.NONE);

		GridLayout layout = new GridLayout(2, false);

		layout.marginHeight = 0;

		layout.marginWidth = 0;

		panel.setLayout(layout);

		GridData layoutData = new GridData(SWT.FILL, SWT.CENTER, true, true);

		layoutData.widthHint = convertWidthInCharsToPixels(50);

		panel.setLayoutData(layoutData);

		Label label = new Label(panel, SWT.HORIZONTAL);

		layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setText("Repo Name");
		label.setLayoutData(layoutData);

		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		layoutData.widthHint = convertWidthInCharsToPixels(30);

		_repoName = new Text(panel, SWT.HORIZONTAL | SWT.SINGLE | SWT.BORDER);

		_repoName.setLayoutData(layoutData);

		if (_edit) {
			_repoName.setEnabled(false);

			_repoName.setText(_configurationData.getProperty(LiferayGradleDockerServer.PROP_REPO_NAME));
		}

		_repoName.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					validate();
				}

			});

		layoutData = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label = new Label(panel, SWT.HORIZONTAL);

		label.setText("Docker Registry Host");
		label.setLayoutData(layoutData);

		Composite panel2 = new Composite(panel, SWT.NONE);

		layout = new GridLayout(2, false);

		layout.marginHeight = 0;
		layout.marginWidth = 0;

		panel2.setLayout(layout);

		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		panel2.setLayoutData(layoutData);

		layoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);

		layoutData.widthHint = convertWidthInCharsToPixels(30);

		_registryUrl = new Text(panel2, SWT.HORIZONTAL | SWT.SINGLE | SWT.BORDER);

		_registryUrl.setLayoutData(layoutData);

		_registryUrl.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					validate();
				}

			});

		if (_configurationData != null) {
			String urlValue = _configurationData.getProperty(LiferayGradleDockerServer.PROP_REGISTRY_URL);

			_registryUrl.setText(CoreUtil.isNotNullOrEmpty(urlValue) ? urlValue : "");
		}
	}

	protected void okPressed() {
		if ((_repoName != null) && (_registryUrl != null)) {
			if (_configurationData == null) {
				_configurationData = new Properties();
			}

			String name = _repoName.getText();

			String url = _registryUrl.getText();

			if (!CoreUtil.isNullOrEmpty(name.trim()) && !CoreUtil.isNullOrEmpty(url.trim())) {
				_configurationData.setProperty(LiferayGradleDockerServer.PROP_REPO_NAME, name);

				_configurationData.setProperty(LiferayGradleDockerServer.PROP_REGISTRY_URL, url);
			}
			else {
				_configurationData.remove(name);
			}
		}
		else {
			_configurationData = null;
		}

		super.okPressed();
	}

	private Properties _configurationData;
	private final boolean _edit;
	private Text _registryUrl;
	private Text _repoName;

}