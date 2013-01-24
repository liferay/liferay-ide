/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.portlet.ui.editor;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.model.IModelChangedEvent;
import com.liferay.ide.core.model.IModelChangedListener;
import com.liferay.ide.portlet.core.PluginPackageModel;
import com.liferay.ide.portlet.ui.PortletUIPlugin;
import com.liferay.ide.ui.editor.InputContext;
import com.liferay.ide.ui.editor.InputContextManager;
import com.liferay.ide.ui.editor.PluginPackageInputContextManager;
import com.liferay.ide.ui.form.IDEFormEditor;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.internal.ui.propertiesfileeditor.PropertiesFileEditor;
import org.eclipse.jface.text.IDocument;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.internal.forms.widgets.BusyIndicator;
import org.eclipse.ui.part.FileEditorInput;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction", "rawtypes" } )
public class PluginPackageEditor extends IDEFormEditor implements IModelChangedListener
{

    public static final String EDITOR_ID = "com.liferay.ide.eclipse.portlet.ui.editor.pluginpackage"; //$NON-NLS-1$

    protected BusyIndicator busyLabel;

    /**
     * The properties text editor.
     */
    protected PropertiesFileEditor editor;

    protected boolean ignoreModelChanges = false;

    protected int lastPageIndex = -1;

    protected PluginPackageModel model;;

    public void contextRemoved( InputContext context )
    {
    }

    @Override
    public void editorContextAdded( InputContext context )
    {
    }

    @Override
    public Object getAdapter( Class adapterClass )
    {
        Object adapter = super.getAdapter( adapterClass );

        if( adapter == null )
        {
            adapter = editor.getAdapter( adapterClass );
        }

        return adapter;
    }

    @Override
    public IFileEditorInput getEditorInput()
    {
        return (IFileEditorInput) super.getEditorInput();
    }

    public IPath getPortalDir()
    {
        try
        {
            final ILiferayProject liferayProject = LiferayCore.create( getEditorInput().getFile().getProject() );
            return liferayProject.getAppServerPortalDir();
        }
        catch( Exception e )
        {
            return null;
        }
    }

    @Override
    public void init( IEditorSite site, IEditorInput editorInput ) throws PartInitException
    {
        Assert.isLegal( editorInput instanceof IFileEditorInput, "Invalid Input: Must be IFileEditorInput" ); //$NON-NLS-1$

        super.init( site, editorInput );

        setPartName( editorInput.getName() );
    }

    @Override
    public boolean isSaveAsAllowed()
    {
        return false;
    }

    public void modelChanged( IModelChangedEvent event )
    {
        if( ignoreModelChanges )
        {
            return;
        }

        PluginPackageModel model = (PluginPackageModel) getModel();

        IDocument doc = model.getDocument();

        editor.getDocumentProvider().getDocument( getEditorInput() ).set( doc.get() );
    }

    public void monitoredFileAdded( IFile monitoredFile )
    {
    }

    public boolean monitoredFileRemoved( IFile monitoredFile )
    {
        return false;
    }

    private void addPropertiesEditorPage()
    {
        editor = new PropertiesFileEditor();

        ( (PluginPackageModel) getModel() ).addModelChangedListener( this );

        // editor.setEditorPart(this);

        int index;

        try
        {
            index = addPage( editor, getEditorInput() );

            setPageText( index, Msgs.source );
        }
        catch( PartInitException e )
        {
            PortletUIPlugin.logError( e );
        }

    }

    protected void addDependenciesFormPage()
    {
        try
        {
            int index = addPage( new DependenciesFormPage( this ) );
            setPageText( index, Msgs.dependencies );
        }
        catch( PartInitException e )
        {
            PortletUIPlugin.logError( e );
        }
    }

    @Override
    protected void addPages()
    {
        addPluginPackageFormPage();
        // addDependenciesFormPage();
        addPropertiesEditorPage();
    }

    protected void addPluginPackageFormPage()
    {
        try
        {
            int index = addPage( new PluginPackageFormPage( this ) );

            setPageText( index, Msgs.properties );
        }
        catch( PartInitException e )
        {
            PortletUIPlugin.logError( e );
        }
    }

    @Override
    protected InputContextManager createInputContextManager()
    {
        PluginPackageInputContextManager manager = new PluginPackageInputContextManager( this );

        // manager.setUndoManager(new PluginUndoManager(this));

        return manager;
    }

    @Override
    protected void createResourceContexts( InputContextManager manager, IFileEditorInput input )
    {
        IFile file = input.getFile();

        if( file.exists() )
        {
            IEditorInput in = new FileEditorInput( file );

            manager.putContext( in, new PluginPackageInputContext( this, in, true ) );
        }

        manager.monitorFile( file );
    }

    @Override
    protected String getEditorID()
    {
        return EDITOR_ID;
    }

    @Override
    protected InputContext getInputContext( Object object )
    {
        InputContext context = null;

        if( object instanceof IFile )
        {
            context = fInputContextManager.findContext( (IFile) object );
        }

        return context;
    }

    @Override
    protected void pageChange( int newPageIndex )
    {
        super.pageChange( newPageIndex );

        if( this.lastPageIndex == 1 && newPageIndex != 1 )
        {
            String props = editor.getDocumentProvider().getDocument( getEditorInput() ).get();

            try
            {
                ignoreModelChanges = true;

                ( (PluginPackageModel) getModel() ).load( new ByteArrayInputStream( props.getBytes() ), false );

                ignoreModelChanges = false;
            }
            catch( CoreException e )
            {
                PortletUIPlugin.logError( e );
            }
        }

        this.lastPageIndex = newPageIndex;
    }

    private static class Msgs extends NLS
    {
        public static String dependencies;
        public static String properties;
        public static String source;

        static
        {
            initializeMessages( PluginPackageEditor.class.getName(), Msgs.class );
        }
    }
}
