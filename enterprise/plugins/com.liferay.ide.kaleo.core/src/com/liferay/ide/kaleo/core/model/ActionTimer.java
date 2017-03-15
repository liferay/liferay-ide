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
public interface ActionTimer extends WorkflowNode
{

    ElementType TYPE = new ElementType( ActionTimer.class );

    // *** Actions ***

    @Type( base = Action.class )
    @Label( standard = "action" )
    @XmlListBinding( path = "actions", mappings = @XmlListBinding.Mapping( element = "action", type = Action.class ) )
    ListProperty PROP_ACTIONS = new ListProperty( TYPE, "Actions" );

    ElementList<Action> getActions();

    // *** Notifications ***

    @Type( base = ActionNotification.class )
    @Label( standard = "notification" )
    @XmlListBinding( path = "actions", mappings = @XmlListBinding.Mapping(
                    element = "notification",
                    type = ActionNotification.class ) )
    ListProperty PROP_NOTIFICATIONS = new ListProperty( TYPE, "Notifications" );

    ElementList<ActionNotification> getNotifications();

    // *** Timer ***

    @Type( base = Timer.class )
    @Label( standard = "timer" )
    @XmlListBinding( path = "timers", mappings = @XmlListBinding.Mapping( element = "timer", type = Timer.class ) )
    ListProperty PROP_TIMERS = new ListProperty( TYPE, "Timers" );

    ElementList<Timer> getTimers();

}
