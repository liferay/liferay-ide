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
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditor;
import com.liferay.ide.kaleo.ui.wizard.NewNodeOpWizard;

import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.Point;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.actions.DiagramNodeAddActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Gregory Amerson
 */
public abstract class NewNodeAddActionHandler extends DiagramNodeAddActionHandler
{
    public NewNodeAddActionHandler( DiagramNodeTemplate nodeTemplate )
    {
        super( nodeTemplate );
    }

    protected boolean canRunWizard( Presentation context )
    {
        WorkflowDefinitionEditor definitionEditor = context.part().adapt( WorkflowDefinitionEditor.class );

        return definitionEditor.isNodeWizardsEnabled();
    }

    protected NewNodeOpWizard createNewNodeWizard(
        NewNodeOp op, NewNodeAddActionHandler actionHandler, Presentation context )
    {
        return new NewNodeOpWizard( op, getWizardId(), actionHandler, context );
    }

    protected abstract NewNodeOp createOp( Presentation context );

    protected abstract String getWizardId();

    public Object insertDiagramPart( Presentation context, boolean enableDirectEditing )
    {
        SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart) this.getNodeTemplate().parent();

        DiagramNodePart nodePart = this.getNodeTemplate().createNewDiagramNode();
        Point pt = diagramPart.getMouseLocation();
        nodePart.setNodeBounds(pt.getX(), pt.getY());

        if( enableDirectEditing )
        {
            // Select the new node and put it in direct-edit mode
            diagramPart.selectAndDirectEdit(nodePart);
        }

        return nodePart;
    }

    public abstract void postDiagramNodePartAdded(
        NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode );

    @Override
    public final Object run( Presentation context )
    {
        Object retval = null;

        if( canRunWizard( context ) )
        {
            NewNodeOp op = createOp( context );

            final WorkflowDefinition oldWorkflowDefinition = (WorkflowDefinition) getModelElement();
            WorkflowDefinition newWorkflowDefinition = op.getWorkflowDefinition().content( true );

            newWorkflowDefinition.copy( oldWorkflowDefinition );

            // When WorkflowDefinition.getSchemaVersion(), VersionedSchemaDefaultValueService.compute() 
            // can't get the version, always returns the default value, directly set the schema version here.
            newWorkflowDefinition.setSchemaVersion( oldWorkflowDefinition.getSchemaVersion().content() );

            NewNodeOpWizard wizard = createNewNodeWizard( op, this, context );

            WorkflowDefinitionEditor definitionEditor = context.part().adapt( WorkflowDefinitionEditor.class );

            op.setUseNodeWizards( definitionEditor.isNodeWizardsEnabled() );

            runWizard( context, wizard );

            definitionEditor.setNodeWizardsEnabled( op.isUseNodeWizards().content() );
        }
        else
        {
            retval = insertDiagramPart( context, true );
        }

        return retval;
    }

    protected int runWizard( Presentation context, IWizard wizard )
    {
        WizardDialog wizardDiagram = new WizardDialog( ( (SwtPresentation) context ).shell(), wizard );

        return wizardDiagram.open();
    }

}
