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
package com.liferay.ide.ui.notifications;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.runtime.preferences.ConfigurationScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.workbench.forms.ScalingHyperlink;
import org.eclipse.mylyn.internal.commons.notifications.ui.popup.NotificationPopup;
import org.eclipse.mylyn.internal.commons.notifications.ui.popup.PopupNotificationSink;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IHyperlinkListener;
import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class Java8requiredSink extends PopupNotificationSink
{
    private NotificationPopup popup;

    public Java8requiredSink()
    {
        super();
    }

    @Override
    public void showPopup()
    {
        if( popup != null )
        {
            popup.close();
        }

        Shell shell = new Shell( PlatformUI.getWorkbench().getDisplay() );

        popup = new NotificationPopup( shell )
        {
            @Override
            protected String getPopupShellTitle()
            {
                return "Liferay IDE Notification";
            }

            @Override
            protected void createContentArea( Composite parent )
            {
                super.createContentArea( parent );

                Composite composite = (Composite)parent;

                ScalingHyperlink hyperlink = new ScalingHyperlink( composite, SWT.NONE );

                hyperlink.setText( "Got it, please don't show this alert again." );

                hyperlink.setForeground(new Color(null, 12, 81, 172));

                hyperlink.registerMouseTrackListener();

                hyperlink.addHyperlinkListener( new IHyperlinkListener()
                {
                    @Override
                    public void linkExited( HyperlinkEvent e )
                    {
                    }

                    @Override
                    public void linkEntered( HyperlinkEvent e )
                    {
                    }

                    @Override
                    public void linkActivated( HyperlinkEvent e )
                    {
                        preventShowNotifications();

                        if( popup != null )
                        {
                            popup.close();
                        }
                    }

                    private void preventShowNotifications()
                    {
                        try
                        {
                            IEclipsePreferences prefs = ConfigurationScope.INSTANCE.getNode( NotificationsCore.PLUGIN_ID );
                            prefs.putBoolean( NotificationsCore.SHOULD_SHOW_NOTIFICATIONS, false );
                            prefs.flush();
                        }
                        catch( BackingStoreException e )
                        {
                            NotificationsCore.logError( e );
                        }
                    }
                } );
            }
        };

        popup.setFadingEnabled( isAnimationsEnabled() );
        List<AbstractNotification> toDisplay = new ArrayList<AbstractNotification>( getNotifications() );
        Collections.sort( toDisplay );
        popup.setContents( toDisplay );
        getNotifications().clear();
        popup.setBlockOnOpen( false );
        popup.setDelayClose( 60 * 1000 );
        popup.open();
    }

}
