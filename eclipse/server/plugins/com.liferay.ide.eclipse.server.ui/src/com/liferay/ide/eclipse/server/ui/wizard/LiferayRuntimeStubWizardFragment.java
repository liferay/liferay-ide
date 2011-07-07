/**
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.eclipse.server.ui.wizard;

import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.ui.LiferayServerUIPlugin;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Greg Amerson
 */
public class LiferayRuntimeStubWizardFragment extends WizardFragment {

	public static final String LIFERAY_RUNTIME_STUB = "liferay-runtime-stub";

	protected LiferayRuntimeStubComposite composite;

	public LiferayRuntimeStubWizardFragment() {
		super();
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		wizard.setTitle( "Liferay Runtime" );
		wizard.setDescription( "Specify the location of the runtime directory." );
		wizard.setImageDescriptor( ImageDescriptor.createFromURL( LiferayServerUIPlugin.getDefault().getBundle().getEntry(
			"/icons/wizban/server_wiz.png")));

		composite = new LiferayRuntimeStubComposite(parent, wizard);

		return composite;
	}

	public void enter() {
		if (composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			composite.setRuntime(runtime);
		}
	}

	public void exit() {
		// IRuntimeWorkingCopy runtimeStub = getRuntimeStubWorkingCopy();
		// IWebsphereRuntimeWorkingCopy websphereRuntime = getWebsphereRuntimeWorkingCopy();
		//
		// if (websphereRuntime != null) {
		// websphereRuntime.setRuntimeStubTypeId(LiferayTomcatRuntime.RUNTIME_TYPE_ID);
		// }
	}

	public boolean hasComposite() {
		return true;
	}

	public boolean isComplete() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		if (runtime == null) {
			return false;
		}

		IStatus status = runtime.validate(null);

		// if (!status.isOK() && status.getCode() == WebsphereRuntime.INVALID_STUB_CODE) {
		// status = Status.OK_STATUS;
		// }

		// IRuntimeWorkingCopy runtimeStub = getRuntimeStubWorkingCopy();
		//
		// if (runtimeStub == null) {
		// return false;
		// }

		return (status == null || status.getSeverity() != IStatus.ERROR);
	}

	protected ILiferayRuntime getLiferayRuntime() {
		IRuntimeWorkingCopy runtimeWC = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		ILiferayRuntime liferayRuntime = (ILiferayRuntime) runtimeWC.loadAdapter( ILiferayRuntime.class, null );
		return liferayRuntime;
	}

	// protected IWebsphereRuntimeWorkingCopy getWebsphereRuntimeWorkingCopy() {
	// IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
	//
	// if (runtime != null) {
	// return (IWebsphereRuntimeWorkingCopy) runtime.loadAdapter(IWebsphereRuntimeWorkingCopy.class, null);
	// }
	//
	// return null;
	// }

}
