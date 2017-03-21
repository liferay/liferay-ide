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

import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.TaskActionNotification;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.util.ListFactory;


/**
 * @author Gregory Amerson
 */
public class TaskNotificationsDiagramNodeEditHandlerFactory extends NotificationsDiagramNodeEditHandlerFactory
{

    protected Element getElement()
    {
        return getModelElement().nearest( Task.class );
    }

    @Override
    protected String getListPropertyName()
    {
        return Task.PROP_TASK_NOTIFICATIONS.name();
    }

    @Override
    protected List<ActionNotification> getNotifications()
    {
        final ListFactory<ActionNotification> factory = ListFactory.start();

        final Task task = getModelElement().nearest( Task.class );

        if (task != null)
        {
            final ElementList<TaskActionNotification> taskNotifiations = task.getTaskNotifications();

            for( TaskActionNotification notification : taskNotifiations )
            {
                factory.add( notification );
            }
        }

        return factory.result();
    }

}
