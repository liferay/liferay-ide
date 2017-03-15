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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.model.WorkflowNodeMetadata;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Resource;
import org.eclipse.sapphire.modeling.LayeredElementBindingImpl;
import org.eclipse.sapphire.modeling.xml.XmlElement;
import org.eclipse.sapphire.modeling.xml.XmlResource;

/**
 * @author Gregory Amerson
 */
public class WorkflowMetadataBindingImpl extends LayeredElementBindingImpl
{

    private WorkflowNodeMetadataObject underlyingObject;

    @Override
    protected Object readUnderlyingObject()
    {
        WorkflowNodeMetadataObject metadataObject = null;

        final XmlResource xmlResource = property().element().resource().adapt( XmlResource.class );

        final XmlElement metadataElement = xmlResource.getXmlElement().getChildElement( "metadata", false );

        if( metadataElement != null )
        {
            String metadata = metadataElement.getChildNodeText( "" );

            if( !CoreUtil.empty( metadata ) )
            {
                metadataObject = new WorkflowNodeMetadataObject( metadata.trim() );
            }
        }

        if( metadataObject == null )
        {
            metadataObject = new WorkflowNodeMetadataObject();
        }

        if( !metadataObject.equals( this.underlyingObject ) )
        {
            this.underlyingObject = metadataObject;
        }

        return this.underlyingObject;
    }

    @Override
    protected Object createUnderlyingObject( ElementType type )
    {
        Object retval = null;

        if( WorkflowNodeMetadata.TYPE.equals( type ) )
        {
            retval = new WorkflowNodeMetadataObject();
        }

        return retval;
    }

    @Override
    protected Resource createResource( Object obj )
    {
        XmlResource xmlResource = property().element().resource().adapt( XmlResource.class );

        return new WorkflowNodeMetadataResource( (WorkflowNodeMetadataObject) obj, xmlResource );
    }

    @Override
    public ElementType type( Resource resource )
    {
        return WorkflowNodeMetadata.TYPE;
    }

}
