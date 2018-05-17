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

import com.liferay.ide.kaleo.core.model.ConnectionBendpoint;
import com.liferay.ide.kaleo.core.model.Position;
import com.liferay.ide.kaleo.core.model.TransitionMetadata;

import java.util.List;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.LayeredListPropertyBinding;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyBinding;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.ValuePropertyBinding;
import org.eclipse.sapphire.modeling.LayeredElementBindingImpl;

/**
 * @author Gregory Amerson
 */
public class TransitionMetadataResource extends Resource {

	public TransitionMetadataResource(TransitionMetadataObject metadata, Resource parent) {
		super(parent);

		_metadata = metadata;
	}

	public TransitionMetadataObject getMetadata() {
		return _metadata;
	}

	@Override
	protected PropertyBinding createBinding(Property property) {
		PropertyBinding binding = null;
		PropertyDef def = property.definition();

		if (TransitionMetadata.PROP_NAME.equals(def)) {
			binding = new ValuePropertyBinding() {

				@Override
				public String read() {
					return TransitionMetadataResource.this._metadata.getName();
				}

				@Override
				public void write(String value) {
					TransitionMetadataResource.this._metadata.setName(value);

					WorkflowNodeMetadataResource wfNodeMetadataResource =
						parent().adapt(WorkflowNodeMetadataResource.class);

					wfNodeMetadataResource.saveMetadata();
				}

			};
		}
		else if (TransitionMetadata.PROP_LABEL_LOCATION.equals(def)) {
			binding = new LayeredElementBindingImpl() {

				@Override
				public ElementType type(Resource resource) {
					return Position.TYPE;
				}

				@Override
				protected Resource createResource(Object obj) {
					return new LabelPositionResource((Point)obj, TransitionMetadataResource.this);
				}

				@Override
				protected Object readUnderlyingObject() {
					return TransitionMetadataResource.this._metadata.getLabelPosition();
				}

			};
		}
		else if (TransitionMetadata.PROP_BENDPOINTS.equals(def)) {
			binding = new LayeredListPropertyBinding() {

				@Override
				public void remove(Resource resource) {
					if (resource instanceof PositionResource) {
						List<Point> bendPoints = TransitionMetadataResource.this._metadata.getBendpoints();

						bendPoints.remove(((PositionResource)resource).getPoint());

						WorkflowNodeMetadataResource wfNodeMetadataResource =
							parent().adapt(WorkflowNodeMetadataResource.class);

						wfNodeMetadataResource.saveMetadata();
					}
				}

				@Override
				public ElementType type(Resource resource) {
					return ConnectionBendpoint.TYPE;
				}

				@Override
				protected Object insertUnderlyingObject(ElementType type, int position) {
					Point newBendpoint = new Point();

					List<Point> bendPoints = TransitionMetadataResource.this._metadata.getBendpoints();

					bendPoints.add(position, newBendpoint);

					WorkflowNodeMetadataResource wfNodeMetadataResource =
						parent().adapt(WorkflowNodeMetadataResource.class);

					wfNodeMetadataResource.saveMetadata();

					return newBendpoint;
				}

				@Override
				protected List<?> readUnderlyingList() {
					return TransitionMetadataResource.this._metadata.getBendpoints();
				}

				@Override
				protected Resource resource(Object obj) {
					return new PositionResource((Point)obj, TransitionMetadataResource.this);
				}

			};
		}

		if (binding != null) {
			binding.init(property);
		}

		return binding;
	}

	private TransitionMetadataObject _metadata;

}