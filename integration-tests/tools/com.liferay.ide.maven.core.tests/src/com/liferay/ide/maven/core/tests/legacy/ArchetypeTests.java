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

package com.liferay.ide.maven.core.tests.legacy;

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.test.project.core.base.ProjectOpBase;

import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class ArchetypeTests extends ProjectOpBase<NewLiferayPluginProjectOp> {

	@Test
	public void testArchetypeCustomValue() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setPluginType("hook");
		op.setArchetype("com.liferay.maven.archetypes:liferay-hook-archetype:6.2.10.9");

		createOrImportAndBuild(op, project.getName());

		assertProjectFileContains(project.getName(), "pom.xml", "<pluginType>hook</pluginType>");

		deleteProject(project.getName());
	}

	@Test
	public void testArchetypeDefaultValueService() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectProvider("maven");

		assertComponentValue(op.getArchetype(), "com.liferay.maven.archetypes:liferay-portlet-archetype:6.2.5");

		op.setPortletFramework("jsf-2.x");

		assertComponentValue(op.getArchetype(), "com.liferay.maven.archetypes:liferay-portlet-jsf-archetype:6.2.5");

		op.setPortletFrameworkAdvanced("primefaces");

		assertComponentValue(
			op.getArchetype(), "com.liferay.maven.archetypes:liferay-portlet-primefaces-archetype:6.2.5");
	}

	@Override
	protected String provider() {
		return "maven";
	}

	protected void verifyProjectFiles(String projectName) {
		assertProjectFileExists(projectName, "pom.xml");
	}

}