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

package com.liferay.ide.eclipse.hook.ui.editor;

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
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

import com.liferay.ide.eclipse.hook.core.model.IHook;
import com.liferay.ide.eclipse.hook.core.model.IHookCommonElement;
import com.liferay.ide.eclipse.hook.core.model.internal.HookVersion;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class HookXmlEditor extends SapphireEditor {

	public static final String ID = "com.liferay.ide.eclipse.hook.ui.editor.HookXmlEditor";

	private static final String EDITOR_DEFINITION_PATH =
		"com.liferay.ide.eclipse.hook.ui/com/liferay/ide/eclipse/hook/ui/editor/hook-editor.sdef/DetailsPage";

	private StructuredTextEditor sourceEditor;
	private MasterDetailsEditorPage formEditor;
	private IModelElement model;

	/**
	 * 
	 */
	public HookXmlEditor() {
		super( ID );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.sapphire.ui.SapphireEditor#createModel()
	 */
	@Override
	protected IModelElement createModel() {
		RootXmlResource rootXmlResource = new RootXmlResource( new XmlEditorResourceStore( this, sourceEditor ) );

		Document document = rootXmlResource.getDomDocument();
		HookVersion dtdVersion = getDTDVersion( document );
		if ( document != null ) {
			switch ( dtdVersion ) {
			case v5_2_0: {
				model = IHook.TYPE.instantiate( rootXmlResource );
				break;
			}
			case v6_1_0: {
				model = com.liferay.ide.eclipse.hook.core.model610.IHook.TYPE.instantiate( rootXmlResource );
				break;
			}
			default: {
				model = com.liferay.ide.eclipse.hook.core.model600.IHook.TYPE.instantiate( rootXmlResource );
				break;
			}
			}

		}
		model.write( IHookCommonElement.PROP_VERSION, dtdVersion );
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
		setPageText( pgIndex, "liferay-hook.xml" );

	}

	@Override
	protected void createFormPages() throws PartInitException {
		IPath pageDefinitionLocation = new Path( EDITOR_DEFINITION_PATH );
		formEditor = new MasterDetailsEditorPage( this, model, pageDefinitionLocation );
		addPage( 0, formEditor );
		setPageText( 0, "Liferay Hook" );
		setPageId( this.pages.get( 0 ), "liferay-hook", this.formEditor.getPart() );

	}

	/**
	 * A small utility method used to compute the DTD version
	 * 
	 * @param document
	 *            - the document that is loaded by the editor
	 * @return - {@link HookVersion}
	 */
	HookVersion getDTDVersion( Document document ) {
		HookVersion dtdVersion = null;
		DocumentType docType = document.getDoctype();
		if ( docType != null ) {
			String publicId = docType.getPublicId();
			String systemId = docType.getSystemId();
			if ( publicId != null && systemId != null ) {
				if ( publicId.contains( "5.2.0" ) || systemId.contains( "5.2.0" ) ) {
					dtdVersion = HookVersion.v5_2_0;
				}
				else if ( publicId.contains( "6.0.0" ) || systemId.contains( "6.0.0" ) ) {
					dtdVersion = HookVersion.v6_0_0;
				}
				else if ( publicId.contains( "6.1.0" ) || systemId.contains( "6.1.0" ) ) {
					dtdVersion = HookVersion.v6_1_0;
				}
			}

		}

		return dtdVersion;
	}

}
