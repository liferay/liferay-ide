/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.layouttpl.ui.wizard;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.core.operation.LayoutTplDescriptorHelper;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.layouttpl.ui.model.LayoutTplDiagram;
import com.liferay.ide.layouttpl.ui.model.PortletColumn;
import com.liferay.ide.layouttpl.ui.model.PortletLayout;
import com.liferay.ide.project.core.util.LiferayDataModelOperation;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.TemplateBuffer;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.TemplateException;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class AddLayoutTplOperation extends LiferayDataModelOperation implements INewLayoutTplDataModelProperties
{

    public AddLayoutTplOperation( IDataModel model, TemplateStore templateStore, TemplateContextType contextType )
    {
        super( model, templateStore, contextType );
    }

    @Override
    public IStatus execute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {

        IStatus retval = null;
        IStatus status = checkDescriptorFile( getTargetProject() );

        if( !status.isOK() )
        {
            return status;
        }

        IDataModel dm = getDataModel();

        LayoutTplDiagram diagramModel = createLayoutTplDigram( dm );

        try
        {
            String templateFile = getDataModel().getStringProperty( LAYOUT_TEMPLATE_FILE );

            IFile templateFileValue = null;
            if( !CoreUtil.isNullOrEmpty( templateFile ) )
            {
                templateFileValue = createTemplateFile( templateFile, diagramModel );
            }

            getDataModel().setProperty( LAYOUT_TPL_FILE_CREATED, templateFileValue );

            String wapTemplateFile = getDataModel().getStringProperty( LAYOUT_WAP_TEMPLATE_FILE );

            if( !CoreUtil.isNullOrEmpty( wapTemplateFile ) )
            {
                createTemplateFile( wapTemplateFile, diagramModel );
            }

            String thumbnailFile = getDataModel().getStringProperty( LAYOUT_THUMBNAIL_FILE );

            if( !CoreUtil.isNullOrEmpty( thumbnailFile ) )
            {
                createThumbnailFile( thumbnailFile, diagramModel );
            }
        }
        catch( CoreException ex )
        {
            LayoutTplUI.logError( ex );
            return LayoutTplUI.createErrorStatus( ex );
        }
        catch( IOException ex )
        {
            LayoutTplUI.logError( ex );
            return LayoutTplUI.createErrorStatus( ex );
        }

        LayoutTplDescriptorHelper layoutTplDescHelper = new LayoutTplDescriptorHelper( getTargetProject() );
        retval = layoutTplDescHelper.addNewLayoutTemplate( dm );

        return retval;
    }

    protected void createThumbnailFile( String thumbnailFile, LayoutTplDiagram diagramModel ) throws CoreException,
        IOException
    {

        IFolder docroot = CoreUtil.getDocroot( getTargetProject() );
        IFile thumbnailFileValue = docroot.getFile( thumbnailFile );
        URL iconFileURL = LayoutTplUI.getDefault().getBundle().getEntry( "/icons/blank_columns.png" );

        CoreUtil.prepareFolder( (IFolder) thumbnailFileValue.getParent() );

        if( thumbnailFileValue.exists() )
        {
            thumbnailFileValue.setContents( iconFileURL.openStream(), IResource.FORCE, null );
        }
        else
        {
            thumbnailFileValue.create( iconFileURL.openStream(), true, null );
        }
    }

    protected IFile createTemplateFile( String templateFile, LayoutTplDiagram diagramModel ) throws CoreException
    {

        IFolder docroot = CoreUtil.getDocroot( getTargetProject() );
        IFile templateFileValue = docroot.getFile( templateFile );
        CoreUtil.prepareFolder( (IFolder) templateFileValue.getParent() );

        if( diagramModel != null )
        {
            diagramModel.saveToFile( templateFileValue, null );
        }
        else
        {
            ByteArrayInputStream input = new ByteArrayInputStream( "".getBytes() );

            if( templateFileValue.exists() )
            {
                templateFileValue.setContents( input, IResource.FORCE, null );
            }
            else
            {
                templateFileValue.create( input, true, null );
            }
        }

        return templateFileValue;
    }

    protected LayoutTplDiagram createLayoutTplDigram( IDataModel dm )
    {
        LayoutTplDiagram diagram = new LayoutTplDiagram();

        if( dm.getBooleanProperty( LAYOUT_IMAGE_BLANK_COLUMN ) )
        {
            diagram = null;
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn(), 0 );
            diagram.addRow( row );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_I_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn(), 0 );

            PortletLayout row2 = new PortletLayout();
            row2.addColumn( new PortletColumn( 70 ), 0 );
            row2.addColumn( new PortletColumn( 30 ), 0 );

            diagram.addRow( row );
            diagram.addRow( row2 );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_II_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn(), 0 );

            PortletLayout row2 = new PortletLayout();
            row2.addColumn( new PortletColumn( 30 ), 0 );
            row2.addColumn( new PortletColumn( 70 ), 0 );

            diagram.addRow( row );
            diagram.addRow( row2 );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_1_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn(), 0 );

            PortletLayout row2 = new PortletLayout();
            row2.addColumn( new PortletColumn( 50 ), 0 );
            row2.addColumn( new PortletColumn( 50 ), 0 );

            PortletLayout row3 = new PortletLayout();
            row3.addColumn( new PortletColumn(), 0 );

            diagram.addRow( row );
            diagram.addRow( row2 );
            diagram.addRow( row3 );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_I_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn( 50 ), 0 );
            row.addColumn( new PortletColumn( 50 ), 0 );

            diagram.addRow( row );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_II_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn( 70 ), 0 );
            row.addColumn( new PortletColumn( 30 ), 0 );

            diagram.addRow( row );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_III_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn( 30 ), 0 );
            row.addColumn( new PortletColumn( 70 ), 0 );

            diagram.addRow( row );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_2_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn( 30 ), 0 );
            row.addColumn( new PortletColumn( 70 ), 0 );

            PortletLayout row2 = new PortletLayout();
            row2.addColumn( new PortletColumn( 70 ), 0 );
            row2.addColumn( new PortletColumn( 30 ), 0 );

            diagram.addRow( row );
            diagram.addRow( row2 );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_3_COLUMN ) )
        {
            PortletLayout row = new PortletLayout();
            row.addColumn( new PortletColumn( 33 ), 0 );
            row.addColumn( new PortletColumn( 33 ), 0 );
            row.addColumn( new PortletColumn( 33 ), 0 );

            diagram.addRow( row );
        }

        return diagram;
    }

    public IProject getTargetProject()
    {
        String projectName = model.getStringProperty( PROJECT_NAME );

        return ProjectUtil.getProject( projectName );
    }

    protected IStatus checkDescriptorFile( IProject project )
    {
        IFolder docroot = CoreUtil.getDocroot( project );

        IFile layoutTplDescriptorFile = docroot.getFile( "WEB-INF/" + ILiferayConstants.LIFERAY_LAYOUTTPL_XML_FILE );

        if( !layoutTplDescriptorFile.exists() )
        {
            try
            {
                createDefaultLayoutTplDescriptorFile( layoutTplDescriptorFile );
            }
            catch( Exception ex )
            {
                return LayoutTplCore.createErrorStatus( ex );
            }
        }

        return Status.OK_STATUS;
    }

    protected void createDefaultLayoutTplDescriptorFile( IFile layoutTplDescriptorFile )
        throws UnsupportedEncodingException, CoreException, BadLocationException, TemplateException
    {

        String templateString = null;
        IDocument document = new Document();

        TemplateContext context = new DocumentTemplateContext( contextType, document, 0, 0 );

        Template template = templateStore.findTemplateById( LAYOUTTPL_DESCRIPTOR_TEMPLATE );

        TemplateBuffer buffer = context.evaluate( template );

        templateString = buffer.getString();

        layoutTplDescriptorFile.create(
            new ByteArrayInputStream( templateString.getBytes( "UTF-8" ) ), IResource.FORCE, null );
    }

}
