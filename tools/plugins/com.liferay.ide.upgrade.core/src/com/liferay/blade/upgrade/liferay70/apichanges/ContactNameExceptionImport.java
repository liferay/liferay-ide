/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.apichanges;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.ImportStatementMigrator;

import java.util.HashMap;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"file.extensions=java,jsp,jspf",
		"problem.title=Moved the Contact Name Exception Classes to Inner Classes of ContactNameException",
		"problem.summary=The use of classes ContactFirstNameException, ContactFullNameException, and ContactLastNameException has been moved to inner classes in a new class called ContactNameException.",
		"problem.tickets=LPS-55364",
		"problem.section=#moved-the-contact-name-exception-classes-to-inner-classes-of-contactnameexc",
		"auto.correct=import",
		"implName=ContactNameExceptionImport"
	},
	service = {
		AutoMigrator.class,
		FileMigrator.class
	}
)
public class ContactNameExceptionImport extends ImportStatementMigrator {

	private final static String[] IMPORTS = new String[] {
		"com.liferay.portal.ContactFirstNameException",
		"com.liferay.portal.ContactFullNameException",
		"com.liferay.portal.ContactLastNameException"
	};

	private final static String[] IMPORTS_FIXED = new String[] {
		"com.liferay.portal.kernel.exception.ContactNameException",
		"com.liferay.portal.kernel.exception.ContactNameException",
		"com.liferay.portal.kernel.exception.ContactNameException"
	};

	private final static Map<String, String> importFixes;

	static {
		importFixes = new HashMap<>();

		for (int i = 0; i < IMPORTS.length; i++) {
			importFixes.put(IMPORTS[i], IMPORTS_FIXED[i]);
		}
	}

	public ContactNameExceptionImport() {
		super(importFixes);
	}

}
