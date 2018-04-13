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
import com.liferay.ide.server.ui.cmd.SetPortalServerHttpPortCommand;

import java.beans.PropertyChangeEvent;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.FormToolkit;
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
	}

	protected void createEditorSection(FormToolkit toolkit, Composite composite) {
		Label label = createLabel(toolkit, composite, Msgs.httpPort);

		GridData data = new GridData(SWT.BEGINNING, SWT.CENTER, false, false);

		label.setLayoutData(data);

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
				}

			});
	}

	protected String getSectionLabel() {
		return Msgs.ports;
	}

	protected void initProperties() {
		httpPort.setText(portalBundle.getHttpPort());
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
	}

	protected Text httpPort;

	private static class Msgs extends NLS {

		public static String httpPort;
		public static String ports;

		static {
			initializeMessages(PortalServerPortsEditorSection.class.getName(), Msgs.class);
		}

	}

}