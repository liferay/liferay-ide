/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

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
