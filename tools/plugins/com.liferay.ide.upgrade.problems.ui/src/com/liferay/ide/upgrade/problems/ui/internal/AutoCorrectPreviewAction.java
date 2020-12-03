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

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeCompare;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.plan.ui.util.UIUtil;

import java.io.File;

import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.ISelectionProvider;

import org.osgi.framework.Bundle;
import org.osgi.framework.FrameworkUtil;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class AutoCorrectPreviewAction extends BaseAutoCorrectAction implements UpgradeProblemUISupport {

	public AutoCorrectPreviewAction(ISelectionProvider provider) {
		super(provider, "Correct automatically - preview");

		Bundle bundle = FrameworkUtil.getBundle(AutoCorrectPreviewAction.class);

		_serviceTracker = new ServiceTracker<>(bundle.getBundleContext(), UpgradeCompare.class, null);

		_serviceTracker.open();
	}

	@Override
	public void run() {
		UpgradeProblem upgradeProblem = getUpgradeProblem(getSelection());

		File file = upgradeProblem.getResource();

		String fileName = file.getName();

		File tempDir = _getTempDir();

		String[] fileNames = fileName.split("\\.");

		String previewFileName = fileNames[0] + "-preview." + fileNames[1];

		FileUtil.copyFileToDir(file, previewFileName, tempDir);

		File tempFile = new File(tempDir, previewFileName);

		autoCorrect(tempFile, upgradeProblem, false);

		UpgradeCompare upgradeCompare = _serviceTracker.getService();

		UIUtil.async(() -> upgradeCompare.openCompareEditor(file, tempFile));
	}

	private File _getTempDir() {
		UpgradeProblemsUIPlugin plugin = UpgradeProblemsUIPlugin.getInstance();

		IPath stateLocation = plugin.getStateLocation();

		IPath tempDirPath = stateLocation.append("temp");

		File tempDir = tempDirPath.toFile();

		tempDir.mkdirs();

		return tempDir;
	}

	private final ServiceTracker<UpgradeCompare, UpgradeCompare> _serviceTracker;

}