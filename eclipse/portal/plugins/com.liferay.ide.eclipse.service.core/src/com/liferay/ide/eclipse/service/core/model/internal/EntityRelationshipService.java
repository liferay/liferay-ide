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
package com.liferay.ide.eclipse.service.core.model.internal;

import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.ReferenceService;


public class EntityRelationshipService extends ReferenceService {

    @Override
	public Object resolve(final String reference) {
		if (reference != null) {
			IServiceBuilder serviceBuilder = element().nearest( IServiceBuilder.class );

			if ( serviceBuilder != null ) {
				for (IEntity entity : serviceBuilder.getEntities()) {
					if ( reference.equals( entity.getName().getContent() ) ) {
						return entity;
					}
				}
			}
        }
        
        return null;
    }

}
