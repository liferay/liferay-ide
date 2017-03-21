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

package com.liferay.ide.kaleo.ui.navigator;

import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.action.EditWorkflowDefinitionAction;
import com.liferay.ide.kaleo.ui.action.PublishWorkflowDefinitionAction;
import com.liferay.ide.kaleo.ui.action.RefreshWorkflowDefinitionsAction;
import com.liferay.ide.kaleo.ui.action.UploadNewWorkflowDefinitionAction;

import java.util.Iterator;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.IOpenListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.OpenEvent;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionsActionProvider extends CommonActionProvider
{

    public static final String NEW_MENU_ID = "org.eclipse.wst.server.ui.internal.cnf.newMenuId";
    public static final String TOP_SECTION_END_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionEnd";
    public static final String TOP_SECTION_START_SEPARATOR = "org.eclipse.wst.server.ui.internal.cnf.topSectionStart";

    private ICommonActionExtensionSite actionSite;
    private EditWorkflowDefinitionAction editAction;
    private PublishWorkflowDefinitionAction publishAction;
    private RefreshWorkflowDefinitionsAction refreshAction;
    private UploadNewWorkflowDefinitionAction uploadAction;

    public WorkflowDefinitionsActionProvider()
    {
        super();
    }

    private void addListeners( CommonViewer tableViewer )
    {
        tableViewer.addOpenListener( new IOpenListener()
        {

            public void open( OpenEvent event )
            {
                try
                {
                    IStructuredSelection sel = (IStructuredSelection) event.getSelection();
                    Object data = sel.getFirstElement();

                    if( !( data instanceof WorkflowDefinitionEntry ) )
                    {
                        return;
                    }

                    WorkflowDefinitionsActionProvider.this.editAction.run();

                }
                catch( Exception e )
                {
                    KaleoUI.logError( "Error opening kaleo workflow.", e );
                }
            }
        } );
    }

    protected void addTopSection(
        IMenuManager menu, WorkflowDefinitionEntry definition, WorkflowDefinitionsFolder definitionsFolder )
    {
        // open action
        if( definition != null )
        {
            menu.add( editAction );
            menu.add( publishAction );
        }

        if( definitionsFolder != null )
        {
            menu.add( refreshAction );
            menu.add( uploadAction );
        }
    }

    public void fillContextMenu( IMenuManager menu )
    {
        // This is a temp workaround to clean up the default group that are provided by CNF
        menu.removeAll();

        ICommonViewerSite site = actionSite.getViewSite();
        IStructuredSelection selection = null;

        if( site instanceof ICommonViewerWorkbenchSite )
        {
            ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) site;
            selection = (IStructuredSelection) wsSite.getSelectionProvider().getSelection();
        }

        WorkflowDefinitionEntry definition = null;
        WorkflowDefinitionsFolder definitionsFolder = null;

        if( selection != null && !selection.isEmpty() )
        {
            Iterator<?> iterator = selection.iterator();
            Object obj = iterator.next();

            if( obj instanceof WorkflowDefinitionEntry )
            {
                definition = (WorkflowDefinitionEntry) obj;
            }

            if( obj instanceof WorkflowDefinitionsFolder )
            {
                definitionsFolder = (WorkflowDefinitionsFolder) obj;
            }

            if( iterator.hasNext() )
            {
                definition = null;
                definitionsFolder = null;
            }
        }

        menu.add( invisibleSeparator( TOP_SECTION_START_SEPARATOR ) );
        addTopSection( menu, definition, definitionsFolder );
        menu.add( invisibleSeparator( TOP_SECTION_END_SEPARATOR ) );
        menu.add( new Separator() );

        menu.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS ) );
        menu.add( new Separator( IWorkbenchActionConstants.MB_ADDITIONS + "-end" ) );
    }

    public void init( ICommonActionExtensionSite site )
    {
        super.init( site );
        this.actionSite = site;
        ICommonViewerSite viewerSite = site.getViewSite();

        if( viewerSite instanceof ICommonViewerWorkbenchSite )
        {
            StructuredViewer v = site.getStructuredViewer();

            if( v instanceof CommonViewer )
            {
                CommonViewer cv = (CommonViewer) v;
                ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) viewerSite;
                addListeners( cv );
                makeActions( cv, wsSite.getSelectionProvider() );
            }
        }
    }

    private Separator invisibleSeparator( String s )
    {
        Separator sep = new Separator( s );
        sep.setVisible( false );
        return sep;
    }

    private void makeActions( CommonViewer tableViewer, ISelectionProvider provider )
    {
        // Shell shell = tableViewer.getTree().getShell();

        // create the open action
        editAction = new EditWorkflowDefinitionAction( provider );
        refreshAction = new RefreshWorkflowDefinitionsAction( provider );
        publishAction = new PublishWorkflowDefinitionAction( provider );
        uploadAction = new UploadNewWorkflowDefinitionAction( provider );

        // create copy, paste, and delete actions
        // pasteAction = new PasteAction(shell, provider, clipboard);
        // copyAction = new CopyAction(provider, clipboard, pasteAction);
        // globalDeleteAction = new GlobalDeleteAction(shell, provider);
        // renameAction = new RenameAction(shell, tableViewer, provider);

    }

}
