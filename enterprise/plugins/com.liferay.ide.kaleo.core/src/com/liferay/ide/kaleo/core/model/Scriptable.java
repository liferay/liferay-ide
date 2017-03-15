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

import com.liferay.ide.kaleo.core.model.internal.CDataValueBindingImpl;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.VersionCompatibility;
import org.eclipse.sapphire.modeling.annotations.Image;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.LongString;
import org.eclipse.sapphire.modeling.annotations.Whitespace;
import org.eclipse.sapphire.modeling.xml.annotations.CustomXmlValueBinding;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
@Image( path = "images/script_16x16.gif" )
public interface Scriptable extends Element
{
    ElementType TYPE = new ElementType( Scriptable.class );

    // *** properties ***

    @LongString
    @Whitespace(trim = false)
    @Label( standard = "&script" )
    @XmlBinding( path = "script" )
    @CustomXmlValueBinding( impl = CDataValueBindingImpl.class )
    ValueProperty PROP_SCRIPT = new ValueProperty( TYPE, "Script" );

    Value<String> getScript();
    void setScript( String value );

    @Type( base = ScriptLanguageType.class )
    @Label( standard = "script &language" )
    @XmlBinding( path = "script-language" )
    ValueProperty PROP_SCRIPT_LANGUAGE = new ValueProperty( TYPE, "ScriptLanguage" );

    Value<ScriptLanguageType> getScriptLanguage();
    void setScriptLanguage( String scriptLanguage );
    void setScriptLanguage( ScriptLanguageType scriptLanguage );

    // *** ScriptRequiredContexts ***

    @Label( standard = "script required contexts" )
    @XmlBinding( path = "script-required-contexts" )
    @VersionCompatibility( "[6.2.0" )
    ValueProperty PROP_SCRIPT_REQUIRED_CONTEXTS = new ValueProperty( TYPE, "ScriptRequiredContexts" );

    Value<String> getScriptRequiredContexts();
    void setScriptRequiredContexts( String value );

}
