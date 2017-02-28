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

package com.liferay.ide.project.core.modules.fragment;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Joye Luo
 */
@SuppressWarnings( "restriction" )
public class ModuleFragmentProjectGroupIdValidationService extends ValidationService
{

    private Listener listener;

    @Override
    protected Status compute()
    {
        if( "maven-module-fragment".equals( op().getProjectProvider().content( true ).getShortName() ) )
        {
            final String groupId = op().getGroupId().content( true );

            final IStatus javaStatus = JavaConventions.validatePackageName(
                groupId, CompilerOptions.VERSION_1_7, CompilerOptions.VERSION_1_7 );

            if( !javaStatus.isOK() )
            {
                return StatusBridge.create( javaStatus );
            }
        }

        return Status.createOkStatus();
    }

    @Override
    public void dispose()
    {
        if( this.listener != null && op() != null && !op().disposed() )
        {
            op().getProjectName().detach( this.listener );
            op().getLocation().attach( this.listener );

            this.listener = null;
        }

        super.dispose();
    }

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.listener = new FilteredListener<PropertyContentEvent>()
        {

            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        op().getProjectProvider().attach( this.listener );
        op().getLocation().attach( this.listener );
    }

    private NewModuleFragmentOp op()
    {
        return context( NewModuleFragmentOp.class );
    }

}
