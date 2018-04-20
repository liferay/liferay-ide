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

package com.liferay.ide.bndtools.core.templates;

import aQute.bnd.build.model.BndEditModel;
import aQute.bnd.build.model.clauses.ExportedPackage;
import aQute.bnd.header.Attrs;

import java.util.Collections;

import org.apache.commons.lang.WordUtils;

import org.bndtools.api.ProjectPaths;

/**
 * @author Gregory Amerson
 */
public abstract class AbstractProjectTemplate {

	public AbstractProjectTemplate() {
	}

	public void modifyInitialBndModel(BndEditModel model, String projectName, ProjectPaths projectPaths) {
		model.setBundleVersion("1.0.0.${tstamp}");
		model.setBundleDescription(safeJavaClassName(projectName));
		model.setExportedPackages(
			Collections.singletonList(new ExportedPackage(safePackageName(projectName), Attrs.EMPTY_ATTRS)));
	}

	protected String safeJavaClassName(String projectName) {
		String javaClassName = WordUtils.capitalizeFully(projectName, new char[] {'_', '-', '.', ' '});

		return javaClassName.replaceAll("[_|\\-|\\.|\\s+]", "");
	}

	protected String safePackageName(String projectName) {
		return projectName.replaceAll("[_|\\-|\\s+]", "");
	}

}