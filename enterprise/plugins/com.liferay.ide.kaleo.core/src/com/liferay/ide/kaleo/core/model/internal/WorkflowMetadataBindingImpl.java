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
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.modeling.LayeredElementBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResource;

/**
 * @author Gregory Amerson
 */
public class WorkflowMetadataBindingImpl extends LayeredElementBindingImpl {

	@Override
	public ElementType type(Resource resource) {
		return WorkflowNodeMetadata.TYPE;
	}

	@Override
	protected Resource createResource(Object obj) {
		Element element = property().element();

		Resource resource = element.resource();

		XmlResource xmlResource = resource.adapt(XmlResource.class);

		return new WorkflowNodeMetadataResource((WorkflowNodeMetadataObject)obj, xmlResource);
	}

	@Override
	protected Object createUnderlyingObject(ElementType type) {
		Object retval = null;

		if (WorkflowNodeMetadata.TYPE.equals(type)) {
			retval = new WorkflowNodeMetadataObject();
		}

		return retval;
	}

	@Override
	protected Object readUnderlyingObject() {
		WorkflowNodeMetadataObject metadataObject = null;

		Element element = property().element();

		Resource resource = element.resource();

		XmlResource xmlResource = resource.adapt(XmlResource.class);

		XmlElement xmlElement = xmlResource.getXmlElement();

		XmlElement metadataElement = xmlElement.getChildElement("metadata", false);

		if (metadataElement != null) {
			String metadata = metadataElement.getChildNodeText("");

			if (!CoreUtil.empty(metadata)) {
				metadataObject = new WorkflowNodeMetadataObject(metadata.trim());
			}
		}

		if (metadataObject == null) {
			metadataObject = new WorkflowNodeMetadataObject();
		}

		if (!metadataObject.equals(_underlyingObject)) {
			_underlyingObject = metadataObject;
		}

		return _underlyingObject;
	}

	private WorkflowNodeMetadataObject _underlyingObject;

}