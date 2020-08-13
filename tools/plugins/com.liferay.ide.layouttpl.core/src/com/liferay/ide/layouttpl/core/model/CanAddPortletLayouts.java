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

package com.liferay.ide.layouttpl.core.model;

import com.liferay.ide.layouttpl.core.model.internal.PortletLayoutsListener;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Listeners;

/**
 * @author Kuo Zhang
 */
public interface CanAddPortletLayouts extends Element {

	public static final ElementType TYPE = new ElementType(CanAddPortletLayouts.class);

	@Listeners(PortletLayoutsListener.class)
	@Type(base = PortletLayoutElement.class)
	public static final ListProperty PROP_PORTLET_LAYOUTS = new ListProperty(TYPE, "PortletLayouts");

	public ElementList<PortletLayoutElement> getPortletLayouts();

}