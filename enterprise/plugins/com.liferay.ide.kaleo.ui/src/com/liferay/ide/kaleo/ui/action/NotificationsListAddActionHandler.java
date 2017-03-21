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

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Executable;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.model.TemplateLanguageType;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ui.Presentation;

/**
 * @author Gregory Amerson
 */
public class NotificationsListAddActionHandler extends DefaultListAddActionHandler
{

    public NotificationsListAddActionHandler()
    {
        super( ActionNotification.TYPE, ActionTimer.PROP_NOTIFICATIONS );
    }

    public NotificationsListAddActionHandler( ElementType type, ListProperty listProperty )
    {
        super( type, listProperty );
    }

    public static void addNotificationDefaults( final ActionNotification newNotification )
    {
        final String defaultTemplateLanguage =
            KaleoModelUtil.getDefaultValue(
                newNotification, KaleoCore.DEFAULT_TEMPLATE_LANGUAGE_KEY, TemplateLanguageType.FREEMARKER );

        Node[] nodes = new Node[0];

        if( newNotification.nearest( Task.class ) != null )
        {
            nodes = newNotification.nearest( Task.class ).getTaskNotifications().toArray( new Node[0] );
        }
        else
        {
            nodes = newNotification.nearest( ActionTimer.class ).getNotifications().toArray( new Node[0] );
        }

        final String newName = getDefaultName( "newNotification1", newNotification, nodes );

        newNotification.setName( newName );
        newNotification.setTemplateLanguage( defaultTemplateLanguage );
        newNotification.setExecutionType( Executable.DEFAULT_EXECUTION_TYPE );

        if( newNotification.nearest( Task.class ) != null )
        {
            newNotification.setTemplate( "/* specify task notification template */" );
        }
        else
        {
            newNotification.setTemplate( "/* specify notification template */" );
        }

        newNotification.getNotificationTransports().insert().setNotificationTransport( "email" );
    }

    @Override
    protected Object run( final Presentation context )
    {
        Element newElement = (Element) super.run( context );
        ActionNotification newNotification = newElement.nearest( ActionNotification.class );

        addNotificationDefaults( newNotification );

        return newNotification;
    }

}
