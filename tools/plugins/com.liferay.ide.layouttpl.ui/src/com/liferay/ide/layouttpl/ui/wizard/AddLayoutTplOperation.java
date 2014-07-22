/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;
import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.core.operation.LayoutTplDescriptorHelper;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelOperation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
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
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Kuo Zhang
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

        IDataModel dm = getDataModel();

        String diagramClassName = dm.getStringProperty( LAYOUT_TEMPLATE_ID );
        LayoutTplElement diagramModel = createLayoutTplDigram( dm, isBootstrapStyle(), diagramClassName );

        try
        {
            IFile templateFile = null;

            String templateFileName = getDataModel().getStringProperty( LAYOUT_TEMPLATE_FILE );

            if( !CoreUtil.isNullOrEmpty( templateFileName ) )
            {
                templateFile = createTemplateFile( templateFileName, diagramModel );
            }

            getDataModel().setProperty( LAYOUT_TPL_FILE_CREATED, templateFile );

            String wapTemplateFileName = getDataModel().getStringProperty( LAYOUT_WAP_TEMPLATE_FILE );

            diagramModel.setClassName( diagramClassName + ".wap" );

            if( !CoreUtil.isNullOrEmpty( wapTemplateFileName ) )
            {
                createTemplateFile( wapTemplateFileName, diagramModel );
            }

            String thumbnailFileName = getDataModel().getStringProperty( LAYOUT_THUMBNAIL_FILE );

            if( !CoreUtil.isNullOrEmpty( thumbnailFileName ) )
            {
                createThumbnailFile( thumbnailFileName );
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

    protected void createThumbnailFile( String thumbnailFileName ) 
        throws CoreException, IOException
    {
        IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( getTargetProject() );
        IFile thumbnailFile = defaultDocroot.getFile( thumbnailFileName );
        URL iconFileURL = LayoutTplUI.getDefault().getBundle().getEntry( "/icons/blank_columns.png" ); //$NON-NLS-1$

        CoreUtil.prepareFolder( (IFolder) thumbnailFile.getParent() );

        if( thumbnailFile.exists() )
        {
            thumbnailFile.setContents( iconFileURL.openStream(), IResource.FORCE, null );
        }
        else
        {
            thumbnailFile.create( iconFileURL.openStream(), true, null );
        }
    }

    protected IFile createTemplateFile( String templateFileName, LayoutTplElement element ) throws CoreException
    {
        IFolder defaultDocroot = CoreUtil.getDefaultDocrootFolder( getTargetProject() );
        IFile templateFile = defaultDocroot.getFile( templateFileName );

        if( element != null )
        {
            LayoutTplUtil.saveToFile( element, templateFile, null );
        }
        else
        {
            ByteArrayInputStream input = new ByteArrayInputStream( StringPool.EMPTY.getBytes() );

            if( templateFile.exists() )
            {
                templateFile.setContents( input, IResource.FORCE, null );
            }
            else
            {
                templateFile.create( input, true, null );
            }
        }

        return templateFile;
    }

    protected LayoutTplElement createLayoutTplDigram( IDataModel dm, boolean isBootstrapStyle, String className )
    {
        LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( isBootstrapStyle );
        layoutTpl.setClassName( className );

        if( dm.getBooleanProperty( LAYOUT_IMAGE_1_COLUMN ) )
        {
            PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column = row.getPortletColumns().insert();

            column.setWeight( column.getFullWeight().content() );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_I_COLUMN ) )
        {
            PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert(); 

            PortletColumnElement column11 = row1.getPortletColumns().insert();
            column11.setWeight( column11.getFullWeight().content() );

            PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column21 = row2.getPortletColumns().insert();
            PortletColumnElement column22 = row2.getPortletColumns().insert();

            if( isBootstrapStyle )
            {
                column21.setWeight( 8 );
                column22.setWeight( 4 );
            }
            else
            {
                column21.setWeight( 70 );
                column22.setWeight( 30 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_II_COLUMN ) )
        {
            PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

            PortletColumnElement column11 = row1.getPortletColumns().insert();
            column11.setWeight( column11.getFullWeight().content() );

            PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert(); 
            PortletColumnElement column21 = row2.getPortletColumns().insert();
            PortletColumnElement column22 = row2.getPortletColumns().insert();

            if( isBootstrapStyle )
            {
                column21.setWeight( 4 );
                column22.setWeight( 8 );
            }
            else
            {
                column21.setWeight( 30 );
                column22.setWeight( 70 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_1_2_1_COLUMN ) )
        {
            PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column11 = row1.getPortletColumns().insert();
            column11.setWeight( column11.getFullWeight().content() );

            PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column21 = row2.getPortletColumns().insert();
            column21.setWeight( column21.getFullWeight().content() / 2 );
            PortletColumnElement column22 = row2.getPortletColumns().insert();
            column22.setWeight( column22.getFullWeight().content() / 2 );

            PortletLayoutElement row3 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column31 = row3.getPortletColumns().insert();
            column11.setWeight( column31.getFullWeight().content() );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_I_COLUMN ) )
        {
            PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column11 = row1.getPortletColumns().insert();
            column11.setWeight( column11.getFullWeight().content() / 2 );
            PortletColumnElement column12 = row1.getPortletColumns().insert();
            column12.setWeight( column12.getFullWeight().content() / 2 );

            PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column21 = row2.getPortletColumns().insert();
            column21.setWeight( column21.getFullWeight().content() / 2 );
            PortletColumnElement column22 = row2.getPortletColumns().insert();
            column22.setWeight( column22.getFullWeight().content() / 2 );
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_II_COLUMN ) )
        {
            PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column1 = row.getPortletColumns().insert();
            PortletColumnElement column2 = row.getPortletColumns().insert();

            if( isBootstrapStyle )
            {
                column1.setWeight( 8 );
                column2.setWeight( 4 );
            }
            else
            {
                column1.setWeight( 70 );
                column2.setWeight( 30 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_III_COLUMN ) )
        {
            PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column1 = row.getPortletColumns().insert();
            PortletColumnElement column2 = row.getPortletColumns().insert();

            if( isBootstrapStyle )
            {
                column1.setWeight( 4 );
                column2.setWeight( 8 );
            }
            else
            {
                column1.setWeight( 30 );
                column2.setWeight( 70 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_2_2_COLUMN ) )
        {
            PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column11 = row1.getPortletColumns().insert();
            PortletColumnElement column12 = row1.getPortletColumns().insert();

            PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column21 = row2.getPortletColumns().insert();
            PortletColumnElement column22 = row2.getPortletColumns().insert();

            if( isBootstrapStyle )
            {
                column11.setWeight( 4 );
                column12.setWeight( 8 );
                column21.setWeight( 8 );
                column22.setWeight( 4 );
            }
            else
            {
                column11.setWeight( 30 );
                column12.setWeight( 70 );
                column21.setWeight( 70 );
                column22.setWeight( 30 );
            }
        }
        else if( dm.getBooleanProperty( LAYOUT_IMAGE_3_COLUMN ) )
        {
            PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
            PortletColumnElement column1 = row.getPortletColumns().insert();
            PortletColumnElement column2 = row.getPortletColumns().insert();
            PortletColumnElement column3 = row.getPortletColumns().insert();

            if( isBootstrapStyle )
            {
                column1.setWeight( 4 );
                column2.setWeight( 4 );
                column3.setWeight( 4 );
            }
            else
            {
                column1.setWeight( 33 );
                column2.setWeight( 33 );
                column3.setWeight( 33 );
            }
        }

        return layoutTpl;
    }

    public IProject getTargetProject()
    {
        String projectName = model.getStringProperty( PROJECT_NAME );

        return ProjectUtil.getProject( projectName );
    }

    private boolean isBootstrapStyle()
    {
        final Version version = new Version( LiferayCore.create( getTargetProject() ).getPortalVersion() );

        return CoreUtil.compareVersions( version, ILiferayConstants.V620 ) >= 0 ;
    }

}
