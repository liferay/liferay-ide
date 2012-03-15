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

import static com.liferay.ide.eclipse.core.util.CoreUtil.empty;

import com.liferay.ide.eclipse.service.core.model.IColumn;
import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.modeling.BindingImpl;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.ValueBindingImpl;


public class RelationshipResource2 extends Resource {

	protected RelationshipObject base;

	public RelationshipResource2( final Resource parent, final RelationshipObject base )
	{
		super(parent);
		this.base = base;
	}

	public RelationshipResource2(final Resource parent) {
		super(parent);
	}

	@Override
	protected BindingImpl createBinding(ModelProperty property) {
		if (property == IRelationship.PROP_NAME) {
			ValueBindingImpl binding = new ValueBindingImpl() {

				@Override
				public String read() {
					return getBase().getName();
				}

				@Override
				public void write(final String value) {
					getBase().setName(value);
					
					if( !empty( value ) && empty( getBase().getForeignKeyColumnName() ) )
					{
						for( IEntity entity : parent().element().nearest( IServiceBuilder.class ).getEntities() )
						{
							if( value.equals( entity.getName().getContent( false ) ) )
							{
								for( IColumn column : entity.getColumns() )
								{
									if( column.isPrimary().getContent() )
									{
										getBase().setForeignKeyColumnName( column.getName().getContent() );
										break;
									}
								}
							}
						}
					}
				}
			};

			binding.init(element(), IRelationship.PROP_NAME, null);

			return binding;
		}
		else if ( property == IRelationship.PROP_FOREIGN_KEY_COLUMN_NAME ) {
			ValueBindingImpl binding = new ValueBindingImpl() {

				@Override
				public String read() {
					return getBase().getForeignKeyColumnName();
				}

				@Override
				public void write( final String value ) {
					getBase().setForeignKeyColumnName( value );
				}
			};

			binding.init( element(), IRelationship.PROP_NAME, null );

			return binding;
		}
		else if ( property == IRelationship.PROP_LABEL ) {
			ValueBindingImpl binding = new ValueBindingImpl() {

				@Override
				public String read() {
					return getBase().getForeignKeyColumnName();
				}

				@Override
				public void write( final String value ) {
					// read only
				}
			};

			binding.init( element(), IRelationship.PROP_LABEL, null );

			return binding;
		}

		return null;
	}

	public void setBase( RelationshipObject b )
	{
		this.base = b;
	}

	public RelationshipObject getBase()
	{
		return this.base;
	}

}
