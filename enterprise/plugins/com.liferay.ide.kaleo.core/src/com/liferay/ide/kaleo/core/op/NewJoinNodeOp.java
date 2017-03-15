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
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface NewJoinNodeOp extends NewNodeOp
{
    ElementType TYPE = new ElementType( NewJoinNodeOp.class );

    @Type( base = NewJoinNode.class )
    @Label( standard = "new join node" )
    ImpliedElementProperty PROP_NEW_JOIN_NODE = new ImpliedElementProperty( TYPE, "NewJoinNode" );

    NewJoinNode getNewJoinNode();

    @Label( standard = "nodes to join" )
    @Length( min = 0, max = 2 )
    ListProperty PROP_CONNECTED_NODES = new ListProperty( TYPE, NewNodeOp.PROP_CONNECTED_NODES );

    @Label( standard = "&exit transition name" )
    @Service( impl = TransitionPossibleValuesService.class )
    ValueProperty PROP_EXIT_TRANSITION_NAME = new ValueProperty( TYPE, "ExitTransitionName" );

    Value<String> getExitTransitionName();

    void setExitTransitionName( String value );
}
