/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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

package com.liferay.portal.security.auth;

import com.liferay.portal.kernel.util.CharPool;
import com.liferay.portal.kernel.util.StringUtil;
import com.liferay.portal.kernel.util.Validator;

/**
 * @author Brian Wing Shun Chan
 */
public class DefaultScreenNameValidator implements ScreenNameValidator {

	public static final String CYRUS = "cyrus";

	public static final String POSTFIX = "postfix";

	@Override
	public boolean validate(long companyId, String screenName) {
		if (Validator.isEmailAddress(screenName) ||
			StringUtil.equalsIgnoreCase(screenName, CYRUS) ||
			StringUtil.equalsIgnoreCase(screenName, POSTFIX) ||
			(screenName.indexOf(CharPool.SLASH) != -1) ||
			(screenName.indexOf(CharPool.UNDERLINE) != -1)) {

			return false;
		}
		else {
			return true;
		}
	}

}
