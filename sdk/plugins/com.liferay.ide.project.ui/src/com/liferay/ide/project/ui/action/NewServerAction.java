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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.ProjectUIPlugin;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.wst.server.ui.ServerUIUtil;

/**
 * @author Gregory Amerson
 */
public class NewServerAction extends Action
{
    private Shell shell;

    public NewServerAction( Shell shell )
    {
        super( "New Liferay Server", ImageDescriptor.createFromURL( ProjectUIPlugin.getDefault().getBundle().getEntry(
            "/icons/n16/server_new.png" ) ) );
        this.shell = shell;
    }

    @Override
    public void run()
    {
        ServerUIUtil.showNewServerWizard( shell, null, null, "com.liferay." );
    }

}
