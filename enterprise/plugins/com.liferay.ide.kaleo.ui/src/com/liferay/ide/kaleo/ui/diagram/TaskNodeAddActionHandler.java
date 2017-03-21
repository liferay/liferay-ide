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

package com.liferay.ide.kaleo.ui.diagram;

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.op.AssignableOp;
import com.liferay.ide.kaleo.core.op.NewNodeOp;
import com.liferay.ide.kaleo.core.op.NewTaskNode;
import com.liferay.ide.kaleo.core.op.NewTaskNode.INewTaskNotification;
import com.liferay.ide.kaleo.core.op.NewTaskNodeOp;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.diagram.editor.DiagramNodeTemplate;

/**
 * @author Gregory Amerson
 */
public class TaskNodeAddActionHandler extends NewNodeAddActionHandler
{

    private static final String WIZARD_ID = "newTaskNodeWizard";

    public TaskNodeAddActionHandler( DiagramNodeTemplate nodeTemplate )
    {
        super( nodeTemplate );
    }

    @Override
    protected NewNodeOp createOp( Presentation context )
    {
        NewTaskNodeOp op = NewTaskNodeOp.TYPE.instantiate();

        op.getImpliedScriptable().setScriptLanguage(
            KaleoModelUtil.getDefaultValue(
                getModelElement(), KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY ) );

        return op;
    }

    @Override
    protected String getWizardId()
    {
        return WIZARD_ID;
    }

    @Override
    public void postDiagramNodePartAdded( NewNodeOp op, CanTransition newNodeFromWizard, CanTransition newNode )
    {
        Task newTask = newNode.nearest( Task.class );
        NewTaskNode newTaskFromWizard = newNodeFromWizard.nearest( NewTaskNode.class );

        KaleoModelUtil.changeTaskAssignments( newTask, op.nearest( AssignableOp.class ) );

        for( Action taskAction : newTaskFromWizard.getTaskActions() )
        {
            newTask.getTaskActions().insert().copy( taskAction );
        }

        for( INewTaskNotification taskNotification : newTaskFromWizard.getNewTaskNotifications() )
        {
            ActionNotification newTaskNotification = newTask.getTaskNotifications().insert();
            newTaskNotification.setName( taskNotification.getName().content() );
            newTaskNotification.setExecutionType( taskNotification.getExecutionType().content() );
            newTaskNotification.setTemplateLanguage( taskNotification.getTemplateLanguage().content() );
        }
    }
}
