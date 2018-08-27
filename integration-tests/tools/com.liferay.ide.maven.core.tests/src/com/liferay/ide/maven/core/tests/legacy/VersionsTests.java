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

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.maven.core.tests.util.MavenTestUtil;
import com.liferay.ide.test.core.base.support.ImportProjectSupport;
import com.liferay.ide.test.project.core.base.ProjectBase;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.core.project.IMavenProjectRegistry;
import org.eclipse.m2e.tests.common.RequireMavenExecutionContext;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@RequireMavenExecutionContext
@SuppressWarnings("restriction")
public class VersionsTests extends ProjectBase {

	@Test
	public void testFindLiferayVersionByDeps() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("versions/deps-portlet");

		ips.before();

		IPath pomFile = ips.getFile("pom.xml");

		IProject project = MavenTestUtil.importProject(pomFile.toOSString());

		Assert.assertNotNull(project);

		IMavenProjectRegistry mavenProjectRegistry = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade facade = mavenProjectRegistry.create(project, npm);

		Assert.assertNotNull(facade);

		ILiferayProject lrproject = LiferayCore.create(project);

		Assert.assertNotNull(lrproject);

		ILiferayPortal portal = lrproject.adapt(ILiferayPortal.class);

		Assert.assertNotNull(portal);

		Assert.assertEquals("6.2.1", portal.getVersion());

		deleteProject(project);
	}

	@Test
	public void testFindLiferayVersionByProperties() throws Exception {
		ImportProjectSupport ips = new ImportProjectSupport("versions/properties-portlet");

		ips.before();

		IPath pomFile = ips.getFile("pom.xml");

		IProject project = MavenTestUtil.importProject(pomFile.toOSString());

		Assert.assertNotNull(project);

		IMavenProjectRegistry mavenProjectRegistry = MavenPlugin.getMavenProjectRegistry();

		IMavenProjectFacade facade = mavenProjectRegistry.create(project, npm);

		Assert.assertNotNull(facade);

		final ILiferayProject lrproject = LiferayCore.create(project);

		Assert.assertNotNull(lrproject);

		final ILiferayPortal portal = lrproject.adapt(ILiferayPortal.class);

		Assert.assertNotNull(portal);

		Assert.assertEquals("6.2.1", portal.getVersion());

		deleteProject(project);
	}

}