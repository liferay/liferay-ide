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

import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.EnumSerialization;
import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
@Label(standard = "script language type")
public enum ScriptLanguageType {

	@Label(standard = "Beanshell")
		@EnumSerialization(primary = "beanshell")
		@DefaultValue(text = "script.bsh")
	BEANSHELL,

	@Label(standard = "Drl")
		@EnumSerialization(primary = "drl")
		@DefaultValue(text = "script.drl")
	DRL,

	@Label(standard = "Groovy")
		@EnumSerialization(primary = "groovy")
		@DefaultValue(text = "script.groovy")
	GROOVY,

	@Label(standard = "Javascript")
		@EnumSerialization(primary = "javascript")
		@DefaultValue(text = "script.js")
	JAVASCRIPT,

	@Label(standard = "Python")
		@EnumSerialization(primary = "python")
		@DefaultValue(text = "script.py")
	PYTHON,

	@Label(standard = "Ruby")
		@EnumSerialization(primary = "ruby")
		@DefaultValue(text = "script.rb")
	RUBY,

}