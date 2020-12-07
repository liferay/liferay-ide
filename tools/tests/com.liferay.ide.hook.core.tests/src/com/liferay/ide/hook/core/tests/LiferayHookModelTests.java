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

package com.liferay.ide.hook.core.tests;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.hook.core.model.Hook;
import com.liferay.ide.hook.core.model.Hook6xx;
import com.liferay.ide.hook.core.model.StrutsAction;
import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesCacheService;
import com.liferay.ide.hook.core.model.internal.StrutsActionPathPossibleValuesService;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.PluginType;
import com.liferay.ide.project.core.tests.ProjectCoreBase;

import java.util.Set;
import java.util.TreeSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class LiferayHookModelTests extends ProjectCoreBase {

	@Test
	public void strutsActionPathPossibleValuesCacheService() throws Exception {
		if (shouldSkipBundleTests())

			return;

		final NewLiferayPluginProjectOp op = newProjectOp("testPossibleValuesCache");

		op.setPluginType(PluginType.hook);

		final IProject hookProject = createAntProject(op);

		final IWebProject webProject = LiferayCore.create(IWebProject.class, hookProject);

		final IFolder webappRoot = webProject.getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		final IFile hookXml = webappRoot.getFile("WEB-INF/liferay-hook.xml");

		Assert.assertEquals(true, hookXml.exists());

		final Hook hook = Hook6xx.TYPE.instantiate(new RootXmlResource(new XmlResourceStore(hookXml.getContents())));

		Assert.assertNotNull(hook);

		final ILiferayProject liferayProject = LiferayCore.create(ILiferayProject.class, hookProject);

		final ILiferayPortal portal = liferayProject.adapt(ILiferayPortal.class);

		IPath appServerPortalDir = portal.getAppServerPortalDir();

		final IPath strutsConfigPath = appServerPortalDir.append("WEB-INF/struts-config.xml");

		ElementList<StrutsAction> strutsActions = hook.getStrutsActions();

		final StrutsAction strutsAction = strutsActions.insert();

		final Value<String> strutsActionPath = strutsAction.getStrutsActionPath();

		StrutsActionPathPossibleValuesCacheService pathPossibleValuesCacheService = strutsActionPath.service(
			StrutsActionPathPossibleValuesCacheService.class);

		final TreeSet<String> vals1 = pathPossibleValuesCacheService.getPossibleValuesForPath(strutsConfigPath);

		final TreeSet<String> vals2 = pathPossibleValuesCacheService.getPossibleValuesForPath(strutsConfigPath);

		Assert.assertTrue(vals1 == vals2);
	}

	/**
	 * @throws Exception
	 */
	@Test
	public void strutsActionPathPossibleValuesService() throws Exception {
		if (shouldSkipBundleTests())

			return;

		final NewLiferayPluginProjectOp op = newProjectOp("testPossibleValues");

		op.setPluginType(PluginType.hook);

		final IProject hookProject = createAntProject(op);

		IWebProject webProject = LiferayCore.create(IWebProject.class, hookProject);

		final IFolder webappRoot = webProject.getDefaultDocrootFolder();

		Assert.assertNotNull(webappRoot);

		final IFile hookXml = webappRoot.getFile("WEB-INF/liferay-hook.xml");

		Assert.assertEquals(true, hookXml.exists());

		final XmlResourceStore store = new XmlResourceStore(hookXml.getContents()) {

			public <A> A adapt(Class<A> adapterType) {
				if (IProject.class.equals(adapterType)) {
					return adapterType.cast(hookProject);
				}

				return super.adapt(adapterType);
			}

		};

		final Hook hook = Hook6xx.TYPE.instantiate(new RootXmlResource(store));

		Assert.assertNotNull(hook);

		ElementList<StrutsAction> strutActions = hook.getStrutsActions();

		final StrutsAction strutsAction = strutActions.insert();

		final Value<String> strutsActionPath = strutsAction.getStrutsActionPath();

		StrutsActionPathPossibleValuesService strutService = strutsActionPath.service(
			StrutsActionPathPossibleValuesService.class);

		final Set<String> values = strutService.values();

		Assert.assertNotNull(values);

		Assert.assertTrue(values.size() > 10);
	}

}