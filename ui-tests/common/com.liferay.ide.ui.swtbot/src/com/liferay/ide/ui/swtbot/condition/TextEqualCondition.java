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

package com.liferay.ide.ui.swtbot.condition;

import org.eclipse.swtbot.swt.finder.SWTBot;
import org.eclipse.swtbot.swt.finder.waits.ICondition;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotText;

/**
 * @author Terry Jia
 */
public class TextEqualCondition implements ICondition {

	public TextEqualCondition(SWTBotText text, String content, boolean equal) {
		_text = text;
		_content = content;
		_equal = equal;
	}

	public String getFailureMessage() {
		if (_equal) {
			return "wait for text content equals " + _content + " failed"; //$NON-NLS-1$
		}
		else {
			return "wait for text content equals not " + _content + " failed"; //$NON-NLS-1$
		}
	}

	public void init(SWTBot bot) {
	}

	public boolean test() throws Exception {
		if (_content.equals(_text.getText()) == _equal) {
			return true;
		}

		return false;
	}

	private String _content;
	private boolean _equal;
	private SWTBotText _text;

}