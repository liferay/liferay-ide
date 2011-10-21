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
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.eclipse.portlet.ui.editor;

import com.liferay.ide.eclipse.portlet.core.display.model.IDisplay;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.ui.SapphireEditor;
import org.eclipse.sapphire.ui.form.editors.masterdetails.MasterDetailsEditorPage;
import org.eclipse.sapphire.ui.swt.xml.editor.XmlEditorResourceStore;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class LiferayDisplayXmlEditor extends SapphireEditor {

	public static final String ID = "com.liferay.ide.eclipse.portlet.ui.editor.LiferayDisplayXmlEditor";

	private static final String EDITOR_DEFINITION_PATH =
		"com.liferay.ide.eclipse.portlet.ui/com/liferay/ide/eclipse/portlet/ui/editor/liferay-display.sdef/DetailsPage";

	private StructuredTextEditor sourceEditor;
	private MasterDetailsEditorPage formEditor;
	private IModelElement model;

	/**
	 * 
	 */
	public LiferayDisplayXmlEditor() {
		super( ID );

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireEditor#createModel()
	 */
	@Override
	protected IModelElement createModel() {
		// TODO Adapt the model based on the version of the DTD
		RootXmlResource rootXmlResource = new RootXmlResource( new XmlEditorResourceStore( this, sourceEditor ) );
		model = IDisplay.TYPE.instantiate( rootXmlResource );
		return model;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireEditor#createSourcePages()
	 */
	@Override
	protected void createSourcePages() throws PartInitException {
		sourceEditor = new StructuredTextEditor();
		sourceEditor.setEditorPart( this );
		final IFileEditorInput fileEditorInput = (IFileEditorInput) getEditorInput();
		int pgIndex = addPage( sourceEditor, fileEditorInput );
		setPageText( pgIndex, "Source" );

	}

	@Override
	protected void createFormPages() throws PartInitException {
		IPath pageDefinitionLocation = new Path( EDITOR_DEFINITION_PATH );
		formEditor = new MasterDetailsEditorPage( this, model, pageDefinitionLocation );
		addPage( 0, formEditor );
		setPageText( 0, "Display" );
		setPageId( this.pages.get( 0 ), "lfr-display", this.formEditor.getPart() );

	}

}

