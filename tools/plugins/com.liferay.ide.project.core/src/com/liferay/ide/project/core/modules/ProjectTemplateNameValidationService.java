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

package com.liferay.ide.project.core.modules;

import aQute.bnd.version.Version;
import aQute.bnd.version.VersionRange;

import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.commons.io.IOUtils;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
public class ProjectTemplateNameValidationService extends ValidationService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayModuleProjectOp op = context(NewLiferayModuleProjectOp.class);

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferayModuleProjectOp.PROP_LIFERAY_VERSION), _listener);
		}

		super.dispose();
	}

	@Override
	protected Status compute() {
		Status retval = Status.createOkStatus();

		NewLiferayModuleProjectOp op = context(NewLiferayModuleProjectOp.class);

		String liferayVersion = get(op.getLiferayVersion());

		String projectTemplateName = get(op.getProjectTemplateName());

		projectTemplateName = projectTemplateName.replace("-", ".");

		VersionRange versionRange = _projectTemplateVersionRangeMap.get(projectTemplateName);

		if (versionRange != null) {
			boolean include = versionRange.includes(new Version(liferayVersion));

			if (!include) {
				retval = Status.createErrorStatus(
					"Specified Liferay version is invaild. Must be in range " + versionRange);
			}
		}
		else {
			retval = Status.createWarningStatus("Unable to get supported Liferay version.");
		}

		return retval;
	}

	@Override
	protected void initValidationService() {
		super.initValidationService();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayModuleProjectOp op = context(NewLiferayModuleProjectOp.class);

		SapphireUtil.attachListener(op.property(NewLiferayModuleProjectOp.PROP_LIFERAY_VERSION), _listener);

		_loadSupportedVersionRanges();
	}

	private void _loadSupportedVersionRanges() {
		File bladeJar = null;

		try {
			bladeJar = FileUtil.getFile(BladeCLI.getBladeCLIPath());
		}
		catch (BladeCLIException bclie) {
		}

		if (bladeJar != null) {
			try (ZipFile zipFile = new ZipFile(bladeJar)) {
				Enumeration<? extends ZipEntry> entries = zipFile.entries();

				while (entries.hasMoreElements()) {
					ZipEntry entry = entries.nextElement();

					String entryName = entry.getName();

					if (entryName.endsWith(".jar") && entryName.startsWith("com.liferay.project.templates.")) {
						try (InputStream in = zipFile.getInputStream(entry)) {
							ProjectCore projectCore = ProjectCore.getDefault();

							File stateFile = FileUtil.getFile(projectCore.getStateLocation());

							File tempFile = new File(stateFile, entryName);

							FileUtil.writeFileFromStream(tempFile, in);

							try (ZipFile tempZipFile = new ZipFile(tempFile)) {
								Enumeration<? extends ZipEntry> tempEntries = tempZipFile.entries();

								while (tempEntries.hasMoreElements()) {
									ZipEntry tempEntry = tempEntries.nextElement();

									String tempEntryName = tempEntry.getName();

									if (tempEntryName.equals("META-INF/MANIFEST.MF")) {
										try (InputStream manifestInput = tempZipFile.getInputStream(tempEntry)) {
											List<String> lines = IOUtils.readLines(manifestInput);

											for (String line : lines) {
												String liferayVersionString = "Liferay-Versions:";

												if (line.startsWith(liferayVersionString)) {
													String versionRangeValue = line.substring(
														liferayVersionString.length());

													String projectTemplateName = entryName.substring(
														"com.liferay.project.templates.".length(),
														entryName.indexOf('-'));

													_projectTemplateVersionRangeMap.put(
														projectTemplateName, new VersionRange(versionRangeValue));

													break;
												}
											}
										}

										break;
									}
								}
							}

							tempFile.delete();
						}
					}
				}
			}
			catch (IOException ioe) {
			}
		}
	}

	private static Map<String, VersionRange> _projectTemplateVersionRangeMap = new HashMap<>();

	private FilteredListener<PropertyContentEvent> _listener;

}