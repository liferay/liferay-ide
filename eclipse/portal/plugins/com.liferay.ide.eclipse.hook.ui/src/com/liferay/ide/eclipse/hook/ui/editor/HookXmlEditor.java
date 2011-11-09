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

import com.liferay.ide.eclipse.hook.core.model.HookVersion;
import com.liferay.ide.eclipse.hook.core.model.IHook;
import com.liferay.ide.eclipse.hook.core.model.IHook600;
import com.liferay.ide.eclipse.hook.core.model.IHook610;
import com.liferay.ide.eclipse.hook.ui.HookUI;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class HookXmlEditor extends SapphireEditorForXml
{

	public static final String ID = "com.liferay.ide.eclipse.hook.ui.editor.HookXmlEditor";

	private static final String EDITOR_DEFINITION_PATH =
		"com.liferay.ide.eclipse.hook.ui/com/liferay/ide/eclipse/hook/ui/editor/hook-editor.sdef/HookConfigurationPage";

	/**
	 * 
	 */
	public HookXmlEditor() {
		super( ID );

		setEditorDefinitionPath( EDITOR_DEFINITION_PATH );
	}

	@Override
	protected IModelElement createModel()
	{
		IFile editorFile = getFile();
		HookVersion dtdVersion = null;
		RootXmlResource resource = null;

		try
		{
			resource = new RootXmlResource( new XmlResourceStore( editorFile.getContents() ) );
			Document document = resource.getDomDocument();
			dtdVersion = getDTDVersion( document );

			if ( document != null )
			{
				switch ( dtdVersion )
				{

				case v6_0_0:
					setRootModelElementType( IHook600.TYPE );
					break;

				case v6_1_0:
				default:
					setRootModelElementType( IHook610.TYPE );
					break;

				}
			}
		}
		catch ( Exception e )
		{
			HookUI.logError( e );
			setRootModelElementType( IHook610.TYPE );
		}
		finally
		{
			if ( resource != null )
			{
				resource.dispose();
			}
		}

		IModelElement modelElement = super.createModel();

		if ( dtdVersion != null )
		{
			IHook hookModel = (IHook) modelElement;

			hookModel.setVersion( dtdVersion );
		}

		return modelElement;
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
				if ( publicId.contains( "6.0.0" ) || systemId.contains( "6.0.0" ) )
				{
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
