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

import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.BaseLiferayDescriptorVersion;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(
	property = {
		"file.extensions=xml", "problem.title=Descriptor XML DTD Versions Changes 7.0",
		"problem.summary=The descriptor XML DTD versions should be matched with version 7.0.",
		"problem.section=#descriptor-XML-DTD-version", "auto.correct=descriptor", "problem.version=7.0",
		"version=[7.0,7.0]"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class Liferay70DescriptorVersion extends BaseLiferayDescriptorVersion {

	public Liferay70DescriptorVersion() {
		super(_publicIDPattern, "7.0.0");
	}

	private static final Pattern _publicIDPattern = Pattern.compile(
		"-\\//(?:[A-z]+)\\//(?:[A-z]+)[\\s+(?:[A-z0-9_]*)]*\\s+(6\\.[1-2]\\.0)\\//(?:[A-z]+)",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

}