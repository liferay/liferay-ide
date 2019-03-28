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

package com.liferay.ide.upgrade.commands.ui.internal;

import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.commands.core.MessagePrompt;

import org.osgi.service.component.annotations.Component;

/**
 * @author Terry Jia
 * @author Gregory Amerson
 */
@Component(service = MessagePrompt.class)
public class MessagePromptImpl implements MessagePrompt {

	@Override
	public boolean prompt(String title, String message) {
		return UIUtil.promptQuestion(title, message);
	}

}