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

import com.liferay.ide.eclipse.service.core.ServiceCore;
import com.liferay.ide.eclipse.service.core.model.IColumn;
import com.liferay.ide.eclipse.service.core.model.IEntity;
import com.liferay.ide.eclipse.service.core.model.IRelationship;
import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelElementListener;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelPath;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Resource;
import org.eclipse.sapphire.modeling.internal.MemoryResource;


public class RelationshipsBinding extends ListBindingImpl {

	protected List<Resource> localResources = new ArrayList<Resource>();
	private RelationshipListener listener;

	@Override
	public List<Resource> read() {
		final List<Resource> resources = new ArrayList<Resource>();

		final IEntity thisEntity = getEntity();
		IServiceBuilder root = (IServiceBuilder) this.element().root();

		List<IColumn> primaryColumns = new ArrayList<IColumn>();

		for (IEntity entity : root.getEntities()) {
			if (!(entity.equals(thisEntity))) {
				for (IColumn column : entity.getColumns()) {
					if (column.isPrimary().getContent()) {
						primaryColumns.add(column);
					}
				}
			}
		}

		for (final IColumn column : getColumns()) {
			if (!(column.isPrimary().getContent())) {
				for (final IColumn primaryColumn : primaryColumns) {
					try {
						if (primaryColumn.getName().getContent().equals(column.getName().getContent()) &&
							primaryColumn.getType().getContent().equals(column.getType().getContent())) {

							IRelationship rel = IRelationship.TYPE.instantiate();
							rel.setName(((IEntity) primaryColumn.parent().parent()).getName().getContent());
							rel.setForeignKeyColumnName( column.getName().getContent() );

							final Resource resource = new RelationshipResource( thisEntity.resource(), rel );
							resources.add(resource);

							column.addListener( new ModelElementListener() {

								@Override
								public void propertyChanged( ModelPropertyChangeEvent event ) {
									if ( "Name".equals( event.getProperty().getName() ) ||
										"Type".equals( event.getProperty().getName() ) ) {
										if ( !( primaryColumn.getName().getContent().equals( column.getName().getContent() ) ) ||
											!( primaryColumn.getType().getContent().equals( column.getType().getContent() ) ) ) {
											localResources.remove( resource );
										}

										thisEntity.refresh();
									}
								}
							} );
						}
					}
					catch (Throwable e) {
						ServiceCore.logError( e );
					}
				}
			}
		}

		resources.addAll(localResources);

		return resources;
	}

	protected ModelElementList<IColumn> getColumns() {
		return getEntity().getColumns();
	}

	protected IEntity getEntity() {
		return (IEntity) this.element();
	}

	@Override
	public ModelElementType type(Resource resource) {
		if (resource instanceof RelationshipResource) {
			return IRelationship.TYPE;
		}

		return null;
	}

	@Override
	public Resource add(ModelElementType type) {
		IRelationship rel = IRelationship.TYPE.instantiate();

		RelationshipResource resource = new RelationshipResource(getEntity().resource(), rel);
		localResources.add(resource);
		// IModelElement e = resource.binding(IRelationship.PROP_NAME).element();
		// resource.element().addListener(listener);

		return resource;
	}

	@Override
	public void remove(Resource resource) {
		if ( resource instanceof RelationshipResource ) {
			RelationshipResource relResource = (RelationshipResource) resource;

			IModelElement parentElement = resource.parent().element();

			if ( parentElement instanceof IEntity ) {
				IEntity referringEntity = (IEntity) parentElement;
				String fkColumnName = relResource.getBase().getForeignKeyColumnName().getContent();

				IColumn remove = null;

				for (IColumn col : referringEntity.getColumns()) {
					if ( col.getName().getContent().equals( fkColumnName ) ) {
						remove = col;
						break;
					}
				}

				if ( remove != null ) {
					referringEntity.getColumns().remove( remove );
				}
			}
		}

		if ( localResources.contains( resource ) ) {
			localResources.remove(resource);
			resource.element().removeListener(listener);
		}

		getEntity().refresh();
	}

	@Override
	public void init(IModelElement element, ModelProperty property, String[] params) {
		super.init(element, property, params);

		listener = new RelationshipListener();

		localResources.clear();

		MemoryResource memory = new MemoryResource(IRelationship.TYPE);
		memory.init(element);

		element.addListener(new ModelPropertyListener() {

			@Override
			public void handlePropertyChangedEvent(ModelPropertyChangeEvent event) {
				handleRelationshipsChangedEvent(event);
			}
		}, new ModelPath("Relationships"));

		// element.addListener( new ModelElementListener() {
		//
		// @Override
		// public void handleElementDisposedEvent( ModelElementDisposedEvent event ) {
		// }
		// } );
		
	}

	protected void handleRelationshipsChangedEvent(ModelPropertyChangeEvent event) {
		// loop through relationships and add necessary columns if they don't exist
		IEntity entity = (IEntity) event.getModelElement();
		for (IRelationship rel : entity.getRelationships()) {
			rel.addListener(listener);
		}
	}

	protected void handleNameChangedEvent(ModelPropertyChangeEvent event) {
		// loop through relationships and add necessary columns if they don't exist
		System.out.println( event );
	}

	public class RelationshipListener extends ModelElementListener {

		@Override
		public void propertyChanged(ModelPropertyChangeEvent event) {
			IRelationship rel = (IRelationship) event.getModelElement();
			IEntity e = rel.getName().resolve();
			if (e != null) {
				IEntity entity = RelationshipsBinding.this.getEntity();
				for (IColumn col : e.getColumns()) {
					if (col.isPrimary().getContent()) {
						boolean hasForeignKey = false;
						for (IColumn col2 : entity.getColumns()) {
							if ((!col2.isPrimary().getContent()) &&
								col2.getName().getContent().equals(col.getName().getContent())) {
								hasForeignKey = true;
							}
						}
						if (hasForeignKey) {
							entity.getRelationships().remove(rel);
						}
						else {
							IColumn foreignColumn = entity.getColumns().addNewElement();
							foreignColumn.setName(col.getName().getContent());
							foreignColumn.setType(col.getType().getContent());
						}
					}
				}
			}
		}
	}
}
