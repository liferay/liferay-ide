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

import com.liferay.ide.kaleo.core.model.internal.TransitionPossibleValuesService;
import com.liferay.ide.kaleo.core.model.internal.TransitionReferenceService;
import com.liferay.ide.kaleo.core.model.internal.TransitionTargetListener;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ReferenceValue;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Listeners;
import org.eclipse.sapphire.modeling.annotations.Reference;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image(path = "images/arrow_16x16.png")
//@Listeners( value = {TransitionTargetListener.class} )
public interface Transition extends Element
{

    ElementType TYPE = new ElementType( Transition.class );

    // *** Name ***

    @XmlBinding(path = "name")
    @Label(standard = "&name")
    @DefaultValue( text = "${Target}" )
    @Required
    ValueProperty PROP_NAME = new ValueProperty(TYPE, "Name");

    Value<String> getName();

    void setName(String value);

    // *** Target ***

    @Reference(target = Node.class)
    @Services
    (
        {
            @Service( impl = TransitionReferenceService.class ),
            @Service( impl = TransitionPossibleValuesService.class )
        }
    )
    @XmlBinding( path = "target" )
    @Label( standard = "&target" )
    @Required
    @Listeners( value = { TransitionTargetListener.class } )
    ValueProperty PROP_TARGET = new ValueProperty( TYPE, "Target" );

    ReferenceValue<String, Node> getTarget();

    void setTarget(String value);

    // *** Default ***

    @Type(base = Boolean.class)
    @Label(standard = "&default")
    @XmlBinding(path = "default")
    @DefaultValue( text = "false" )
    ValueProperty PROP_DEFAULT_TRANSITION = new ValueProperty( TYPE, "DefaultTransition" );

    Value<Boolean> isDefaultTransition();
    void setDefaultTransition( String value );
    void setDefaultTransition( Boolean value );

}
