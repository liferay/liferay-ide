/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.ProjectUIPlugin;

import java.net.URL;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;


/**
 * @author Kuo Zhang
 */
public abstract class LinkToPluginDocAction extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        final IWorkbenchBrowserSupport support = PlatformUI.getWorkbench().getBrowserSupport();

        try
        {
            support.getExternalBrowser().openURL( new URL( getDocURL() ) );

        }
        catch( Exception ex )
        {
            ProjectUIPlugin.logError( "Could not open external browser.", ex );
        }

        return null;
    }

    protected abstract String getDocURL();

}
