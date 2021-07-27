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

import com.github.dockerjava.api.model.Container;

import com.liferay.blade.gradle.tooling.ProjectInfo;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;
import com.liferay.ide.gradle.core.LiferayGradleCore;
import com.liferay.ide.gradle.ui.LiferayGradleUI;
import com.liferay.ide.server.core.portal.docker.PortalDockerServer;
import com.liferay.ide.server.util.LiferayDockerClient;
import com.liferay.ide.ui.util.UIUtil;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Text;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;

/**
 * @author Simon Jiang
 */
public class DockerServerSettingComposite extends Composite implements ModifyListener {

	public static void setFieldValue(Text field, String value) {
		if ((field != null) && !field.isDisposed()) {
			field.setText((value != null) ? value : StringPool.EMPTY);
		}
	}

	public DockerServerSettingComposite(Composite parent, IWizardHandle wizard) {
		super(parent, SWT.NONE);

		_complete = false;

		_wizard = wizard;

		wizard.setTitle(Msgs.dockerContainerTitle);
		wizard.setDescription(Msgs.dockerContainerDescription);
		wizard.setImageDescriptor(LiferayGradleUI.getImageDescriptor(LiferayGradleUI.IMG_WIZ_RUNTIME));

		_project = LiferayWorkspaceUtil.getWorkspaceProject();

		createControl(parent);

		validate();
	}

	@Override
	public void dispose() {
		_nameField.removeModifyListener(this);
		super.dispose();
	}

	public boolean isComplete() {
		return _complete;
	}

	@Override
	public void modifyText(ModifyEvent e) {
		Object source = e.getSource();

		if (source.equals(_nameField)) {
			validate();

			PortalDockerServer portalDockerServer = getPortalDockerServer();

			if (portalDockerServer != null) {
				portalDockerServer.setContainerName(_nameField.getText());
			}
		}
	}

	public void setServer(IServerWorkingCopy newServer) {
		if (newServer == null) {
			_serverWC = null;
		}
		else {
			_serverWC = newServer;
		}

		init();

		try {
			validate();
		}
		catch (NullPointerException npe) {
		}
	}

	protected void createControl(final Composite parent) {
		setLayout(createLayout());
		setLayoutData(_createLayoutData());
		setBackground(parent.getBackground());

		_createFields();

		Dialog.applyDialogFont(this);
	}

	protected Label createLabel(String text) {
		Label label = new Label(this, SWT.NONE);

		label.setText(text);

		GridDataFactory.generate(label, 1, 1);

		return label;
	}

	protected Layout createLayout() {
		return new GridLayout(1, false);
	}

	protected Text createTextField(String labelText) {
		return createTextField(labelText, SWT.READ_ONLY);
	}

	protected Text createTextField(String labelText, int style) {
		createLabel(labelText);

		Text text = new Text(this, SWT.BORDER | style);

		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	protected PortalDockerServer getPortalDockerServer() {
		return (PortalDockerServer)getServer().loadAdapter(PortalDockerServer.class, null);
	}

	protected IServerWorkingCopy getServer() {
		return _serverWC;
	}

	protected void init() {
		_updateFields();
	}

	protected void validate() {
		String containerName = _nameField.getText();

		if (CoreUtil.isNullOrEmpty(containerName)) {
			_wizard.setMessage("Container name can not be empty.", IMessageProvider.ERROR);
			_wizard.update();
			_complete = false;

			return;
		}

		Container container = LiferayDockerClient.getDockerContainerByName(_nameField.getText());

		if (Objects.nonNull(container)) {
			_wizard.setMessage("Container name is existed, Please change it to another.", IMessageProvider.ERROR);
			_wizard.update();
			_complete = false;

			return;
		}

		_complete = true;

		_wizard.setMessage(null, IMessageProvider.NONE);
		_wizard.update();
	}

	private void _createFields() {
		_nameField = createTextField(Msgs.dockerContainerName);

		_nameField.addModifyListener(this);
	}

	private GridData _createLayoutData() {
		return new GridData(GridData.FILL_BOTH);
	}

	private void _updateFields() {
		CompletableFuture<ProjectInfo> projectInfoAsync = CompletableFuture.supplyAsync(
			() -> LiferayGradleCore.getToolingModel(ProjectInfo.class, _project));

		projectInfoAsync.thenAcceptAsync(
			projectInfo -> {
				if (projectInfo != null) {
					UIUtil.async(
						() -> {
							setFieldValue(_nameField, projectInfo.getDockerContainerId());

							_complete = true;

							validate();
						});
				}
			});
	}

	private boolean _complete;
	private Text _nameField;
	private IProject _project;
	private IServerWorkingCopy _serverWC;
	private final IWizardHandle _wizard;

	private static class Msgs extends NLS {

		public static String dockerContainerDescription;
		public static String dockerContainerName;
		public static String dockerContainerTitle;

		static {
			initializeMessages(DockerServerSettingComposite.class.getName(), Msgs.class);
		}

	}

}