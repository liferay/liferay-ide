/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.eclipse.service.ui;


import com.liferay.ide.eclipse.service.core.model.IServiceBuilder;

import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.PartInitException;


/**
 * @author Gregory Amerson
 */
public class ServiceBuilderEditor extends SapphireEditorForXml {

	// private SapphireDiagramEditor pageDiagram;

	public ServiceBuilderEditor() {
		super(ServiceUI.PLUGIN_ID);

		setRootModelElementType(IServiceBuilder.TYPE);
		setEditorDefinitionPath( ServiceUI.PLUGIN_ID +
			"/com/liferay/ide/eclipse/service/ui/ServiceBuilder.sdef/serviceBuilderPage" );
	}

	// @Override
	// public void doSave(final IProgressMonitor monitor) {
	// super.doSave(monitor);
	//
	// this.pageDiagram.doSave(monitor);
	// }

	// @Override
	// protected void createDiagramPages() throws PartInitException {
	// IPath path =
	// new Path( ServiceUI.PLUGIN_ID + "/com/liferay/ide/eclipse/service/ui/ServiceBuilder.sdef/diagramPage" );
	// this.pageDiagram = new SapphireDiagramEditor(this.getModelElement(), path);
	// SapphireDiagramEditorInput diagramEditorInput = null;
	//
	// try {
	// diagramEditorInput =
	// SapphireDiagramEditorFactory.createEditorInput(this.getModelElement().adapt(IFile.class));
	// }
	// catch (Exception e) {
	// ServiceUI.logError(e);
	// }
	//
	// if (diagramEditorInput != null) {
	// addPage(0, this.pageDiagram, diagramEditorInput);
	// setPageText(0, "Diagram");
	// setPageId(this.pages.get(0), "Diagram", this.pageDiagram.getPart());
	// }
	// }

	@Override
	protected void createFormPages() throws PartInitException {
		super.createFormPages();

		setPageText( 0, "Overview" );
	}
}
