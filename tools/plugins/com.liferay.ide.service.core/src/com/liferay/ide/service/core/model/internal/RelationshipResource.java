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

import com.liferay.ide.service.core.model.Column;
import com.liferay.ide.service.core.model.Entity;
import com.liferay.ide.service.core.model.Relationship;
import com.liferay.ide.service.core.model.ServiceBuilder;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyBinding;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.ValuePropertyBinding;

/**
 * @author Gregory Amerson
 */
public class RelationshipResource extends Resource
{
    private RelationshipObject relationshipObject;

    public RelationshipResource( RelationshipObject obj, Resource parent )
    {
        super( parent );
        this.relationshipObject = obj;
    }

    @Override
    protected PropertyBinding createBinding( Property property )
    {
        PropertyBinding binding = null;
        String[] params = null;

        if( Relationship.PROP_FROM_ENTITY.equals( property ) )
        {
            binding = new ValuePropertyBinding()
            {

                @Override
                public String read()
                {
                    return RelationshipResource.this.relationshipObject.getFromName();
                }

                @Override
                public void write( String value )
                {
                    RelationshipResource.this.relationshipObject.setFromName( value );
                    persistRelationship();
                }
            };
        }
        else if( Relationship.PROP_TO_ENTITY.equals( property ) )
        {
            binding = new ValuePropertyBinding()
            {

                @Override
                public String read()
                {
                    return RelationshipResource.this.relationshipObject.getToName();
                }

                @Override
                public void write( String value )
                {
                    RelationshipResource.this.relationshipObject.setToName( value );
                    persistRelationship();
                }
            };
        }

        if( binding != null )
        {
            binding.init( property );
        }

        return binding;
    }

    public RelationshipObject getRelationshipObject()
    {
        return this.relationshipObject;
    }

    private void persistRelationship()
    {
        final ServiceBuilder serviceBuilder = parent().element().nearest( ServiceBuilder.class );

        final String fromName = this.relationshipObject.getFromName();
        final String toName = this.relationshipObject.getToName();

        final Entity fromEntity = EntityRelationshipService.findEntity( fromName, serviceBuilder );
        final Entity toEntity = EntityRelationshipService.findEntity( toName, serviceBuilder );

        if( fromEntity != null && toEntity != null )
        {
            Column primaryKeyColumn = null;

            for( Column column : toEntity.getColumns() )
            {
                if( column.isPrimary().content() )
                {
                    primaryKeyColumn = column;
                    break;
                }
            }

            if( primaryKeyColumn != null )
            {
                final Column column = fromEntity.getColumns().insert();
                column.setName( primaryKeyColumn.getName().content() );
                column.setType( "long" ); //$NON-NLS-1$
            }
        }
    }

}
