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

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireCondition;

/**
 * @author Gregory Amerson
 */
public class ScriptPropertyEditorCondition extends SapphireCondition
{

    private ScriptLanguageType paramType;

    @Override
    protected boolean evaluate()
    {
        if( paramType != null )
        {
            Scriptable scriptable = scriptable();

            ScriptLanguageType scriptLanguageType = scriptable.getScriptLanguage().content( true );

            if (scriptLanguageType == null)
            {
                scriptLanguageType =
                    ScriptLanguageType.valueOf( KaleoModelUtil.getDefaultValue(
                        scriptable, KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY ) );
            }

            if( paramType.equals( scriptLanguageType ) )
            {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void initCondition( ISapphirePart part, String parameter )
    {
        super.initCondition( part, parameter );

        // TODO replace with visible when

//        SapphireIfElseDirectiveDef ifDef = this.getPart().definition().nearest( SapphireIfElseDirectiveDef.class );

//        String param = ifDef.getConditionParameter().content();

//        for( ScriptLanguageType type : ScriptLanguageType.class.getEnumConstants() )
//        {
//            if( type.name().equals( param ) )
//            {
//                this.paramType = type;
//                break;
//            }
//        }

        Scriptable scriptable = scriptable();

        Listener listener = new FilteredListener<PropertyContentEvent>()
        {
            @Override
            protected void handleTypedEvent( final PropertyContentEvent event )
            {
                ScriptPropertyEditorCondition.this.updateConditionState();
            }
        };

        scriptable.attach( listener, "ScriptLanguage" );
    }

    private Scriptable scriptable()
    {
        return this.getPart().getLocalModelElement().nearest( Scriptable.class );
    }

}
