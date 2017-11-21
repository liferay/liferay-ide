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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Position;
import com.liferay.ide.kaleo.core.model.TransitionMetadata;
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;

import java.util.List;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.LayeredListPropertyBinding;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.PropertyBinding;
import org.eclipse.sapphire.PropertyDef;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.ValuePropertyBinding;
import org.eclipse.sapphire.modeling.ElementPropertyBinding;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResource;

import org.json.JSONException;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Gregory Amerson
 */
public class WorkflowNodeMetadataResource extends Resource {

	public WorkflowNodeMetadataResource(WorkflowNodeMetadataObject obj, Resource parent) {
		super(parent);

		_metadata = obj;
	}

	public WorkflowNodeMetadataObject getMetadata() {
		return _metadata;
	}

	public void saveMetadata() {
		XmlResource xmlResource = parent().adapt(XmlResource.class);

		XmlElement xmlElement = xmlResource.getXmlElement();

		XmlElement metadataElement = xmlElement.getChildElement("metadata", true);

		Element domElement = metadataElement.getDomNode();

		try {
			Document document = domElement.getOwnerDocument();

			CDATASection cdata = document.createCDATASection(this._metadata.toJSONString());

			CoreUtil.removeChildren(domElement);
			domElement.insertBefore(cdata, null);
		}
		catch (JSONException jsone) {
			KaleoCore.logError(jsone);
		}
	}

	@Override
	protected PropertyBinding createBinding(Property property) {
		PropertyBinding binding = null;

		PropertyDef def = property.definition();

		if (WorkflowNodeMetadata.PROP_TERMINAL.equals(def)) {
			binding = new ValuePropertyBinding() {

				@Override
				public String read() {
					return Boolean.toString(WorkflowNodeMetadataResource.this._metadata.isTerminal());
				}

				@Override
				public void write(String value) {
					WorkflowNodeMetadataResource.this._metadata.setTerminal(Boolean.parseBoolean(value));

					saveMetadata();
				}

			};
		}
		else if (WorkflowNodeMetadata.PROP_POSITION.equals(def)) {
			binding = new ElementPropertyBinding() {

				@Override
				public Resource read() {
					return new PositionResource(
						WorkflowNodeMetadataResource.this._metadata.getNodeLocation(),
						WorkflowNodeMetadataResource.this);
				}

				@Override
				public ElementType type(Resource resource) {
					return Position.TYPE;
				}

			};
		}
		else if (WorkflowNodeMetadata.PROP_TRANSITIONS_METADATA.equals(def)) {
			binding = new LayeredListPropertyBinding() {

				@Override
				public void remove(Resource resource) {
					TransitionMetadataResource transitionMetaResource = (TransitionMetadataResource)resource;

					List<TransitionMetadataObject> transitionMetadataObject =
						WorkflowNodeMetadataResource.this._metadata.getTransitionsMetadata();

					transitionMetadataObject.remove(transitionMetaResource.getMetadata());

					saveMetadata();
				}

				@Override
				public ElementType type(Resource resource) {
					return TransitionMetadata.TYPE;
				}

				@Override
				protected Object insertUnderlyingObject(ElementType type, int position) {
					TransitionMetadataObject newTransitionMeta = new TransitionMetadataObject();

					List<TransitionMetadataObject> transitionMetadataObject =
						WorkflowNodeMetadataResource.this._metadata.getTransitionsMetadata();

					transitionMetadataObject.add(position, newTransitionMeta);

					saveMetadata();

					return newTransitionMeta;
				}

				@Override
				protected List<?> readUnderlyingList() {
					return WorkflowNodeMetadataResource.this._metadata.getTransitionsMetadata();
				}

				@Override
				protected Resource resource(Object obj) {
					return new TransitionMetadataResource(
						(TransitionMetadataObject)obj, WorkflowNodeMetadataResource.this);
				}

			};
		}

		if (binding != null) {
			binding.init(property);
		}

		return binding;
	}

	private WorkflowNodeMetadataObject _metadata;

}