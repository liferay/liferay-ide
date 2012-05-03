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
 * 		Kamesh Sampath - initial implementation
 * 		Gregory Amerson - IDE-355
 *******************************************************************************/

package com.liferay.ide.eclipse.hook.ui.editor;

import static com.liferay.ide.eclipse.core.util.CoreUtil.empty;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.hook.core.model.HookVersionType;
import com.liferay.ide.eclipse.hook.core.model.ICustomJsp;
import com.liferay.ide.eclipse.hook.core.model.ICustomJspDir;
import com.liferay.ide.eclipse.hook.core.model.IHook;
import com.liferay.ide.eclipse.hook.core.model.IHook600;
import com.liferay.ide.eclipse.hook.core.model.IHook610;
import com.liferay.ide.eclipse.hook.ui.HookUI;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.FileInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ModelElementList;
import org.eclipse.sapphire.modeling.ModelPropertyChangeEvent;
import org.eclipse.sapphire.modeling.ModelPropertyListener;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorPart;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

/**
 * @author <a href="mailto:kamesh.sampath@accenture.com">Kamesh Sampath</a>
 */
public class HookXmlEditor extends SapphireEditorForXml
{

	private static final String EDITOR_DEFINITION_PATH =
		"com.liferay.ide.eclipse.hook.ui/com/liferay/ide/eclipse/hook/ui/editor/hook-editor.sdef/HookConfigurationPage";

	public static final String ID = "com.liferay.ide.eclipse.hook.ui.editor.HookXmlEditor";

	protected boolean customModelDirty = false;

	private boolean ignoreCustomModelChanges;

	/**
	 *
	 */
	public HookXmlEditor() {
		super( ID );

		setEditorDefinitionPath( EDITOR_DEFINITION_PATH );
	}

	private void copyCustomJspsToProject( ModelElementList<ICustomJsp> customJsps )
	{
		try
		{
			ICustomJspDir customJspDirElement = this.getModelElement().nearest( IHook.class ).getCustomJspDir().element();

			if ( customJspDirElement != null && customJspDirElement.validation().ok() )
			{
				Path customJspDir = customJspDirElement.getValue().getContent();
				IFolder docroot = CoreUtil.getDocroot( getProject() );
				IFolder customJspFolder = docroot.getFolder( customJspDir.toPortableString() );

				ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( getProject() );
				IPath portalDir = liferayRuntime.getPortalDir();

				for ( ICustomJsp customJsp : customJsps )
				{
					String content = customJsp.getValue().getContent();

					if( !empty( content ) )
					{
						IFile customJspFile = customJspFolder.getFile( content );

						if( !customJspFile.exists() )
						{
							IPath portalJsp = portalDir.append( content );

							try
							{
								CoreUtil.makeFolders( (IFolder) customJspFile.getParent() );
								customJspFile.create( new FileInputStream( portalJsp.toFile() ), true, null );
							}
							catch( Exception e )
							{
								HookUI.logError( e );
							}
						}
					}
				}
			}
		}
		catch ( CoreException e )
		{
			HookUI.logError( e);
		}

	}

	@Override
	protected IModelElement createModel()
	{
		IFile editorFile = getFile();
		HookVersionType dtdVersion = null;
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

		ModelPropertyListener listener = new ModelPropertyListener()
		{

			@Override
			public void handlePropertyChangedEvent( ModelPropertyChangeEvent event )
			{
				handleCustomJspsPropertyChangedEvent( event );
			}
		};


		this.ignoreCustomModelChanges = true;
		modelElement.addListener( listener, "CustomJsps/*" );
		this.ignoreCustomModelChanges = false;

		return modelElement;
	}

	@Override
	public void doSave( IProgressMonitor monitor )
	{
		if ( this.customModelDirty )
		{
			ModelElementList<ICustomJsp> customJsps = getModelElement().nearest( IHook.class ).getCustomJsps();

			copyCustomJspsToProject( customJsps );

			this.customModelDirty = false;

			super.doSave( monitor );

			this.firePropertyChange( IEditorPart.PROP_DIRTY );
		}
		else
		{
			super.doSave( monitor );
		}
	}

	/**
	 * A small utility method used to compute the DTD version
	 *
	 * @param document
	 *            - the document that is loaded by the editor
	 * @return - {@link HookVersionType}
	 */
	HookVersionType getDTDVersion( Document document ) {
		HookVersionType dtdVersion = null;
		DocumentType docType = document.getDoctype();
		if ( docType != null ) {
			String publicId = docType.getPublicId();
			String systemId = docType.getSystemId();
			if ( publicId != null && systemId != null ) {
				if ( publicId.contains( "6.0.0" ) || systemId.contains( "6.0.0" ) )
				{
					dtdVersion = HookVersionType.v6_0_0;
				}
				else if ( publicId.contains( "6.1.0" ) || systemId.contains( "6.1.0" ) ) {
					dtdVersion = HookVersionType.v6_1_0;
				}
			}

		}

		return dtdVersion;
	}

	@Override
	protected void pageChange( int pageIndex )
	{
		this.ignoreCustomModelChanges = true;
		super.pageChange( pageIndex );
		this.ignoreCustomModelChanges = false;
	}

	protected void handleCustomJspsPropertyChangedEvent( ModelPropertyChangeEvent event )
	{
		if ( this.ignoreCustomModelChanges )
		{
			return;
		}

		this.customModelDirty = true;
		this.firePropertyChange( IEditorPart.PROP_DIRTY );
	}

	@Override
	public boolean isDirty()
	{
		if ( this.customModelDirty )
		{
			return true;
		}

		return super.isDirty();
	}

}
