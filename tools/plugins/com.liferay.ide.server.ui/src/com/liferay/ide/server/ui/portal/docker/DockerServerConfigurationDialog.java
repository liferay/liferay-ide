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

import java.util.Objects;

import org.apache.commons.validator.routines.UrlValidator;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
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
import org.eclipse.ui.ISharedImages;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.UIUtil;

/**
 * @author Ethan Sun
 * @author Simon Jiang
 */
public class DockerServerConfigurationDialog extends Dialog {

	public DockerServerConfigurationDialog(Shell shell, DockerRegistryConfiguration _configuration) {
		super(shell);

		_configurationData = _configuration;
		
		_edit = Objects.nonNull(_configuration);
	}

	public DockerRegistryConfiguration getConfigurationData() {
		return _configurationData;
	}

	public void setDialogTitle(String title) {
		Shell shell = getShell();

		if ((shell != null) && !shell.isDisposed()) {
			shell.setText(title);
		}
	}

	@Override
	protected Button createButton(Composite parent, int id, String label, boolean defaultButton) {
		if (IDialogConstants.OK_ID == id) {
			Button button = super.createButton(parent, id, "Ok", defaultButton);

			button.setEnabled(false);

			return button;
		}

		return super.createButton(parent, id, label, defaultButton);
	}
	
	public void validate() {
		boolean validRegistryName = true;

		if ((_repoName != null) && !_repoName.isDisposed()) {
			validRegistryName = CoreUtil.isNotNullOrEmpty(_repoName.getText());
			
			if (!validRegistryName) {
				errorMessageLabel.setText("Invalid registry name");
			}
		}
			
		boolean validRegistryUrl = true;

		if ((_registryUrl != null) && !_registryUrl.isDisposed()) {
			String value = _registryUrl.getText();
			
			UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS | UrlValidator.ALLOW_ALL_SCHEMES);

			validRegistryUrl = CoreUtil.isNotNullOrEmpty(value) && urlValidator.isValid(value);
			
			if (!validRegistryUrl) {
				errorMessageLabel.setText("Invalid registry ulr value");
			}
			
		}
		
		boolean validateValue = validRegistryName && validRegistryUrl;
		
		errorMessageLabel.setVisible(!validateValue);

		Button okButton = getButton(OK);

		if (okButton != null) {
			okButton.setEnabled(validateValue);
		}
	}

	protected void cancelPressed() {
		_configurationData = null;

		super.cancelPressed();
	}

	@Override
	protected final Control createDialogArea(Composite parent) {
		Composite dialogArea = (Composite)super.createDialogArea(parent);

		setDialogTitle(_edit ? "Edit Docker Registry Url" : "Add Docker Registry Url");

		dialogArea.setLayout(new GridLayout(2, false));

		Label registryNamelabel = new Label(dialogArea, SWT.LEFT);

		registryNamelabel.setText("Registrl Name");
		registryNamelabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		_repoName = new Text(dialogArea, SWT.HORIZONTAL | SWT.SINGLE);

		_repoName.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		Label registryUrllabel = new Label(dialogArea, SWT.LEFT);

		registryUrllabel.setText("Registrl Url");
		registryUrllabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		_registryUrl = new Text(dialogArea, SWT.HORIZONTAL | SWT.SINGLE);

		_registryUrl.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		_createErrorMessageGroup(dialogArea);

		if (_edit) {
			_repoName.setEnabled(false);

			_repoName.setText(_configurationData.getName());
		}

		_repoName.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					validate();
				}

			});
		
		
		_registryUrl.addModifyListener(
			new ModifyListener() {

				@Override
				public void modifyText(ModifyEvent e) {
					validate();
				}

			});
		
		if (_configurationData != null) {
			String urlValue = _configurationData.getRegitstryUrl();

			_registryUrl.setText(CoreUtil.isNotNullOrEmpty(urlValue) ? urlValue : "");
		}

		return dialogArea;
	}

	protected CLabel errorMessageLabel;
	
	private void _createErrorMessageGroup(Composite parent) {
		ISharedImages sharedImages = UIUtil.getSharedImages();

		errorMessageLabel = new CLabel(parent, SWT.LEFT_TO_RIGHT);

		errorMessageLabel.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
		errorMessageLabel.setImage(sharedImages.getImage(ISharedImages.IMG_OBJS_ERROR_TSK));
		errorMessageLabel.setVisible(false);
	}

	
	protected void okPressed() {
		if ((_repoName != null) && (_registryUrl != null)) {
			if (_configurationData == null) {
				_configurationData = new DockerRegistryConfiguration();
			}

			String name = _repoName.getText();

			String url = _registryUrl.getText();

			if (!CoreUtil.isNullOrEmpty(name.trim()) && !CoreUtil.isNullOrEmpty(url.trim())) {
				_configurationData.setName(name);

				_configurationData.setRegitstryUrl(url);
			}
		}
		else {
			_configurationData = null;
		}

		super.okPressed();
	}

	private DockerRegistryConfiguration _configurationData;
	private final boolean _edit;
	private Text _registryUrl;
	private Text _repoName;

}