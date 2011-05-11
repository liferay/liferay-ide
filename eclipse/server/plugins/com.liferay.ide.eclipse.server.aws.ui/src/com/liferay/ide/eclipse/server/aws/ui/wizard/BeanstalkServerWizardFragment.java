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

import com.liferay.ide.eclipse.core.util.ZipUtil;
import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.sdk.util.SDKUtil;
import com.liferay.ide.eclipse.server.aws.core.AWSCorePlugin;
import com.liferay.ide.eclipse.server.aws.core.BeanstalkServer;
import com.liferay.ide.eclipse.server.aws.core.IBeanstalkServerWorkingCopy;
import com.liferay.ide.eclipse.server.aws.ui.AWSUIPlugin;
import com.liferay.ide.eclipse.ui.util.UIUtil;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URL;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.egit.core.Activator;
import org.eclipse.egit.core.RepositoryCache;
import org.eclipse.egit.core.op.ConnectProviderOperation;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.transport.RemoteConfig;
import org.eclipse.jgit.transport.URIish;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.ide.undo.WorkspaceUndoUtil;
import org.eclipse.ui.internal.ide.filesystem.FileSystemConfiguration;
import org.eclipse.ui.internal.ide.filesystem.FileSystemSupportRegistry;
import org.eclipse.ui.internal.wizards.newresource.ResourceMessages;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.wst.server.core.IServer;
import org.eclipse.wst.server.core.IServerLifecycleListener;
import org.eclipse.wst.server.core.IServerWorkingCopy;
import org.eclipse.wst.server.core.ServerCore;
import org.eclipse.wst.server.core.TaskModel;
import org.eclipse.wst.server.ui.wizard.IWizardHandle;
import org.eclipse.wst.server.ui.wizard.WizardFragment;

/**
 * @author Greg Amerson
 */
public class BeanstalkServerWizardFragment extends WizardFragment {

	protected BeanstalkServerComposite composite;

	protected IStatus lastServerStatus = null;

	protected IWizardHandle wizard;

	public BeanstalkServerWizardFragment() {
		super();
	}

	@Override
	public Composite createComposite(Composite parent, IWizardHandle wizard) {
		this.wizard = wizard;

		composite = new BeanstalkServerComposite(parent, this, wizard);

		wizard.setTitle("AWS Beanstalk PaaS Environment");
		wizard.setDescription("Configure Liferay Portal running on AWS Beanstalk instance.");
		wizard.setImageDescriptor(ImageDescriptor.createFromURL(AWSUIPlugin.getDefault().getBundle().getEntry(
			"/icons/logo_aws.gif")));

		return composite;
	}

	@Override
	public void enter() {
		if (composite != null && !composite.isDisposed()) {
			IServerWorkingCopy serverWC = getServerWorkingCopy();

			// need to set defaults now that we have a connection

			composite.setServer(serverWC);
		}
	}

	@Override
	public void exit() {
		if (lastServerStatus != null && lastServerStatus.getSeverity() != IStatus.ERROR) {
			AWSCorePlugin.getPreferences().put(
				BeanstalkServer.PREF_DEFAULT_SERVER_HOSTNAME_PREFIX + getServerWorkingCopy().getServerType().getId(),
				getServerWorkingCopy().getHost());
		}
	}

	@Override
	public boolean hasComposite() {
		return true;
	}

	@Override
	public boolean isComplete() {
		return lastServerStatus != null && lastServerStatus.getSeverity() != IStatus.ERROR;
	}

	@Override
	public void performFinish(IProgressMonitor monitor)
		throws CoreException {

		try {
			this.wizard.run(false, false, new IRunnableWithProgress() {

				public void run(IProgressMonitor monitor)
					throws InvocationTargetException, InterruptedException {

					if (lastServerStatus == null || (!lastServerStatus.isOK())) {
						lastServerStatus = getBeanstalkServerWC().validate(monitor);

						if (!lastServerStatus.isOK()) {
							throw new InterruptedException(lastServerStatus.getMessage());
						}
					}
				}
			});
		}
		catch (Exception e) {

		}

		ServerCore.addServerLifecycleListener(new IServerLifecycleListener() {
			String id = getServerWorkingCopy().getId();

			public void serverAdded(final IServer server) {
				if (server.getId().equals(id)) {
					UIUtil.async(new Runnable() {

						public void run() {
							IViewPart serversView = UIUtil.showView("org.eclipse.wst.server.ui.ServersView");
							CommonViewer viewer = (CommonViewer) serversView.getAdapter(CommonViewer.class);
							viewer.setSelection(new StructuredSelection(server));
						}
					});

					ServerCore.removeServerLifecycleListener(this);
				}
			}

			public void serverChanged(IServer server) {
			}

			public void serverRemoved(IServer server) {
			}

		});

		final String sdkname = composite.getSDKName();
		final String remoteName = composite.getRemoteName();
		final String host = composite.getHost();

		new WorkspaceJob("") {

			@Override
			public IStatus runInWorkspace(IProgressMonitor monitor) throws CoreException {
				Repository repo = createGitRepo(sdkname, remoteName, host);
				IProject project = importProject(sdkname, repo);
				SDK sdk = SDKUtil.createSDKFromLocation(project.getLocation());
				SDKManager.getInstance().addSDK(sdk);
				return Status.OK_STATUS;
			}
		}.schedule();

	}

	protected IProject importProject(String projectname, Repository repo) {
		IProject newProject = createNewProject(projectname);

		try {
			newProject.create(null);
			newProject.open(null);
			URL zipFileUrl =
				FileLocator.toFileURL(AWSUIPlugin.getDefault().getBundle().getEntry("liferay-plugins-sdk-6.0.6.zip"));
			ZipUtil.unzip(new File(zipFileUrl.getFile()), newProject.getLocation().toFile());
			newProject.refreshLocal(IResource.DEPTH_INFINITE, null);
			// newProject.delete(false, true, null);
			ConnectProviderOperation cpo = new ConnectProviderOperation(newProject, repo.getDirectory());
			cpo.execute(new NullProgressMonitor());
			return newProject;
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	public URI getProjectLocationURI(String path) {

		FileSystemConfiguration configuration = getSelectedConfiguration();
		if (configuration == null) {
			return null;
		}

		return configuration.getContributor().getURI(path);

	}

	/**
	 * Return the selected contributor
	 * 
	 * @return FileSystemConfiguration or <code>null</code> if it cannot be determined.
	 */
	private FileSystemConfiguration getSelectedConfiguration() {
		return FileSystemSupportRegistry.getInstance().getDefaultConfiguration();
	}

	private IProject createNewProject(String projectname) {
		// get a project handle
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		final IProject newProjectHandle = root.getProject(projectname);

		// get a project descriptor
		URI location = root.getLocation().append(projectname).toFile().toURI();

		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		final IProjectDescription description = workspace.newProjectDescription(newProjectHandle.getName());
		description.setLocationURI(location);


		CreateProjectOperation op = new CreateProjectOperation(description, ResourceMessages.NewProject_windowTitle);
		try {
			// see bug https://bugs.eclipse.org/bugs/show_bug.cgi?id=219901
			// directly execute the operation so that the undo state is
			// not preserved. Making this undoable resulted in too many
			// accidental file deletions.
			op.execute(
				new NullProgressMonitor(), WorkspaceUndoUtil.getUIInfoAdapter(Display.getDefault().getActiveShell()));
		}
		catch (Exception e) {

		}

		return newProjectHandle;
	}

	protected Repository createGitRepo(String sdkname, String remoteName, String host) {
		RepositoryCache cache = Activator.getDefault().getRepositoryCache();
		try {
			String workspaceDir = ResourcesPlugin.getWorkspace().getRoot().getLocation().toOSString();
			File repoFile = new File(workspaceDir + "/" + sdkname);
			repoFile = new File(repoFile, Constants.DOT_GIT);

			Repository newRepo = cache.lookupRepository(repoFile);
			newRepo.create(false);
			Activator.getDefault().getRepositoryUtil().addConfiguredRepository(repoFile);

			RemoteConfig remoteConfig = new RemoteConfig(newRepo.getConfig(), remoteName);
			URIish pushUri = new URIish("ssh://ec2-user@" + host + "/home/ec2-user/" + remoteName);
			remoteConfig.addPushURI(pushUri);
			remoteConfig.update(newRepo.getConfig());
			return newRepo;
		}
		catch (Exception e) {
			AWSCorePlugin.logError(e.getMessage(), e);
		}
		return null;
	}

	protected IServerWorkingCopy getServerWorkingCopy() {
		return (IServerWorkingCopy) getTaskModel().getObject(TaskModel.TASK_SERVER);
	}

	IBeanstalkServerWorkingCopy getBeanstalkServerWC() {
		return (IBeanstalkServerWorkingCopy) getServerWorkingCopy().loadAdapter(IBeanstalkServerWorkingCopy.class, null);
	}

}
