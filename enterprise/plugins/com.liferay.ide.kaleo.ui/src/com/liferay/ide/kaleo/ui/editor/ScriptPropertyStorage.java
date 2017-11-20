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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.ScriptLanguageType;
import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;

/**
 * @author Gregory Amerson
 */
public class ScriptPropertyStorage extends ValuePropertyStorage {

	public ScriptPropertyStorage(Element modelElement, ValueProperty valueProperty) {
		super(modelElement, valueProperty);
	}

	@Override
	public String getName() {
		Scriptable scriptable = element().nearest(Scriptable.class);

		Value<ScriptLanguageType> languageType = scriptable.getScriptLanguage();

		ScriptLanguageType scriptLanguageType = languageType.content(true);

		if (scriptLanguageType == null) {
			scriptLanguageType = ScriptLanguageType.valueOf(
				KaleoModelUtil.getDefaultValue(
					element(), KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY));
		}

		DefaultValue defaultValue =
			ScriptLanguageType.class.getFields()[scriptLanguageType.ordinal()].getAnnotation(DefaultValue.class);

		return defaultValue.text();
	}

	/**
	 * @Override public IPath getFullPath() { IPath path = super.getFullPath();
	 *
	 *           return path.removeLastSegments( 1 ).append( ".task." ).append(
	 *           getName() ); }
	 */
}