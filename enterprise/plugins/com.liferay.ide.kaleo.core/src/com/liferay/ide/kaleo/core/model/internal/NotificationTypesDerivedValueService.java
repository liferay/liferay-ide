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

import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.core.model.NotificationTransport;

import java.util.List;

import org.eclipse.sapphire.DerivedValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;


/**
 * @author Gregory Amerson
 */
public class NotificationTypesDerivedValueService extends DerivedValueService
{

    @Override
    protected void initDerivedValueService()
    {
        final FilteredListener<PropertyContentEvent> listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            public void handleTypedEvent( final PropertyContentEvent event )
            {
                refresh();
            }
        };

        this.context( Notification.class ).attach( listener, "NotificationTransports" );
        this.context( Notification.class ).attach( listener, "NotificationTransports/*" );
    }

    @Override
    protected String compute()
    {
        StringBuilder data = new StringBuilder();

        List<NotificationTransport> transports = this.context( Notification.class ).getNotificationTransports();

        for (NotificationTransport transport : transports)
        {
            if (data.length() != 0)
            {
                data.append( ',' );
            }

            data.append( transport.getNotificationTransport().content() );
        }

        return data.toString();
    }

}
