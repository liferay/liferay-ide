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

package com.liferay.ide.server.tomcat.ui;

import org.eclipse.wst.common.project.facet.core.runtime.IRuntimeComponent;
import org.eclipse.wst.common.project.facet.ui.IRuntimeComponentLabelProvider;
import org.eclipse.wst.server.ui.FacetRuntimeComponentLabelProvider;

/**
 * @author Gregory Amerson
 */
public class LiferayTomcatRuntimeComponentLabelProvider extends FacetRuntimeComponentLabelProvider {

	@SuppressWarnings("rawtypes")
	public Object getAdapter(Object adaptable, Class adapterType) {
		IRuntimeComponent rc = (IRuntimeComponent)adaptable;

		return new RuntimeLabelProvider(rc);
	}

	public class RuntimeLabelProvider implements IRuntimeComponentLabelProvider {

		public RuntimeLabelProvider(IRuntimeComponent rc) {
			_rc = rc;
		}

		public String getLabel() {
			return _rc.getProperty("type");
		}

		private IRuntimeComponent _rc;

	}

}