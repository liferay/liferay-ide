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

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;

/**
 * @author Gregory Amerson
 */
public class ScriptPropertyStorage extends ValuePropertyStorage
{

    public ScriptPropertyStorage( Element modelElement, ValueProperty valueProperty )
    {
        super( modelElement, valueProperty );
    }

    @Override
    public String getName()
    {
        ScriptLanguageType scriptLanguageType =
            element().nearest( Scriptable.class ).getScriptLanguage().content( true );

        if (scriptLanguageType == null)
        {
            scriptLanguageType =
                ScriptLanguageType.valueOf( KaleoModelUtil.getDefaultValue(
                    element(), KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY ) );
        }

        DefaultValue defaultValue =
            ScriptLanguageType.class.getFields()[scriptLanguageType.ordinal()].getAnnotation( DefaultValue.class );

        return defaultValue.text();
    }

    // @Override
    // public IPath getFullPath()
    // {
    // IPath path = super.getFullPath();
    //
    // return path.removeLastSegments( 1 ).append( ".task." ).append( getName() );
    // }

}
