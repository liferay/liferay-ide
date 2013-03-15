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

package com.liferay.ide.portlet.jsf.ui.wizard;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.portlet.core.dd.PortletDescriptorHelper;
import com.liferay.ide.portlet.jsf.core.JSFCorePlugin;
import com.liferay.ide.portlet.jsf.core.JSFPortletUtil;
import com.liferay.ide.portlet.jsf.core.operation.INewJSFPortletClassDataModelProperties;
import com.liferay.ide.portlet.jsf.core.operation.JSFPortletDescriptorHelper;
import com.liferay.ide.portlet.ui.wizard.AddPortletOperation;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.ByteArrayInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.templates.DocumentTemplateContext;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class AddJSFPortletOperation extends AddPortletOperation implements INewJSFPortletClassDataModelProperties
{

    public AddJSFPortletOperation( IDataModel dataModel, TemplateStore store, TemplateContextType type )
    {
        super( dataModel, store, type );
    }

    @Override
    protected String createClass()
    {
        // do nothing we aren't creating a new class but instead create or append i18n.properties in the source folder
        String sourceFolderValue = getDataModel().getStringProperty( SOURCE_FOLDER );

        IFolder sourceFolder = ResourcesPlugin.getWorkspace().getRoot().getFolder( new Path( sourceFolderValue ) );

        IFile i18nPropertiesFile = sourceFolder.getFile( "i18n.properties" ); //$NON-NLS-1$

        String outputToAppend =
            getDataModel().getStringProperty( PORTLET_NAME ) + "-hello-world=Hello " + //$NON-NLS-1$
                getDataModel().getStringProperty( DISPLAY_NAME );

        try
        {
            if( i18nPropertiesFile.exists() )
            {
                String propsContents = FileUtil.readContents( i18nPropertiesFile.getContents() );

                String newContents = propsContents + "\n" + outputToAppend; //$NON-NLS-1$

                i18nPropertiesFile.setContents(
                    new ByteArrayInputStream( newContents.getBytes( "UTF-8" ) ), IResource.FORCE, null ); //$NON-NLS-1$
            }
            else
            {
                i18nPropertiesFile.create( new ByteArrayInputStream( outputToAppend.getBytes( "UTF-8" ) ), true, null ); //$NON-NLS-1$
            }
        }
        catch( Exception e )
        {
            JSFCorePlugin.logError( "Could not append to i18n.properties file.", e ); //$NON-NLS-1$
        }

        return null;
    }

    @Override
    protected IStatus createModeJSPFiles()
    {
        IDataModel dm = getDataModel();

        TemplateContext context = new DocumentTemplateContext( portletContextType, new Document(), 0, 0 );
        context.setVariable( "portlet_name", getDataModel().getStringProperty( PORTLET_NAME ) ); //$NON-NLS-1$

        if( dm.getBooleanProperty( VIEW_MODE ) )
        {
            createResourceForMode( "javax.portlet.faces.defaultViewId.view", JSF_VIEW_MODE_TEMPLATE, context ); //$NON-NLS-1$
        }

        if( dm.getBooleanProperty( EDIT_MODE ) )
        {
            createResourceForMode( "javax.portlet.faces.defaultViewId.edit", JSF_EDIT_MODE_TEMPLATE, context ); //$NON-NLS-1$
        }

        if( dm.getBooleanProperty( HELP_MODE ) )
        {
            createResourceForMode( "javax.portlet.faces.defaultViewId.help", JSF_HELP_MODE_TEMPLATE, context ); //$NON-NLS-1$
        }

        return Status.OK_STATUS;
    }

    @Override
    protected PortletDescriptorHelper createPortletDescriptorHelper( IProject targetProject )
    {
        return new JSFPortletDescriptorHelper( targetProject );
    }

    @Override
    protected boolean shouldGenerateMetaData( IDataModel aModel )
    {
        return ProjectUtil.isPortletProject( getTargetProject() ) && JSFPortletUtil.isJSFProject( getTargetProject() );
    }

}
