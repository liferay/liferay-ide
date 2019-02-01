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

import java.io.File;
import java.util.List;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.upgrade.plan.tasks.core.SearchResult;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.JavaFile;
import com.liferay.ide.upgrade.problems.core.internal.JavaFileMigrator;

/**
 * @author Gregory Amerson
 */
@Component(property = {
	"file.extensions=java",
	"problem.title=Replaced the ReservedUserEmailAddressException with UserEmailAddressException",
	"problem.section=#replaced-the-reserveduseremailaddressexception-with-useremailaddressexcepti",
	"problem.summary=Replaced the ReservedUserEmailAddressException with UserEmailAddressException Inner Classes in " +
		"User Services",
	"problem.tickets=LPS-53279", "implName=ReplacedReservedUserEmailAddressException", "version=7.0"
},
	service = FileMigrator.class)
public class ReplacedReservedUserEmailAddressException extends JavaFileMigrator {

	@Override
	protected List<SearchResult> searchFile(File file, JavaFile javaFileChecker) {
		return javaFileChecker.findCatchExceptions(new String[] {"ReservedUserEmailAddressException"});
	}

}