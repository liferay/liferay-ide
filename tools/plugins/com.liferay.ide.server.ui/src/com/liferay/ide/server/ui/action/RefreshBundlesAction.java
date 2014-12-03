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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.server.core.ILiferayServer;
import com.liferay.ide.server.ui.navigator.BundlesContentProvider;
import com.liferay.ide.server.ui.navigator.NavigatorUIUtil;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.jface.action.IAction;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.wst.server.core.IServer;

/**
 * @author Gregory Amerson
 */
public class RefreshBundlesAction extends AbstractServerRunningAction
{

    public RefreshBundlesAction()
    {
        super();
    }

    protected ILiferayServer getLiferayServer()
    {
        return (ILiferayServer) selectedServer.loadAdapter( ILiferayServer.class, null );
    }

    @Override
    protected int getRequiredServerState()
    {
        return IServer.STATE_STARTED;
    }

    public void run( IAction action )
    {
        if( selectedServer != null )
        {
            final IViewPart view = UIUtil.showView( "org.eclipse.wst.server.ui.ServersView" );

            if( view instanceof CommonNavigator )
            {
                final CommonNavigator nav = (CommonNavigator) view;
                final BundlesContentProvider bundlesProvider = (BundlesContentProvider) nav.getNavigatorContentService().getContentExtensionById( "com.liferay.ide.server.ui.serverview.osgibundles" ).getContentProvider();

                bundlesProvider.aboutToRefresh( selectedServer );

                NavigatorUIUtil.refreshUI( nav.getNavigatorContentService(), selectedServer );
            }
        }
    }

}
