/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.liferay.blade.upgrade.liferay70.cmds;

import com.liferay.blade.api.Command;
import com.liferay.blade.api.CommandException;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Map;

import org.osgi.service.component.annotations.Component;

@Component(
	property = {
		"osgi.command.scope=blade",
		"osgi.command.function=copyPortalSettings"
	},
	service = Command.class
)
public class CopyPortalSettingsCommand implements Command {

	public static final String PARAM_SOURCE = "source";
	public static final String PARAM_DEST = "dest";

	private final String[] PROPERTIES_FILENAME_PATTERNS = {
		"portal-.*\\.properties",
		"system-ext\\.properties",
	};

	public Object copyPortalSettings(File sourcePortalDir, File destPortalDir)
			throws CommandException {

		if (!sourcePortalDir.exists() || !destPortalDir.exists()) {
			return null;
		}

		final File[] propertiesFiles = sourcePortalDir.listFiles(
			new FilenameFilter() {
				@Override
				public boolean accept(File dir, String name) {
					for (String pattern : PROPERTIES_FILENAME_PATTERNS) {
						if (name.matches(pattern)) {
							return true;
						}
					}

					return false;
				}
			});

		final StringBuilder errors = new StringBuilder();

		for (File propertiesFile : propertiesFiles ) {
			try {
				Files.copy(
					propertiesFile.toPath(),
					destPortalDir.toPath().resolve(propertiesFile.getName()),
					StandardCopyOption.REPLACE_EXISTING,
					StandardCopyOption.COPY_ATTRIBUTES);
			} catch (IOException e) {
				errors.append(e.getMessage() + "\n");
			}
		}

		if (errors.length() > 0) {
			throw new CommandException(errors.toString());
		}

		return null;
	}

	@Override
	public Object execute(Map<String, ?> parameters) throws CommandException {
		File src = (File) parameters.get(PARAM_SOURCE);
		File dest = (File) parameters.get(PARAM_DEST);

		return copyPortalSettings(src, dest);
	}

	@Override
	public Object execute(String... args) throws CommandException {
		if (args != null && args.length == 2) {
			File src = new File(args[0]);
			File dest = new File(args[1]);

			return copyPortalSettings(src, dest);
		}

		return null;
	}

}