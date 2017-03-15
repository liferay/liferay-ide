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

import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
@Label( standard = "script language type" )
public enum ScriptLanguageType
{

    @Label( standard = "Beanshell" )
    @EnumSerialization( primary = "beanshell" )
    @DefaultValue( text = "script.bsh" )
    BEANSHELL,

    @Label( standard = "Drl" )
    @EnumSerialization( primary = "drl" )
    @DefaultValue( text = "script.drl" )
    DRL,

    @Label( standard = "Groovy" )
    @EnumSerialization( primary = "groovy" )
    @DefaultValue( text = "script.groovy" )
    GROOVY,

    @Label( standard = "Javascript" )
    @EnumSerialization( primary = "javascript" )
    @DefaultValue( text = "script.js" )
    JAVASCRIPT,

    @Label( standard = "Python" )
    @EnumSerialization( primary = "python" )
    @DefaultValue( text = "script.py" )
    PYTHON,

    @Label( standard = "Ruby" )
    @EnumSerialization( primary = "ruby" )
    @DefaultValue( text = "script.rb" )
    RUBY,

}
