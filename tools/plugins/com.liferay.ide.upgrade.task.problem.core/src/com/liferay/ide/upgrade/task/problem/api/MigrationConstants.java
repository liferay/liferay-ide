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

package com.liferay.ide.upgrade.task.problem.api;

/**
 * @author Gregory Amerson
 */
public interface MigrationConstants {

	public String HELPER_PROJECT_NAME = "__migration_helper__";

	public String MARKER_ATTRIBUTE_AUTOCORRECTCONTEXT = "migrationProblem.autoCorrectContext";

	public String MARKER_ATTRIBUTE_RESOLVED = "migrationProblem.resolved";

	public String MARKER_ATTRIBUTE_SECTION = "migrationProblem.section";

	public String MARKER_ATTRIBUTE_SUMMARY = "migrationProblem.summary";

	public String MARKER_ATTRIBUTE_TICKET = "migrationProblem.ticket";

	public String MARKER_ATTRIBUTE_TIMESTAMP = "migrationProblem.timestamp";

	public String MARKER_ATTRIBUTE_TYPE = "migrationProblem.type";

	public String MARKER_TYPE = "com.liferay.ide.project.core.MigrationProblemMarker";

}