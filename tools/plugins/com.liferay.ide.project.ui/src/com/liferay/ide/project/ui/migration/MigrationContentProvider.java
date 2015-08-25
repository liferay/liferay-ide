package com.liferay.ide.project.ui.migration;

import blade.migrate.api.Problem;

import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class MigrationContentProvider implements ITreeContentProvider {

	private Object _input;
	private Problem[] _problems;

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		_input = newInput;

		if (_input instanceof List<?>) {
		    List<?> problems = (List<?>) _input;

		    _problems = problems.toArray( new Problem[0] );
		}
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return _problems;
	}

	@Override
	public Object[] getChildren(Object parentElement) {

		if (parentElement instanceof Problem) {
			Problem problem = (Problem) parentElement;

			return new String[] { (String) problem.summary, problem.ticket };
		}

		return null;
	}

	@Override
	public Object getParent(Object element) {
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		return element instanceof Problem;
	}

}
