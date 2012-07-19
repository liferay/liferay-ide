/*******************************************************************************
 * Copyright (c) 2010-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.taglib.ui.model;

import com.liferay.ide.taglib.ui.model.internal.PreviewSourceContentProvider;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListProperty;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DependsOn;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;


/**
 * @author Gregory Amerson
 */
@GenerateImpl
@XmlBinding( path = "tag" )
public interface ITag extends IModelElement {

	ModelElementType TYPE = new ModelElementType(ITag.class);

	// *** Name ***

	@XmlBinding(path = "name")
	@ReadOnly
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	Value<String> getName();

	// *** Prefix ***

	@XmlBinding(path = "prefix")
	@ReadOnly
	ValueProperty PROP_PREFIX = new ValueProperty(TYPE, "Prefix");

	Value<String> getPrefix();

	// *** Required Attributes ***

	@Type(base = IAttribute.class)
	@ReadOnly
	@XmlListBinding(path = "required", mappings = @XmlListBinding.Mapping(element = "attribute", type = IAttribute.class))
	ListProperty PROP_REQUIRED_ATTRIBUTES = new ListProperty(TYPE, "RequiredAttributes");

	ModelElementList<IAttribute> getRequiredAttributes();

	// *** Event Attributes ***

	@Type(base = IAttribute.class)
	@ReadOnly
	@XmlListBinding(path = "events", mappings = @XmlListBinding.Mapping(element = "attribute", type = IAttribute.class))
	ListProperty PROP_EVENTS = new ListProperty(TYPE, "Events");

	ModelElementList<IAttribute> getEvents();

	// *** Other Attributes ***

	@Type(base = IAttribute.class)
	@ReadOnly
	@XmlListBinding(path = "other", mappings = @XmlListBinding.Mapping(element = "attribute", type = IAttribute.class))
	ListProperty PROP_OTHER_ATTRIBUTES = new ListProperty(TYPE, "OtherAttributes");

	ModelElementList<IAttribute> getOtherAttributes();

	@Service( impl = PreviewSourceContentProvider.class )
	@DependsOn({ "RequiredAttributes/*", "Events/*", "OtherAttributes/*" })
	@ReadOnly
	ValueProperty PROP_PREVIEW = new ValueProperty(TYPE, "Preview");

	Value<String> getPreview();

	@Service( impl = PreviewSourceContentProvider.class )
	@DependsOn({ "RequiredAttributes/*", "Events/*", "OtherAttributes/*" })
	@ReadOnly
	ValueProperty PROP_SOURCE = new ValueProperty(TYPE, "Source");

	Value<String> getSource();
}
