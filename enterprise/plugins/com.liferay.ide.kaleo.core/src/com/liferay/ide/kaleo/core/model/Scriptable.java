/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
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
@Image(path = "images/script_16x16.gif")
public interface Scriptable extends Element {

	public ElementType TYPE = new ElementType(Scriptable.class);

	public Value<String> getScript();

	public Value<ScriptLanguageType> getScriptLanguage();

	public Value<String> getScriptRequiredContexts();

	public void setScript(String value);

	public void setScriptLanguage(ScriptLanguageType scriptLanguage);

	public void setScriptLanguage(String scriptLanguage);

	public void setScriptRequiredContexts(String value);

	@CustomXmlValueBinding(impl = CDataValueBindingImpl.class)
	@Label(standard = "&script")
	@LongString
	@Whitespace(trim = false)
	@XmlBinding(path = "script")
	public ValueProperty PROP_SCRIPT = new ValueProperty(TYPE, "Script");

	@Label(standard = "script &language")
	@Type(base = ScriptLanguageType.class)
	@XmlBinding(path = "script-language")
	public ValueProperty PROP_SCRIPT_LANGUAGE = new ValueProperty(TYPE, "ScriptLanguage");

	@Label(standard = "script required contexts")
	@VersionCompatibility("[6.2.0")
	@XmlBinding(path = "script-required-contexts")
	public ValueProperty PROP_SCRIPT_REQUIRED_CONTEXTS = new ValueProperty(TYPE, "ScriptRequiredContexts");

}