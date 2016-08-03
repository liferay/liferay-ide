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

package com.liferay.ide.project.ui.upgrade.action;

import com.liferay.ide.server.util.ServerUtil;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.ui.internal.wizard.ModifyModulesWizard;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class DeployActionHandler extends BaseActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        final String serverName = op( context ).getLiferayServerName().content();

        final IServer server = ServerUtil.getServer( serverName );

        final ModifyModulesWizard wizard = new ModifyModulesWizard( server );

        final WizardDialog dialog = new WizardDialog( UIUtil.getActiveShell(), wizard );

        dialog.open();

        return null;
    }

}
