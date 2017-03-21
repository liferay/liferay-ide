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

import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditor;

import org.eclipse.gef.requests.CreationFactory;
import org.eclipse.gef.tools.ConnectionCreationTool;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart.ConnectionPalette;
import org.eclipse.sapphire.ui.swt.gef.ConnectionCreationFactory;
import org.eclipse.sapphire.ui.swt.gef.SapphireDiagramEditor;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Event;

/**
 * @author Gregory Amerson
 */
public class StartTransitionActionHandler extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        final WorkflowDefinitionEditor editor = context.part().adapt( WorkflowDefinitionEditor.class );

        final SapphireDiagramEditor diagramEditor = editor.getDiagramEditor();

        final ISapphirePart part = context.part();

        final DiagramNodePart diagramPart = part.nearest( DiagramNodePart.class );
        final SapphireDiagramEditorPagePart editorPart = part.nearest( SapphireDiagramEditorPagePart.class );
        final ConnectionPalette connectionPalette = editorPart.getConnectionPalettes().get( 0 );

        final CreationFactory factory = new ConnectionCreationFactory( connectionPalette.getConnectionDef() );

        final ConnectionCreationTool tool = new ConnectionCreationTool( factory );
        diagramEditor.getEditDomain().setActiveTool( tool );

        final Event e = new Event();
        e.x = diagramPart.getNodeBounds().getX();
        e.y = diagramPart.getNodeBounds().getY();
        e.widget = diagramEditor.getGraphicalViewer().getControl();
        e.button = 1;

        final MouseEvent me = new MouseEvent( e );

        tool.mouseDown( me, diagramEditor.getGraphicalViewer() );

        return null;
    }

}
