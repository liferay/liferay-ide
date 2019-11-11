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

package com.liferay.ide.gradle.ui.portal.docker;

import com.liferay.ide.gradle.ui.LiferayGradleUI;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import org.osgi.framework.Bundle;

/**
 * @author Simon Jiang
 */
public class DockerRuntimeSettingFragment extends WizardFragment {

	public DockerRuntimeSettingFragment() {
	}

	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		_composite = new DockerRuntimeSettingComposite(parent, wizard);

		LiferayGradleUI plugin = LiferayGradleUI.getDefault();

		Bundle bundle = plugin.getBundle();

		wizard.setImageDescriptor(ImageDescriptor.createFromURL(bundle.getEntry("/icons/wizban/server_wiz.png")));

		return _composite;
	}

	@Override
	public void enter() {
		if (_composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			_composite.setRuntime(runtime);
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return _composite.isComplete();
	}

	public boolean isForceLastFragment() {
		return true;
	}

	private DockerRuntimeSettingComposite _composite;

}