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

import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JSPTagMigrator;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=jsp,jspf",
		"problem.title=Deprecated the liferay-security:encrypt Tag with No Direct Replacement",
		"problem.section=#deprecated-the-liferay-securityencrypt-tag-with-no-direct-replacement",
		"problem.summary=Deprecated the liferay-security:encrypt Tag with No Direct Replacement",
		"problem.tickets=LPS-63106", "problem.version=7.0", "version=7.0"
	},
	service = FileMigrator.class
)
public class DeprecatedLiferaySecurityEncryptTag extends JSPTagMigrator {

	public DeprecatedLiferaySecurityEncryptTag() {
		super(new String[0], new String[0], new String[0], new String[0], _TAG_NAMES, new String[0]);
	}

	private static final String[] _TAG_NAMES = {"liferay-security:encrypt"};

}