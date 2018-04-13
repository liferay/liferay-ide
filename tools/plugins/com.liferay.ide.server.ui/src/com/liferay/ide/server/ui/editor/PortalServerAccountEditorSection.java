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

package com.liferay.ide.server.ui.editor;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerConstants;
import com.liferay.ide.server.ui.cmd.SetPortalServerPasswordCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerUsernameCommand;

import java.beans.PropertyChangeEvent;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * @author Terry Jia
 */
public class PortalServerAccountEditorSection extends AbstractPortalServerEditorSection {

	public PortalServerAccountEditorSection() {
	}

	@Override
	protected void addPropertyListeners(PropertyChangeEvent event) {
		if (PortalServer.ATTR_USERNAME.equals(event.getPropertyName())) {
			String s = (String)event.getNewValue();

			PortalServerAccountEditorSection.this.username.setText(s);

			validate();
		}
		else if (PortalServer.ATTR_PASSWORD.equals(event.getPropertyName())) {
			String s = (String)event.getNewValue();

			PortalServerAccountEditorSection.this.password.setText(s);

			validate();
		}
	}

	@Override
	protected void createEditorSection(FormToolkit toolkit, Composite composite) {
		Label label = createLabel(toolkit, composite, Msgs.username);

		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		username = toolkit.createText(composite, null);

		username.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		username.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String userName = username.getText();

					execute(new SetPortalServerUsernameCommand(server, userName.trim()));

					updating = false;
				}

			});

		label = createLabel(toolkit, composite, StringPool.EMPTY);

		data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

		label = createLabel(toolkit, composite, Msgs.password);

		label.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));

		password = toolkit.createText(composite, null, SWT.PASSWORD);

		password.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		password.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String p = password.getText();

					execute(new SetPortalServerPasswordCommand(server, p.trim()));

					updating = false;
				}

			});
	}

	@Override
	protected String getSectionLabel() {
		return Msgs.liferayAccount;
	}

	@Override
	protected void initProperties() {
		username.setText(portalServer.getUsername());
		password.setText(portalServer.getPassword());
	}

	@Override
	protected boolean needCreate() {
		return true;
	}

	@Override
	protected void setDefault() {
		execute(new SetPortalServerUsernameCommand(server, PortalServerConstants.DEFAULT_USERNAME));

		username.setText(PortalServerConstants.DEFAULT_USERNAME);

		execute(new SetPortalServerPasswordCommand(server, StringPool.EMPTY));

		password.setText(StringPool.EMPTY);
	}

	protected Text password;
	protected Text username;

	private static class Msgs extends NLS {

		public static String liferayAccount;
		public static String password;
		public static String username;

		static {
			initializeMessages(PortalServerAccountEditorSection.class.getName(), Msgs.class);
		}

	}

}