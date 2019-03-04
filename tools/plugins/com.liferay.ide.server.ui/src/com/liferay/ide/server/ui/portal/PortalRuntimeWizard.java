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

package com.liferay.ide.server.ui.portal;

import com.liferay.ide.server.core.LiferayServerCore;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeType;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class PortalRuntimeWizard extends WizardFragment {

	public PortalRuntimeWizard() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle handle) {
		_composite = new PortalRuntimeComposite(parent, handle);

		return _composite;
	}

	@Override
	public void enter() {
		if (_composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			_composite.setRuntime(runtime);
		}
	}

	public void exit() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		IPath path = runtime.getLocation();
		IStatus status = runtime.validate(null);

		if (status.getSeverity() != IStatus.ERROR) {
			IRuntimeType runtimeType = runtime.getRuntimeType();

			LiferayServerCore.setPreference("location." + runtimeType.getId(), path.toPortableString());
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		boolean retval = false;

		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		if (runtime != null) {
			IStatus status = runtime.validate(null);

			retval = (status == null) || (status.getSeverity() != IStatus.ERROR);
		}

		return retval;
	}

	private PortalRuntimeComposite _composite;

}