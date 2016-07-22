/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.server.ui.util;

import com.liferay.ide.core.util.CoreUtil;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.util.Util;
import org.eclipse.ui.internal.ide.IDEInternalPreferences;
import org.eclipse.ui.internal.ide.IDEWorkbenchPlugin;

/**
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class ServerUIUtil
{

    private static final String VARIABLE_FOLDER = "${selected_resource_parent_loc}";
    private static final String VARIABLE_RESOURCE = "${selected_resource_loc}";
    private static final String VARIABLE_RESOURCE_URI = "${selected_resource_uri}";

    public static String getSystemExplorerCommand( File file ) throws IOException
    {
        String retval = null;

        String command = IDEWorkbenchPlugin.getDefault().getPreferenceStore().getString(
            IDEInternalPreferences.WORKBENCH_SYSTEM_EXPLORER );

        if( !CoreUtil.isNullOrEmpty( command ) )
        {
            command = Util.replaceAll( command, VARIABLE_RESOURCE, quotePath( file.getCanonicalPath() ) );
            command = Util.replaceAll( command, VARIABLE_RESOURCE_URI, file.getCanonicalFile().toURI().toString() );

            final File parent = file.getParentFile();

            if( parent != null )
            {
                retval = Util.replaceAll( command, VARIABLE_FOLDER, quotePath( parent.getCanonicalPath() ) );
            }
        }

        return retval;
    }

    public static void openFileInSystemExplorer( IPath path ) throws IOException
    {
        final String launchCmd = ServerUIUtil.getSystemExplorerCommand( path.toFile() );

        if( !CoreUtil.isNullOrEmpty( launchCmd ) )
        {
            if( path.toFile().isFile() )
            {
                path = path.removeLastSegments( 1 );
            }

            ServerUIUtil.openInSystemExplorer( launchCmd, path.toFile() );
        }
    }

    public static void openInSystemExplorer( String systemCommand, File file ) throws IOException
    {
        if( Util.isLinux() || Util.isMac() )
        {
            Runtime.getRuntime().exec( new String[] { "/bin/sh", "-c", systemCommand }, null, file );
        }
        else
        {
            Runtime.getRuntime().exec( systemCommand, null, file );
        }
    }

    public static String quotePath( String path )
    {
        if( CoreUtil.isLinux() || CoreUtil.isMac() )
        {
            path = path.replaceAll( "[\"$`]", "\\\\$0" );
        }

        return path;
    }

}
