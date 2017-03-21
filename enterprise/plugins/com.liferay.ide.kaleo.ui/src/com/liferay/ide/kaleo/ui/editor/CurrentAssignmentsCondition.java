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

import com.liferay.ide.kaleo.core.model.Assignable;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireCondition;


/**
 * @author Gregory Amerson
 */
public class CurrentAssignmentsCondition extends SapphireCondition
{

    private String parameter;

    @Override
    protected boolean evaluate()
    {
        boolean retval = false;

        Assignable assignable = op();

        boolean hasUser = assignable.getUser().content( false ) != null;
        boolean hasScript = assignable.getScriptedAssignment().content( false ) != null;
        boolean hasRoles = assignable.getRoles().size() > 0;
        boolean hasResourceActions = assignable.getResourceActions().size() > 0;

        if (hasUser)
        {
            retval = "user".equals(this.parameter) || "creator".equals(this.parameter);
        }
        else if (hasScript)
        {
            retval = "script".equals(this.parameter);
        }
        else if (hasRoles)
        {
            retval = "role".equals(this.parameter) || "roles".equals(this.parameter);
        }
        else if (hasResourceActions)
        {
            retval = "resources".equals(this.parameter);
        }

        return retval;
    }

    @Override
    protected void initCondition( ISapphirePart part, String parameter )
    {
        super.initCondition( part, parameter );

        this.parameter = parameter;

        Assignable assignable = op();

        Listener assignmentTypeListener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyContentEvent event )
            {
                updateConditionState();
            }
        };

        assignable.attach( assignmentTypeListener, "*" );

        updateConditionState();
    }

    private Assignable op()
    {
        return getPart().getLocalModelElement().nearest( Assignable.class );
    }
}
