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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image( path = "images/action_16x16.gif" )
public interface Action extends Executable, MustScript, Node
{

    ElementType TYPE = new ElementType( Action.class );

    // *** Priority ***

    @Type( base = Integer.class )
    @Label( standard = "&priority" )
    @XmlBinding( path = "priority" )
    ValueProperty PROP_PRIORITY = new ValueProperty( TYPE, "Priority" );

    Value<Integer> getPriority();
    void setPriority( String val );
    void setPriority( Integer val );
}
