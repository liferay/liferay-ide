/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 */

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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.LayeredListPropertyBinding;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.Resource;

/**
 * @author Gregory Amerson
 */
public class RelationshipsBindingImpl extends LayeredListPropertyBinding {

	@Override
	public void init(Property property) {
		super.init(property);

		_serviceBuilder().attach(
			new FilteredListener<PropertyEvent>() {

				@Override
				public void handleTypedEvent(PropertyEvent event) {
					if (event != null) {
						_refreshRelationships();
					}
				}

			},
			"Entities/*");

		_refreshRelationships();
	}

	@Override
	public void remove(Resource resource) {
		RelationshipObject relObject = resource.adapt(RelationshipResource.class).getRelationshipObject();

		_relationships.remove(relObject);
		_removeRelationship(relObject);
	}

	@Override
	public ElementType type(Resource resource) {
		return Relationship.TYPE;
	}

	@Override
	protected Object insertUnderlyingObject(ElementType type, int position) {
		RelationshipObject newRelationship = new RelationshipObject();

		if (position > _relationships.size()) {
			_relationships.add(newRelationship);
		}
		else {
			_relationships.add(position, newRelationship);
		}

		return newRelationship;
	}

	@Override
	protected List<?> readUnderlyingList() {
		return _relationships;
	}

	@Override
	protected Resource resource(Object obj) {
		Element element = property().element();

		return new RelationshipResource((RelationshipObject)obj, element.resource());
	}

	private Column _findPrimaryKey(Entity entity) {
		if (entity != null) {
			for (Column column : entity.getColumns()) {
				if (column.isPrimary().content()) {
					return column;
				}
			}
		}

		return null;
	}

	private void _refreshRelationships() {
		_relationships.clear();

		Map<String, String> primaryKeys = new HashMap<>();

		for (Entity entity : _serviceBuilder().getEntities()) {
			Column primaryKeyColumn = _findPrimaryKey(entity);

			if ((primaryKeyColumn != null) && !empty(primaryKeyColumn.getName().content())) {
				primaryKeys.put(primaryKeyColumn.getName().content(), entity.getName().content());
			}
		}

		for (Entity entity : _serviceBuilder().getEntities()) {
			for (Column column : entity.getColumns()) {
				if (!column.isPrimary().content()) {
					String columnName = column.getName().content();

					String entityName = primaryKeys.get(columnName);

					if (entityName != null) {
						_relationships.add(new RelationshipObject(entity.getName().content(), entityName));
					}
				}
			}
		}
	}

	private void _removeRelationship(RelationshipObject relObject) {
		String fromName = relObject.getFromName();
		String toName = relObject.getToName();

		Entity fromEntity = EntityRelationshipService.findEntity(fromName, _serviceBuilder());
		Entity toEntity = EntityRelationshipService.findEntity(toName, _serviceBuilder());

		Column primaryKeyColumn = _findPrimaryKey(toEntity);

		if (primaryKeyColumn != null) {
			String primaryKeyName = primaryKeyColumn.getName().content();

			if (!empty(primaryKeyName)) {
				Column columnToRemove = null;

				for (Column column : fromEntity.getColumns()) {
					if (primaryKeyName.equals(column.getName().content())) {
						columnToRemove = column;

						break;
					}
				}

				if (columnToRemove != null) {
					fromEntity.getColumns().remove(columnToRemove);
				}
			}
		}
	}

	private ServiceBuilder _serviceBuilder() {
		return property().nearest(ServiceBuilder.class);
	}

	private List<RelationshipObject> _relationships = new ArrayList<>();

}