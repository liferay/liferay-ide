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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.server.core.portal.PortalServer;

import java.util.Collections;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.ServerCore;

/**
 * @author Simon Jiang
 * @author Lovett Li
 */
public class ServicePossibleValuesService extends PossibleValuesService {

	@Override
	public void dispose() {
		NewLiferayModuleProjectOp op = _op();

		if (_listener != null) {
			op.property(NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME).detach(_listener);

			_listener = null;
		}

		super.dispose();
	}

	@Override
	public Status problem(Value<?> value) {
		return Status.createOkStatus();
	}

	@Override
	protected void compute(Set<String> values) {
		NewLiferayModuleProjectOp op = _op();

		String template = op.getProjectTemplateName().content(true);

		IServer runningServer = null;
		IServer[] servers = ServerCore.getServers();

		if (template.equals("service-wrapper")) {
			for (IServer server : servers) {
				String serverId = server.getServerType().getId();

				if (serverId.equals(PortalServer.ID)) {
					runningServer = server;

					break;
				}
			}

			try {
				// TODO use target platform instead soon

				// ServiceContainer serviceWrapperList = new ServiceWrapperCommand(runningServer).execute();

				// values.addAll(serviceWrapperList.getServiceList());

				values.addAll(Collections.emptySet());
			}
			catch (Exception e) {
				ProjectCore.logError("Get service wrapper list error.", e);
			}
		}
		else if (template.equals("service")) {
			for (IServer server : servers) {
				String serverId = server.getServerType().getId();

				if ((server.getServerState() == IServer.STATE_STARTED) && serverId.equals(PortalServer.ID)) {
					runningServer = server;

					break;
				}
			}

			try {
				// TODO use target platform instead soon

				// ServiceCommand serviceCommand = new ServiceCommand(runningServer);

				// ServiceContainer allServices = serviceCommand.execute();

				// values.addAll(allServices.getServiceList());

				values.addAll(Collections.emptySet());
			}
			catch (Exception e) {
				ProjectCore.logError("Get services list error. ", e);
			}
		}
	}

	protected void initPossibleValuesService() {
		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayModuleProjectOp op = _op();

		op.property(NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME).attach(_listener);
	}

	private NewLiferayModuleProjectOp _op() {
		return context(NewLiferayModuleProjectOp.class);
	}

	private Listener _listener;

}