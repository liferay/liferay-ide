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

import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlListBinding;

/**
 * @author Gregory Amerson
 */
public interface TimerAction extends Timer
{

    ElementType TYPE = new ElementType( TimerAction.class );

    // *** timer actions ***

    @Type( base = Action.class )
    @Label( standard = "timer actions" )
    @XmlListBinding
    (
        path = "timer-actions",
        mappings = @XmlListBinding.Mapping
        (
            element = "timer-action",
            type = Action.class
        )
    )
    ListProperty PROP_TIMER_ACTIONS = new ListProperty( TYPE, "TimerActions" );

    ElementList<Action> getTimerActions();

    // *** timer notifications ***

    @Type( base = Notification.class )
    @Label( standard = "timer notifications" )
    @XmlListBinding
    (
        path = "timer-actions",
        mappings = @XmlListBinding.Mapping
        (
            element = "timer-notification",
            type = Notification.class
        )
    )
    ListProperty PROP_TIMER_NOTIFICATIONS = new ListProperty( TYPE, "TimerNotifications" );

    ElementList<Notification> getTimerNotifications();

}
