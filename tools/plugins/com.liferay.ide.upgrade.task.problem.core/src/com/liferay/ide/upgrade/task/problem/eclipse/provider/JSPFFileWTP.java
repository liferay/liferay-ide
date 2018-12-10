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

package com.liferay.ide.upgrade.task.problem.eclipse.provider;

import com.liferay.ide.upgrade.task.problem.api.JSPFile;
import com.liferay.ide.upgrade.task.problem.api.JavaFile;

import java.io.File;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = "file.extension=jspf", service = {JavaFile.class, JSPFile.class})
public class JSPFFileWTP extends JSPFileWTP {

	public JSPFFileWTP() {
	}

	public JSPFFileWTP(File file) {
		super(file);
	}

}