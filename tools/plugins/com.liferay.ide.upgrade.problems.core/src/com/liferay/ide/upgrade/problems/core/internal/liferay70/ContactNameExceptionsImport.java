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

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JavaImportsMigrator;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=Moved the Contact Name Exception Classes to Inner Classes of ContactNameException",
		"problem.summary=The use of classes ContactFirstNameException, ContactFullNameException, and " +
			"ContactLastNameException has been moved to inner classes in a new class called ContactNameException.",
		"problem.tickets=LPS-55364",
		"problem.section=#moved-the-contact-name-exception-classes-to-inner-classes-of-contactnameexc",
		"auto.correct=import", "implName=ContactNameExceptionImport", "version=7.0"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class ContactNameExceptionsImport extends JavaImportsMigrator {

	public ContactNameExceptionsImport() {
		super(_importFixes);
	}

	private static final String[] _IMPORTS = {
		"com.liferay.portal.ContactFirstNameException", "com.liferay.portal.ContactFullNameException",
		"com.liferay.portal.ContactLastNameException"
	};

	private static final String[] _IMPORTS_FIXED = {
		"com.liferay.portal.kernel.exception.ContactNameException",
		"com.liferay.portal.kernel.exception.ContactNameException",
		"com.liferay.portal.kernel.exception.ContactNameException"
	};

	private static final Map<String, String> _importFixes;

	static {
		_importFixes = new HashMap<>();

		for (int i = 0; i < _IMPORTS.length; i++) {
			_importFixes.put(_IMPORTS[i], _IMPORTS_FIXED[i]);
		}
	}

}