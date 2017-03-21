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
import com.liferay.ide.kaleo.core.model.Join;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.NewJoinNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class JoinNodeAddActionHandler extends NewNodeAddActionHandler
{

    private static final String WIZARD_ID = "newJoinNodeWizard";

    public JoinNodeAddActionHandler( DiagramNodeTemplate nodeTemplate )
    {
        super( nodeTemplate );
    }

    @Override
    protected NewNodeOp createOp( Presentation context )
    {
        return NewJoinNodeOp.TYPE.instantiate();
    }

    @Override
    protected String getWizardId()
    {
        return WIZARD_ID;
    }

    @Override
    public void postDiagramNodePartAdded( NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode )
    {
        NewJoinNodeOp newJoinNodeOp = op.nearest( NewJoinNodeOp.class );

        Join newJoin = newNode.nearest( Join.class );

        WorkflowDefinition workflowDefinition = newJoin.nearest( WorkflowDefinition.class );

        for( Node nodeName : newJoinNodeOp.getConnectedNodes() )
        {
            for( WorkflowNode diagramNode : workflowDefinition.getDiagramNodes() )
            {
                if( nodeName.getName().content() != null &&
                    nodeName.getName().content().equals( diagramNode.getName().content() ) )
                {
                    CanTransition canTransition = diagramNode.nearest( CanTransition.class );

                    Transition newTransition = canTransition.getTransitions().insert();
                    newTransition.setName( newJoin.getName().content() );
                    newTransition.setTarget( newJoin.getName().content() );
                }
            }
        }

        if( newJoinNodeOp.getExitTransitionName().content() != null )
        {
            Transition newTransition = newJoin.getTransitions().insert();
            newTransition.setTarget( newJoinNodeOp.getExitTransitionName().content() );
            newTransition.setName( newJoinNodeOp.getExitTransitionName().content() );
        }
    }

}
