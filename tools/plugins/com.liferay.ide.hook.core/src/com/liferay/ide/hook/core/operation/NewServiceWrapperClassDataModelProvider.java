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

package com.liferay.ide.hook.core.operation;

import com.liferay.ide.core.util.StringPool;

import org.eclipse.jst.j2ee.internal.common.operations.INewJavaClassDataModelProperties;
import org.eclipse.jst.j2ee.internal.common.operations.NewJavaClassDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

/**
 * @author Greg Amerson
 */
@SuppressWarnings( "restriction" )
public class NewServiceWrapperClassDataModelProvider extends NewJavaClassDataModelProvider
    implements INewJavaClassDataModelProperties
{

    protected IDataModel hookModel;
    protected String qualifiedClassname;
    protected String qualifiedSuperclassname;

    public NewServiceWrapperClassDataModelProvider(
        IDataModel model, String qualifiedClassname, String qualifiedSuperclassname )
    {
        this.hookModel = model;
        this.qualifiedClassname = qualifiedClassname;
        this.qualifiedSuperclassname = qualifiedSuperclassname;
    }

    @Override
    public Object getDefaultProperty( String propertyName )
    {
        if( SOURCE_FOLDER.equals( propertyName ) )
        {
            return this.hookModel.getProperty( SOURCE_FOLDER );
        }
        else if( JAVA_PACKAGE.equals( propertyName ) )
        {
            int lastDot = this.qualifiedClassname.lastIndexOf( '.' );

            if( lastDot > -1 )
            {
                return this.qualifiedClassname.substring( 0, lastDot );
            }
            else
            {
                return StringPool.EMPTY;
            }
        }
        else if( JAVA_PACKAGE_FRAGMENT_ROOT.equals( propertyName ) )
        {
            return this.hookModel.getProperty( JAVA_PACKAGE_FRAGMENT_ROOT );
        }
        else if( CLASS_NAME.equals( propertyName ) )
        {
            return this.qualifiedClassname.substring(
                this.qualifiedClassname.lastIndexOf( '.' ) + 1, this.qualifiedClassname.length() );
        }
        else if( SUPERCLASS.equals( propertyName ) )
        {
            return this.qualifiedSuperclassname;
        }
        else if( PROJECT_NAME.equals( propertyName ) )
        {
            return this.hookModel.getProperty( PROJECT_NAME );
        }

        return super.getDefaultProperty( propertyName );
    }

}
