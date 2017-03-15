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
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public interface NewTaskNodeOp extends NewNodeOp, AssignableOp
{
    ElementType TYPE = new ElementType( NewTaskNodeOp.class );

    @Type( base = NewTaskNode.class )
    @Label( standard = "new task node" )
    ImpliedElementProperty PROP_NEW_TASK_NODE = new ImpliedElementProperty( TYPE, "NewTaskNode" );

    NewTaskNode getNewTaskNode();

    @Label( standard = "task transitions" )
    ListProperty PROP_CONNECTED_NODES = new ListProperty( TYPE, NewNodeOp.PROP_CONNECTED_NODES );

}
