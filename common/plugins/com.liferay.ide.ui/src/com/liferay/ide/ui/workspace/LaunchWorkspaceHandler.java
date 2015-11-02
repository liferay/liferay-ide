package com.liferay.ide.ui.workspace;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.internal.ide.ChooseWorkspaceData;
import org.eclipse.ui.internal.ide.ChooseWorkspaceDialog;


/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class LaunchWorkspaceHandler extends AbstractHandler
{

    static final String COMMAND_ID = "com.liferay.ide.ui.workspace.launchWorkspace";
    static final String PARAM_WORKSPACE_LOCATION = "workspaceLocation";
    static final String PARAM_LIFERAY_7_SDK_DIR = "liferay7SDKDir";

    @Override
    public Object execute( ExecutionEvent event ) throws ExecutionException
    {
        final String liferay7SDKDir = event.getParameter( PARAM_LIFERAY_7_SDK_DIR );

        String workspaceLocation = event.getParameter( PARAM_WORKSPACE_LOCATION );

        if( workspaceLocation == null )
        {
            final ChooseWorkspaceData chooseWorkspaceData =
                new ChooseWorkspaceData( Platform.getInstallLocation().getURL() );

            final ChooseWorkspaceDialog chooseWorkspaceDialog =
                new ChooseWorkspaceDialog( Display.getDefault().getActiveShell(), chooseWorkspaceData, true, true );

            if( chooseWorkspaceDialog.open() == IDialogConstants.OK_ID )
            {
                workspaceLocation = chooseWorkspaceData.getSelection();
            }
            else
            {
                return null;
            }
        }

        final List<String> commands = new ArrayList<>();

        final File launchDir = new File( Platform.getInstallLocation().getURL().getFile() );

        final File launcher;
        {
            if( launchDir.exists() )
            {
                switch( Platform.getOS() )
                {
                    case Platform.OS_MACOSX:
                        launcher = launchDir.getParentFile().getParentFile();
                        break;
                    default:
                        launcher = new File( launchDir, System.getProperty( "eclipse.launcher" ) );
                }
            }
            else
            {
                launcher = null;
            }
        }

        if( launcher != null && launcher.exists() )
        {
            switch( Platform.getOS() )
            {
                case Platform.OS_MACOSX:
                    commands.add( "open" );
                    commands.add( "-n" );
                    commands.add( launcher.getAbsolutePath() );
                    commands.add( "--args" );
                    break;

                case Platform.OS_LINUX:
                    commands.add( "/bin/bash" );
                    commands.add( "-c" );
                    commands.add( "./" + launcher.getName() );
                    break;

                case Platform.OS_WIN32:
                    commands.add( "cmd" );
                    commands.add( "/c" );
                    commands.add( launcher.getName() );
                    break;
            }

            commands.add("-data");
            commands.add( workspaceLocation );

            if( liferay7SDKDir != null )
            {
                commands.add("-vmargs");
                commands.add("-Dliferay7.sdk.dir=\"" + liferay7SDKDir + "\"");
            }
        }
        else
        {
            throw new ExecutionException( "Unable to find Eclipse launcher." );
        }

        if( launchDir.isDirectory() )
        {
            final ProcessBuilder processBuilder = new ProcessBuilder( commands );
            processBuilder.directory( launchDir );
            processBuilder.environment().putAll( System.getenv() );

            try
            {
                processBuilder.start();
            }
            catch( IOException e )
            {
                throw new ExecutionException( "Unable to start Eclipse process.", e );
            }
        }

        return null;
    }

}
