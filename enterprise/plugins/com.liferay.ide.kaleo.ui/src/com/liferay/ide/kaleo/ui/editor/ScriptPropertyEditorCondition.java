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
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.SapphireCondition;

/**
 * @author Gregory Amerson
 */
public class ScriptPropertyEditorCondition extends SapphireCondition {

	@Override
	protected boolean evaluate() {
		if (_paramType != null) {
			Scriptable scriptable = _scriptable();

			ScriptLanguageType scriptLanguageType = scriptable.getScriptLanguage().content(true);

			if (scriptLanguageType == null) {
				scriptLanguageType = ScriptLanguageType.valueOf(
					KaleoModelUtil.getDefaultValue(
						scriptable, KaleoCore.DEFAULT_SCRIPT_LANGUAGE_KEY, ScriptLanguageType.GROOVY));
			}

			if (_paramType.equals(scriptLanguageType)) {
				return true;
			}
		}

		return false;
	}

	@Override
	protected void initCondition(ISapphirePart part, String parameter) {
		super.initCondition(part, parameter);

		/*
		 * TODO replace with visible when
		 *
		 * SapphireIfElseDirectiveDef ifDef =
		 * this.getPart().definition().nearest( SapphireIfElseDirectiveDef.class
		 * );
		 *
		 * String param = ifDef.getConditionParameter().content();
		 *
		 * for( ScriptLanguageType type :
		 * ScriptLanguageType.class.getEnumConstants() ) { if(
		 * type.name().equals( param ) ) { this.paramType = type; break; } }
		 */
		Scriptable scriptable = _scriptable();

		Listener listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				ScriptPropertyEditorCondition.this.updateConditionState();
			}

		};

		scriptable.attach(listener, "ScriptLanguage");
	}

	private Scriptable _scriptable() {
		Element element = getPart().getLocalModelElement();

		return element.nearest(Scriptable.class);
	}

	private ScriptLanguageType _paramType;

}