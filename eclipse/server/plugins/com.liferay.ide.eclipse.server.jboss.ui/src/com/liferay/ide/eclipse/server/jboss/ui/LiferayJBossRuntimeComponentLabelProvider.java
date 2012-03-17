/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.server.jboss.ui;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.eclipse.wst.server.ui.FacetRuntimeComponentLabelProvider;

/**
 * @author Greg Amerson
 */
public class LiferayJBossRuntimeComponentLabelProvider extends FacetRuntimeComponentLabelProvider {

	public final class RuntimeLabelProvider implements IRuntimeComponentLabelProvider {

		private final IRuntimeComponent rc;

		public RuntimeLabelProvider(IRuntimeComponent rc) {
			this.rc = rc;
		}

		public String getLabel() {
			return rc.getProperty("type");
		}
	}

	public Object getAdapter(Object adaptable, Class adapterType) {
		IRuntimeComponent rc = (IRuntimeComponent) adaptable;
		
		return new RuntimeLabelProvider(rc);
	}
}
