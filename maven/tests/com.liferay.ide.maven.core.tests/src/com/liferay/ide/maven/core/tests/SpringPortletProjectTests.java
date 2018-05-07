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
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;

import org.eclipse.core.resources.IProject;

import org.junit.Test;

/**
 * @author Terry Jia
 */
public class SpringPortletProjectTests extends LiferayMavenProjectTestCase {

	@Test
	public void testNewSpringPortletProject() throws Exception {
		NewLiferayPluginProjectOp op = _newMavenProjectOp("spring-mvc", "spring_mvc");

		final IProject newProject = base.createProject(op);

		assertNotNull(newProject);

		String pomContents = CoreUtil.readStreamToString(newProject.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<artifactId>spring-aop</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>spring-beans</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>spring-context</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>spring-core</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>spring-expression</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>spring-web</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>spring-webmvc</artifactId>"));
		assertTrue(pomContents.contains("<artifactId>spring-webmvc-portlet</artifactId>"));
	}

	private NewLiferayPluginProjectOp _newMavenProjectOp(String name, String framework) {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName(name);
		op.setProjectProvider("maven");
		op.setPortletFramework(framework);

		createTestBundleProfile(op);

		return op;
	}

}