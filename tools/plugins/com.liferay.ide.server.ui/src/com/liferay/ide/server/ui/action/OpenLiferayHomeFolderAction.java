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

package com.liferay.ide.server.ui.action;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.server.ui.navigator.PropertiesFile;
import com.liferay.ide.server.ui.util.ServerUIUtil;

import java.io.IOException;
import java.util.Iterator;

import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Terry Jia
 */
public class OpenLiferayHomeFolderAction extends SelectionProviderAction
{

    private Shell shell;

    public OpenLiferayHomeFolderAction( ISelectionProvider sp )
    {
        super( sp, "Open Liferay Home Folder" );
    }

    public OpenLiferayHomeFolderAction( ISelectionProvider selectionProvider, String text )
    {
        this( null, selectionProvider, text );
    }

    public OpenLiferayHomeFolderAction( Shell shell, ISelectionProvider selectionProvider, String text )
    {
        super( selectionProvider, text );

        this.shell = shell;

        setEnabled( false );
    }

    public boolean accept( Object node )
    {
        return node instanceof PropertiesFile;
    }

    public Shell getShell()
    {
        return this.shell;
    }

    public void perform( Object entry )
    {
        if( entry instanceof PropertiesFile )
        {
            final PropertiesFile workflowEntry = (PropertiesFile) entry;

            final String path = workflowEntry.getPath();

            try
            {
                ServerUIUtil.openFileInSystemExplorer( new Path( path ) );
            }
            catch( IOException e )
            {
                LiferayServerUI.logError( "Error opening portal home folder.", e );
            }
        }
    }

    @SuppressWarnings( "rawtypes" )
    public void run()
    {
        Iterator iterator = getStructuredSelection().iterator();

        if( !iterator.hasNext() )
        {
            return;
        }

        Object obj = iterator.next();

        if( accept( obj ) )
        {
            perform( obj );
        }

        selectionChanged( getStructuredSelection() );
    }

    /**
     * Update the enabled state.
     *
     * @param sel
     *            a selection
     */
    @SuppressWarnings( "rawtypes" )
    public void selectionChanged( IStructuredSelection sel )
    {
        if( sel.isEmpty() )
        {
            setEnabled( false );

            return;
        }

        boolean enabled = false;
        Iterator iterator = sel.iterator();

        while( iterator.hasNext() )
        {
            Object obj = iterator.next();

            if( obj instanceof PropertiesFile )
            {
                final PropertiesFile node = (PropertiesFile) obj;

                final String path = node.getPath();

                try
                {
                    if( !CoreUtil.isNullOrEmpty( ServerUIUtil.getSystemExplorerCommand( new Path( path ).toFile() ) ) &&
                        accept( node ) )
                    {
                        enabled = true;
                    }
                }
                catch( IOException e )
                {
                }
            }
            else
            {
                setEnabled( false );

                return;
            }
        }

        setEnabled( enabled );
    }

}
