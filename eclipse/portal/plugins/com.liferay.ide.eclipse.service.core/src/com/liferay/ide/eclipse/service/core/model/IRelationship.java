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
package com.liferay.ide.eclipse.service.core.model;

import com.liferay.ide.eclipse.service.core.model.internal.EntityRelationshipService;
import com.liferay.ide.eclipse.service.core.model.internal.RelationshipLabelService;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ReferenceValue;
import org.eclipse.sapphire.modeling.Value;
import org.eclipse.sapphire.modeling.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.GenerateImpl;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.ReadOnly;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;


@GenerateImpl
@Image(path = "images/references_16x16.png")
public interface IRelationship extends IModelElement {

	ModelElementType TYPE = new ModelElementType(IRelationship.class);

	@Reference(target = IEntity.class)
	@Service(impl = EntityRelationshipService.class)
	@Required
	@XmlBinding(path = "name")
	ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

	ReferenceValue<String, IEntity> getName();

	void setName(String value);

	ValueProperty PROP_FOREIGN_KEY_COLUMN_NAME = new ValueProperty( TYPE, "ForeignKeyColumnName" );

    Value<String> getForeignKeyColumnName();

	void setForeignKeyColumnName( String value );

	@ReadOnly
	@Service( impl = RelationshipLabelService.class )
	ValueProperty PROP_LABEL = new ValueProperty( TYPE, "Label" );

	Value<String> getLabel();

}
