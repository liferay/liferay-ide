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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ImpliedElementProperty;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public interface NewForkNodeOp extends NewNodeOp
{
    ElementType TYPE = new ElementType( NewForkNodeOp.class );

    @Type( base = NewForkNode.class )
    @Label( standard = "new fork node" )
    ImpliedElementProperty PROP_NEW_FORK_NODE = new ImpliedElementProperty( TYPE, "NewForkNode" );

    NewForkNode getNewForkNode();

    @Label( standard = "fork nodes" )
    @Length( min = 0, max = 2 )
    ListProperty PROP_CONNECTED_NODES = new ListProperty( TYPE, NewNodeOp.PROP_CONNECTED_NODES );

    // *** AddJoin ***

    @Type( base = Boolean.class )
    @Label( standard = "Automatically add join node" )
    @DefaultValue( text = "true" )
    ValueProperty PROP_ADD_JOIN = new ValueProperty( TYPE, "AddJoin" );

    Value<Boolean> isAddJoin();
    void setAddJoin( Boolean value );
    void setAddJoin( String value );
}
