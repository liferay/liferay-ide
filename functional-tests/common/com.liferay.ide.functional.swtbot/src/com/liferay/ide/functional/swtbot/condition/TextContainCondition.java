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

package com.liferay.ide.functional.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/**
 * @author Terry Jia
 */
public class TextContainCondition implements ICondition {

	public TextContainCondition(SWTBotText text, String content, boolean contain) {
		_text = text;
		_content = content;
		_contain = contain;
	}

	public String getFailureMessage() {
		if (_contain) {
			return "wait for text content contains " + _content + " failed"; //$NON-NLS-1$
		}
		else {
			return "wait for text content contains not " + _content + " failed"; //$NON-NLS-1$
		}
	}

	public void init(SWTBot bot) {
	}

	public boolean test() throws Exception {
		String text = _text.getText();

		if (text.contains(_content) == _contain) {
			return true;
		}

		return false;
	}

	private boolean _contain;
	private String _content;
	private SWTBotText _text;

}