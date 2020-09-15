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

package com.liferay.ide.upgrade.commands.ui.internal.code;

import com.liferay.ide.upgrade.commands.ui.internal.UpgradeCommandsUIPlugin;

import java.io.File;

import java.nio.file.Files;
import java.nio.file.Paths;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Simon Jiang
 */
public class CfgToConfigFileConverter {

	public static void convertCfgToConfig(IFile originalCfgFile) {
		try {
			File cfgFile = new File(originalCfgFile.getLocationURI());

			if (!cfgFile.exists()) {
				return;
			}

			File cfgCommnetFile = new File(
				cfgFile.getParent(), FilenameUtils.removeExtension(cfgFile.getName()) + ".txt");
			List<String> allLines = new CopyOnWriteArrayList<>(Files.readAllLines(Paths.get(cfgFile.toURI())));

			List<String> commentLines = new ArrayList<>();

			for (String line : allLines) {
				if (line.startsWith("#")) {
					allLines.remove(line);

					commentLines.add(line);
				}
			}

			if (!commentLines.isEmpty()) {
				allLines.add(0, MessageFormat.format(_configCommnet, cfgCommnetFile.getName(), cfgFile.getName()));

				FileUtils.writeLines(cfgFile, allLines);
				FileUtils.writeLines(cfgCommnetFile, commentLines);
			}

			File targetFile = new File(
				cfgFile.getParent(), FilenameUtils.removeExtension(cfgFile.getName()) + ".config");

			FileUtils.moveFile(cfgFile, targetFile);

			IContainer parentContainer = originalCfgFile.getParent();

			parentContainer.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
		}
		catch (Exception e) {
			UpgradeCommandsUIPlugin.logError(e.getMessage());
		}
	}

	private static String _configCommnet = "#comments has been moved to {0} in same folder as {1}";

}