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

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.util.UploadWorkflowFileJob;
import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @author Gregory Amerson
 */
public class WorkflowDropAdapterAssistant extends CommonDropAdapterAssistant
{

    public WorkflowDropAdapterAssistant()
    {
        super();
    }

    @Override
    public IStatus validateDrop( Object target, int operation, TransferData data )
    {
        try
        {
            if( target instanceof WorkflowDefinitionsFolder )
            {
                if( LocalSelectionTransfer.getTransfer().isSupportedType( data ) )
                {
                    Object dropData = LocalSelectionTransfer.getTransfer().nativeToJava( data );

                    if( dropData instanceof IStructuredSelection )
                    {
                        IStructuredSelection selection = (IStructuredSelection) dropData;
                        Object element = selection.getFirstElement();

                        if( element instanceof IFile )
                        {
                            IFile file = (IFile) element;

                            IContentType contentType = file.getContentDescription().getContentType();

                            if( "com.liferay.ide.kaleo.core.workflowdefinitioncontent".equals( contentType.getId() ) )
                            {
                                return Status.OK_STATUS;
                            }
                        }
                    }
                }
            }
        }
        catch( Exception e )
        {
            // ignore and don't allow drop
        }

        return null;
    }

    @Override
    public IStatus handleDrop( CommonDropAdapter aDropAdapter, final DropTargetEvent aDropTargetEvent, Object aTarget )
    {
        if( aTarget instanceof WorkflowDefinitionsFolder )
        {
            final WorkflowDefinitionsFolder folder = (WorkflowDefinitionsFolder) aTarget;

            IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection( folder.getParent() );

            final TransferData transferData = aDropTargetEvent.currentDataType;

            if( LocalSelectionTransfer.getTransfer().isSupportedType( transferData ) )
            {
                Object dropData = LocalSelectionTransfer.getTransfer().nativeToJava( transferData );

                if( dropData instanceof IStructuredSelection )
                {
                    IStructuredSelection selection = (IStructuredSelection) dropData;
                    Object element = selection.getFirstElement();

                    if( element instanceof IFile )
                    {
                        IFile file = (IFile) element;

                        new UploadWorkflowFileJob( kaleoConnection, file, new Runnable()
                        {
                            public void run()
                            {
                                folder.clearCache();
                                Display.getDefault().asyncExec( new Runnable(){

                                    public void run()
                                    {
                                        IViewPart serversView = UIUtil.showView("org.eclipse.wst.server.ui.ServersView");
                                        CommonViewer viewer = (CommonViewer) serversView.getAdapter(CommonViewer.class);
                                        viewer.refresh( true );
                                    }} );
                            }
                        } ).schedule();
                    }
                }
            }
        }

        return Status.OK_STATUS;
    }

}
