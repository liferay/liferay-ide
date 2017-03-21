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

package com.liferay.ide.kaleo.ui.action;

import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;

import java.util.Iterator;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.actions.SelectionProviderAction;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractWorkflowDefinitionAction extends SelectionProviderAction
{

    protected Shell shell;

    public AbstractWorkflowDefinitionAction( ISelectionProvider selectionProvider, String text )
    {
        this( null, selectionProvider, text );
    }

    public AbstractWorkflowDefinitionAction( Shell shell, ISelectionProvider selectionProvider, String text )
    {
        super( selectionProvider, text );
        this.shell = shell;
        setEnabled( false );
    }

    public boolean accept( Object node )
    {
        return node instanceof WorkflowDefinitionEntry || node instanceof WorkflowDefinitionsFolder;
    }

    public Shell getShell()
    {
        return this.shell;
    }

    public abstract void perform( Object node );

    @SuppressWarnings( "rawtypes" )
    public void run()
    {
        Iterator iterator = getStructuredSelection().iterator();

        if( !iterator.hasNext() )
            return;

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
            if( obj instanceof WorkflowDefinitionEntry )
            {
                WorkflowDefinitionEntry node = (WorkflowDefinitionEntry) obj;
                if( accept( node ) )
                {
                    enabled = true;
                }
            }
            else if( obj instanceof WorkflowDefinitionsFolder )
            {
                WorkflowDefinitionsFolder node = (WorkflowDefinitionsFolder) obj;
                if( accept( node ) )
                {
                    enabled = true;
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
