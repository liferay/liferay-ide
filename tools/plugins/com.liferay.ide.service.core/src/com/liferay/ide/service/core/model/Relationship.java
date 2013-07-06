/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.service.core.model;

import com.liferay.ide.service.core.model.internal.EntityRelationshipService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;


/**
 * @author Gregory Amerson
 */
@Image(path = "images/references_16x16.png")
public interface Relationship extends Element
{
	ElementType TYPE = new ElementType(Relationship.class);

	@Reference(target = Entity.class)
	@Service(impl = EntityRelationshipService.class)
	@Required
	ValueProperty PROP_FROM_ENTITY = new ValueProperty(TYPE, "FromEntity"); //$NON-NLS-1$

	ReferenceValue<String, Entity> getFromEntity();
	void setFromEntity(String value);

	@Reference(target = Entity.class)
    @Service(impl = EntityRelationshipService.class)
    @Required
    ValueProperty PROP_TO_ENTITY = new ValueProperty(TYPE, "ToEntity"); //$NON-NLS-1$

    ReferenceValue<String, Entity> getToEntity();
    void setToEntity(String value);

}
