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
import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.ActionTimer;
import com.liferay.ide.kaleo.core.model.Executable;
import com.liferay.ide.kaleo.core.model.Node;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ListProperty;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireAction;
import org.eclipse.sapphire.ui.def.ActionHandlerDef;


/**
 * @author Gregory Amerson
 */
public class ActionsListAddActionHandler extends DefaultListAddActionHandler
{

    public static void addActionDefaults(Action newAction)
    {
        Node[] actions = new Node[0];

        if( newAction.nearest( Task.class ) != null )
        {
            actions = newAction.nearest( Task.class ).getTaskActions().toArray( new Node[0] );
        }
        else
        {
            actions = newAction.nearest( ActionTimer.class ).getActions().toArray( new Node[0] );
        }

        String newName = getDefaultName("newAction1", newAction, actions);
        String defaultScriptLanguage =
            KaleoModelUtil.getDefaultValue( newAction, KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY );

        newAction.setName( newName );
        newAction.setScriptLanguage( defaultScriptLanguage );
        newAction.setExecutionType( Executable.DEFAULT_EXECUTION_TYPE );

        if (newAction.nearest( Task.class ) != null)
        {
            newAction.setScript( "/* specify task action script */" );
        }
        else
        {
            newAction.setScript( "/* specify action script */" );
        }
    }

    public ActionsListAddActionHandler()
    {
        super(Action.TYPE, ActionTimer.PROP_ACTIONS);
    }

    public ActionsListAddActionHandler( ElementType type, ListProperty listProperty )
    {
        super(type, listProperty);
    }

    @Override
    public void init( SapphireAction action, ActionHandlerDef def )
    {
        super.init( action, def );

    }

    @Override
    protected Object run( final Presentation context )
    {
        Element newElement = (Element) super.run( context );
        Action newAction = newElement.nearest( Action.class );

        addActionDefaults( newAction );

        return newAction;
    }

}
