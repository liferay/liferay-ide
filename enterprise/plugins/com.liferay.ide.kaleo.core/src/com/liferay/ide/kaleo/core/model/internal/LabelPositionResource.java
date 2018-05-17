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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.Position;

import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyBinding;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.ValuePropertyBinding;

/**
 * @author Gregory Amerson
 */
public class LabelPositionResource extends Resource {

	public LabelPositionResource(Point point, Resource parent) {
		super(parent);

		_point = point;
	}

	public Point getPoint() {
		return _point;
	}

	@Override
	protected PropertyBinding createBinding(Property property) {
		PropertyBinding binding = null;

		PropertyDef def = property.definition();

		if (Position.PROP_X.equals(def) || Position.PROP_Y.equals(def)) {
			binding = new ValuePropertyBinding() {

				@Override
				public String read() {
					String retval = null;

					if (LabelPositionResource.this._point != null) {
						if (Position.PROP_X.equals(def)) {
							retval = String.valueOf(LabelPositionResource.this._point.getX());
						}
						else if (Position.PROP_Y.equals(def)) {
							retval = String.valueOf(LabelPositionResource.this._point.getY());
						}
					}

					return retval;
				}

				@Override
				public void write(String value) {
					if (LabelPositionResource.this._point == null) {
						LabelPositionResource.this._point = new Point();
					}

					if (Position.PROP_X.equals(def)) {
						LabelPositionResource.this._point.setX(Integer.parseInt(value));
					}
					else if (Position.PROP_Y.equals(def)) {
						LabelPositionResource.this._point.setY(Integer.parseInt(value));
					}

					/*
					 * if (Position.PROP_X.equals( def ) ||
					 * Position.PROP_Y.equals( def )) {
					 * WorkflowNodeMetadataObject parentMetadata =
					 * parent().adapt( WorkflowNodeMetadataResource.class
					 * ).getMetadata(); parentMetadata.setNodeLocation(
					 * LabelPositionResource.this.point ); }
					 */
					WorkflowNodeMetadataResource metadataResource = parent().adapt(WorkflowNodeMetadataResource.class);

					metadataResource.saveMetadata();
				}

			};
		}

		if (binding != null) {
			binding.init(property);
		}

		return binding;
	}

	private Point _point;

}