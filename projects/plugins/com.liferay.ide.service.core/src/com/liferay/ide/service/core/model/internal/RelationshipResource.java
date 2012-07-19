/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

import com.liferay.ide.service.core.model.IColumn;
import com.liferay.ide.service.core.model.IEntity;
import com.liferay.ide.service.core.model.IRelationship;
import com.liferay.ide.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;


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
    protected BindingImpl createBinding( ModelProperty property )
    {
        BindingImpl binding = null;
        String[] params = null;

        if( IRelationship.PROP_FROM_ENTITY.equals( property ) )
        {
            binding = new ValueBindingImpl()
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
        else if ( IRelationship.PROP_TO_ENTITY.equals( property ) )
        {
            binding = new ValueBindingImpl()
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
            binding.init( element(), property, params );
        }

        return binding;
    }

    public RelationshipObject getRelationshipObject()
    {
        return this.relationshipObject;
    }

    private void persistRelationship()
    {
        final IServiceBuilder serviceBuilder = parent().element().nearest( IServiceBuilder.class );

        final String fromName = this.relationshipObject.getFromName();
        final String toName = this.relationshipObject.getToName();

        final IEntity fromEntity = EntityRelationshipService.findEntity( fromName, serviceBuilder );
        final IEntity toEntity = EntityRelationshipService.findEntity( toName, serviceBuilder );

        if( fromEntity != null && toEntity != null )
        {
            IColumn primaryKeyColumn = null;

            for( IColumn column : toEntity.getColumns() )
            {
                if( column.isPrimary().getContent() )
                {
                    primaryKeyColumn = column;
                    break;
                }
            }

            if( primaryKeyColumn != null )
            {
                final IColumn column = fromEntity.getColumns().insert();
                column.setName( primaryKeyColumn.getName().getContent() );
                column.setType( "long" );
            }
        }
    }

}
