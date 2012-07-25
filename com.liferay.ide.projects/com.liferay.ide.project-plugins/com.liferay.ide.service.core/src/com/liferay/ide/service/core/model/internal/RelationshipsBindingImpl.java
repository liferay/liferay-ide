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

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.service.core.model.Column;
import com.liferay.ide.service.core.model.Entity;
import com.liferay.ide.service.core.model.Relationship;
import com.liferay.ide.service.core.model.ServiceBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.LayeredListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.PropertyEvent;
import org.eclipse.sapphire.modeling.Resource;

/**
 * @author Gregory Amerson
 */
public class RelationshipsBindingImpl extends LayeredListBindingImpl
{

    private List<RelationshipObject> relationships = new ArrayList<RelationshipObject>();

    private Column findPrimaryKey( final Entity entity )
    {
        if( entity != null )
        {
            for( Column column : entity.getColumns() )
            {
                if( column.isPrimary().getContent() )
                {
                    return column;
                }
            }
        }

        return null;
    }

    @Override
    public void init( IModelElement element, ModelProperty property, String[] params )
    {
        super.init( element, property, params );

        serviceBuilder().attach( new FilteredListener<PropertyEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyEvent event )
            {
                if( event != null )
                {
                    refreshRelationships();
                }
            }
        },

        "Entities/*" );

        refreshRelationships();
    }

    @Override
    protected Object insertUnderlyingObject( ModelElementType type, int position )
    {
        RelationshipObject newRelationship = new RelationshipObject();

        if( position > this.relationships.size() )
        {
            this.relationships.add( newRelationship );
        }
        else
        {
            this.relationships.add( position, newRelationship );
        }
        return newRelationship;
    }

    @Override
    protected List<?> readUnderlyingList()
    {
        return this.relationships;
    }

    private void refreshRelationships()
    {
        this.relationships.clear();

        Map<String, String> primaryKeys = new HashMap<String, String>();

        for( Entity entity : serviceBuilder().getEntities() )
        {
            Column primaryKeyColumn = findPrimaryKey( entity );

            if( primaryKeyColumn != null && !empty( primaryKeyColumn.getName().getContent() ) )
            {
                primaryKeys.put( primaryKeyColumn.getName().getContent(), entity.getName().getContent() );
            }
        }

        for( Entity entity : serviceBuilder().getEntities() )
        {
            for( Column column : entity.getColumns() )
            {
                if( !column.isPrimary().getContent() )
                {
                    final String columnName = column.getName().getContent();

                    final String entityName = primaryKeys.get( columnName );

                    if( entityName != null )
                    {
                        this.relationships.add( new RelationshipObject( entity.getName().getContent(), entityName ) );
                    }
                }
            }
        }
    }

    @Override
    public void remove( Resource resource )
    {
        RelationshipObject relObject = resource.adapt( RelationshipResource.class ).getRelationshipObject();
        this.relationships.remove( relObject );
        removeRelationship( relObject );
    }

    private void removeRelationship( RelationshipObject relObject )
    {
        String fromName = relObject.getFromName();
        String toName = relObject.getToName();

        Entity fromEntity = EntityRelationshipService.findEntity( fromName, serviceBuilder() );
        Entity toEntity = EntityRelationshipService.findEntity( toName, serviceBuilder() );

        Column primaryKeyColumn = findPrimaryKey( toEntity );

        if( primaryKeyColumn != null )
        {
            String primaryKeyName = primaryKeyColumn.getName().getContent();

            if( !empty( primaryKeyName ) )
            {
                Column columnToRemove = null;

                for( Column column : fromEntity.getColumns() )
                {
                    if( primaryKeyName.equals( column.getName().getContent() ) )
                    {
                        columnToRemove = column;
                        break;
                    }
                }

                if( columnToRemove != null )
                {
                    fromEntity.getColumns().remove( columnToRemove );
                }
            }
        }
    }

    @Override
    protected Resource resource( Object obj )
    {
        return new RelationshipResource( (RelationshipObject) obj, this.element().resource() );
    }

    private ServiceBuilder serviceBuilder()
    {
        return this.element().nearest( ServiceBuilder.class );
    }

    @Override
    public ModelElementType type( Resource resource )
    {
        return Relationship.TYPE;
    }

}
