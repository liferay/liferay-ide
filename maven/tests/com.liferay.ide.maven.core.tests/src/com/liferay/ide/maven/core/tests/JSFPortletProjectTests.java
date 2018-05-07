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

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayProfile;
import com.liferay.ide.project.core.model.ProfileLocation;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;

import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class JSFPortletProjectTests extends LiferayMavenProjectTestCase {

	@Test
	public void testNewAlloyPortletProject() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp("liferay_faces_alloy", "jsf-2.x", "liferay_faces_alloy");

		IProject newProject = base.createProject(op);

		assertNotNull(newProject);

		String pomContents = CoreUtil.readStreamToString(newProject.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<artifactId>liferay-faces-alloy</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-faces-bridge-impl</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-faces-portal</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>portal-service</artifactId>"));
	}

	@Test
	public void testNewIcefacesPortletProject() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp("icefaces", "jsf-2.x", "icefaces");

		IProject newProject = base.createProject(op);

		assertNotNull(newProject);

		String pomContents = CoreUtil.readStreamToString(newProject.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<artifactId>icefaces</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-faces-bridge-impl</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-faces-portal</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>portal-service</artifactId>"));
	}

	@Test
	public void testNewJSFPortletProject() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp("jsf-2.x", "jsf-2.x", "jsf");

		IProject newProject = base.createProject(op);

		assertNotNull(newProject);

		String pomContents = CoreUtil.readStreamToString(newProject.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<artifactId>liferay-faces-bridge-impl</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-faces-portal</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>portal-service</artifactId>"));
	}

	@Test
	public void testNewPrimefacesPortletProject() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp("primefaces", "jsf-2.x", "primefaces");

		IProject newProject = base.createProject(op);

		assertNotNull(newProject);

		String pomContents = CoreUtil.readStreamToString(newProject.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<artifactId>primefaces</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-faces-bridge-impl</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-faces-portal</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>portal-service</artifactId>"));
	}

	@Test
	public void testUpdateLiferayPortletFileDTDVersion() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("test-update-liferay-portlet-file");
		op.setProjectProvider("maven");
		op.setPortletFramework("jsf-2.x");
		op.setPortletFrameworkAdvanced("jsf");

		NewLiferayProfile profile = op.getNewLiferayProfiles().insert();

		profile.setLiferayVersion("6.1.0");
		profile.setId("test-bundle");
		profile.setRuntimeName("6.1.0");
		profile.setProfileLocation(ProfileLocation.projectPom);

		op.setActiveProfilesValue("test-bundle");

		IProject newProject = base.createProject(op);

		assertNotNull(newProject);

		IWebProject webProject = LiferayCore.create(IWebProject.class, newProject);

		IFile descriptorFile = webProject.getDescriptorFile("liferay-portlet.xml");

		assertNotNull(descriptorFile);
		assertTrue(descriptorFile.exists());

		String contents = CoreUtil.readStreamToString(descriptorFile.getContents());

		assertTrue(contents.contains("PUBLIC \"-//Liferay//DTD Portlet Application 6.1.0//EN\""));
		assertTrue(contents.contains("http://www.liferay.com/dtd/liferay-portlet-app_6_1_0.dtd"));
	}

	private NewLiferayPluginProjectOp _newMavenProjectOp(String name, String framework, String advFramework) {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName(name);
		op.setProjectProvider("maven");
		op.setPortletFramework(framework);
		op.setPortletFrameworkAdvanced(advFramework);

		createTestBundleProfile(op);

		return op;
	}

}