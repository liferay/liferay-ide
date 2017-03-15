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

package com.liferay.ide.kaleo.core.op;

import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.op.internal.NewNodeNameValidationService;

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface NewTaskNode extends Task
{
    ElementType TYPE = new ElementType( NewTaskNode.class );

    public interface INewTaskNotification extends ActionNotification
    {
        ElementType TYPE = new ElementType( INewTaskNotification.class );

        @Length( min = 0 )
        ListProperty PROP_NOTIFICATION_TRANSPORTS = new ListProperty(
            TYPE, ActionNotification.PROP_NOTIFICATION_TRANSPORTS );

        @DefaultValue( text = "" )
        ValueProperty PROP_TEMPLATE = new ValueProperty( TYPE, "Template" );
    }

    @DefaultValue( text = "New Task" )
    @Service( impl = NewNodeNameValidationService.class )
    ValueProperty PROP_NAME = new ValueProperty( TYPE, Node.PROP_NAME );

    @Length( min = 0 )
    ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, Task.PROP_TRANSITIONS );

    @Type( base = INewTaskNotification.class )
    @Label( standard = "task notifications" )
    @XmlListBinding
    (
        path = "actions",
        mappings =
        {
            @XmlListBinding.Mapping
            (
                element = "notification", type = INewTaskNotification.class
            )
        }
    )
    ListProperty PROP_NEW_TASK_NOTIFICATIONS = new ListProperty( TYPE, "NewTaskNotifications" );

    ElementList<INewTaskNotification> getNewTaskNotifications();
}
