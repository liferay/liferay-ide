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

package com.liferay.ide.gradle.core;

import java.io.File;

import java.nio.file.Path;

import java.util.Arrays;
import java.util.List;

import org.eclipse.buildship.core.invocation.InvocationCustomizer;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 * @author Simon Jiang
 */
public class LiferayGradleInvocationCustomizer implements InvocationCustomizer {

	@Override
	public List<String> getExtraArguments() {
		IVMInstall defaultVMInstall = JavaRuntime.getDefaultVMInstall();

		File vmInstallLocation = defaultVMInstall.getInstallLocation();

		Path vmInstallPath = vmInstallLocation.toPath();

		return Arrays.asList("-Dorg.gradle.java.home=" + vmInstallPath.toString());
	}

}