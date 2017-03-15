/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
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
 * abstract-workflow-node-complex-type
 *
 * @author Gregory Amerson
 */
@Image( path = "images/node_16x16.gif" )
public interface WorkflowNode extends Node
{

    ElementType TYPE = new ElementType( WorkflowNode.class );

    @Type( base = WorkflowNodeMetadata.class )
    @CustomXmlElementBinding( impl = WorkflowMetadataBindingImpl.class )
    ElementProperty PROP_METADATA = new ElementProperty( TYPE, "Metadata" );

    ElementHandle<WorkflowNodeMetadata> getMetadata();
}
