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

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.project.core.IPortletFramework;
import com.liferay.ide.project.core.NewLiferayProjectProvider;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.model.ProjectName;
import com.liferay.ide.project.core.tests.ProjectCoreBase;
import com.liferay.ide.sdk.core.SDK;
import com.liferay.ide.sdk.core.SDKUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.EnablementService;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.platform.PathBridge;
import org.eclipse.sapphire.services.ValidationService;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class NewLiferayPluginProjectMavenTests extends ProjectCoreBase {

	@Test
	public void testCreateNewMavenProject() throws Exception {
		createMavenProjectName("test-name-1");
	}

	@Test
	public void testCreateProjectNameWithSpaces() throws Exception {
		createMavenProjectName("Test With Spaces");
	}

	@Test
	public void testCreateProjectNameWithUnderscores() throws Exception {
		createMavenProjectName("test_name_1");
	}

	@Test
	public void testGroupIdValidation() throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-group-id-validation");

		op.setProjectProvider("maven");

		ValidationService vs = op.getGroupId().service(ValidationService.class);

		op.setGroupId(".com.liferay.test");

		String expected = "A package name cannot start or end with a dot";

		Value<String> groupId = op.getGroupId();

		Assert.assertEquals(expected, vs.validation().message());
		Assert.assertEquals(expected, groupId.validation().message());

		op.setGroupId("com.life*ray.test");

		String expected2 = "'life*ray' is not a valid Java identifier";

		groupId = op.getGroupId();

		Assert.assertEquals(expected2, vs.validation().message());
		Assert.assertEquals(expected2, groupId.validation().message());
	}

	@Test
	public void testHookProjectName() throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-hook");

		op.setProjectProvider("maven");
		op.setPluginType(PluginType.hook);
		op.setUseDefaultLocation(true);

		IProject expectedProject = createMavenProject(op);

		String expectedProjectName = expectedProject.getName();

		ElementList<ProjectName> names = op.getProjectNames();

		ProjectName name = names.get(0);

		String actualProjectName = name.getName().content();

		Assert.assertEquals(expectedProjectName, actualProjectName);
	}

	@Test
	@Ignore
	public void testLocationValidation() throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-location-validation-service");

		op.setProjectProvider("maven");
		op.setPluginType("portlet");
		op.setUseDefaultLocation(false);

		ValidationService vs = op.getLocation().service(ValidationService.class);

		String invalidLocation = null;

		invalidLocation = "not-absolute-location";

		op.setLocation(invalidLocation);

		String expected = "\"" + invalidLocation + "\" is not an absolute path.";

		Assert.assertEquals(_normalize(expected), _normalize(vs.validation().message()));

		Value<Path> location = op.getLocation();

		Assert.assertEquals(_normalize(expected), _normalize(location.validation().message()));

		if (CoreUtil.isWindows()) {
			invalidLocation = "Z:\\test-location-validation-service";
		}
		else {
			invalidLocation = "/test-location-validation-service";
		}

		op.setLocation(invalidLocation);

		String expected2 = "Cannot create project content at \"" + invalidLocation + "\"";

		Assert.assertEquals(expected2, vs.validation().message());

		Value<Path> location2 = op.getLocation();

		Assert.assertEquals(expected2, location2.validation().message());

		if (CoreUtil.isWindows()) {
			invalidLocation = CoreUtil.getWorkspaceRootLocation().getDevice() + "\\";
		}
		else {
			invalidLocation = "/";
		}

		op.setLocation(invalidLocation);

		String expected3 = "Project location is not empty or a parent pom.";

		Assert.assertEquals(expected3, vs.validation().message());

		// IDE-2069

		IPath path = getLiferayRuntimeDir();

		invalidLocation = path.removeLastSegments(2).toOSString();

		op.setLocation(invalidLocation);

		String expected5 = "Project location is not empty or a parent pom.";

		Assert.assertEquals(expected5, vs.validation().message());

		op.setLocation("");

		String expected4 = "Location must be specified.";

		Assert.assertEquals(expected4, vs.validation().message());

		Value<Path> location4 = op.getLocation();

		Assert.assertEquals(expected4, location4.validation().message());
	}

	@Test
	public void testParentFolderLocationValidation() throws Exception {
		File parentFolder = CoreUtil.getWorkspaceRootLocation(
		).append(
			"test-parent-folder-location-validation-service"
		).toFile();

		if (!parentFolder.exists()) {
			parentFolder.mkdir();
		}

		File pomFile = new File("projects/validations/location/pom.xml");

		FileUtil.copyFileToDir(pomFile, parentFolder);

		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("test-parent-folder-location-validation-service");

		ValidationService vs = op.getLocation().service(ValidationService.class);

		op.setProjectProvider("maven");
		op.setPluginType("portlet");
		op.setUseDefaultLocation(false);
		op.setLocation(parentFolder.getAbsolutePath());

		String projectName = op.getProjectName().content();

		String expected = "The project name \"" + projectName + "\" can't be the same as the parent.";

		Assert.assertEquals(expected, vs.validation().message());

		Value<Path> location = op.getLocation();

		Assert.assertEquals(expected, location.validation().message());
	}

	@Test
	@Ignore
	public void testPortletFrameworkValidation() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-portlet-framework-validation");

		op.setPluginType("portlet");

		ValidationService vs = op.getPortletFramework().service(ValidationService.class);

		Assert.assertEquals(true, vs.validation().ok());

		SDK newSDK = createNewSDK();

		newSDK.setVersion("6.0.0");

		IPortletFramework jsf = ProjectCore.getPortletFramework("jsf");

		op.setProjectProvider("ant");
		op.setPortletFramework("jsf");

		Assert.assertEquals(
			"Selected portlet framework requires SDK version at least " + jsf.getRequiredSDKVersion(),
			vs.validation().message());
	}

	@Test
	public void testPortletProjectName() throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-name");

		op.setProjectProvider("maven");
		op.setPluginType(PluginType.portlet);
		op.setUseDefaultLocation(true);
		op.setPortletFramework("mvc");
		op.setPortletName("testPortlet");

		IProject expectedProject = createMavenProject(op);

		String expectedProjectName = expectedProject.getName();

		ElementList<ProjectName> names = op.getProjectNames();

		ProjectName name = names.get(0);

		String actualProjectName = name.getName().content();

		Assert.assertEquals(expectedProjectName, actualProjectName);
	}

	@Test
	public void testProjectNameListener() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("");

		SDK sdk = SDKUtil.createSDKFromLocation(getLiferayPluginsSdkDir());

		String projectName = "test-project-name-listener";
		String projectName2 = "test-project-name-listener-2";

		op.setProjectProvider("ant");
		op.setUseDefaultLocation(true);
		op.setPluginType("portlet");

		IPath exceptedLocation = null;

		op.setProjectName(projectName);

		IPath location = sdk.getLocation();

		exceptedLocation = location.append("portlets").append(projectName + "-portlet");

		Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));

		op.setProjectName(projectName2);

		exceptedLocation = location.append("portlets").append(projectName2 + "-portlet");

		Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));

		op.setProjectProvider("maven");

		op.setProjectName(projectName);

		exceptedLocation = CoreUtil.getWorkspaceRootLocation().append(projectName);

		Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));

		op.setProjectName(projectName2);

		exceptedLocation = CoreUtil.getWorkspaceRootLocation().append(projectName2);

		Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));
	}

	@Test
	public void testProjectProviderListener() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-project-provider-listener");

		String projectName = op.getProjectName().content();

		op.setPluginType("portlet");
		op.setUseDefaultLocation(true);

		SDK sdk = SDKUtil.createSDKFromLocation(getLiferayPluginsSdkDir());

		IPath exceptedLocation = null;

		op.setProjectProvider("ant");

		IPath location = sdk.getLocation();

		exceptedLocation = location.append("portlets").append(projectName + "-portlet");

		Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));

		op.setProjectProvider("maven");

		exceptedLocation = CoreUtil.getWorkspaceRootLocation().append(projectName);

		Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));
	}

	@Test
	public void testProjectProviderPossibleValues() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = newProjectOp("test-project-provider-possbile-values");

		Value<NewLiferayProjectProvider<NewLiferayPluginProjectOp>> provider = op.getProjectProvider();

		Set<String> acturalValues = provider.service(PossibleValuesService.class).values();

		Assert.assertNotNull(acturalValues);

		Set<String> exceptedValues = new HashSet<>();

		exceptedValues.add("ant");
		exceptedValues.add("maven");

		Assert.assertEquals(true, exceptedValues.containsAll(acturalValues));
		Assert.assertEquals(true, acturalValues.containsAll(exceptedValues));
	}

	@Test
	public void testServiceBuilderProjectName() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("test-name");
		op.setProjectProvider("maven");
		op.setPluginType(PluginType.servicebuilder);

		IProject project = base.createProject(op, op.getProjectName() + "-portlet");

		waitForBuildAndValidation(project);

		String projectName = project.getName();

		String finalProjectName = projectName.substring(0, projectName.lastIndexOf("-"));

		ElementList<ProjectName> projectNames = op.getProjectNames();

		List<String> finalProjectnames = new ArrayList<>();

		for (ProjectName expectedProjectName : projectNames) {
			finalProjectnames.add(expectedProjectName.getName().content());
		}

		Assert.assertEquals(true, finalProjectnames.contains(finalProjectName));
		Assert.assertEquals(true, finalProjectnames.contains(finalProjectName + "-portlet"));
		Assert.assertEquals(true, finalProjectnames.contains(finalProjectName + "-portlet-service"));
	}

	@Test
	public void testUseDefaultLocationEnablement() throws Exception {
		testUseDefaultLocationEnablement(true);
	}

	public void testUseDefaultLocationEnablement(boolean versionRestriction) throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-use-default-location-enablement");

		op.setProjectProvider("maven");

		Value<Boolean> useDefaultLocation = op.getUseDefaultLocation();

		Assert.assertEquals(true, useDefaultLocation.service(EnablementService.class).enablement());
		Assert.assertEquals(true, useDefaultLocation.enabled());

		if (versionRestriction) {
			op.setProjectProvider("ant");

			useDefaultLocation = op.getUseDefaultLocation();

			Assert.assertEquals(false, useDefaultLocation.service(EnablementService.class).enablement());
			Assert.assertEquals(false, useDefaultLocation.enabled());
		}
	}

	@Test
	public void testUseDefaultLocationListener() throws Exception {
		testUseDefaultLocationListener(false);
	}

	protected IProject createMavenProject(NewLiferayPluginProjectOp op) throws Exception {
		IProject project = createProject(op);

		waitForBuildAndValidation(project);

		Assert.assertEquals(true, project.getFolder("src").exists());

		return project;
	}

	protected void createMavenProjectName(String projectName) throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName(projectName);
		op.setProjectProvider("maven");

		createMavenProject(op);
	}

	protected void testUseDefaultLocationListener(boolean versionRestriction) throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-use-default-location-listener");

		String projectName = op.getProjectName().content();

		op.setProjectProvider("maven");

		IPath exceptedLocation = null;

		op.setUseDefaultLocation(true);

		exceptedLocation = CoreUtil.getWorkspaceRootLocation().append(projectName);

		Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));

		op.setUseDefaultLocation(false);

		Assert.assertEquals(null, op.getLocation().content());

		if (versionRestriction) {
			op.setProjectProvider("ant");
			op.setPluginType("portlet");
			op.setUseDefaultLocation(true);

			SDK sdk = SDKUtil.createSDKFromLocation(getLiferayPluginsSdkDir());

			IPath location = sdk.getLocation();

			exceptedLocation = location.append("portlets").append(projectName + "-portlet");

			Assert.assertEquals(exceptedLocation, PathBridge.create(op.getLocation().content()));

			op.setUseDefaultLocation(false);

			Assert.assertEquals(null, op.getLocation().content());
		}
	}

	protected final ProjectCoreBase base = new ProjectCoreBase();

	private String _normalize(String val) {
		return val.replaceAll("\\\\", "/");
	}

}