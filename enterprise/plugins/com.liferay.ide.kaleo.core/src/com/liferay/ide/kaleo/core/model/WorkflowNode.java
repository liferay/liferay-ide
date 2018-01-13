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

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.WorkflowMetadataBindingImpl;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlElementBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/node_16x16.gif")
public interface WorkflowNode extends Node {

	public ElementType TYPE = new ElementType(WorkflowNode.class);

	public ElementHandle<WorkflowNodeMetadata> getMetadata();

	@CustomXmlElementBinding(impl = WorkflowMetadataBindingImpl.class)
	@Type(base = WorkflowNodeMetadata.class)
	public ElementProperty PROP_METADATA = new ElementProperty(TYPE, "Metadata");

}