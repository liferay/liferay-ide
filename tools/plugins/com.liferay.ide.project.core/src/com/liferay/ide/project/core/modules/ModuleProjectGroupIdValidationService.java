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

import com.liferay.ide.project.core.model.internal.GroupIdValidationService;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jdt.core.JavaConventions;
import org.eclipse.jdt.internal.compiler.impl.CompilerOptions;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.StatusBridge;

/**
 * @author Simon Jiang
 */
@SuppressWarnings( "restriction" )
public class ModuleProjectGroupIdValidationService extends GroupIdValidationService
{
    private Listener listener;

    @Override
    protected Status compute()
    {
        if( "maven".equals( op().getProjectProvider().content( true ).getShortName() ) )
        {
            final String groupId = op().getGroupId().content( true );

            final IStatus javaStatus =
                JavaConventions.validatePackageName( groupId, CompilerOptions.VERSION_1_5, CompilerOptions.VERSION_1_5 );

            return StatusBridge.create( javaStatus );
        }

        return StatusBridge.create( org.eclipse.core.runtime.Status.OK_STATUS );
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
    }

    private NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }
}
