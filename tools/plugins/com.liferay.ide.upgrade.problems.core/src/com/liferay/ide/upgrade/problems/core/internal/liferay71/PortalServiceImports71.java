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

package com.liferay.ide.upgrade.problems.core.internal.liferay71;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.internal.JavaImportsMigrator;

/**
 * @author Terry Jia
 */
@Component(property = {
	"file.extensions=java,jsp,jspf", "problem.title=Classes in portal-service.jar moved",
	"problem.summary=Many classes from former portal-service.jar from Liferay Portal 6.x have been moved into application and framework API modules.",
	"problem.tickets=", "problem.section=#classes-in-portal-service-jar-moved", "auto.correct=import",
	 "version=7.1"
},
	service = {AutoFileMigrator.class, FileMigrator.class})
public class PortalServiceImports71 extends JavaImportsMigrator {

	public PortalServiceImports71() {
		Map<String, String> importFixes = new HashMap<>();

		for (String _modularizationPropertiesFileName : _modularizationPropertiesFileNames) {
			Class<?> clazz = this.getClass();

			InputStream inputStream = clazz.getResourceAsStream(_modularizationPropertiesFileName);

			Properties properties = new Properties();

			try {
				properties.load(inputStream);
			}
			catch (IOException e) {
			}

			Set<Object> keys = properties.keySet();

			for (Object key : keys) {
				String value = (String)properties.get(key);

				if (CoreUtil.isNotNullOrEmpty(value)) {
					importFixes.put((String)key, value);
				}
			}
		}

		setImportFixes(importFixes);
	}

	private static final String[] _modularizationPropertiesFileNames = {
		"modularization-blogs-api.properties", "modularization-document-library-content-api.properties",
		"modularization-document-library-file-rank-api.properties",
		"modularization-document-library-sync-api.properties",
		"modularization-message-boards-api.properties", "modularization-petra-api.properties",
		"modularization-petra-string.properties" };

}