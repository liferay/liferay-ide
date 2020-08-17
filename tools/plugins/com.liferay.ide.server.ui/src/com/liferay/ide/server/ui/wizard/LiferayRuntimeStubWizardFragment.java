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

package com.liferay.ide.server.ui.wizard;

import com.liferay.ide.server.core.ILiferayRuntime;
import com.liferay.ide.server.ui.LiferayServerUI;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
public class LiferayRuntimeStubWizardFragment extends WizardFragment {

	public static final String LIFERAY_RUNTIME_STUB = "liferay-runtime-stub";

	public LiferayRuntimeStubWizardFragment() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		wizard.setTitle(Msgs.liferayRuntimeStub);
		wizard.setDescription(Msgs.specifyDirectoryLocation);

		LiferayServerUI liferayServerUI = LiferayServerUI.getDefault();

		Bundle bundle = liferayServerUI.getBundle();

		wizard.setImageDescriptor(ImageDescriptor.createFromURL(bundle.getEntry("/icons/wizban/server_wiz.png")));

		composite = new LiferayRuntimeStubComposite(parent, wizard);

		return composite;
	}

	public void enter() {
		if (composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			composite.setRuntime(runtime);
		}
	}

	public boolean hasComposite() {
		return true;
	}

	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		if (runtime == null) {
			return false;
		}

		IStatus status = runtime.validate(null);

		if ((status != null) && (status.getSeverity() != IStatus.ERROR)) {
			return true;
		}

		return false;
	}

	protected ILiferayRuntime getLiferayRuntime() {
		IRuntimeWorkingCopy runtimeWC = (IRuntimeWorkingCopy)getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		return (ILiferayRuntime)runtimeWC.loadAdapter(ILiferayRuntime.class, null);
	}

	protected LiferayRuntimeStubComposite composite;

	private static class Msgs extends NLS {

		public static String liferayRuntimeStub;
		public static String specifyDirectoryLocation;

		static {
			initializeMessages(LiferayRuntimeStubWizardFragment.class.getName(), Msgs.class);
		}

	}

}