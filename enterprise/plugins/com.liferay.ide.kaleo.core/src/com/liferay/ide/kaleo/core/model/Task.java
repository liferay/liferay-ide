/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core.model;

import com.liferay.ide.kaleo.core.model.internal.TaskValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
@Image( path = "images/task_16x16.png" )
public interface Task extends WorkflowNode, Assignable, MustTransition
{

    ElementType TYPE = new ElementType( Task.class );


    // *** TaskValidationService ***

    @Service( impl = TaskValidationService.class )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, Node.PROP_NAME );

    // *** Actions ***

    @Type( base = Action.class )
    @Label( standard = "task actions" )
    @XmlListBinding
    (
        path = "actions",
        mappings = @XmlListBinding.Mapping( element = "action", type = Action.class )
    )
    ListProperty PROP_TASK_ACTIONS = new ListProperty( TYPE, "TaskActions" );

    ElementList<Action> getTaskActions();

    // *** Notifications ***

    @Type( base = TaskActionNotification.class )
    @Label( standard = "task notifications" )
    @XmlListBinding
    (
        path = "actions",
        mappings = @XmlListBinding.Mapping( element = "notification", type = TaskActionNotification.class )
    )
    ListProperty PROP_TASK_NOTIFICATIONS = new ListProperty( TYPE, "TaskNotifications" );

    ElementList<TaskActionNotification> getTaskNotifications();


    // *** TaskTimers ***

    @Type( base = TaskTimer.class )
    @Label( standard = "task timers" )
    @XmlListBinding
    (
        path = "task-timers",
        mappings = @XmlListBinding.Mapping( element = "task-timer", type = TaskTimer.class )
    )
    ListProperty PROP_TASK_TIMERS = new ListProperty( TYPE, "TaskTimers" );

    ElementList<TaskTimer> getTaskTimers();
}
