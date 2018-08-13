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

package com.liferay.blade.upgrade.liferay70.cmds;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import java.util.Map;

import org.osgi.service.component.annotations.Component;

/**
 * @author Gregory Amerson
 */
@Component(property = {"osgi.command.scope=blade", "osgi.command.function=copyPortalSettings"}, service = Command.class)
public class CopyPortalSettingsCommand implements Command {

	public static final String PARAM_DEST = "dest";

	public static final String PARAM_SOURCE = "source";

	public Object copyPortalSettings(File sourcePortalDir, File destPortalDir) throws CommandException {
		if (!sourcePortalDir.exists() || !destPortalDir.exists()) {
			return null;
		}

		File[] propertiesFiles = sourcePortalDir.listFiles(
			new FilenameFilter() {

				@Override
				public boolean accept(File dir, String name) {
					for (String pattern : _PROPERTIES_FILENAME_PATTERNS) {
						if (name.matches(pattern)) {
							return true;
						}
					}

					return false;
				}

			});

		StringBuilder errors = new StringBuilder();

		for (File propertiesFile : propertiesFiles) {
			try {
				Path destPortalDirPath = destPortalDir.toPath();

				Files.copy(
					propertiesFile.toPath(), destPortalDirPath.resolve(propertiesFile.getName()),
					StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.COPY_ATTRIBUTES);
			}
			catch (IOException ioe) {
				errors.append(ioe.getMessage() + "\n");
			}
		}

		if (errors.length() > 0) {
			throw new CommandException(errors.toString());
		}

		return null;
	}

	@Override
	public Object execute(Map<String, ?> parameters) throws CommandException {
		File src = (File)parameters.get(PARAM_SOURCE);
		File dest = (File)parameters.get(PARAM_DEST);

		return copyPortalSettings(src, dest);
	}

	@Override
	public Object execute(String... args) throws CommandException {
		if ((args != null) && (args.length == 2)) {
			File src = new File(args[0]);
			File dest = new File(args[1]);

			return copyPortalSettings(src, dest);
		}

		return null;
	}

	private static final String[] _PROPERTIES_FILENAME_PATTERNS =
		{"portal-.*\\.properties", "system-ext\\.properties", };

}