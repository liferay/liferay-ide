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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.Assignable;
import com.liferay.ide.kaleo.core.op.NewNodeOp.TaskForOp;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;


/**
 * @author Gregory Amerson
 */
public class TaskValidationService extends ValidationService
{
    private Listener listener;

    @Override
    public void dispose()
    {
        final Assignable assignable = assignable();

        if( assignable != null )
        {
            assignable.detach( this.listener );
        }
    }

    @Override
    protected void initValidationService()
    {
        final Assignable assignable = assignable();

        if( assignable != null )
        {
            this.listener = new FilteredListener<PropertyContentEvent>()
            {
                @Override
                protected void handleTypedEvent( PropertyContentEvent event )
                {
                    refresh();
                }
            };

            assignable.attach( this.listener, "*" );
        }
    }

    protected Assignable assignable()
    {
        return context( Assignable.class );
    }

    @Override
    public Status compute()
    {
        final Assignable assignable = assignable();

        if( assignable != null && assignable.nearest( TaskForOp.class ) == null )
        {
             final Value<String> currentAssignments = assignable.getCurrentAssignments();

             if( currentAssignments.content( false ) == null )
             {
                 return Status.createErrorStatus( "Task assignments have not been set." );
             }
        }

        return Status.createOkStatus();
    }

}
