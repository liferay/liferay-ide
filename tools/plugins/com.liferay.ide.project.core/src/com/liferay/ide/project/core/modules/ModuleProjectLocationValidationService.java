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

import com.liferay.ide.project.core.NewLiferayProjectProvider;

import org.eclipse.sapphire.Listener;

/**
 * @author Simon Jiang
 */
public class ModuleProjectLocationValidationService
    extends AbstractProjectLocationValidationService<NewLiferayModuleProjectOp>
{

    @Override
    protected NewLiferayProjectProvider<NewLiferayModuleProjectOp> getProjectProvider()
    {
        return op().getProjectProvider().content();
    }

    @Override
    protected void attachListener( final Listener listener )
    {
        op().getProjectName().attach( listener );
        op().getProjectProvider().attach( listener );
    }

    @Override
    protected NewLiferayModuleProjectOp op()
    {
        return context( NewLiferayModuleProjectOp.class );
    }

}
