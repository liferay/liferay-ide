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

import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionsFolder;

import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * "Refresh" menu action.
 * @author Gregory Amerson
 */
public class RefreshWorkflowDefinitionsAction extends AbstractWorkflowDefinitionAction
{

    /**
     * OpenAction constructor.
     *
     * @param sp
     *            a selection provider
     */
    public RefreshWorkflowDefinitionsAction( ISelectionProvider sp )
    {
        super( sp, "Refresh" );

        // setActionDefinitionId( org.eclipse.ui.actions.RefreshAction.ID );
    }

    @Override
    public void perform( final Object node )
    {
        if( this.getSelectionProvider() instanceof CommonViewer )
        {
            if( node instanceof WorkflowDefinitionsFolder )
            {
                ( (WorkflowDefinitionsFolder) node ).clearCache();
            }

            final CommonViewer viewer = (CommonViewer) getSelectionProvider();

            Display.getDefault().asyncExec
            (
                new Runnable()
                {
                    public void run()
                    {
                        viewer.refresh( true );
                    }
                }
            );
        }
    }

}
