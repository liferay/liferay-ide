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

import com.liferay.ide.eclipse.service.core.model.IColumn;
import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.LayeredListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementListener;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Resource;


public class RelationshipsBinding2 extends LayeredListBindingImpl
{

	private List<RelationshipObject> newRelationships = new ArrayList<RelationshipObject>();

	@Override
	protected List<?> readUnderlyingList()
	{
		final List<RelationshipObject> relationships = new ArrayList<RelationshipObject>();

		final IEntity thisEntity = entity();
		List<IColumn> primaryColumns = new ArrayList<IColumn>();

		for( IEntity entity : serviceBuilder().getEntities() )
		{
			if (!(entity.equals(thisEntity))) {
				for (IColumn column : entity.getColumns()) {
					if (column.isPrimary().getContent()) {
						primaryColumns.add(column);
					}
				}
			}
		}

		for( final IColumn column : columns() )
		{
			if( !( column.isPrimary().getContent() ) )
			{
				for( final IColumn primaryColumn : primaryColumns )
				{
					if( primaryColumn.getName().getContent().equals( column.getName().getContent() ) &&
						primaryColumn.getType().getContent().equals( column.getType().getContent() ) )
					{
						// IRelationship rel = IRelationship.TYPE.instantiate();
						RelationshipObject rel = new RelationshipObject();

						rel.setName( ( (IEntity) primaryColumn.parent().parent() ).getName().getContent() );
						rel.setForeignKeyColumnName( column.getName().getContent() );

						column.addListener( new ModelElementListener()
						{

							@Override
							public void propertyChanged( ModelPropertyChangeEvent event )
							{
								if( "Name".equals( event.getProperty().getName() ) ||
									"Type".equals( event.getProperty().getName() ) )
								{
									if( !( primaryColumn.getName().getContent().equals( column.getName().getContent() ) ) ||
										!( primaryColumn.getType().getContent().equals( column.getType().getContent() ) ) )
									{
										// localResources.remove( resource );
									}

									thisEntity.refresh();
								}
							}
						} );

						relationships.add( rel );
					}
				}
			}
		}

		for( RelationshipObject rel : newRelationships )
		{
			relationships.add( rel );
		}

		return relationships;
	}

	@Override
	public void init( IModelElement element, ModelProperty property, String[] params )
	{
		super.init( element, property, params );

		element.addListener( new ModelPropertyListener()
		{
			@Override
			public void handlePropertyChangedEvent( ModelPropertyChangeEvent event )
			{
				IRelationship rel = (IRelationship) event.getModelElement();
				IEntity e = rel.getName().resolve();
				if( e != null )
				{
					IEntity entity = RelationshipsBinding2.this.entity();
					for( IColumn col : e.getColumns() )
					{
						if( col.isPrimary().getContent() )
						{
							boolean hasForeignKey = false;
							for( IColumn col2 : entity.getColumns() )
							{
								if( ( !col2.isPrimary().getContent() ) &&
									col2.getName().getContent().equals( col.getName().getContent() ) )
								{
									hasForeignKey = true;
								}
							}
							if( hasForeignKey )
							{
								entity.getRelationships().remove( rel );
							}
							else
							{
								IColumn foreignColumn = entity.getColumns().addNewElement();
								foreignColumn.setName( col.getName().getContent() );
								foreignColumn.setType( col.getType().getContent() );
							}
						}
					}

					RelationshipObject remove = null;

					for( RelationshipObject newRelationship : newRelationships )
					{
						if( newRelationship.equals( rel.resource().adapt( RelationshipResource2.class ).getBase() ) )
						{
							remove = newRelationship;
						}
					}

					if( remove != null )
					{
						newRelationships.remove( remove );
					}
				}
			}
		}, "Relationships" );
	}

	protected ModelElementList<IColumn> columns()
	{
		return entity().getColumns();
	}

	private IEntity entity()
	{
		return this.element().nearest( IEntity.class );
	}

	private IServiceBuilder serviceBuilder()
	{
		return this.element().nearest( IServiceBuilder.class );
	}

	@Override
	protected Resource createResource( Object obj )
	{
		Resource retval = null;

		if( obj instanceof RelationshipObject )
		{
			retval = new RelationshipResource2( entity().resource(), (RelationshipObject) obj );
		}

		return retval;
	}

	@Override
	public ModelElementType type( Resource resource )
	{
		ModelElementType retval = null;

		if( resource instanceof RelationshipResource2 )
		{
			retval = IRelationship.TYPE;
		}

		return retval;
	}

	@Override
	protected Object addUnderlyingObject( ModelElementType type )
	{
		RelationshipObject retval = null;

		if( IRelationship.TYPE.equals( type ) )
		{
			retval = new RelationshipObject();
			newRelationships.add( retval );
		}

		return retval;
	}

	@Override
	public void remove( Resource resource )
	{
		if( resource instanceof RelationshipResource2 )
		{
			RelationshipResource2 relResource = (RelationshipResource2) resource;

			IModelElement parentElement = resource.parent().element();

			if( parentElement instanceof IEntity )
			{
				IEntity referringEntity = (IEntity) parentElement;
				String fkColumnName = relResource.getBase().getForeignKeyColumnName();

				IColumn remove = null;

				for( IColumn col : referringEntity.getColumns() )
				{
					if( col.getName().getContent().equals( fkColumnName ) )
					{
						remove = col;
						break;
					}
				}

				if( remove != null )
				{
					referringEntity.getColumns().remove( remove );
				}
			}
		}
	}
}
