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

import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.test.project.core.base.ProjectOpBase;

import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 * @author Terry Jia
 */
public class NewVaadinPortletProjectTests extends ProjectOpBase<NewLiferayPluginProjectOp> {

	@Test
	public void createVaadin7() throws Exception {
		NewLiferayPluginProjectOp op = NewLiferayPluginProjectOp.TYPE.instantiate();

		op.setProjectName(project.getName());
		op.setProjectProvider(provider());
		op.setPortletFramework("vaadin");

		createOrImportAndBuild(op, project.getName());

		assertProjectFileContains(project.getName(), "pom.xml", "<artifactId>vaadin-server</artifactId>");
		assertProjectFileContains(project.getName(), "pom.xml", "<artifactId>vaadin-client</artifactId>");
		assertProjectFileContains(project.getName(), "pom.xml", "<artifactId>portal-service</artifactId>");

		deleteProject(project.getName());
	}

	@Override
	protected String provider() {
		return "maven";
	}

}