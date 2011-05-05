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

package com.liferay.ide.eclipse.server.aws.ui.wizard;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.server.aws.core.AWSCorePlugin;
import com.liferay.ide.eclipse.server.aws.core.BeanstalkRuntime;
import com.liferay.ide.eclipse.server.aws.core.IBeanstalkRuntimeWorkingCopy;
import com.liferay.ide.eclipse.server.aws.ui.AWSUIPlugin;
import com.liferay.ide.eclipse.server.tomcat.core.LiferayTomcatRuntime;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.server.core.IRuntime;
import org.eclipse.wst.server.core.IRuntimeWorkingCopy;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Greg Amerson
 */
public class BeanstalkRuntimeWizardFragment extends WizardFragment {

	public static final String LIFERAY_RUNTIME_STUB = "liferay-runtime-stub";

	protected BeanstalkRuntimeComposite composite;

	public BeanstalkRuntimeWizardFragment() {
		super();
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		wizard.setTitle("Beanstalk Runtime for Liferay v6.0 EE");
		wizard.setDescription("Specify a local installation directory of Liferay Tomcat bundle");
		wizard.setImageDescriptor(ImageDescriptor.createFromURL(AWSUIPlugin.getDefault().getBundle().getEntry(
			"/icons/logo_aws.gif")));

		composite = new BeanstalkRuntimeComposite(parent, wizard);

		return composite;
	}

	public void enter() {
		if (composite != null) {
			IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);

			IBeanstalkRuntimeWorkingCopy websphereWC = getBeanstalkRuntimeWorkingCopy();

			IPath runtimeStubLocation = websphereWC.getRuntimeStubLocation();

			if (runtimeStubLocation == null || runtimeStubLocation.isEmpty()) {
				// look for existing EE runtimes to automatically set the runtime location to
				IRuntime[] eeRuntimes = AWSCorePlugin.getBeanstalkRuntimes();

				if (!CoreUtil.isNullOrEmpty(eeRuntimes)) {
					websphereWC.setRuntimeStubLocation(eeRuntimes[0].getLocation());
				}
			}

			composite.setRuntime(runtime);

			// IRuntimeWorkingCopy runtimeStub = getRuntimeStubWorkingCopy();
			//
			// // check to see if websphereRuntime has an existing runtime stub
			// IBeanstalkRuntimeWorkingCopy websphereWC = getBeanstalkRuntimeWorkingCopy();
			//
			// if (websphereWC != null) {
			// runtimeStub.setLocation(websphereWC.getRuntimeStubLocation());
			// }
			//
			// composite.setRuntimeStub(runtimeStub);
		}
	}

	public void exit() {
		// IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		// IPath path = runtime.getLocation();

		// if (runtime.validate(null).getSeverity() != IStatus.ERROR) {
		// BeanstalkCore.getPreferences().put(
		// BeanstalkRuntime.PREF_DEFAULT_RUNTIME_LOCATION_PREFIX + runtime.getRuntimeType().getId(),
		// path.toString());
		// }

		// IRuntimeWorkingCopy runtimeStub = getRuntimeStubWorkingCopy();
		IBeanstalkRuntimeWorkingCopy websphereRuntime = getBeanstalkRuntimeWorkingCopy();

		if (websphereRuntime != null) {
			// websphereRuntime.setRuntimeStubLocation(runtimeStub.getLocation());
			websphereRuntime.setRuntimeStubTypeId(LiferayTomcatRuntime.RUNTIME_TYPE_ID);
		}
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

		if (!status.isOK() && status.getCode() == BeanstalkRuntime.INVALID_STUB_CODE) {
			status = Status.OK_STATUS;
		}

		// IRuntimeWorkingCopy runtimeStub = getRuntimeStubWorkingCopy();
		//
		// if (runtimeStub == null) {
		// return false;
		// }

		return (status == null || status.getSeverity() != IStatus.ERROR);
	}

	// @Override
	// public void setTaskModel(TaskModel taskModel) {
	// super.setTaskModel(taskModel);
	//
	// IRuntimeWorkingCopy runtime = getRuntimeStubWorkingCopy();
	//
	// if (runtime == null) {
	// IRuntimeType runtimeType = ServerCore.findRuntimeType(LiferayTomcatRuntime.RUNTIME_TYPE_ID);
	//
	// if (runtimeType != null) {
	// IRuntimeWorkingCopy runtimeStub;
	//
	// try {
	// runtimeStub = runtimeType.createRuntime(null, null);
	// runtimeStub.setStub(true);
	//
	// getTaskModel().putObject(LIFERAY_RUNTIME_STUB, runtimeStub);
	// }
	// catch (CoreException e) {
	// BeanstalkUI.logError("Error creating runtime stub.", e);
	// }
	// }
	// }
	// }

	// protected IRuntimeWorkingCopy getRuntimeStubWorkingCopy() {
	// return (IRuntimeWorkingCopy) getTaskModel().getObject(LIFERAY_RUNTIME_STUB);
	// }

	protected BeanstalkRuntime getBeanstalkRuntime() {
		IRuntimeWorkingCopy runtimeWC = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);
		IBeanstalkRuntimeWorkingCopy websphereRuntimeWC =
			(IBeanstalkRuntimeWorkingCopy) runtimeWC.loadAdapter(IBeanstalkRuntimeWorkingCopy.class, null);
		return (BeanstalkRuntime) websphereRuntimeWC;
	}

	protected IBeanstalkRuntimeWorkingCopy getBeanstalkRuntimeWorkingCopy() {
		IRuntimeWorkingCopy runtime = (IRuntimeWorkingCopy) getTaskModel().getObject(TaskModel.TASK_RUNTIME);

		if (runtime != null) {
			return (IBeanstalkRuntimeWorkingCopy) runtime.loadAdapter(IBeanstalkRuntimeWorkingCopy.class, null);
		}

		return null;
	}

}
