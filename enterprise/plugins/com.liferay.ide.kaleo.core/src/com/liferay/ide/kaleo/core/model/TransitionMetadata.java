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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;

/**
 * abstract-workflow-node-complex-type
 *
 * @author Gregory Amerson
 */
public interface TransitionMetadata extends Element
{
    ElementType TYPE = new ElementType( TransitionMetadata.class );

    ValueProperty PROP_NAME = new ValueProperty( TYPE, "Name" );

    Value<String> getName();
    void setName( String value );

    @Type( base = Position.class )
    ImpliedElementProperty PROP_LABEL_LOCATION = new ImpliedElementProperty( TYPE, "LabelLocation" );

    Position getLabelLocation();

    @Type( base = ConnectionBendpoint.class )
    ListProperty PROP_BENDPOINTS = new ListProperty( TYPE, "Bendpoints" );

    ElementList<ConnectionBendpoint> getBendpoints();
}
