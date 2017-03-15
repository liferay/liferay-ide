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
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public interface TimeDelay extends Element
{

    ElementType TYPE = new ElementType( TimeDelay.class );

    // *** duration ***

    @Type( base = Double.class )
    @Label( standard = "&duration" )
    @XmlBinding( path = "duration" )
    ValueProperty PROP_DURATION = new ValueProperty( TYPE, "Duration" );

    Value<Double> getDuration();

    void setDuration( String val );

    void setDuration( Double val );

    // *** scale ***

    @Type( base = TimeScaleType.class )
    @Label( standard = "scale" )
    @XmlBinding( path = "scale" )
    ValueProperty PROP_SCALE = new ValueProperty( TYPE, "Scale" );

    Value<TimeScaleType> getScale();

    void setScale( String scaleType );

    void setScale( TimeScaleType scaleType );
}
