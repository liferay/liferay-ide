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
 *******************************************************************************/

package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.sdk.pref.SDKsPreferencePage;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.PreferencesUtil;

/**
 * @author Greg Amerson
 */
public class NewPluginsSDKAction extends Action
{
    protected Shell shell;

    public NewPluginsSDKAction( Shell shell )
    {
        super( Msgs.newLiferaySDK, ImageDescriptor.createFromURL( ProjectUIPlugin.getDefault().getBundle().getEntry(
            "/icons/n16/sdk_new.png" ) ) ); //$NON-NLS-1$
        this.shell = shell;
    }

    @Override
    public void run()
    {
        PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn( shell, SDKsPreferencePage.ID, null, "new" ); //$NON-NLS-1$

        dialog.open();
    }

    private static class Msgs extends NLS
    {
        public static String newLiferaySDK;

        static
        {
            initializeMessages( NewPluginsSDKAction.class.getName(), Msgs.class );
        }
    }
}
