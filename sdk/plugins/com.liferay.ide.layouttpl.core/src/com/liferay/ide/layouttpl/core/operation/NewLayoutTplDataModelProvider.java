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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.operation;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.layouttpl.core.LayoutTplCore;

import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.jface.text.templates.TemplateContextType;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.wst.common.componentcore.internal.operation.ArtifactEditOperationDataModelProvider;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( { "restriction", "unchecked", "rawtypes" } )
public class NewLayoutTplDataModelProvider extends ArtifactEditOperationDataModelProvider
    implements INewLayoutTplDataModelProperties
{

    protected TemplateContextType contextType;

    protected boolean ignoreLayoutOptionPropertySet = false;

    protected TemplateStore templateStore;

    public NewLayoutTplDataModelProvider( TemplateStore templateStore, TemplateContextType contextType )
    {
        super();

        this.templateStore = templateStore;
        this.contextType = contextType;
    }

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( LAYOUT_TEMPLATE_NAME.equals( propertyName ) )
        {
            return "New Template";
        }
        else if( LAYOUT_TEMPLATE_ID.equals( propertyName ) )
        {
            String name = getStringProperty( LAYOUT_TEMPLATE_NAME );

            if( !CoreUtil.isNullOrEmpty( name ) )
            {
                return name.replaceAll( "[^a-zA-Z0-9]+", "" ).toLowerCase();
            }
        }
        else if( LAYOUT_TEMPLATE_FILE.equals( propertyName ) )
        {
            return "/" + getStringProperty( LAYOUT_TEMPLATE_ID ) + ".tpl";
        }
        else if( LAYOUT_WAP_TEMPLATE_FILE.equals( propertyName ) )
        {
            return "/" + getStringProperty( LAYOUT_TEMPLATE_ID ) + ".wap.tpl";
        }
        else if( LAYOUT_THUMBNAIL_FILE.equals( propertyName ) )
        {
            return "/" + getStringProperty( LAYOUT_TEMPLATE_ID ) + ".png";
        }
        else if( LAYOUT_IMAGE_BLANK_COLUMN.equals( propertyName ) )
        {
            return true;
        }
        else if( LAYOUT_IMAGE_1_COLUMN.equals( propertyName ) || LAYOUT_IMAGE_1_2_1_COLUMN.equals( propertyName ) ||
            LAYOUT_IMAGE_1_2_I_COLUMN.equals( propertyName ) || LAYOUT_IMAGE_1_2_II_COLUMN.equals( propertyName ) ||
            LAYOUT_IMAGE_2_2_COLUMN.equals( propertyName ) || LAYOUT_IMAGE_2_I_COLUMN.equals( propertyName ) ||
            LAYOUT_IMAGE_2_II_COLUMN.equals( propertyName ) || LAYOUT_IMAGE_2_III_COLUMN.equals( propertyName ) ||
            LAYOUT_IMAGE_3_COLUMN.equals( propertyName ) )
        {

            return false;
        }

        return super.getDefaultProperty( propertyName );
    }

    @Override
    public Set getPropertyNames()
    {
        Set propertyNames = super.getPropertyNames();

        propertyNames.add( LAYOUT_TEMPLATE_NAME );
        propertyNames.add( LAYOUT_TEMPLATE_ID );
        propertyNames.add( LAYOUT_TEMPLATE_FILE );
        propertyNames.add( LAYOUT_WAP_TEMPLATE_FILE );
        propertyNames.add( LAYOUT_THUMBNAIL_FILE );

        propertyNames.add( LAYOUT_IMAGE_1_2_1_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_1_2_I_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_1_2_II_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_1_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_2_2_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_2_I_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_2_II_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_2_III_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_3_COLUMN );
        propertyNames.add( LAYOUT_IMAGE_BLANK_COLUMN );

        propertyNames.add( LAYOUT_TPL_FILE_CREATED );

        return propertyNames;
    }

    @Override
    public boolean propertySet( String propertyName, Object propertyValue )
    {
        boolean isLayoutOption = false;

        for( int i = 0; i < LAYOUT_PROPERTIES.length; i++ )
        {
            if( LAYOUT_PROPERTIES[i].equals( propertyName ) )
            {
                isLayoutOption = true;
                break;
            }
        }

        if( isLayoutOption && !ignoreLayoutOptionPropertySet )
        {
            ignoreLayoutOptionPropertySet = true;

            for( int i = 0; i < LAYOUT_PROPERTIES.length; i++ )
            {
                setBooleanProperty( LAYOUT_PROPERTIES[i], false );
            }

            setProperty( propertyName, propertyValue );

            ignoreLayoutOptionPropertySet = false;

        }

        return super.propertySet( propertyName, propertyValue );
    }

    @Override
    public IStatus validate( String propertyName )
    {
        if( LAYOUT_TEMPLATE_ID.equals( propertyName ) )
        {
            // first check to see if an existing property exists.
            LayoutTplDescriptorHelper helper = new LayoutTplDescriptorHelper( getTargetProject() );

            if( helper.hasTemplateId( getStringProperty( propertyName ) ) )
            {
                return LayoutTplCore.createErrorStatus( "Template id already exists in project." );
            }

            // to avoid marking text like "this" as bad add a z to the end of the string
            String idValue = getStringProperty( propertyName ) + "z";

            if( CoreUtil.isNullOrEmpty( idValue ) )
            {
                return super.validate( propertyName );
            }

            IStatus status =
                JavaConventions.validateFieldName( idValue, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 );

            if( !status.isOK() )
            {
                return LayoutTplCore.createErrorStatus( "Template id is invalid." );
            }
        }
        else if( LAYOUT_TEMPLATE_FILE.equals( propertyName ) )
        {
            IProject targetProject = getTargetProject();

            if( targetProject != null )
            {
                IFile templateFile =
                    CoreUtil.getDocroot( targetProject ).getFile( getStringProperty( LAYOUT_TEMPLATE_FILE ) );

                if( templateFile.exists() )
                {
                    return LayoutTplCore.createWarningStatus( "Template file already exists and will be overwritten." );
                }
            }
        }
        else if( LAYOUT_WAP_TEMPLATE_FILE.equals( propertyName ) )
        {
            IProject targetProject = getTargetProject();

            if( targetProject != null )
            {
                IFile wapTemplateFile =
                    CoreUtil.getDocroot( targetProject ).getFile( getStringProperty( LAYOUT_WAP_TEMPLATE_FILE ) );

                if( wapTemplateFile.exists() )
                {
                    return LayoutTplCore.createWarningStatus( "WAP template file already exists and will be overwritten." );
                }
            }
        }
        else if( LAYOUT_THUMBNAIL_FILE.equals( propertyName ) )
        {
            IProject targetProject = getTargetProject();

            if( targetProject != null )
            {
                IFile thumbnailFile =
                    CoreUtil.getDocroot( targetProject ).getFile( getStringProperty( LAYOUT_THUMBNAIL_FILE ) );

                if( thumbnailFile.exists() )
                {
                    return LayoutTplCore.createWarningStatus( "Thumbnail file already exists and will be overwritten." );
                }
            }
        }

        return super.validate( propertyName );
    }

    protected IStatus validateListItems( String propertyName )
    {
        Object items = getProperty( propertyName );

        if( items instanceof List )
        {
            List itemsList = (List) items;

            if( itemsList.size() > 0 )
            {
                return Status.OK_STATUS;
            }
        }

        return LayoutTplCore.createErrorStatus( "Need to specify at least one item." );
    }
}
