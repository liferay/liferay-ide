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

package com.liferay.ide.project.core.tests.workspace;

import org.eclipse.sapphire.modeling.Status;
import org.junit.Assert;
import org.junit.Test;

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.ext.NewModuleExtOp;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.springmvcportlet.NewSpringMVCPortletProjectOp;

/**
 * @author Seiphon Wang
 */
public class LiferayWorkspaceNeededTest {

	@Test
	public void testNewModuleProjectWithoutWorkspace() {
		NewLiferayModuleProjectOp op = NewLiferayModuleProjectOp.TYPE.instantiate();

		op.setProjectName("test-module");

		Status projectErrorValidationStatus = op.getProjectName().validation();

		Assert.assertEquals("Cannot create project content out of liferay workspace project.", projectErrorValidationStatus.message());
	}

	@Test
	public void testNewModuleFragmentProjectWithoutWorkspace() {
		NewModuleFragmentOp op = NewModuleFragmentOp.TYPE.instantiate();

		op.setProjectName("test-module-fragment");

		Status projectErrorValidationStatus = op.getProjectName().validation();

		Assert.assertEquals("Cannot create project content out of liferay workspace project.", projectErrorValidationStatus.message());
	}

	@Test
	public void testNewModuleExtProjectWithoutWorkspace() {
		NewModuleExtOp op = NewModuleExtOp.TYPE.instantiate();

		op.setProjectName("test-module-ext");

		Status projectErrorValidationStatus = op.getProjectName().validation();

		Assert.assertEquals("Cannot create project content out of liferay workspace project.", projectErrorValidationStatus.message());
	}

	@Test
	public void testNewSpringMVCPortletProjectWithoutWorkspace() {
		NewSpringMVCPortletProjectOp op = NewSpringMVCPortletProjectOp.TYPE.instantiate();

		op.setProjectName("test-module-ext");

		Status projectErrorValidationStatus = op.getProjectName().validation();

		Assert.assertEquals("Cannot create project content out of liferay workspace project.", projectErrorValidationStatus.message());
	}
}
