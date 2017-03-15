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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * abstract-timer-complex-type
 *
 * @author Gregory Amerson
 */
public interface Timer extends WorkflowNode
{

    ElementType TYPE = new ElementType( Timer.class );

    // *** delay ***

    @Type( base = TimeDelay.class )
    @Label( standard = "delay" )
    @Required
    // @XmlElementBinding( mappings = @XmlElementBinding.Mapping( element = "delay", type = ITimeDelay.class ) )
    @XmlBinding( path = "delay" )
    ElementProperty PROP_DELAY = new ElementProperty( TYPE, "Delay" );

    ElementHandle<TimeDelay> getDelay();

    // *** recurrance ***

    @Type( base = TimeDelay.class )
    @Label( standard = "recurrence" )
    // @XmlElementBinding( mappings = @XmlElementBinding.Mapping( element = "recurrence", type = ITimeDelay.class ) )
    @XmlBinding( path = "recurrence" )
    ElementProperty PROP_RECURRENCE = new ElementProperty( TYPE, "Recurrence" );

    ElementHandle<TimeDelay> getRecurrence();

}
