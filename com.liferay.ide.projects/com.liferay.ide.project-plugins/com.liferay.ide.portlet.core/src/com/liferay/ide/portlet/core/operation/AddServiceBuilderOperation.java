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

package com.liferay.ide.portlet.core.operation;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.portlet.core.PortletCore;
import com.liferay.ide.project.core.util.LiferayDataModelOperation;
import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.util.ServerUtil;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

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
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.osgi.framework.Version;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction" } )
public class AddServiceBuilderOperation extends LiferayDataModelOperation
    implements INewServiceBuilderDataModelProperties
{

    public AddServiceBuilderOperation( IDataModel model, TemplateStore templateStore, TemplateContextType contextType )
    {
        super( model, templateStore, contextType );
    }

    public IStatus execute( IProgressMonitor monitor, IAdaptable info ) throws ExecutionException
    {
        IStatus retval = null;

        IStatus status = createServiceBuilderFile( getTargetProject() );

        if( !status.isOK() )
        {
            return status;
        }

        return retval;
    }

    private IStatus createServiceBuilderFile( IProject project )
    {
        IFolder docroot = CoreUtil.getDocroot( project );

        IFile serviceBuilderFile = docroot.getFile( "WEB-INF/" + getDataModel().getStringProperty( SERVICE_FILE ) );

        if( !serviceBuilderFile.exists() )
        {
            try
            {
                createDefaultServiceBuilderFile( serviceBuilderFile );
            }
            catch( Exception ex )
            {
                return PortletCore.createErrorStatus( ex );
            }
        }

        return Status.OK_STATUS;
    }

    protected void createDefaultServiceBuilderFile( IFile serviceBuilderFile ) throws UnsupportedEncodingException,
        CoreException, BadLocationException, TemplateException
    {
        Template template = null;

        try
        {
            ILiferayRuntime liferayRuntime = ServerUtil.getLiferayRuntime( getTargetProject() );
            Version portalVersion = new Version( liferayRuntime.getPortalVersion() );

            if( CoreUtil.compareVersions( portalVersion, new Version( 6, 1, 0 ) ) >= 0 )
            {
                if( getDataModel().getBooleanProperty( USE_SAMPLE_TEMPLATE ) )
                {
                    template = getTemplateStore().findTemplateById( SAMPLE_SERVICE_61_FILE_TEMPLATE );
                }
                else
                {
                    template = getTemplateStore().findTemplateById( SERVICE_61_FILE_TEMPLATE );
                }
            }
        }
        catch( Exception e )
        {
        }

        if( template == null )
        {
            if( getDataModel().getBooleanProperty( USE_SAMPLE_TEMPLATE ) )
            {
                template = getTemplateStore().findTemplateById( SAMPLE_SERVICE_FILE_TEMPLATE );
            }
            else
            {
                template = getTemplateStore().findTemplateById( SERVICE_FILE_TEMPLATE );
            }
        }

        IDocument document = new Document();

        TemplateContext context = new DocumentTemplateContext( getContextType(), document, 0, 0 );
        context.setVariable( "package_path", getDataModel().getStringProperty( PACKAGE_PATH ) );
        context.setVariable( "namespace", getDataModel().getStringProperty( NAMESPACE ) );
        context.setVariable( "author", getDataModel().getStringProperty( AUTHOR ) );

        String templateString = null;

        TemplateBuffer buffer = context.evaluate( template );

        templateString = buffer.getString();

        CoreUtil.prepareFolder( (IFolder) serviceBuilderFile.getParent() );

        serviceBuilderFile.create(
            new ByteArrayInputStream( templateString.getBytes( "UTF-8" ) ), IResource.FORCE, null );

        FormatProcessorXML processor = new FormatProcessorXML();

        try
        {
            processor.formatFile( serviceBuilderFile );
        }
        catch( IOException e )
        {
            PortletCore.logError( e );
        }

        getDataModel().setProperty( CREATED_SERVICE_FILE, serviceBuilderFile );
    }

}
