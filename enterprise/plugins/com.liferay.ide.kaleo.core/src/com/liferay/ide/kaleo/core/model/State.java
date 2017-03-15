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

import com.liferay.ide.kaleo.core.model.internal.StateEndValueBinding;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image( path = "images/initial_16x16.png" )
public interface State extends ActionTimer, CanTransition
{
    ElementType TYPE = new ElementType( State.class );

    // *** Initial ***

    @Type( base = Boolean.class )
    @Label( standard = "&initial" )
    @XmlBinding( path = "initial" )
    @DefaultValue( text = "false" )
    ValueProperty PROP_INITIAL = new ValueProperty( TYPE, "Initial" );

    Value<Boolean> isInitial();
    void setInitial( String value );
    void setInitial( Boolean value );

    @Type( base = Boolean.class )
    @Label( standard = "&end" )
    @CustomXmlValueBinding( impl = StateEndValueBinding.class )
    ValueProperty PROP_END = new ValueProperty( TYPE, "End" );

    Value<Boolean> isEnd();
    void setEnd( String value );
    void setEnd( Boolean value );

}
