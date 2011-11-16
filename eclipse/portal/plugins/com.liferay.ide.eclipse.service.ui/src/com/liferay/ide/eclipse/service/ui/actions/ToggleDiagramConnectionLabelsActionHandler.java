/******************************************************************************
 * Copyright (c) 2011 Oracle
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Shenxue Zhou - initial implementation and ongoing maintenance
 ******************************************************************************/

package com.liferay.ide.eclipse.service.ui.actions;

import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.ui.SapphireRenderingContext;
import org.eclipse.sapphire.ui.diagram.SapphireDiagramActionHandler;
import org.eclipse.sapphire.ui.diagram.editor.SapphireDiagramEditorPagePart;

public class ToggleDiagramConnectionLabelsActionHandler extends SapphireDiagramActionHandler {

	protected boolean checked = true;

	@Override
	public boolean canExecute( Object obj ) {
		return true;
	}

	@Override
	public boolean isChecked() {
		return checked;
	}

	@Override
	protected Object run( SapphireRenderingContext context ) {
		checked = !checked;

		IServiceBuilder serviceBuilder = (IServiceBuilder) context.getPart().getModelElement();
		serviceBuilder.setShowRelationshipLabels( checked );
		serviceBuilder.refresh( false, true );

		SapphireDiagramEditorPagePart diagramPart = (SapphireDiagramEditorPagePart) context.getPart();

		//
		// DiagramRenderingContext dContext = (DiagramRenderingContext) context;
		// List<IDiagramConnectionDef> defs = dContext.getDiagramEditor().getPart().getDiagramConnectionDefs();
		// dContext.getDiagramEditor().refreshContent();
		// HashSet<PictogramElement> set1 = dContext.getDiagramEditor().getRefreshedFigure4PE();
		// for ( PictogramElement pe : set1 ) {
		// System.out.println( pe );
		// if ( pe instanceof FreeFormConnection ) {
		// FreeFormConnection ffc = (FreeFormConnection) pe;
		// for ( ConnectionDecorator cd : ffc.getConnectionDecorators() ) {
		// GraphicsAlgorithm ga = cd.getGraphicsAlgorithm();
		// if ( ga instanceof Text ) {
		// Text text = (Text) ga;
		// text.setValue( "zzz" );
		// }
		// System.out.println( ga );
		// }
		// }
		// }
		// dContext.getDiagramEditor().refresh();
		// // dContext.layout();
		// dContext.getDiagramEditor().getGraphicalViewer().flush();
		// dContext.getDiagramEditor().getGraphicalViewer().getContents().refresh();
		// dContext.getDiagramEditor().getFigureCanvas().layout( true );
		// dContext.getDiagramEditor().getFigureCanvas().redraw();
		// dContext.getDiagramEditor().syncDiagramWithModel();
		// ScrollingGraphicalViewer viewer = (ScrollingGraphicalViewer)
		// dContext.getDiagramEditor().getGraphicalViewer();
		// RootEditPart root = viewer.getRootEditPart();
		// root.refresh();
		return null;
	}

}
