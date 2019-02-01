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

package com.liferay.ide.upgrade.problems.core.internal.liferay70;

import com.liferay.ide.upgrade.problems.core.AutoMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=jsp,jspf",
	"problem.title=Deprecated the liferay-ui:captcha Tag and Replaced with liferay-captcha:captcha",
	"problem.section=#deprecated-the-liferay-uicaptcha-tag-and-replaced-with-liferay-captchacaptc",
	"problem.summary=Deprecated the liferay-ui:captcha Tag and Replaced with liferay-captcha:captcha",
	"problem.tickets=LPS-69383", "auto.correct=jsptag", "implName=DeprecatedLiferayUICaptchaTags", "version=7.0"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class DeprecatedLiferayUICaptchaTags extends JSPTagMigrator {

	public DeprecatedLiferayUICaptchaTags() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, _NEW_TAG_NAMES);
	}

	private static final String[] _NEW_TAG_NAMES = {"liferay-captcha:captcha"};

	private static final String[] _TAG_NAMES = {"liferay-ui:captcha"};

}