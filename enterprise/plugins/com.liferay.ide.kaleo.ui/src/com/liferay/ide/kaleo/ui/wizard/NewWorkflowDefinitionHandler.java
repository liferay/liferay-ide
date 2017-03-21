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

package com.liferay.ide.kaleo.ui.wizard;

import com.liferay.ide.kaleo.core.op.NewWorkflowDefinitionOp;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

/**
 * @author Gregory Amerson
 */
public class NewWorkflowDefinitionHandler extends AbstractHandler implements IHandler
{

    public Object execute( final ExecutionEvent event ) throws ExecutionException
    {
        final IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow( event );

        final NewWorkflowDefinitionOp op = NewWorkflowDefinitionOp.TYPE.instantiate();

        final NewWorkflowDefinitionWizard wizard = new NewWorkflowDefinitionWizard( op );

        final WizardDialog dialog = new WizardDialog( window.getShell(), wizard );

        dialog.open();

        return null;
    }

}
