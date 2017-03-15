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
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;

/**
 * @author Gregory Amerson
 */
public interface Position extends Element
{

    ElementType TYPE = new ElementType( Position.class );

    // *** X ***

    @Type( base = Integer.class )
    @DefaultValue( text = "0" )
    ValueProperty PROP_X = new ValueProperty( TYPE, "X" );

    Value<Integer> getX();
    void setX( Integer value );
    void setX( String value );

    // *** Y ***

    @Type( base = Integer.class )
    @DefaultValue( text = "0" )
    ValueProperty PROP_Y = new ValueProperty( TYPE, "Y" );

    Value<Integer> getY();
    void setY( Integer value );
    void setY( String value );

}
