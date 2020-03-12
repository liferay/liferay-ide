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

package com.liferay.ide.upgrade.problems.core.internal.liferay72;

import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.BaseLiferayDescriptorVersion;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=xml", "problem.title=Descriptor XML DTD Versions Changes 7.2",
		"problem.summary=The descriptor XML DTD versions should be matched with version 7.2.",
		"problem.section=#descriptor-XML-DTD-version", "auto.correct=descriptor", "version=7.2"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class Liferay72DescriptorVersion extends BaseLiferayDescriptorVersion {

	public Liferay72DescriptorVersion() {
		super(_publicPattern, "7.2.0");
	}

	private static final Pattern _publicPattern = Pattern.compile(
		"-\\//(?:[A-z]+)\\//(?:[A-z]+)[\\s+(?:[A-z0-9_]*)]*\\s+(7\\.[1-9]\\.[0-9])\\//(?:[A-z]+)",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

}