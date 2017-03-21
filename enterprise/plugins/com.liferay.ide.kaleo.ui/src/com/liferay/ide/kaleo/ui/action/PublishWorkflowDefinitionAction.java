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

import com.liferay.ide.kaleo.core.IKaleoConnection;
import com.liferay.ide.kaleo.core.KaleoAPIException;
import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;
import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * "Publish" menu action.
 *
 * @author Gregory Amerson
 */
public class PublishWorkflowDefinitionAction extends AbstractWorkflowDefinitionAction
{

    /**
     * OpenAction constructor.
     *
     * @param sp
     *            a selection provider
     */
    public PublishWorkflowDefinitionAction( ISelectionProvider sp )
    {
        super( sp, "Publish Kaleo workflow" );
    }

    @Override
    public void perform( final Object node )
    {
        if( this.getSelectionProvider() instanceof CommonViewer && node instanceof WorkflowDefinitionEntry )
        {
            final WorkflowDefinitionEntry definitionNode = (WorkflowDefinitionEntry) node;

            Job publishJob = new Job( "Publishing workflow draft definition" )
            {
                @Override
                protected IStatus run( IProgressMonitor monitor )
                {
                    IKaleoConnection kaleoConnection = KaleoCore.getKaleoConnection( definitionNode.getParent().getParent());

                    try
                    {
                        kaleoConnection.publishKaleoDraftDefinition(
                            definitionNode.getName(), definitionNode.getTitleMap(), definitionNode.getContent(),
                            definitionNode.getCompanyId() + "", definitionNode.getUserId() + "",
                            definitionNode.getGroupId() + "" );
                    }
                    catch( KaleoAPIException e )
                    {
                        e.printStackTrace();
                    }

                    final WorkflowDefinitionsFolder definitionsFolder =
                        (WorkflowDefinitionsFolder) definitionNode.getParent();

                    final CommonViewer viewer = (CommonViewer) getSelectionProvider();

                    Display.getDefault().asyncExec
                    (
                        new Runnable()
                        {
                            public void run()
                            {
                                definitionsFolder.clearCache();
                                viewer.refresh( true );
                            }
                        }
                    );

                    return Status.OK_STATUS;
                }
            };

            publishJob.schedule();

        }
    }

}
