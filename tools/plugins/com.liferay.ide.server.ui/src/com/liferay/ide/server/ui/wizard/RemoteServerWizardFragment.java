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

import com.liferay.ide.server.remote.IRemoteServerWorkingCopy;
import com.liferay.ide.server.ui.LiferayServerUI;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerListener;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.ServerEvent;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 * @author Simon Jiang
 */
public class RemoteServerWizardFragment extends WizardFragment {

	public RemoteServerWizardFragment() {
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		this.wizard = wizard;

		composite = new RemoteServerComposite(parent, this, wizard);

		wizard.setTitle(Msgs.remoteLiferayServer);
		wizard.setDescription(Msgs.configureRemoteLiferayServerInstance);

		LiferayServerUI liferayServerUi = LiferayServerUI.getDefault();

		Bundle bundle = liferayServerUi.getBundle();

		wizard.setImageDescriptor(ImageDescriptor.createFromURL(bundle.getEntry("/icons/wizban/server_wiz.png")));

		return composite;
	}

	@Override
	public void enter() {
		if ((composite != null) && !composite.isDisposed()) {
			IServerWorkingCopy serverWC = getServerWorkingCopy();

			// need to set defaults now that we have a connection

			composite.setServer(serverWC);
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		if ((lastServerStatus != null) && (lastServerStatus.getSeverity() != IStatus.ERROR)) {
			return true;
		}

		return false;
	}

	@Override
	public void performFinish(IProgressMonitor monitor) throws CoreException {
		try {
			wizard.run(
				false, false,
				new IRunnableWithProgress() {

					public void run(IProgressMonitor monitor) throws InterruptedException, InvocationTargetException {
						if ((lastServerStatus == null) || !lastServerStatus.isOK()) {
							lastServerStatus = getRemoteServerWC().validate(monitor);

							if (!lastServerStatus.isOK()) {
								throw new InterruptedException(lastServerStatus.getMessage());
							}
						}
					}

				});
		}
		catch (Exception e) {
		}

		ServerCore.addServerLifecycleListener(
			new IServerLifecycleListener() {

				public void serverAdded(IServer server) {
					String serverId = server.getId();

					if (serverId.equals(id)) {
						UIUtil.async(
							new Runnable() {

								public void run() {
									IViewPart serversView = UIUtil.showView("org.eclipse.wst.server.ui.ServersView");

									CommonViewer viewer = (CommonViewer)serversView.getAdapter(CommonViewer.class);

									viewer.setSelection(new StructuredSelection(server));
								}

							});

						ServerCore.removeServerLifecycleListener(this);

						server.addServerListener(
							new IServerListener() {

								public void serverChanged(ServerEvent event) {
									IServer s = event.getServer();

									if (s.getServerState() == IServer.STATE_STARTED) {
										server.publish(IServer.PUBLISH_INCREMENTAL, null, null, null);

										server.removeServerListener(this);
									}
								}

							});
					}
				}

				public void serverChanged(IServer server) {
				}

				public void serverRemoved(IServer server) {
				}

				public String id = getServerWorkingCopy().getId();

			});
	}

	protected IRemoteServerWorkingCopy getRemoteServerWC() {
		return (IRemoteServerWorkingCopy)getServerWorkingCopy().loadAdapter(IRemoteServerWorkingCopy.class, null);
	}

	protected IServerWorkingCopy getServerWorkingCopy() {
		return (IServerWorkingCopy)getTaskModel().getObject(TaskModel.TASK_SERVER);
	}

	protected RemoteServerComposite composite;
	protected IStatus lastServerStatus = null;
	protected IWizardHandle wizard;

	private static class Msgs extends NLS {

		public static String configureRemoteLiferayServerInstance;
		public static String remoteLiferayServer;

		static {
			initializeMessages(RemoteServerWizardFragment.class.getName(), Msgs.class);
		}

	}

}