/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.diagram;

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Fork;
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.NewForkNode;
import com.liferay.ide.kaleo.core.op.NewForkNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class ForkNodeAddActionHandler extends NewNodeAddActionHandler
{

    private static final String WIZARD_ID = "newForkNodeWizard";

    public ForkNodeAddActionHandler( DiagramNodeTemplate nodeTemplate )
    {
        super( nodeTemplate );
    }

    @Override
    protected NewNodeOp createOp( Presentation context )
    {
        return NewForkNodeOp.TYPE.instantiate();
    }

    @Override
    protected String getWizardId()
    {
        return WIZARD_ID;
    }

    @Override
    public void postDiagramNodePartAdded( NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode )
    {
        final NewForkNodeOp newForkOp = op.nearest( NewForkNodeOp.class );
        NewForkNode newForkNodeFromWizard = newNodeFromWizard.nearest( NewForkNode.class );
        final Fork newFork = newNode.nearest( Fork.class );
        final WorkflowDefinition workflowDefinition = newFork.nearest( WorkflowDefinition.class );

        if( newForkOp.isAddJoin().content( true ) )
        {
            Join newJoin = workflowDefinition.getJoins().insert();
            final String newJoinName = newForkNodeFromWizard.getName().content() + " Join";

            newJoin.setName( newJoinName );

            for( Node connectedNode : op.getConnectedNodes() )
            {
                for( WorkflowNode diagramNode : workflowDefinition.getDiagramNodes() )
                {
                    if( connectedNode.getName().content().equals( diagramNode.getName().content() ) )
                    {
                        Transition newTransition =
                            diagramNode.nearest( CanTransition.class ).getTransitions().insert();
                        newTransition.setName( newJoinName );
                        newTransition.setTarget( newJoinName );
                    }
                }
            }
        }
    }
}
