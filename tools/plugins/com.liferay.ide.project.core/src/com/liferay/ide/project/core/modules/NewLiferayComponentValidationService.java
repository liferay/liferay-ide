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

package com.liferay.ide.project.core.modules;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.java.JavaPackageName;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class NewLiferayComponentValidationService extends ValidationService
{

    private FilteredListener<PropertyContentEvent> listener;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).attach( this.listener );
        op().property( NewLiferayComponentOp.PROP_PACKAGE_NAME ).attach( this.listener );
        op().property( NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME ).attach( this.listener );

    }

    @Override
    protected Status compute()
    {
        Status retval = Status.createOkStatus();

        final String className = op().getComponentClassName().content( true );

        if( !CoreUtil.isNullOrEmpty( className ) )
        {
            int classNameStatus = JavaConventions.validateJavaTypeName(
                className, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7 ).getSeverity();;

            if( className.indexOf( '.' ) != -1 )
            {
                classNameStatus = IStatus.ERROR;
            }

            if( classNameStatus == IStatus.ERROR )
            {
                retval = Status.createErrorStatus( "Invalid class name" );
            }
        }

        String projectName = op().getProjectName().content( true );

        if( projectName != null )
        {
            IProject project = CoreUtil.getProject( projectName );

            if( project != null )
            {
                try
                {
                    JavaPackageName pack = op().getPackageName().content( true );

                    if( pack != null )
                    {
                        String packageName = op().getPackageName().content( true ).toString();
                        IType type = JavaCore.create( project ).findType( packageName + "." + className );

                        if( type != null )
                        {
                            retval = Status.createErrorStatus( packageName + "." + className + " already existed." );
                        }
                    }
                }
                catch( Exception e )
                {
                    ProjectCore.logError( "Checking component class name failed.", e );
                }

            }
        }

        return retval;
    }

    @Override
    public void dispose()
    {
        if( this.listener != null )
        {
            op().property( NewLiferayComponentOp.PROP_PROJECT_NAME ).detach( this.listener );
            op().property( NewLiferayComponentOp.PROP_PACKAGE_NAME ).detach( this.listener );
            op().property( NewLiferayComponentOp.PROP_COMPONENT_CLASS_TEMPLATE_NAME ).detach( this.listener );

            this.listener = null;
        }
        super.dispose();
    }

    private NewLiferayComponentOp op()
    {
        return context( NewLiferayComponentOp.class );
    }
}
