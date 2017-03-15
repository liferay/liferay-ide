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
public class TransitionMetadataResource extends Resource
{
    private TransitionMetadataObject metadata;

    public TransitionMetadataResource( TransitionMetadataObject metadata, Resource parent )
    {
        super( parent );
        this.metadata = metadata;
    }

    public TransitionMetadataObject getMetadata()
    {
        return this.metadata;
    }

    @Override
    protected PropertyBinding createBinding( final Property property )
    {
        PropertyBinding binding = null;
        final PropertyDef def = property.definition();

        if( TransitionMetadata.PROP_NAME.equals( def ) )
        {
            binding = new ValuePropertyBinding()
            {
                @Override
                public String read()
                {
                    return TransitionMetadataResource.this.metadata.getName();
                }

                @Override
                public void write( String value )
                {
                    TransitionMetadataResource.this.metadata.setName( value );
                    parent().adapt( WorkflowNodeMetadataResource.class ).saveMetadata();
                }

            };
        }
        else if( TransitionMetadata.PROP_LABEL_LOCATION.equals( def ) )
        {
            binding = new LayeredElementBindingImpl()
            {
                @Override
                public ElementType type( Resource resource )
                {
                    return Position.TYPE;
                }

                @Override
                protected Object readUnderlyingObject()
                {
                    return TransitionMetadataResource.this.metadata.getLabelPosition();
                }

                @Override
                protected Resource createResource( Object obj )
                {
                    return new LabelPositionResource( (Point) obj, TransitionMetadataResource.this );
                }
            };
        }
        else if(TransitionMetadata.PROP_BENDPOINTS.equals( def ) )
        {
            binding = new LayeredListPropertyBinding()
            {
                @Override
                public ElementType type( Resource resource )
                {
                    return ConnectionBendpoint.TYPE;
                }

                @Override
                protected List<?> readUnderlyingList()
                {
                    return TransitionMetadataResource.this.metadata.getBendpoints();
                }

                @Override
                protected Resource resource( Object obj )
                {
                    return new PositionResource( (Point) obj, TransitionMetadataResource.this );
                }

                @Override
                protected Object insertUnderlyingObject( ElementType type, int position)
                {
                    Point newBendpoint = new Point();

                    TransitionMetadataResource.this.metadata.getBendpoints().add(position, newBendpoint );

                    parent().adapt( WorkflowNodeMetadataResource.class ).saveMetadata();

                    return newBendpoint;
                }

                @Override
                public void remove( Resource resource )
                {
                    if( resource instanceof PositionResource )
                    {
                        TransitionMetadataResource.this.metadata.getBendpoints().remove(
                            ( (PositionResource) resource ).getPoint() );
                        parent().adapt( WorkflowNodeMetadataResource.class ).saveMetadata();
                    }
                }
            };
        }

        if( binding != null )
        {
            binding.init( property );
        }

        return binding;
    }

}
