package com.liferay.ide.eclipse.server.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;


public interface IPluginDeployer {

	public static final String ID = "com.liferay.ide.eclipse.server.core.pluginDeployer";

	public String getFacetId();

	public boolean prePublishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor);

}
