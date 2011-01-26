package com.liferay.ide.eclipse.server.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.wst.server.core.IModule;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;
import org.eclipse.wst.server.core.model.ServerBehaviourDelegate;


public interface IPluginPublisher {

	public static final String ID = "com.liferay.ide.eclipse.server.core.pluginPublishers";

	public IStatus canPublishModule(IServer server, IModule module);

	public String getFacetId();

	public String getRuntimeTypeId();

	public boolean prePublishModule(
		ServerBehaviourDelegate delegate, int kind, int deltaKind, IModule[] moduleTree, IModuleResourceDelta[] delta,
		IProgressMonitor monitor);

}
