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

import com.liferay.ide.server.core.portal.PortalRuntime;
import com.liferay.ide.server.core.portal.PortalServer;
import com.liferay.ide.server.core.portal.PortalServerConstants;
import com.liferay.ide.server.ui.cmd.SetGogoShellPortCommand;
import com.liferay.ide.server.ui.cmd.SetPortalServerHttpPortCommand;
import com.liferay.ide.server.util.ServerUtil;

import java.beans.PropertyChangeEvent;

import java.util.Objects;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.IMessageManager;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.wst.server.core.IRuntime;

/**
 * @author Terry Jia
 */
public class PortalServerPortsEditorSection extends AbstractPortalServerEditorSection {

	public PortalServerPortsEditorSection() {
	}

	protected void addPropertyListeners(PropertyChangeEvent event) {
		if (PortalServer.ATTR_HTTP_PORT.equals(event.getPropertyName())) {
			String s = (String)event.getNewValue();

			PortalServerPortsEditorSection.this.httpPort.setText(s);

			validate();
		}

		if (PortalServer.PROPERTY_GOGOSHELL_PORT.equals(event.getPropertyName())) {
			String s = (String)event.getNewValue();

			PortalServerPortsEditorSection.this.gogoShellPort.setText(s);

			validate();
		}
	}

	protected void createEditorSection(FormToolkit toolkit, Composite composite) {
		Label httpPortLabel = createLabel(toolkit, composite, Msgs.httpPort);

		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false, 2, 1);

		httpPortLabel.setLayoutData(data);

		httpPort = toolkit.createText(composite, null);

		httpPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		httpPort.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String hp = httpPort.getText();

					execute(new SetPortalServerHttpPortCommand(server, hp.trim()));

					updating = false;

					validate();
				}

			});

		Label gogoShellPortLabel = createLabel(toolkit, composite, Msgs.gogoShellPort);

		gogoShellPortLabel.setLayoutData(data);

		gogoShellPort = toolkit.createText(composite, null);

		gogoShellPort.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		gogoShellPort.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					if (updating) {
						return;
					}

					updating = true;

					String gogoPort = gogoShellPort.getText();

					execute(new SetGogoShellPortCommand(server, gogoPort.trim()));

					updating = false;

					validate();
				}

			});
	}

	protected void doValidate() {
		String gogoShellPort = portalServer.getGogoShellPort();

		String extGogoShellPort = ServerUtil.getGogoShellPort(server.getOriginal());

		_modifyFormHeadHeight();

		IMessageManager messageManager = getManagedForm().getMessageManager();

		if (!Objects.equals(gogoShellPort, extGogoShellPort)) {
			String errorMessage =
				"The customized gogo-shell port is not equals defined value in portal-ext.properties.";

			messageManager.addMessage(
				PortalServerPortsEditorSection.this.gogoShellPort, errorMessage, Status.WARNING, IStatus.WARNING);
		}
		else {
			messageManager.removeMessage(PortalServerPortsEditorSection.this.gogoShellPort);
		}
	}

	protected String getSectionLabel() {
		return Msgs.ports;
	}

	protected void initProperties() {
		httpPort.setText(portalBundle.getHttpPort());
		gogoShellPort.setText(portalServer.getGogoShellPort());

		validate();
	}

	@Override
	protected boolean needCreate() {
		IRuntime serverRuntime = server.getRuntime();

		PortalRuntime runtime = (PortalRuntime)serverRuntime.loadAdapter(
			PortalRuntime.class, new NullProgressMonitor());

		if (runtime != null) {
			return true;
		}

		return false;
	}

	protected void setDefault() {
		execute(new SetPortalServerHttpPortCommand(server, PortalServerConstants.DEFAULT_HTTP_PORT));

		httpPort.setText(PortalServerConstants.DEFAULT_HTTP_PORT);

		execute(new SetGogoShellPortCommand(server, PortalServerConstants.DEFAULT_GOGOSHELL_PORT));

		gogoShellPort.setText(PortalServerConstants.DEFAULT_GOGOSHELL_PORT);
	}

	protected Text gogoShellPort;
	protected Text httpPort;

	private void _modifyFormHeadHeight() {
		IManagedForm managedForm = getManagedForm();

		ScrolledForm scrolledForm = managedForm.getForm();

		Form form = scrolledForm.getForm();

		Composite head = form.getHead();

		Rectangle bounds = head.getBounds();

		head.setBounds(bounds.x, bounds.y, bounds.width, bounds.height + 5);
	}

	private static class Msgs extends NLS {

		public static String gogoShellPort;
		public static String httpPort;
		public static String ports;

		static {
			initializeMessages(PortalServerPortsEditorSection.class.getName(), Msgs.class);
		}

	}

}