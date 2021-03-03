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
import com.liferay.ide.upgrade.problems.core.internal.JavaImportsMigrator;

import java.io.IOException;
import java.io.InputStream;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(
	property = {
		"file.extensions=java,jsp,jspf", "problem.title=Classes in portal-service.jar moved 7.0",
		"problem.summary=Many classes from former portal-service.jar from Liferay Portal 6.x have been moved into application and framework API modules.",
		"problem.tickets=", "problem.section=#classes-in-portal-service-jar-moved", "auto.correct=import",
		"problem.version=7.0", "version=7.0"
	},
	service = {AutoFileMigrator.class, FileMigrator.class}
)
public class PortalServiceImports70 extends JavaImportsMigrator {

	public PortalServiceImports70() {
		Map<String, String> importFixes = new HashMap<>();

		for (String modularizationPropertiesFileName : _modularizationPropertiesFileNames) {
			Class<?> clazz = getClass();

			InputStream inputStream = clazz.getResourceAsStream(modularizationPropertiesFileName);

			Properties properties = new Properties();

			try {
				properties.load(inputStream);
			}
			catch (IOException ioe) {
			}

			String moduleLayout = properties.getProperty("module.layout");

			Set<Object> keys = properties.keySet();

			for (Object key : keys) {
				String value = (String)properties.get(key);

				if ((value != null) && !value.isEmpty()) {
					value = value + "," + moduleLayout;

					importFixes.put((String)key, value);
				}
			}
		}

		setImportFixes(importFixes);
	}

	private final String[] _modularizationPropertiesFileNames = {
		"modularization-asset-publisher-web.properties", "modularization-bookmarks-api.properties",
		"modularization-document-library-repository-cmis-api.properties",
		"modularization-document-library-repository-cmis-impl.properties",
		"modularization-document-library-service.properties", "modularization-dynamic-data-lists-api.properties",
		"modularization-dynamic-data-lists-service.properties", "modularization-dynamic-data-mapping-api.properties",
		"modularization-flags-api.properties", "modularization-flags-service.properties",
		"modularization-journal-api.properties", "modularization-knowledge-base-api.properties",
		"modularization-mobile-device-rules-api.properties", "modularization-mobile-device-rules-service.properties",
		"modularization-polls-api.properties", "modularization-portal-background-task-api.properties",
		"modularization-portal-cache.properties", "modularization-portal-compound-session-id.properties",
		"modularization-portal-executor.properties", "modularization-portal-jmx.properties",
		"modularization-portal-lock-api.properties", "modularization-portal-messaging.properties",
		"modularization-portal-monitoring.properties", "modularization-portal-reports-engine-api.properties",
		"modularization-portal-rules-engine-api.properties", "modularization-portal-scheduler.properties",
		"modularization-portal-scripting-api.properties", "modularization-portal-search-test.properties",
		"modularization-portal-search.properties", "modularization-portal-security-audit-api.properties",
		"modularization-portal-security-ldap.properties", "modularization-portal-tools-db-upgrade-client.properties",
		"modularization-portal-workflow-kaleo-api.properties",
		"modularization-portal-workflow-kaleo-definition-api.properties",
		"modularization-portlet-display-template.properties", "modularization-shopping-api.properties",
		"modularization-wiki-api.properties", "modularization-wiki-service.properties"
	};

}