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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.core.model.Task;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireCondition;

/**
 * @author Gregory Amerson
 */
public class TaskScriptedAssignmentCondition extends SapphireCondition
{

    @Override
    protected boolean evaluate()
    {
        return task() != null && task().getScriptedAssignment().content( false ) != null;
    }

    @Override
    protected void initCondition( ISapphirePart part, String parameter )
    {
        super.initCondition( part, parameter );

        Task task = task();

        Listener assignmentTypeListener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyContentEvent event )
            {
                updateConditionState();
            }
        };

        if( task != null )
        {
            task.attach( assignmentTypeListener, Task.PROP_SCRIPTED_ASSIGNMENT.name() );
        }

        updateConditionState();
    }

    private Task task()
    {
        Task retval = null;

        Element modelElement = getPart().getLocalModelElement();

        retval = modelElement.nearest( Task.class );

        return retval;
    }

}
