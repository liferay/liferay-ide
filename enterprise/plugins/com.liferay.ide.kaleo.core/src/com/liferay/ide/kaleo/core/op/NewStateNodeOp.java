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

package com.liferay.ide.kaleo.core.op;

import com.liferay.ide.kaleo.core.model.internal.TransitionPossibleValuesService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.PossibleValues;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Enablement;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface NewStateNodeOp extends NewNodeOp
{
    ElementType TYPE = new ElementType( NewStateNodeOp.class );

    @Type( base = NewStateNode.class )
    ImpliedElementProperty PROP_NEW_STATE_NODE = new ImpliedElementProperty( TYPE, "NewStateNode" );

    NewStateNode getNewStateNode();

    @Type( base = NewStateType.class )
    @Label( standard = "state &type" )
    @DefaultValue( text = "default" )
    ValueProperty PROP_TYPE = new ValueProperty( TYPE, "Type" );

    Value<NewStateType> getType();
    void setType( String type );
    void setType( NewStateType type );

    // *** WorkflowStatus ***

    @Label( standard = "&workflow status" )
    @PossibleValues
    (
        values =
        {
            "approved",
            "denied",
            "draft",
            "expired",
            "inactive",
            "incomplete",
            "pending"
        }
    )
    ValueProperty PROP_WORKFLOW_STATUS = new ValueProperty( TYPE, "WorkflowStatus" );

    Value<String> getWorkflowStatus();
    void setWorkflowStatus( String value );


    @Label( standard = "&exit transition node" )
    @Enablement( expr = "${Type != \"end\"}" )
    @Service( impl = TransitionPossibleValuesService.class )
    ValueProperty PROP_EXIT_TRANSITION_NAME = new ValueProperty( TYPE, "ExitTransitionName" );

    Value<String> getExitTransitionName();
    void setExitTransitionName( String value );

}
