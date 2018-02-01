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

package com.liferay.ide.kaleo.core;

import com.liferay.ide.kaleo.core.util.IWorkflowValidation;
import com.liferay.ide.kaleo.core.util.WorkflowValidationProxy;
import com.liferay.ide.server.core.ILiferayServer;

import java.lang.reflect.Proxy;

import java.util.HashMap;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.core.runtime.QualifiedName;
import org.eclipse.core.runtime.Status;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.ServerCore;

import org.osgi.framework.BundleContext;

/**
 * @author Gregory Amerson
 */
public class KaleoCore extends Plugin {

	public static final String PLUGIN_ID = "com.liferay.ide.kaleo.core";

	public static final String DEFAULT_KALEO_VERSION = "7.0.0";

	public static final QualifiedName DEFAULT_SCRIPT_LANGUAGE_KEY = new QualifiedName(
		PLUGIN_ID, "defaultScriptLanguage");

	public static final QualifiedName DEFAULT_TEMPLATE_LANGUAGE_KEY = new QualifiedName(
		PLUGIN_ID, "defaultTemplateLanguage");

	public static final QualifiedName GRID_VISIBLE_KEY = new QualifiedName(PLUGIN_ID, "gridVisible");

	public static IStatus createErrorStatus(String msg) {
		return createErrorStatus(msg, null);
	}

	public static IStatus createErrorStatus(String msg, Exception e) {
		return new Status(IStatus.ERROR, PLUGIN_ID, msg, e);
	}

	public static KaleoCore getDefault() {
		return _plugin;
	}

	public static IKaleoConnection getKaleoConnection(ILiferayServer liferayServer) {
		if (_kaleoConnections == null) {
			_kaleoConnections = new HashMap<>();

			IServerLifecycleListener serverLifecycleListener = new IServerLifecycleListener() {

				public void serverAdded(IServer server) {
				}

				public void serverChanged(IServer server) {
				}

				public void serverRemoved(IServer s) {
					ILiferayServer server = (ILiferayServer)s.loadAdapter(
						ILiferayServer.class, new NullProgressMonitor());

					if (liferayServer.equals(server)) {
						IKaleoConnection service = _kaleoConnections.get(liferayServer.getId());

						if (service != null) {
							service = null;
							_kaleoConnections.put(liferayServer.getId(), null);
						}
					}
				}

			};

			ServerCore.addServerLifecycleListener(serverLifecycleListener);
		}

		IKaleoConnection service = _kaleoConnections.get(liferayServer.getId());

		if (service == null) {
			service = new KaleoConnection();

			updateKaleoConnectionSettings(liferayServer, service);

			_kaleoConnections.put(liferayServer.getId(), service);
		}

		return service;
	}

	public static IKaleoConnection getKaleoConnection(IServer parent) {
		return getKaleoConnection((ILiferayServer)parent.loadAdapter(ILiferayServer.class, null));
	}

	public static IWorkflowValidation getWorkflowValidation(IRuntime runtime) {
		if (_workflowValidators == null) {
			_workflowValidators = new HashMap<>();
		}

		IWorkflowValidation validator = _workflowValidators.get(runtime.getId());

		if (validator == null) {
			Class<?>[] interfaces = new Class<?>[] {IWorkflowValidation.class};

			validator = (IWorkflowValidation)Proxy.newProxyInstance(
				IWorkflowValidation.class.getClassLoader(), interfaces, new WorkflowValidationProxy(runtime));

			_workflowValidators.put(runtime.getId(), validator);
		}

		return validator;
	}

	public static void logError(Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}

	public static void logError(String msg, Exception e) {
		ILog log = getDefault().getLog();

		log.log(new Status(IStatus.ERROR, PLUGIN_ID, msg, e));
	}

	public static void updateKaleoConnectionSettings(ILiferayServer server) {
		updateKaleoConnectionSettings(server, getKaleoConnection(server));
	}

	public static void updateKaleoConnectionSettings(ILiferayServer server, IKaleoConnection connection) {
		connection.setHost(server.getHost());
		connection.setHttpPort(server.getHttpPort());
		connection.setPortalHtmlUrl(server.getPortalHomeUrl());
		connection.setPortalContextPath("/");
		connection.setUsername(server.getUsername());
		connection.setPassword(server.getPassword());
	}

	public synchronized WorkflowSupportManager getWorkflowSupportManager() {
		if (_workflowSupportManager == null) {
			_workflowSupportManager = new WorkflowSupportManager();
		}

		return _workflowSupportManager;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		_plugin = this;
	}

	public void stop(BundleContext context) throws Exception {
		_plugin = null;
		super.stop(context);
	}

	private static HashMap<String, IKaleoConnection> _kaleoConnections;
	private static KaleoCore _plugin;
	private static HashMap<String, IWorkflowValidation> _workflowValidators;

	private WorkflowSupportManager _workflowSupportManager;

}