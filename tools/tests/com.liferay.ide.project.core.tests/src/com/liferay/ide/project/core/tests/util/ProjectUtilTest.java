/*******************************************************************************
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
 *
 *******************************************************************************/

package com.liferay.ide.project.core.tests.util;

import com.liferay.ide.project.core.util.ProjectUtil;

import java.io.File;

import java.net.URL;

import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMInstall2;
import org.eclipse.jdt.launching.IVMInstallType;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.LibraryLocation;

import org.junit.Assert;
import org.junit.Test;

public class ProjectUtilTest {

	@Test
	public void testVMCompliance() throws Exception {
		_testGetVmCompliance("1.5", "1.5.0-22");
		_testGetVmCompliance("1.6", "1.6.0-119");
		_testGetVmCompliance("1.7", "1.7.0_352");
		_testGetVmCompliance("1.8", "");
		_testGetVmCompliance("1.8", "1.8.0_482");
		_testGetVmCompliance("11", "11.0.30");
		_testGetVmCompliance("12", "12.0.2");
		_testGetVmCompliance("13", "13.0.13");
		_testGetVmCompliance("14", "14.0.2");
		_testGetVmCompliance("15", "15.0.10");
		_testGetVmCompliance("16", "16.0.1");
		_testGetVmCompliance("17", "17.0.18");
		_testGetVmCompliance("18", "18.0.2");
		_testGetVmCompliance("19", "19.0.1");
		_testGetVmCompliance("20", "20.0.1");
		_testGetVmCompliance("21", "21.0.10");
		_testGetVmCompliance("21", "22.0.2");
	}

	private void _testGetVmCompliance(String expectedCompliance, String javaVersion) throws Exception {
		IVMInstall vm;

		if (javaVersion.isEmpty()) {
			vm = new FakeLegacyVM();
		}
		else {
			vm = new FakeModernVM(javaVersion);
		}

		String actualCompliance = ProjectUtil.getVmCompliance(vm);

		Assert.assertEquals(expectedCompliance, actualCompliance);
	}

	private static class FakeLegacyVM implements IVMInstall {

		@Override
		public String getId() {
			return null;
		}

		@Override
		public File getInstallLocation() {
			return null;
		}

		@Override
		public URL getJavadocLocation() {
			return null;
		}

		@Override
		public LibraryLocation[] getLibraryLocations() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String[] getVMArguments() {
			return null;
		}

		@Override
		public IVMInstallType getVMInstallType() {
			return null;
		}

		@Override
		public IVMRunner getVMRunner(String mode) {
			return null;
		}

		@Override
		public void setInstallLocation(File installLocation) {
		}

		@Override
		public void setJavadocLocation(URL url) {
		}

		@Override
		public void setLibraryLocations(LibraryLocation[] locations) {
		}

		@Override
		public void setName(String name) {
		}

		@Override
		public void setVMArguments(String[] vmArgs) {
		}

	}

	private static class FakeModernVM implements IVMInstall, IVMInstall2 {

		public FakeModernVM(String version) {
			this.version = version;
		}

		@Override
		public String getId() {
			return null;
		}

		@Override
		public File getInstallLocation() {
			return null;
		}

		@Override
		public URL getJavadocLocation() {
			return null;
		}

		@Override
		public String getJavaVersion() {
			return version;
		}

		@Override
		public LibraryLocation[] getLibraryLocations() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public String getVMArgs() {
			return null;
		}

		@Override
		public String[] getVMArguments() {
			return null;
		}

		@Override
		public IVMInstallType getVMInstallType() {
			return null;
		}

		@Override
		public IVMRunner getVMRunner(String mode) {
			return null;
		}

		@Override
		public void setInstallLocation(File installLocation) {
		}

		@Override
		public void setJavadocLocation(URL url) {
		}

		@Override
		public void setLibraryLocations(LibraryLocation[] locations) {
		}

		@Override
		public void setName(String name) {
		}

		@Override
		public void setVMArgs(String vmArgs) {
		}

		@Override
		public void setVMArguments(String[] vmArgs) {
		}

		private String version;

	}

}