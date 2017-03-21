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

import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ActionNotification;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.ui.action.ActionsListAddActionHandler;
import com.liferay.ide.kaleo.ui.action.NotificationsListAddActionHandler;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;


/**
 * @author Gregory Amerson
 */
public class WorkflowNodeAddHandlerFactory extends SapphireActionHandlerFactory
{

    @Override
    public List<SapphireActionHandler> create()
    {
        final List<SapphireActionHandler> handlers = new ArrayList<SapphireActionHandler>();

        if( this.getModelElement() instanceof Task )
        {
            final Task task = this.getModelElement().nearest( Task.class );

            handlers.add
            (
                new SapphireActionHandler()
                {
                    @Override
                    public void init( SapphireAction action, ActionHandlerDef def )
                    {
                        super.init( action, def );
                        this.addImage( Action.TYPE.image() );
                        this.setLabel( "Task Action" );
                    }

                    @Override
                    protected Object run( Presentation context )
                    {
                        Action newAction = task.getTaskActions().insert();

                        ActionsListAddActionHandler.addActionDefaults( newAction );

                        return newAction;
                    }
                }
            );

            handlers.add
            (
                new SapphireActionHandler()
                {
                    @Override
                    public void init( SapphireAction action, ActionHandlerDef def )
                    {
                        super.init( action, def );
                        this.addImage( ActionNotification.TYPE.image() );
                        this.setLabel( "Task Notification" );
                    }

                    @Override
                    protected Object run( Presentation context )
                    {
                        ActionNotification newTaskNotificaction = task.getTaskNotifications().insert();

                        NotificationsListAddActionHandler.addNotificationDefaults( newTaskNotificaction );

                        return newTaskNotificaction;
                    }

                }
            );
        }
        else
        {
            final ActionTimer actionTimer = this.getModelElement().nearest( ActionTimer.class );

            handlers.add
            (
                new SapphireActionHandler()
                {
                    @Override
                    public void init( SapphireAction action, ActionHandlerDef def )
                    {
                        super.init( action, def );
                        this.addImage( Action.TYPE.image() );
                        this.setLabel( "Action" );
                    }

                    @Override
                    protected Object run( Presentation context )
                    {
                        Action newAction = actionTimer.getActions().insert();

                        ActionsListAddActionHandler.addActionDefaults( newAction );

                        return newAction;
                    }
                }
            );

            handlers.add
            (
                new SapphireActionHandler()
                {
                    @Override
                    public void init( SapphireAction action, ActionHandlerDef def )
                    {
                        super.init( action, def );
                        this.addImage( Notification.TYPE.image() );
                        this.setLabel( "Notification" );
                    }

                    @Override
                    protected Object run( Presentation context )
                    {
                        ActionNotification newNotificaction = actionTimer.getNotifications().insert();

                        NotificationsListAddActionHandler.addNotificationDefaults( newNotificaction );

                        return newNotificaction;
                    }

                }
            );
        }

        return handlers;
    }

}
