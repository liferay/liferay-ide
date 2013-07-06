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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/
package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.service.core.model.Entity;
import com.liferay.ide.service.core.model.ServiceBuilder;

import org.eclipse.sapphire.services.ReferenceService;

/**
 * @author Gregory Amerson
 */
public class EntityRelationshipService extends ReferenceService
{

    public static Entity findEntity(final String entityName, final ServiceBuilder serviceBuilder)
    {
        if( entityName != null && serviceBuilder != null)
        {
            if( serviceBuilder != null )
            {
                for( Entity entity : serviceBuilder.getEntities() )
                {
                    if( entityName.equals( entity.getName().content() ) )
                    {
                        return entity;
                    }
                }
            }
        }

        return null;
    }

    @Override
    public Object resolve( final String reference )
    {
        return findEntity( reference, context( ServiceBuilder.class ) );
    }

}
