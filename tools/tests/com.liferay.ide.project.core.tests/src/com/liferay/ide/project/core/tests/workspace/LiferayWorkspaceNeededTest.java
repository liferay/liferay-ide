package com.liferay.ide.project.core.tests.workspace;

import org.eclipse.sapphire.modeling.Status;
import org.junit.Assert;
import org.junit.Test;

import com.liferay.ide.project.core.modules.NewLiferayModuleProjectOp;
import com.liferay.ide.project.core.modules.ext.NewModuleExtOp;
import com.liferay.ide.project.core.modules.fragment.NewModuleFragmentOp;
import com.liferay.ide.project.core.springmvcportlet.NewSpringMVCPortletProjectOp;

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
