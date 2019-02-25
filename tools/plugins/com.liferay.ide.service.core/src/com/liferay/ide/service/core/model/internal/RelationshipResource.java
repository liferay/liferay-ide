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

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.service.core.model.Column;
import com.liferay.ide.service.core.model.Entity;
import com.liferay.ide.service.core.model.Relationship;
import com.liferay.ide.service.core.model.ServiceBuilder;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyBinding;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.ValuePropertyBinding;

/**
 * @author Gregory Amerson
 */
public class RelationshipResource extends Resource implements SapphireContentAccessor {

	public RelationshipResource(RelationshipObject obj, Resource parent) {
		super(parent);

		_relationshipObject = obj;
	}

	public RelationshipObject getRelationshipObject() {
		return _relationshipObject;
	}

	@Override
	protected PropertyBinding createBinding(Property property) {
		PropertyBinding binding = null;

		if (Relationship.PROP_FROM_ENTITY.equals(property.definition())) {
			binding = new ValuePropertyBinding() {

				@Override
				public String read() {
					return RelationshipResource.this._relationshipObject.getFromName();
				}

				@Override
				public void write(String value) {
					RelationshipResource.this._relationshipObject.setFromName(value);
					_persistRelationship();
				}

			};
		}
		else if (Relationship.PROP_TO_ENTITY.equals(property.definition())) {
			binding = new ValuePropertyBinding() {

				@Override
				public String read() {
					return RelationshipResource.this._relationshipObject.getToName();
				}

				@Override
				public void write(String value) {
					RelationshipResource.this._relationshipObject.setToName(value);
					_persistRelationship();
				}

			};
		}

		if (binding != null) {
			binding.init(property);
		}

		return binding;
	}

	private void _persistRelationship() {
		Element element = parent().element();

		ServiceBuilder serviceBuilder = element.nearest(ServiceBuilder.class);

		String fromName = _relationshipObject.getFromName();
		String toName = _relationshipObject.getToName();

		EntityRelationshipService entityRelationshipService = new EntityRelationshipService();

		Entity fromEntity = entityRelationshipService.findEntity(fromName, serviceBuilder);
		Entity toEntity = entityRelationshipService.findEntity(toName, serviceBuilder);

		if ((fromEntity != null) && (toEntity != null)) {
			Column primaryKeyColumn = null;

			for (Column column : toEntity.getColumns()) {
				if (get(column.isPrimary())) {
					primaryKeyColumn = column;

					break;
				}
			}

			if (primaryKeyColumn != null) {
				ElementList<Column> columns = fromEntity.getColumns();

				Column column = columns.insert();

				column.setName(get(primaryKeyColumn.getName()));
				column.setType("long");
			}
		}
	}

	private RelationshipObject _relationshipObject;

}