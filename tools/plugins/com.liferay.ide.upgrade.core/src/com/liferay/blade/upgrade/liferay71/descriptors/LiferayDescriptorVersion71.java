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

package com.liferay.blade.upgrade.liferay71.descriptors;

import com.liferay.blade.api.AutoMigrator;
import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.upgrade.liferay70.apichanges.BaseLiferayDescriptorVersion;

import java.util.regex.Pattern;

import org.osgi.service.component.annotations.Component;

/**
 * @author Seiphon Wang
 */
@Component(property = {
	"file.extensions=xml", "problem.title=Descriptor XML DTD Versions Changes",
	"problem.summary=The descriptor XML DTD versions should be matched with version 7.1.",
	"problem.section=#descriptor-XML-DTD-version", "auto.correct=descriptor", "version=7.1"
},
	service = {AutoMigrator.class, FileMigrator.class})
public class LiferayDescriptorVersion71 extends BaseLiferayDescriptorVersion {

	public LiferayDescriptorVersion71() {
		super(_publicPattern, "7.1.0");
	}

	private static final Pattern _publicPattern = Pattern.compile(
		"-\\//(?:[A-z]+)\\//(?:[A-z]+)[\\s+(?:[A-z0-9_]*)]*\\s+(7\\.[1-9]\\.[0-9])\\//(?:[A-z]+)",
		Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

}