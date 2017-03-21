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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.KaleoUI;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;
import com.liferay.ide.kaleo.ui.util.KaleoUtil;
import com.liferay.ide.kaleo.ui.util.UploadWorkflowFileJob;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.DecoratingLabelProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.dialogs.ISelectionStatusValidator;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * "Open" menu action.
 *
 * @author Gregory Amerson
 */
public class UploadNewWorkflowDefinitionAction extends AbstractWorkflowDefinitionAction
{

    /**
     * OpenAction constructor.
     *
     * @param sp
     *            a selection provider
     */
    public UploadNewWorkflowDefinitionAction( ISelectionProvider sp )
    {
        super( sp, "Upload new workflow..." );
    }

    @Override
    public void perform( final Object node )
    {
        if( this.getSelectionProvider() instanceof CommonViewer && node instanceof WorkflowDefinitionsFolder )
        {
            final IFile workspaceFile = promptForWorkspaceFile();

            if( workspaceFile == null || !workspaceFile.exists() )
            {
                return;
            }

            String errorMsgs = KaleoUtil.checkWorkflowDefinitionForErrors(workspaceFile);

            if (!CoreUtil.empty( errorMsgs ))
            {
                MessageDialog.openError( Display.getDefault().getActiveShell(), "Upload Kaleo Workflow",
                    "Unable to upload kaleo workflow:\n\n" + errorMsgs);

                return;
            }

            final WorkflowDefinitionsFolder definitionsFolder = (WorkflowDefinitionsFolder) node;
            IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection( definitionsFolder.getParent() );

            Job upload = new UploadWorkflowFileJob(kaleoConnection, workspaceFile, new Runnable()
            {
                public void run()
                {
                    final CommonViewer viewer = (CommonViewer) getSelectionProvider();

                    Display.getDefault().asyncExec( new Runnable()
                    {
                        public void run()
                        {
                            definitionsFolder.clearCache();
                            viewer.refresh( true );
                        }
                    } );

                }
            });

            upload.schedule();

        }
    }

    protected IFile promptForWorkspaceFile()
    {
        ISelectionStatusValidator validator = getContainerDialogSelectionValidator();

        ViewerFilter filter = getContainerDialogViewerFilter();

        ITreeContentProvider contentProvider = new WorkbenchContentProvider();

        ILabelProvider labelProvider =
            new DecoratingLabelProvider(
                new WorkbenchLabelProvider(), PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator() );

        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog( getShell(), labelProvider, contentProvider );
        dialog.setValidator( validator );
        dialog.setTitle( "workspace file" );
        dialog.setMessage( "workspace file" );
        dialog.addFilter( filter );
        dialog.setInput( ResourcesPlugin.getWorkspace() );

        if( dialog.open() == Window.OK )
        {
            Object element = dialog.getFirstResult();

            try
            {
                if( element instanceof IFile )
                {
                    IFile file = (IFile) element;

                    return file;
                }
            }
            catch( Exception ex )
            {
                // Do nothing
            }
        }

        return null;
    }

    protected ViewerFilter getContainerDialogViewerFilter()
    {
        return new ViewerFilter()
        {
            public boolean select( Viewer viewer, Object parent, Object element )
            {
                if( element instanceof IProject )
                {
                    return true;
                }
                else if( element instanceof IFolder )
                {
                    return true;
                }
                else if( element instanceof IFile )
                {
                    return true;
                }

                return false;
            }
        };
    }

    protected ISelectionStatusValidator getContainerDialogSelectionValidator()
    {
        return new ISelectionStatusValidator()
        {
            public IStatus validate( Object[] selection )
            {
                if( selection != null && selection.length > 0 && selection[0] != null &&
                    !( selection[0] instanceof IProject ) && !( selection[0] instanceof IFolder ) )
                {
                    return Status.OK_STATUS;
                }

                return KaleoUI.createErrorStatus( "Choose a valid project file" );
            }
        };
    }
}
