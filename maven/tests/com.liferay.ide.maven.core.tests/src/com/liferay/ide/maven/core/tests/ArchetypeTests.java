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
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;

import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class ArchetypeTests extends AbstractMavenProjectTestCase {

	@Test
	public void testArchetypeCustomValue() throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-archetype-value");

		op.setProjectProvider("maven");
		op.setPluginType("hook");
		op.setArchetype("com.liferay.maven.archetypes:liferay-hook-archetype:6.2.10.9");

		IProject project = createMavenProject(op);

		String pomContents = CoreUtil.readStreamToString(project.getFile("pom.xml").getContents());

		assertTrue(pomContents.contains("<pluginType>hook</pluginType>"));
	}

	@Test
	public void testArchetypeDefaultValueService() throws Exception {
		NewLiferayPluginProjectOp op = newProjectOp("test-archetype-default-value-service");

		op.setProjectProvider("maven");

		assertEquals("com.liferay.maven.archetypes:liferay-portlet-archetype:6.2.5", op.getArchetype().content());

		op.setPortletFramework("jsf-2.x");

		assertEquals("com.liferay.maven.archetypes:liferay-portlet-jsf-archetype:6.2.5", op.getArchetype().content());

		op.setPortletFrameworkAdvanced("primefaces");

		assertEquals(
			"com.liferay.maven.archetypes:liferay-portlet-primefaces-archetype:6.2.5", op.getArchetype().content());
	}

	protected IProject createMavenProject(NewLiferayPluginProjectOp op) throws Exception {
		IProject project = new ProjectCoreBase().createProject(op);

		assertEquals(true, project.getFolder("src").exists());

		return project;
	}

	protected NewLiferayPluginProjectOp newProjectOp(String projectName) throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName(projectName);

		return op;
	}

}