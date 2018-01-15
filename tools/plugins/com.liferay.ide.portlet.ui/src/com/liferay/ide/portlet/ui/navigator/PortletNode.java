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

package com.liferay.ide.portlet.ui.navigator;

import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.portlet.core.model.Portlet;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.CapitalizationType;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 * @author Gregory Amerson
 */
public class PortletNode {

	public PortletNode(PortletsNode portletsNode, Portlet portlet) {
		_parent = portletsNode;
		_portlet = portlet;
	}

	public Element getModel() {
		return _portlet;
	}

	public String getName() {
		String retval = StringPool.EMPTY;

		if (_portlet != null) {
			Value<String> label = this._portlet.getPortletName();

			retval = label.localized(CapitalizationType.TITLE_STYLE, false);
		}

		return retval;
	}

	public PortletsNode getParent() {
		return _parent;
	}

	private PortletsNode _parent;
	private Portlet _portlet;

}