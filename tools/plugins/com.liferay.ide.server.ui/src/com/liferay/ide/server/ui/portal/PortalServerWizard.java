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
/**
 *
 */
package com.liferay.ide.server.ui.portal;

import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.util.ServerUtil;

import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.WizardFragment;


/**
 * @author Gregory Amerson
 */
public class PortalServerWizard extends WizardFragment
{

    public PortalServerWizard()
    {
        super();
    }

    public void exit()
    {
        IServerWorkingCopy serverWorkingCopy = getServerWorkingCopy();
        IRuntime runtime = serverWorkingCopy.getRuntime();
        IServer[] servers = ServerUtil.getServersForRuntime( runtime );

        if( servers.length > 1 )
        {
            LiferayServerUI.logWarning(
                "The runtime selected already has server(s), you shouldn't make multiple servers point to the same runtime if you want to launch multiple servers in IDE at one time" );
        }
    }

    protected IServerWorkingCopy getServerWorkingCopy()
    {
        return (IServerWorkingCopy) getTaskModel().getObject( TaskModel.TASK_SERVER );
    }
}
