package com.liferay.ide.eclipse.server.core;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.wst.server.core.IModule;

/**
 * @author gregory.amerson@liferay.com
 */
public abstract class AbstractPluginDeployer implements IPluginDeployer {

	protected String facetId;

	public AbstractPluginDeployer() {
		this(null);
	}

	public AbstractPluginDeployer(String facetId) {
		super();
		this.facetId = facetId;
	}

	public String getFacetId() {
		return facetId;
	}

	public boolean prePublishModule(int kind, int deltaKind, IModule[] moduleTree, IProgressMonitor monitor) {
		return true;
	}

	public void setFacetId(String facetId) {
		this.facetId = facetId;
	}

}
