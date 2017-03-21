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
import com.liferay.ide.kaleo.core.model.JoinXor;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.NewJoinXorNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class JoinXorNodeAddActionHandler extends NewNodeAddActionHandler
{

    private static final String WIZARD_ID = "newJoinXorNodeWizard";

    public JoinXorNodeAddActionHandler( DiagramNodeTemplate nodeTemplate )
    {
        super( nodeTemplate );
    }

    @Override
    protected NewNodeOp createOp( Presentation context )
    {
        return NewJoinXorNodeOp.TYPE.instantiate();
    }

    @Override
    protected String getWizardId()
    {
        return WIZARD_ID;
    }

    @Override
    public void postDiagramNodePartAdded( NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode )
    {
        final NewJoinXorNodeOp newJoinXorNodeOp = op.nearest( NewJoinXorNodeOp.class );

        final JoinXor newJoinXor = newNode.nearest( JoinXor.class );

        final WorkflowDefinition workflowDefinition = newJoinXor.nearest( WorkflowDefinition.class );

        for( Node nodeName : newJoinXorNodeOp.getConnectedNodes() )
        {
            for( WorkflowNode diagramNode : workflowDefinition.getDiagramNodes() )
            {
                if( nodeName.getName().content() != null &&
                    nodeName.getName().content().equals( diagramNode.getName().content() ) )
                {
                    final CanTransition canTransition = diagramNode.nearest( CanTransition.class );

                    final Transition newTransition = canTransition.getTransitions().insert();
                    newTransition.setName( newJoinXor.getName().content() );
                    newTransition.setTarget( newJoinXor.getName().content() );
                }
            }
        }

        if( newJoinXorNodeOp.getExitTransitionName().content() != null )
        {
            Transition newTransition = newJoinXor.getTransitions().insert();
            newTransition.setTarget( newJoinXorNodeOp.getExitTransitionName().content() );
            newTransition.setName( newJoinXorNodeOp.getExitTransitionName().content() );
        }
    }

}
