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

import com.liferay.ide.kaleo.core.model.Fork;
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.JoinXor;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.internal.DefaultOpMethods;
import com.liferay.ide.kaleo.core.op.internal.NewNodeOpAdapter;

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.modeling.annotations.DelegateImplementation;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
@Service( impl = NewNodeOpAdapter.class )
public interface NewNodeOp extends ExecutableElement
{

    @Label( standard = "new condition" )
    @Image( path = "images/condition_16x16.png" )
    public interface ConditionForOp extends Fork, Scriptable
    {
        ElementType TYPE = new ElementType( ConditionForOp.class );

        @Length( min = 0 )
        ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, Fork.PROP_TRANSITIONS );
    }

    @Label( standard = "new fork" )
    public interface ForkForOp extends Fork
    {
        ElementType TYPE = new ElementType( ForkForOp.class );

        @Length( min = 0 )
        ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, Fork.PROP_TRANSITIONS );
    }

    @Label( standard = "new join" )
    public interface JoinForOp extends Join
    {
        ElementType TYPE = new ElementType( JoinForOp.class );

        @Length( min = 0 )
        ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, Join.PROP_TRANSITIONS );
    }

    @Label( standard = "new join XOR" )
    public interface JoinXorForOp extends JoinXor
    {
        ElementType TYPE = new ElementType( JoinXorForOp.class );

        @Length( min = 0 )
        ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, Join.PROP_TRANSITIONS );
    }

    @Label( standard = "new state" )
    public interface StateForOp extends State
    {
        ElementType TYPE = new ElementType( StateForOp.class );

        @Length( min = 0 )
        ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, State.PROP_TRANSITIONS );
    }

    @Label( standard = "new task" )
    public interface TaskForOp extends Task
    {
        ElementType TYPE = new ElementType( TaskForOp.class );

        @Length( min = 0 )
        ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, Task.PROP_TRANSITIONS );
    }

    ElementType TYPE = new ElementType( NewNodeOp.class );

    // *** ConnectedNodes ***

    @Type
    (
        base = Node.class,
        possible =
        {
            ChooseDiagramNode.class,
            StateForOp.class,
            TaskForOp.class,
            ConditionForOp.class,
            ForkForOp.class,
            JoinForOp.class,
            JoinXorForOp.class
        }
    )
    @Label( standard = "connected nodes" )
    ListProperty PROP_CONNECTED_NODES = new ListProperty( TYPE, "ConnectedNodes" );

    ElementList<Node> getConnectedNodes();

    @Type( base = Boolean.class )
    @Label( standard = "Use Add wizards on drop from palette" )
    ValueProperty PROP_USE_NODE_WIZARDS = new ValueProperty( TYPE, "UseNodeWizards" );

    Value<Boolean> isUseNodeWizards();
    void setUseNodeWizards( Boolean value );
    void setUseNodeWizards( String value );

    @Type( base = WorkflowDefinition.class )
    ElementProperty PROP_WORKFLOW_DEFINITION = new ElementProperty( TYPE, "WorkflowDefinition" );

    ElementHandle<WorkflowDefinition> getWorkflowDefinition();

    @DelegateImplementation( DefaultOpMethods.class )
    Status execute( ProgressMonitor monitor );

}
