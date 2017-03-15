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

package com.liferay.ide.kaleo.core.op.internal;

import static org.eclipse.sapphire.modeling.Status.createErrorStatus;
import static org.eclipse.sapphire.modeling.Status.createOkStatus;

import com.liferay.ide.kaleo.core.model.User;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class UserValidationService extends ValidationService
{
    private Listener listener;

    @Override
    protected void initValidationService()
    {
        final User user = context( User.class );

        if( user != null )
        {
            this.listener = new FilteredListener<PropertyContentEvent>()
            {
                @Override
                protected void handleTypedEvent( PropertyContentEvent event )
                {
                    refresh();
                }
            };

            user.attach( this.listener, "*" );
        }
    }

    @Override
    public void dispose()
    {
        final User user = context( User.class );

        if( user != null )
        {
            user.detach( this.listener );
        }
    }

    @Override
    public Status compute()
    {
        final User user = context( User.class );

        if( user != null )
        {
            int count = 0;

            boolean userId = user.getUserId().content() != null;
            boolean screenName = user.getScreenName().content() != null;
            boolean emailAddress = user.getEmailAddress().content() != null;

            if( userId )
            {
                count++;
            }

            if( screenName )
            {
                count++;
            }

            if( emailAddress )
            {
                count++;
            }

            if( count > 1 )
            {
                return createErrorStatus( "Only specify one of the three user fields." );
            }
        }

        return createOkStatus();
    }

}
