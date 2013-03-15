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

package com.liferay.ide.service.ui.wizard;

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.ui.wizard.LiferayDataModelOperation;
import com.liferay.ide.service.core.ServiceCore;
import com.liferay.ide.service.core.operation.INewServiceBuilderDataModelProperties;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
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
import org.eclipse.wst.common.componentcore.resources.IVirtualFolder;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.xml.core.internal.provisional.format.FormatProcessorXML;
import org.osgi.framework.Version;

/**
 * @author Gregory Amerson
 * @author Cindy Li
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
        // IDE-110 IDE-648
        IVirtualFolder webappRoot = CoreUtil.getDocroot( project );

        if( webappRoot == null )
        {
            return ServiceCore.createErrorStatus( "Could not find webapp root folder." ); //$NON-NLS-1$
        }

        for( IContainer container : webappRoot.getUnderlyingFolders() )
        {
            if( container != null && container.exists() )
            {
                final Path path = new Path( "WEB-INF/" + getDataModel().getStringProperty( SERVICE_FILE ) ); //$NON-NLS-1$
                IFile serviceBuilderFile = container.getFile( path );

                if( !serviceBuilderFile.exists() )
                {
                    try
                    {
                        createDefaultServiceBuilderFile( serviceBuilderFile );
                        break;
                    }
                    catch( Exception ex )
                    {
                        return ServiceCore.createErrorStatus( ex );
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

    protected void createDefaultServiceBuilderFile( IFile serviceBuilderFile ) throws UnsupportedEncodingException,
        CoreException, BadLocationException, TemplateException
    {
        String descriptorVersion = null;

        try
        {
            ILiferayProject liferayProject = LiferayCore.create( getTargetProject() );

            Version portalVersion = new Version( liferayProject.getPortalVersion() );

            descriptorVersion = portalVersion.getMajor() + "." + portalVersion.getMinor() + ".0";  //$NON-NLS-1$//$NON-NLS-2$
        }
        catch( Exception e )
        {
            ServiceCore.logError( "Could not determine liferay runtime version", e ); //$NON-NLS-1$
            descriptorVersion = "6.0.0"; //$NON-NLS-1$
        }

        Template template = null;

        if( getDataModel().getBooleanProperty( USE_SAMPLE_TEMPLATE ) )
        {
            template = getTemplateStore().findTemplateById( SAMPLE_SERVICE_FILE_TEMPLATE );
        }
        else
        {
            template = getTemplateStore().findTemplateById( SERVICE_FILE_TEMPLATE );
        }

        IDocument document = new Document();

        TemplateContext context = new DocumentTemplateContext( getContextType(), document, 0, 0 );
        context.setVariable( "package_path", getDataModel().getStringProperty( PACKAGE_PATH ) ); //$NON-NLS-1$
        context.setVariable( "namespace", getDataModel().getStringProperty( NAMESPACE ) ); //$NON-NLS-1$
        context.setVariable( "author", getDataModel().getStringProperty( AUTHOR ) ); //$NON-NLS-1$

        String templateString = null;

        TemplateBuffer buffer = context.evaluate( template );

        templateString =
            MessageFormat.format( buffer.getString(), descriptorVersion, descriptorVersion.replace( '.', '_' ) );

        CoreUtil.prepareFolder( (IFolder) serviceBuilderFile.getParent() );

        serviceBuilderFile.create(
            new ByteArrayInputStream( templateString.getBytes( "UTF-8" ) ), IResource.FORCE, null ); //$NON-NLS-1$

        FormatProcessorXML processor = new FormatProcessorXML();

        try
        {
            processor.formatFile( serviceBuilderFile );
        }
        catch( IOException e )
        {
            ServiceCore.logError( e );
        }

        getDataModel().setProperty( CREATED_SERVICE_FILE, serviceBuilderFile );
    }

}
