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
import org.w3c.dom.Element;

/**
 * @author Gregory Amerson
 */
public class WorkflowNodeMetadataResource extends Resource
{
    private WorkflowNodeMetadataObject metadata;

    public WorkflowNodeMetadataResource( WorkflowNodeMetadataObject obj, Resource parent )
    {
        super( parent );
        this.metadata = obj;
    }

    public WorkflowNodeMetadataObject getMetadata()
    {
        return metadata;
    }

    @Override
    protected PropertyBinding createBinding( final Property property )
    {
        PropertyBinding binding = null;

        final PropertyDef def = property.definition();

        if( WorkflowNodeMetadata.PROP_TERMINAL.equals( def ) )
        {
            binding = new ValuePropertyBinding()
            {
                @Override
                public String read()
                {
                    return Boolean.toString( WorkflowNodeMetadataResource.this.metadata.isTerminal() );
                }

                @Override
                public void write( String value )
                {
                    WorkflowNodeMetadataResource.this.metadata.setTerminal( Boolean.parseBoolean( value ) );
                    saveMetadata();
                }
            };
        }
        else if( WorkflowNodeMetadata.PROP_POSITION.equals( def ) )
        {
            binding = new ElementPropertyBinding()
            {
                @Override
                public Resource read()
                {
                    return new PositionResource(
                        WorkflowNodeMetadataResource.this.metadata.getNodeLocation(), WorkflowNodeMetadataResource.this );
                }

                @Override
                public ElementType type( Resource resource )
                {
                    return Position.TYPE;
                }
            };
        }
        else if(WorkflowNodeMetadata.PROP_TRANSITIONS_METADATA.equals( def ) )
        {
            binding = new LayeredListPropertyBinding()
            {
                @Override
                public ElementType type( Resource resource )
                {
                    return TransitionMetadata.TYPE;
                }

                @Override
                protected List<?> readUnderlyingList()
                {
                    return WorkflowNodeMetadataResource.this.metadata.getTransitionsMetadata();
                }

                @Override
                protected Object insertUnderlyingObject( ElementType type, int position )
                {
                    TransitionMetadataObject newTransitionMeta = new TransitionMetadataObject();

                    WorkflowNodeMetadataResource.this.metadata.getTransitionsMetadata().add(
                        position, newTransitionMeta );

                    saveMetadata();

                    return newTransitionMeta;
                }

                @Override
                public void remove( Resource resource )
                {
                    TransitionMetadataResource transitionMetaResource = (TransitionMetadataResource) resource;
                    WorkflowNodeMetadataResource.this.metadata.getTransitionsMetadata().remove( transitionMetaResource.getMetadata() );
                    saveMetadata();
                }

                @Override
                protected Resource resource( Object obj )
                {
                    return new TransitionMetadataResource( (TransitionMetadataObject) obj, WorkflowNodeMetadataResource.this );
                }
            };
        }

        if( binding != null )
        {
            binding.init( property );
        }

        return binding;
    }


    public void saveMetadata()
    {
        XmlElement metadataElement =
                        parent().adapt( XmlResource.class ).getXmlElement().getChildElement( "metadata", true );

        Element domElement = metadataElement.getDomNode();

        try
        {
            CDATASection cdata = domElement.getOwnerDocument().createCDATASection( this.metadata.toJSONString());

            CoreUtil.removeChildren( domElement );
            domElement.insertBefore( cdata, null );
        }
        catch( JSONException e )
        {
            KaleoCore.logError( e );
        }
    }

}
