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

import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditor;
import com.liferay.ide.kaleo.ui.editor.WorkflowDefinitionEditorInput;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPropertyListener;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * "Edit" menu action.
 *
 * @author Gregory Amerson
 */
public class EditWorkflowDefinitionAction extends AbstractWorkflowDefinitionAction
{

    public EditWorkflowDefinitionAction( ISelectionProvider sp )
    {
        super( sp, "Edit Kaleo workflow" );
    }

    @Override
    public void perform( Object entry )
    {
        if( entry instanceof WorkflowDefinitionEntry )
        {
            final WorkflowDefinitionEntry workflowEntry = (WorkflowDefinitionEntry) entry;

            IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();

            IEditorPart[] dirtyEditors = page.getDirtyEditors();

            for( IEditorPart dirtyEditor : dirtyEditors )
            {
                IEditorInput editorInput = dirtyEditor.getEditorInput();

                if( editorInput instanceof WorkflowDefinitionEditorInput )
                {
                    WorkflowDefinitionEditorInput dirtyWorkflowEditorInput = (WorkflowDefinitionEditorInput) editorInput;
                    boolean opened = dirtyWorkflowEditorInput.getName().contains( workflowEntry.getName() );

                    if( opened )
                    {
                        IEditorSite editorSite = dirtyEditor.getEditorSite();

                        boolean saveOld = MessageDialog.openQuestion(
                            editorSite.getShell(), "Save " + dirtyWorkflowEditorInput.getName(),
                            "Do you want to save contents of this editor?" );

                        page.closeEditor( dirtyEditor, saveOld );
                    }
                }
            }

            try
            {
                WorkflowDefinitionEditorInput editorInput = new WorkflowDefinitionEditorInput( workflowEntry );
                final IEditorPart editor = page.openEditor( editorInput, WorkflowDefinitionEditor.EDITOR_ID, true, IWorkbenchPage.MATCH_INPUT );

                editor.addPropertyListener( new IPropertyListener()
                {
                    public void propertyChanged( Object source, int propId )
                    {
                        if( source.equals( editor ) && propId == WorkflowDefinitionEditor.PROP_UPDATE_VERSION )
                        {
                            workflowEntry.getParent().clearCache();
                            ( (CommonViewer) EditWorkflowDefinitionAction.this.getSelectionProvider() ).refresh( true );
                        }
                    }
                } );
            }
            catch( PartInitException e )
            {
                KaleoUI.logError( "Error opening kaleo workflow editor.", e );
            }
        }
    }

}
