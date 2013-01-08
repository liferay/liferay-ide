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

package com.liferay.ide.ui.util;

import com.liferay.ide.ui.LiferayPerspectiveFactory;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.MessageDialogWithToggle;
import org.eclipse.jface.preference.IPersistentPreferenceStore;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IPerspectiveRegistry;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPreferenceConstants;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.osgi.framework.Bundle;

/**
 * @author Greg Amerson
 * @author Cindy Li
 */
public class UIUtil
{

    public static void async( Runnable runnable )
    {
        if( runnable != null )
        {
            try
            {
                Display.getDefault().asyncExec( runnable );
            }
            catch( Throwable t )
            {
                // ignore
            }
        }
    }

    @SuppressWarnings( "restriction" )
    private static boolean confirmPerspectiveSwitch( IWorkbenchWindow window, IPerspectiveDescriptor finalPersp )
    {
        IPreferenceStore store = IDEWorkbenchPlugin.getDefault().getPreferenceStore();

        String pspm = store.getString( IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE );

        if( !IDEInternalPreferences.PSPM_PROMPT.equals( pspm ) )
        {
            // Return whether or not we should always switch
            return IDEInternalPreferences.PSPM_ALWAYS.equals( pspm );
        }

        String desc = finalPersp.getDescription();
        String message;
 
        if( desc == null || desc.length() == 0 ) 
        {
            message = NLS.bind( ResourceMessages.NewProject_perspSwitchMessage, finalPersp.getLabel() );
        }
        else
        {
            message =
                NLS.bind( ResourceMessages.NewProject_perspSwitchMessageWithDesc, new String[] { finalPersp.getLabel(),
                    desc } );
        }

        MessageDialogWithToggle dialog =
            MessageDialogWithToggle.openYesNoQuestion(
                window.getShell(), ResourceMessages.NewProject_perspSwitchTitle, message, null, false, store,
                IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE );

        int result = dialog.getReturnCode();

        // If we are not going to prompt anymore propogate the choice.
        if( dialog.getToggleState() )
        {
            String preferenceValue;

            if( result == IDialogConstants.YES_ID )
            {
                // Doesn't matter if it is replace or new window
                // as we are going to use the open perspective setting
                preferenceValue = IWorkbenchPreferenceConstants.OPEN_PERSPECTIVE_REPLACE;
            }
            else
            {
                preferenceValue = IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE;
            }

            // update PROJECT_OPEN_NEW_PERSPECTIVE to correspond
            PrefUtil.getAPIPreferenceStore().setValue( IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE, preferenceValue );
        }

        return result == IDialogConstants.YES_ID;
    }

    public static Shell getActiveShell()
    {
        final Shell[] retval = new Shell[1];

        Display.getDefault().syncExec( new Runnable()
        {

            public void run()
            {
                retval[0] = Display.getDefault().getActiveShell();
            }
        } );

        return retval[0];
    }

    public static ImageDescriptor getPluginImageDescriptor( String symbolicName, String imagePath )
    {
        Bundle bundle = Platform.getBundle( symbolicName );

        if( bundle != null )
        {
            URL entry = bundle.getEntry( imagePath );

            if( entry != null )
            {
                return ImageDescriptor.createFromURL( entry );
            }
        }

        return null;
    }

    public static void postInfo( final String title, final String msg )
    {
        Display.getDefault().asyncExec( new Runnable()
        {

            public void run()
            {
                MessageDialog.openInformation( Display.getDefault().getActiveShell(), title, msg );
            }

        } );
    }

    public static void postInfoWithToggle(
        final String title, final String msg, final String toggleMessage, final boolean toggleState,
        final IPersistentPreferenceStore store, final String key )
    {

        if( store == null || key == null || store.getString( key ).equals( MessageDialogWithToggle.NEVER ) )
        {
            return;
        }

        Display.getDefault().asyncExec( new Runnable()
        {

            public void run()
            {
                MessageDialogWithToggle dialog =
                    MessageDialogWithToggle.openInformation(
                        Display.getDefault().getActiveShell(), title, msg, toggleMessage, toggleState, store, key );

                try
                {
                    if( dialog.getToggleState() )
                    {
                        store.setValue( key, MessageDialogWithToggle.NEVER );
                        store.save();
                    }
                }
                catch( IOException e )
                {
                }
            }

        } );
    }

    public static boolean promptQuestion( final String title, final String message )
    {
        final boolean[] retval = new boolean[1];

        Display.getDefault().syncExec( new Runnable()
        {

            public void run()
            {
                retval[0] = MessageDialog.openQuestion( getActiveShell(), title, message );
            }
        } );

        return retval[0];
    }

    private static void replaceCurrentPerspective( IPerspectiveDescriptor persp )
    {
        // Get the active page.
        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if( window != null )
        {
            IWorkbenchPage page = window.getActivePage();

            if( page != null )
            {
                // Set the perspective.
                page.setPerspective( persp );
            }
        }
    }

    public static IViewPart showView( String viewId )
    {
        try
        {
            IViewPart view = PlatformUI.getWorkbench().getWorkbenchWindows()[0].getActivePage().showView( viewId );
            return view;
        }
        catch( PartInitException e )
        {
            LiferayUIPlugin.logError( e );
        }

        return null;
    }

    @SuppressWarnings( "restriction" )
    public static void switchToLiferayPerspective()
    {
        // Retrieve the new project open perspective preference setting
        String perspSetting = PrefUtil.getAPIPreferenceStore().getString( IDE.Preferences.PROJECT_OPEN_NEW_PERSPECTIVE );

        String promptSetting =
            IDEWorkbenchPlugin.getDefault().getPreferenceStore().getString(
                IDEInternalPreferences.PROJECT_SWITCH_PERSP_MODE );

        // Return if do not switch perspective setting and are not prompting
        if( !( promptSetting.equals( MessageDialogWithToggle.PROMPT ) ) &&
            perspSetting.equals( IWorkbenchPreferenceConstants.NO_NEW_PERSPECTIVE ) )
        {
            return;
        }

        // Map perspective id to descriptor.
        IPerspectiveRegistry reg = PlatformUI.getWorkbench().getPerspectiveRegistry();

        IPerspectiveDescriptor finalPersp = reg.findPerspectiveWithId( LiferayPerspectiveFactory.ID );

        IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

        if( window != null )
        {
            IWorkbenchPage page = window.getActivePage();

            if( page != null )
            {
                IPerspectiveDescriptor currentPersp = page.getPerspective();

                // don't switch if the current perspective is the Liferay perspective
                if( finalPersp.equals( currentPersp ) )
                {
                    return;
                }
            }

            // prompt the user to switch
            if( !confirmPerspectiveSwitch( window, finalPersp ) )
            {
                return;
            }
        }

        // replace active perspective setting otherwise
        replaceCurrentPerspective( finalPersp );
    }

    public static void sync( Runnable runnable )
    {
        if( runnable != null )
        {
            try
            {
                Display.getDefault().syncExec( runnable );
            }
            catch( Throwable t )
            {
                // ignore
            }
        }
    }
}
