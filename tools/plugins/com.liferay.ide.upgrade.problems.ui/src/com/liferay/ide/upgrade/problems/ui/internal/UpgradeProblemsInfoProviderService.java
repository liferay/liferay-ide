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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.upgrade.plan.ui.UpgradeInfoProvider;
import com.liferay.ide.upgrade.problems.core.FileUpgradeProblem;

import java.io.File;

import java.util.NoSuchElementException;

import org.osgi.service.component.annotations.Component;
import org.osgi.util.promise.Deferred;
import org.osgi.util.promise.Promise;
import org.osgi.util.promise.PromiseFactory;

/**
 * @author Gregory Amerson
 */
@Component
public class UpgradeProblemsInfoProviderService implements UpgradeInfoProvider {

	public UpgradeProblemsInfoProviderService() {
		_promiseFactory = new PromiseFactory(null);
	}

	@Override
	public Promise<String> getDetail(Object element) {
		Deferred<String> deferred = _promiseFactory.deferred();

		if (element instanceof FileProblemsContainer) {
			_doFileProblemsContainerDetail((FileProblemsContainer)element, deferred);
		}
		else if (element instanceof ProjectProblemsContainer) {
			_doProjectProblemsContainerDetail((ProjectProblemsContainer)element, deferred);
		}
		else {
			deferred.fail(new NoSuchElementException());
		}

		return deferred.getPromise();
	}

	@Override
	public String getLabel(Object element) {
		if (element instanceof FileProblemsContainer) {
			FileProblemsContainer fileProblemsContainer = (FileProblemsContainer)element;

			return _doFileProblemsContainerLabel(fileProblemsContainer);
		}
		else if (element instanceof MigrationProblemsContainer) {
			return "Liferay Upgrade";
		}
		else if (element instanceof ProjectProblemsContainer) {
			return _doProjectProblemsContainerLabel((ProjectProblemsContainer)element);
		}
		else if (element instanceof FileUpgradeProblem) {
			return _doProblemLabel((FileUpgradeProblem)element);
		}

		return null;
	}

	@Override
	public boolean provides(Object element) {
		if (element instanceof FileProblemsContainer || element instanceof MigrationProblemsContainer ||
			element instanceof ProjectProblemsContainer) {

			return true;
		}

		return false;
	}

	private void _doFileProblemsContainerDetail(
		FileProblemsContainer fileProblemsContainer, Deferred<String> deferred) {

		StringBuffer sb = new StringBuffer();

		File file = fileProblemsContainer.getFile();

		FileUpgradeProblem[] problems = fileProblemsContainer.getProblems();

		sb.append(file);
		sb.append("<br />");
		sb.append("It has " + problems.length + " issue(s) need to be solved.");
		sb.append("<br />");

		for (FileUpgradeProblem problem : problems) {
			sb.append(problem.title);
			sb.append("<br />");
		}

		deferred.resolve(sb.toString());
	}

	private String _doFileProblemsContainerLabel(FileProblemsContainer fileProblemsContainer) {
		File file = fileProblemsContainer.getFile();

		String fileName = file.getName();
		String path = file.getParent();

		return fileName + " [" + path + "]";
	}

	private String _doProblemLabel(FileUpgradeProblem problem) {
		StringBuffer sb = new StringBuffer();

		sb.append("[");
		sb.append(problem.version);
		sb.append("][");
		sb.append(problem.lineNumber);
		sb.append("]");
		sb.append(problem.title);

		return sb.toString();
	}

	private void _doProjectProblemsContainerDetail(
		ProjectProblemsContainer projectProblemsContainer, Deferred<String> deferred) {

		StringBuffer sb = new StringBuffer();

		FileProblemsContainer[] fileProblemsContainers = projectProblemsContainer.getFileProblemsContainers();

		sb.append(projectProblemsContainer.getProjectName());
		sb.append("<br />");
		sb.append("It has " + fileProblemsContainers.length + " file(s) need to be solved.");
		sb.append("<br />");

		for (FileProblemsContainer fileProblemsContainer : fileProblemsContainers) {
			sb.append(fileProblemsContainer.getFile());
			sb.append("<br />");
		}

		deferred.resolve(sb.toString());
	}

	private String _doProjectProblemsContainerLabel(ProjectProblemsContainer projectProblems) {
		return projectProblems.getProjectName();
	}

	private final PromiseFactory _promiseFactory;

}