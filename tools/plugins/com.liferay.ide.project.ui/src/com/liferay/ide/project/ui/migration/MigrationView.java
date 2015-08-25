package com.liferay.ide.project.ui.migration;

import blade.migrate.api.Problem;
import blade.migrate.api.WorkspaceMigration;

import com.liferay.ide.project.ui.ProjectUI;

import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public class MigrationView extends ViewPart {

	private TreeViewer _viewer;

	@Override
	public void createPartControl(Composite parent) {
		_viewer = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		_viewer.setContentProvider(new MigrationContentProvider());
		_viewer.setLabelProvider(new MigrationLabelProvider());

        final BundleContext context = ProjectUI.getDefault().getBundle().getBundleContext();
        ServiceReference<WorkspaceMigration> sr =
            context.getServiceReference( WorkspaceMigration.class );
        WorkspaceMigration migration = context.getService( sr );
        List<Problem> problems = migration.getStoredProblems( false );

		_viewer.setInput(problems);
	}

	@Override
	public void setFocus() {
		if (_viewer != null && _viewer.getControl() != null) {
			_viewer.getControl().setFocus();
		}
	}

}
