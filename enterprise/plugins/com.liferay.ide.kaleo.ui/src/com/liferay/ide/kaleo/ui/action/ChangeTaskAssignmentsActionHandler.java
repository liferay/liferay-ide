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

import static com.liferay.ide.core.util.CoreUtil.isNullOrEmpty;

import com.liferay.ide.kaleo.ui.wizard.NewWorkflowDefinitionWizard;
import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.model.ResourceAction;
import com.liferay.ide.kaleo.core.model.Role;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.User;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.model.WorkflowNode;
import com.liferay.ide.kaleo.core.op.ChangeTaskAssignmentsOp;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;
import org.eclipse.sapphire.ui.forms.swt.SwtPresentation;

/**
 * @author Gregory Amerson
 */
public class ChangeTaskAssignmentsActionHandler extends SapphireActionHandler
{

    @Override
    protected Object run( Presentation context )
    {
        Task task = task( context );

        ChangeTaskAssignmentsOp op = ChangeTaskAssignmentsOp.TYPE.instantiate();

        for( WorkflowNode node : task.nearest( WorkflowDefinition.class ).getDiagramNodes() )
        {
            final Assignable assignable = node.nearest( Assignable.class );

            if( assignable != null )
            {
                for( Role role : assignable.getRoles() )
                {
                    final String name = role.getName().content( false );

                    if( !isNullOrEmpty( name ))
                    {
                        op.getRoleNames().insert().setName( name );
                    }
                }
            }
        }

        User existingUser = task.getUser().content( false );
        ElementList<Role> existingRoles = task.getRoles();
        ElementList<ResourceAction> existingActions = task.getResourceActions();
        Scriptable scriptedAssignment = task.getScriptedAssignment().content( false );

        if( existingUser != null )
        {
            op.getImpliedUser().copy( existingUser );
        }
        else if( existingRoles.size() > 0 )
        {
            op.getImpliedRole().copy( existingRoles.get( 0 ) );

            for( Role role : existingRoles )
            {
                final Role newRole = op.getRoles().insert();
                newRole.copy( role );

                Boolean autoCreate = role.getAutoCreate().content( false );

                if( autoCreate != null )
                {
                    newRole.setAutoCreate( role.getAutoCreate().content() );
                }
            }
        }
        else if( existingActions.size() > 0 )
        {
            for( ResourceAction action : existingActions )
            {
                op.getResourceActions().insert().copy( action );
            }
        }
        else if( scriptedAssignment != null )
        {
            op.getScriptedAssignment().content( true ).copy( scriptedAssignment );
        }

        final SapphireWizard<ChangeTaskAssignmentsOp> wizard =
            new SapphireWizard<ChangeTaskAssignmentsOp>( op, DefinitionLoader.context(
                NewWorkflowDefinitionWizard.class ).sdef( "WorkflowDefinitionWizards" ).wizard(
                "changeTaskAssignmentsWizard" ) );

        int returnCode = new WizardDialog( ( (SwtPresentation) context ).shell(), wizard ).open();

        if( returnCode == IDialogConstants.OK_ID )
        {
            KaleoModelUtil.changeTaskAssignments( task( context ), op );
        }

        return null;
    }

    private Task task( Presentation context )
    {
        return context.part().getLocalModelElement().nearest( Task.class );
    }

}
