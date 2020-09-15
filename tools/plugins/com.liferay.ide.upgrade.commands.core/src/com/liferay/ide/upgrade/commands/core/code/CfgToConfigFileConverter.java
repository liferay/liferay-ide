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

package com.liferay.ide.upgrade.commands.core.code;

import static com.google.common.io.Files.getNameWithoutExtension;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import java.text.MessageFormat;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.NullProgressMonitor;

/**
 * @author Simon Jiang
 */
public class CfgToConfigFileConverter {

	public CfgToConfigFileConverter(IFile originalCfgFile) throws Exception {
		Path cfgPath = Paths.get(originalCfgFile.getLocationURI());

		Path cfgParentPath = cfgPath.getParent();

		Path cfgFileNamePath = cfgPath.getFileName();

		String baseName = getNameWithoutExtension(cfgFileNamePath.toString());

		Path cfgCommentPath = cfgParentPath.resolve(baseName + ".txt");

		List<String> cfgLines = new CopyOnWriteArrayList<>(Files.readAllLines(cfgPath));

		List<String> commentLines = new ArrayList<>();

		for (String line : cfgLines) {
			if (line.startsWith("#")) {
				cfgLines.remove(line);

				commentLines.add(line);
			}
		}

		if (!commentLines.isEmpty()) {
			Path cfgCommentFileName = cfgCommentPath.getFileName();

			cfgLines.add(
				0, MessageFormat.format(_configComment, cfgFileNamePath.toString(), cfgCommentFileName.toString()));

			String cfgContent = cfgLines.stream(
			).collect(
				Collectors.joining(System.lineSeparator())
			);

			Files.write(cfgPath, cfgContent.getBytes());

			String cfgCommentContent = commentLines.stream(
			).collect(
				Collectors.joining(System.lineSeparator())
			);

			Files.write(cfgCommentPath, cfgCommentContent.getBytes());
		}

		Path configPath = cfgParentPath.resolve(baseName + ".config");

		Files.move(cfgPath, configPath);

		IContainer parentContainer = originalCfgFile.getParent();

		parentContainer.refreshLocal(IResource.DEPTH_INFINITE, new NullProgressMonitor());
	}

	private static String _configComment = "# comments has been extracted from {0} and added to {1}";

}