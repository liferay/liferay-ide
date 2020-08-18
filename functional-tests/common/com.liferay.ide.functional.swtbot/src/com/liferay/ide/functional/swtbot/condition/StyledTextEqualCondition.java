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
import org.eclipse.swtbot.swt.finder.widgets.SWTBotStyledText;

/**
 * @author Terry Jia
 */
public class StyledTextEqualCondition implements ICondition {

	public StyledTextEqualCondition(SWTBotStyledText styledText, String content, boolean equal) {
		_styledText = styledText;
		_content = content;
		_equal = equal;
	}

	public String getFailureMessage() {
		if (_equal) {
			return "wait for styled text equals " + _content + " failed"; //$NON-NLS-1$
		}

		return "wait for styled text not equals " + _content + " failed"; //$NON-NLS-1$
	}

	public void init(SWTBot bot) {
	}

	public boolean test() throws Exception {
		if (_content.equals(_styledText.getText()) == _equal) {
			return true;
		}

		return false;
	}

	private String _content;
	private boolean _equal;
	private SWTBotStyledText _styledText;

}