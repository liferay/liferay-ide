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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface CanTransition extends WorkflowNode
{

    ElementType TYPE = new ElementType( CanTransition.class );

    // *** Transitions ***

    @Type( base = Transition.class )
    @Label( standard = "transitions" )
    @XmlListBinding
    (
        path = "transitions",
        mappings = @XmlListBinding.Mapping ( element = "transition", type = Transition.class )
    )
    ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, "Transitions" );

    ElementList<Transition> getTransitions();
}
