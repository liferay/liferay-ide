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

package com.liferay.ide.portlet.core.model.lfrportlet610;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author kamesh
 */
@GenerateImpl
public interface ILiferayPortlet extends com.liferay.ide.portlet.core.model.lfrportlet600.ILiferayPortlet {

	public ElementType TYPE = new ElementType(ILiferayPortlet.class);

	// *** AtomCollectionAdapter ***

	@Type(base = IAtomCollectionAdapter.class)
	@Label(standard = "label")
	@XmlListBinding(mappings = {@XmlListBinding.Mapping(element = "atom-collection-adapter", type = IAtomCollectionAdapter.class)})
	public ListProperty PROP_ATOM_COLLECTION_ADAPTER = new ListProperty(TYPE, "AtomCollectionAdapter");

	public ElementList<IAtomCollectionAdapter> getAtomCollectionAdapter();

}