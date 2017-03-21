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

import static com.liferay.ide.core.util.CoreUtil.empty;

import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Scriptable;

import java.util.List;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyEvent;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFactory;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;
import org.eclipse.sapphire.util.ListFactory;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class ActionsDiagramNodeEditHandlerFactory extends SapphireActionHandlerFactory
{
    private Listener listener;

    @Override
    public List<SapphireActionHandler> create()
    {
        ListFactory<SapphireActionHandler> factory = ListFactory.start();

        Element element = getElement();

        if (element == null)
        {
            return factory.result();
        }

        ElementList<Action> actions = getActions();

        if (this.listener == null)
        {
            this.listener = new FilteredListener<PropertyEvent>()
            {
                @Override
                public void handleTypedEvent( final PropertyEvent event )
                {
                    broadcast( new Event() );
                }
            };
        }

        element.attach( listener, getListPropertyName() );

        for (final Action action : actions)
        {
            action.getName().attach( listener );

            factory.add
            (
                new ScriptableOpenActionHandler()
                {
                    @Override
                    public void init( SapphireAction sapphireAction, ActionHandlerDef def )
                    {
                        super.init( sapphireAction, def );

                        String name = action.getName().content( true );

                        this.setLabel( empty( name ) ? "<null>" : name );
                        this.addImage( Action.TYPE.image() );
                    }

                    @Override
                    protected Scriptable scriptable( Presentation context )
                    {
                        return action;
                    }
                }
            );
        }

        return factory.result();
    }

    @Override
    public void dispose()
    {
        super.dispose();

        final Element element = getElement();
        element.detach( this.listener, getListPropertyName() );
    }

    protected ElementList<Action> getActions()
    {
        ElementList<Action> actions = null;

        final ActionTimer actionTimer = getModelElement().nearest( ActionTimer.class );

        if (actionTimer != null)
        {
            actions = actionTimer.getActions();
        }

        return actions;
    }

    protected Element getElement()
    {
        return getModelElement().nearest( ActionTimer.class );
    }

    protected String getListPropertyName()
    {
        return ActionTimer.PROP_ACTIONS.name();
    }
}
