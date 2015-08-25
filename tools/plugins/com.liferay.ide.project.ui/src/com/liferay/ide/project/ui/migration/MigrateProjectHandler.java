package com.liferay.ide.project.ui.migration;

import blade.migrate.api.Migration;
import blade.migrate.api.Problem;
import blade.migrate.api.ProgressMonitor;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;

public class MigrateProjectHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);

        if (selection instanceof IStructuredSelection) {
            Object element = ((IStructuredSelection) selection).getFirstElement();

            IProject project = null;

            if (project instanceof IProject) {
				project = (IProject) element;
			}
            else if (element instanceof IAdaptable) {
				IAdaptable adaptable = (IAdaptable) element;
				project = (IProject) adaptable.getAdapter(IProject.class);
			}

            if (project != null ) {
            	final IPath location = project.getLocation();
				Job job = new WorkspaceJob("Project migration") {
					@Override
					public IStatus runInWorkspace(final IProgressMonitor monitor)
							throws CoreException {
						final BundleContext context = FrameworkUtil.getBundle(this.getClass()).getBundleContext();

						ProgressMonitor override = new ProgressMonitor() {

							@Override
							public void worked(int work) {
								monitor.worked(work);
							}

							@Override
							public void setTaskName(String taskName) {
								monitor.setTaskName(taskName);
							}

							@Override
							public void done() {
								monitor.done();
							}

							@Override
							public void beginTask(String taskName, int totalWork) {
								monitor.beginTask(taskName, totalWork);
							}
						};

						Dictionary<String, Integer> properties = new Hashtable<>();

						properties.put(Constants.SERVICE_RANKING,  1000);

						ServiceRegistration<ProgressMonitor> reg = context.registerService(ProgressMonitor.class, override, properties);

		            	ServiceReference<Migration> sr = context.getServiceReference(Migration.class);
		            	Migration m = context.getService(sr);

		            	List<Problem> problems = m.findProblems(location.toFile());

		            	m.reportProblems(problems, Migration.DETAIL_LONG, "ide");

						reg.unregister();

						return Status.OK_STATUS;
					}
				};

				try {
					PlatformUI.getWorkbench().getProgressService().showInDialog(Display.getDefault().getActiveShell(), job);
				} catch (Exception e) {
					e.printStackTrace();
				}

				job.schedule();
            }
        }

		return null;
	}

}
