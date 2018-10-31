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

import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.service.core.model.Column;
import com.liferay.ide.service.core.model.Entity;
import com.liferay.ide.service.core.model.Relationship;
import com.liferay.ide.service.core.model.ServiceBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
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
		RelationshipResource relationshipResource = resource.adapt(RelationshipResource.class);

		RelationshipObject relObject = relationshipResource.getRelationshipObject();

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
				if (SapphireUtil.getContent(column.isPrimary())) {
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

			if ((primaryKeyColumn != null) && !empty(SapphireUtil.getContent(primaryKeyColumn.getName()))) {
				primaryKeys.put(
					SapphireUtil.getContent(primaryKeyColumn.getName()), SapphireUtil.getContent(entity.getName()));
			}
		}

		for (Entity entity : _serviceBuilder().getEntities()) {
			for (Column column : entity.getColumns()) {
				if (SapphireUtil.getContent(column.isPrimary())) {
					continue;
				}

				String columnName = SapphireUtil.getContent(column.getName());

				String entityName = primaryKeys.get(columnName);

				if (entityName != null) {
					_relationships.add(new RelationshipObject(SapphireUtil.getContent(entity.getName()), entityName));
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

		if (primaryKeyColumn == null) {
			return;
		}

		String primaryKeyName = SapphireUtil.getContent(primaryKeyColumn.getName());

		if (empty(primaryKeyName)) {
			return;
		}

		Column columnToRemove = null;

		for (Column column : fromEntity.getColumns()) {
			if (primaryKeyName.equals(SapphireUtil.getContent(column.getName()))) {
				columnToRemove = column;

				break;
			}
		}

		if (columnToRemove != null) {
			ElementList<Column> columns = fromEntity.getColumns();

			columns.remove(columnToRemove);
		}
	}

	private ServiceBuilder _serviceBuilder() {
		return property().nearest(ServiceBuilder.class);
	}

	private List<RelationshipObject> _relationships = new ArrayList<>();

}