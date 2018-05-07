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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.server.remote.IRemoteServerPublisher;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.componentcore.ComponentCore;
import org.eclipse.wst.common.componentcore.resources.IVirtualComponent;

import org.junit.After;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferayMavenProjectTests extends LiferayMavenProjectTestCase {

	@After
	public void afterTestDeleteAllProjects() throws Exception {
		for (IProject project : CoreUtil.getAllProjects()) {
			project.delete(true, new NullProgressMonitor());
		}

		IProject[] projects = CoreUtil.getAllProjects();

		assertEquals(0, projects.length);
	}

	@Test
	public void testNewLiferayHookProject() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("hookproject");
		op.setProjectProvider("maven");
		op.setPluginType(PluginType.hook);

		createTestBundleProfile(op);

		final IProject project = base.createProject(op);

		assertNotNull(project);

		String pomContents = CoreUtil.readStreamToString(project.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<pluginType>hook</pluginType>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>portal-service</artifactId>"));

		waitForJobsToComplete();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		IVirtualComponent projectComponent = ComponentCore.createComponent(project);

		assertEquals("hookproject-hook", projectComponent.getDeployedName());
	}

	@Test
	public void testNewLiferayLayouttplProject() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("template");
		op.setProjectProvider("maven");
		op.setPluginType(PluginType.layouttpl);

		createTestBundleProfile(op);

		final IProject project = base.createProject(op);

		assertNotNull(project);

		String pomContents = CoreUtil.readStreamToString(project.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<pluginType>layouttpl</pluginType>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));

		waitForJobsToComplete();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		IVirtualComponent projectComponent = ComponentCore.createComponent(project);

		assertEquals("template-layouttpl", projectComponent.getDeployedName());
	}

	@Test
	public void testNewLiferayPortletProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("mvc");
		op.setProjectProvider("maven");
		op.setPortletFramework("mvc");

		createTestBundleProfile(op);

		final IProject project = base.createProject(op);

		assertNotNull(project);

		String pomContents = CoreUtil.readStreamToString(project.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<liferay.version>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>portal-service</artifactId>"));

		waitForJobsToComplete();
		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);
		waitForJobsToComplete();

		IVirtualComponent projectComponent = ComponentCore.createComponent(project);

		assertEquals("mvc-portlet", projectComponent.getDeployedName());
	}

	@Test
	public void testNewLiferayRemoteServiceBuilderProject() throws Exception {
		if (shouldSkipBundleTests()) {
			return;
		}

		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("servicebuilder");
		op.setProjectProvider("maven");
		op.setPluginType(PluginType.servicebuilder);

		createTestBundleProfile(op);

		final IProject project = base.createProject(op, op.getProjectName() + "-portlet");

		assertEquals(project.getName(), op.getProjectName() + "-portlet");

		assertNotNull(project);

		String pomContents = CoreUtil.readStreamToString(project.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<artifactId>servicebuilder-portlet</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));
		assertTrue(pomContents.contains("<name>servicebuilder Portlet</name>"));

		waitForJobsToComplete();

		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);

		waitForJobsToComplete();

		IVirtualComponent projectComponent = ComponentCore.createComponent(project);

		assertEquals("servicebuilder-portlet", projectComponent.getDeployedName());

		final ILiferayProject liferayProject = LiferayCore.create(project);

		final IRemoteServerPublisher publisher = liferayProject.adapt(IRemoteServerPublisher.class);

		final IPath warPath = publisher.publishModuleFull(monitor);

		assertTrue(warPath.toFile().exists());
	}

	@Test
	public void testNewLiferayThemeProject() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName("mytheme");
		op.setProjectProvider("maven");
		op.setPluginType(PluginType.theme);

		createTestBundleProfile(op);

		final IProject project = base.createProject(op);

		assertNotNull(project);

		String pomContents = CoreUtil.readStreamToString(project.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<pluginType>theme</pluginType>"));
		assertTrue(pomContents.contains("<artifactId>liferay-maven-plugin</artifactId>"));

		waitForJobsToComplete();

		project.build(IncrementalProjectBuilder.INCREMENTAL_BUILD, monitor);

		waitForJobsToComplete();

		IVirtualComponent projectComponent = ComponentCore.createComponent(project);

		assertEquals("mytheme-theme", projectComponent.getDeployedName());
	}

}