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

package com.liferay.ide.kaleo.ui.wizard;

import static com.liferay.ide.ui.util.UIUtil.sync;

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Condition;
import com.liferay.ide.kaleo.core.model.Fork;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.State;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.Transition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.ChooseDiagramNode;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.ConditionForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.ForkForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.JoinForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.JoinXorForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.StateForOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp.TaskForOp;
import com.liferay.ide.kaleo.ui.diagram.NewNodeAddActionHandler;

import org.eclipse.sapphire.modeling.ProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;

/**
 * @author Gregory Amerson
 */
public class NewNodeOpWizard extends SapphireWizard<NewNodeOp>
{

    private NewNodeAddActionHandler actionHandler;
    private Presentation context;
    private DiagramNodePart diagramNodePart;

    public NewNodeOpWizard(
        NewNodeOp modelElement, String wizardId, NewNodeAddActionHandler actionHandler,
        Presentation context )
    {
        super( modelElement, DefinitionLoader.context( NewNodeOpWizard.class ).sdef( "WorkflowDefinitionWizards" ).wizard( wizardId ) );
        this.actionHandler = actionHandler;
        this.context = context;
    }

    @Override
    protected Status performFinish( ProgressMonitor monitor )
    {
        final Object[] diagramNode = new Object[1];

        monitor.beginTask( "Creating new node", 2 );

        sync
        (
            new Runnable()
            {
                public void run()
                {
                    diagramNode[0] = NewNodeOpWizard.this.actionHandler.insertDiagramPart( NewNodeOpWizard.this.context, false );
                }
            }
        );

        monitor.worked( 1 );

        if( diagramNode[0] instanceof DiagramNodePart )
        {
            this.diagramNodePart = (DiagramNodePart) diagramNode[0];

            sync
            (
                new Runnable()
                {
                    public void run()
                    {
                        performPostDiagramNodeAdded();
                    }
                }
            );

            monitor.worked( 1 );
        }

        return Status.createOkStatus();
    }

    protected void performPostDiagramNodeAdded()
    {
        final NewNodeOp newNodeOp = this.element().nearest( NewNodeOp.class );

        final CanTransition newNode = newNodeOp.adapt( CanTransition.class );

        final CanTransition node = this.diagramNodePart.getLocalModelElement().nearest( CanTransition.class );

        final WorkflowDefinition workflowDefinition = node.nearest( WorkflowDefinition.class );

        if( newNode != null && node != null )
        {
            node.setName( newNode.getName().content() );

            if( newNodeOp.getConnectedNodes().size() > 0 )
            {
                for( Node diagramNode : newNodeOp.getConnectedNodes() )
                {
                    String diagramNodeName = diagramNode.getName().content( true );

                    if( diagramNode instanceof ConditionForOp )
                    {
                        workflowDefinition.getConditions().insert().setName( diagramNodeName );
                    }
                    else if( diagramNode instanceof ForkForOp )
                    {
                        workflowDefinition.getForks().insert().setName( diagramNodeName );
                    }
                    else if( diagramNode instanceof JoinForOp )
                    {
                        workflowDefinition.getJoins().insert().setName( diagramNodeName );
                    }
                    else if( diagramNode instanceof JoinXorForOp)
                    {
                        workflowDefinition.getJoinXors().insert().setName( diagramNodeName );
                    }
                    else if( diagramNode instanceof StateForOp )
                    {
                        workflowDefinition.getStates().insert().setName( diagramNodeName );
                    }
                    else if( diagramNode instanceof TaskForOp )
                    {
                        workflowDefinition.getTasks().insert().setName( diagramNodeName );
                    }

                    if( diagramNode instanceof ChooseDiagramNode || node instanceof State || node instanceof Task ||
                                    node instanceof Condition || node instanceof Fork )
                    {
                        Transition newTransition = node.getTransitions().insert();
                        newTransition.setName( diagramNodeName );
                        newTransition.setTarget( diagramNodeName );
                    }
                    else
                    {
                        Transition joinTransition = diagramNode.nearest( CanTransition.class ).getTransitions().insert();
                        joinTransition.setName( node.getName().content() );
                        joinTransition.setTarget( node.getName().content() );
                    }
                }
            }
        }

        this.actionHandler.postDiagramNodePartAdded( this.element(), newNode, node );
    }
}
