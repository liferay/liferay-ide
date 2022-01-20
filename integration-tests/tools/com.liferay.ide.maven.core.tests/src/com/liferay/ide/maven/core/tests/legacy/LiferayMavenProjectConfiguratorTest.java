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

import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.maven.core.FacetedMavenProject;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author Kamesh Sampath
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferayMavenProjectConfiguratorTest extends AbstractMavenProjectTestCase {

	@Test
	public void testLiferayFacetNoLiferayPluginConfigured() throws Exception {
		IProject project = importProject("projects/configurators/default-webapp-no-liferay-plugin/pom.xml");

		Assert.assertNotNull(project);

		IMavenProjectRegistry registry = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade facade = registry.create(project, monitor);

		Assert.assertNotNull(facade);

		Assert.assertTrue(LiferayCore.create(ILiferayProject.class, project) instanceof FacetedMavenProject);

		deleteProject("default-webapp-no-liferay-plugin");
	}

	@Test
	public void testLiferayFacetNoLiferayPluginWarPluginConfigured() throws Exception {
		IProject project = importProject("projects/configurators/webapp-alternate-webapp-folder/pom.xml");

		Assert.assertNotNull(project);

		IMavenProjectRegistry registry = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade facade = registry.create(project, monitor);

		Assert.assertNotNull(facade);

		waitForJobsToComplete();

		Assert.assertTrue(LiferayCore.create(ILiferayProject.class, project) instanceof FacetedMavenProject);

		deleteProject("webapp-alternate-webapp-folder");
	}

	@Test
	public void testLiferayFacetNotConfigured() throws Exception {
		IProject project = importProject("projects/configurators/webapp-1/pom.xml");

		Assert.assertNotNull(project);

		IMavenProjectRegistry registry = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade facade = registry.create(project, monitor);

		Assert.assertNotNull(facade);

		Assert.assertFalse(LiferayCore.create(ILiferayProject.class, project) instanceof FacetedMavenProject);

		deleteProject("webapp-1");
	}

	@Test
	public void testWarPluginNoWarSourceDirConfigured() throws Exception {
		IProject project = importProject("projects/configurators/bad-war-config/pom.xml");

		Assert.assertNotNull(project);

		IMavenProjectRegistry registry = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade facade = registry.create(project, monitor);

		Assert.assertNotNull(facade);

		waitForJobsToComplete();

		Assert.assertTrue(LiferayCore.create(ILiferayProject.class, project) instanceof FacetedMavenProject);

		deleteProject("bad-war-config");
	}

}