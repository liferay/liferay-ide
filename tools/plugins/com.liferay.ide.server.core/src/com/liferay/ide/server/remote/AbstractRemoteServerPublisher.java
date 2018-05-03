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

package com.liferay.ide.server.remote;

import com.liferay.ide.core.IWebProject;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.server.core.LiferayServerCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TimeZone;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.wst.server.core.model.IModuleResource;
import org.eclipse.wst.server.core.model.IModuleResourceDelta;

/**
 * @author Simon Jiang
 */
public abstract class AbstractRemoteServerPublisher implements IRemoteServerPublisher {

	public AbstractRemoteServerPublisher(IProject project) {
		_project = project;
	}

	public IPath publishModuleDelta(
			String archiveName, IModuleResourceDelta[] deltas, String deletePrefix, boolean adjustGMTOffset)
		throws CoreException {

		IPath path = LiferayServerCore.getTempLocation("partial-war", archiveName);

		File warfile = path.toFile();

		File warParent = warfile.getParentFile();

		warParent.mkdirs();

		try (OutputStream outputStream = Files.newOutputStream(warfile.toPath());
			ZipOutputStream zip = new ZipOutputStream(outputStream)) {

			Map<ZipEntry, String> deleteEntries = new HashMap<>();

			processResourceDeltas(deltas, zip, deleteEntries, deletePrefix, StringPool.EMPTY, adjustGMTOffset);

			for (Entry<ZipEntry, String> entry : deleteEntries.entrySet()) {
				zip.putNextEntry((ZipEntry)entry);

				String deleteEntry = entry.getValue();

				zip.write(deleteEntry.getBytes());
			}
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}

		return new Path(warfile.getAbsolutePath());
	}

	protected void addRemoveProps(
			IPath deltaPath, IResource deltaResource, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries,
			String deletePrefix)
		throws IOException {

		String archive = _removeArchive(deltaPath.toPortableString());

		ZipEntry zipEntry = null;

		// check to see if we already have an entry for this archive

		for (ZipEntry entry : deleteEntries.keySet()) {
			String entryName = entry.getName();

			if (entryName.startsWith(archive)) {
				zipEntry = entry;
			}
		}

		if (zipEntry == null) {
			zipEntry = new ZipEntry(archive + "META-INF/" + deletePrefix + "-partialapp-delete.props");
		}

		String existingFiles = deleteEntries.get(zipEntry);

		String path = deltaPath.toPortableString();

		String file = path.substring(archive.length());

		if (deltaResource.getType() == IResource.FOLDER) {
			file += "/.*";
		}

		deleteEntries.put(zipEntry, (existingFiles != null ? existingFiles : StringPool.EMPTY) + (file + "\n"));
	}

	protected void addToZip(IPath path, IResource resource, ZipOutputStream zip, boolean adjustGMTOffset)
		throws CoreException, IOException {

		switch (resource.getType()) {
			case IResource.FILE:
				ZipEntry zipEntry = new ZipEntry(path.toString());

				zip.putNextEntry(zipEntry);

				try (InputStream inputStream = ((IFile)resource).getContents()) {
					if (adjustGMTOffset) {
						TimeZone currentTimeZone = TimeZone.getDefault();

						Calendar currentDt = new GregorianCalendar(currentTimeZone, Locale.getDefault());

						// Get the Offset from GMT taking current TZ into account

						int gmtOffset = currentTimeZone.getOffset(
							currentDt.get(Calendar.ERA), currentDt.get(Calendar.YEAR), currentDt.get(Calendar.MONTH),
							currentDt.get(Calendar.DAY_OF_MONTH), currentDt.get(Calendar.DAY_OF_WEEK),
							currentDt.get(Calendar.MILLISECOND));

						zipEntry.setTime(System.currentTimeMillis() + (gmtOffset * -1));
					}
				}

				break;

			case IResource.FOLDER:
			case IResource.PROJECT:
				IContainer container = (IContainer)resource;

				IResource[] members = container.members();

				for (IResource res : members) {
					addToZip(path.append(res.getName()), res, zip, adjustGMTOffset);
				}
		}
	}

	protected IProject getProject() {
		return _project;
	}

	protected void processResourceDeltas(
			IModuleResourceDelta[] deltas, ZipOutputStream zip, Map<ZipEntry, String> deleteEntries,
			String deletePrefix, String deltaPrefix, boolean adjustGMTOffset)
		throws CoreException, IOException {

		for (IModuleResourceDelta delta : deltas) {
			int deltaKind = delta.getKind();
			IModuleResource deltaModuleResource = delta.getModuleResource();

			IResource deltaResource = (IResource)deltaModuleResource.getAdapter(IResource.class);

			IProject deltaProject = deltaResource.getProject();

			// IDE-110 IDE-648

			IWebProject lrproject = LiferayCore.create(IWebProject.class, deltaProject);

			if (lrproject != null) {
				IFolder webappRoot = lrproject.getDefaultDocrootFolder();

				IPath deltaPath = null;

				if (FileUtil.exists(webappRoot)) {
					IPath deltaFullPath = deltaResource.getFullPath();
					IPath containerFullPath = webappRoot.getFullPath();

					deltaPath = new Path(deltaPrefix + deltaFullPath.makeRelativeTo(containerFullPath));

					if ((deltaPath != null) && (deltaPath.segmentCount() > 0)) {
						break;
					}
				}

				if ((deltaKind == IModuleResourceDelta.ADDED) || (deltaKind == IModuleResourceDelta.CHANGED)) {
					addToZip(deltaPath, deltaResource, zip, adjustGMTOffset);
				}
				else if (deltaKind == IModuleResourceDelta.REMOVED) {
					addRemoveProps(deltaPath, deltaResource, zip, deleteEntries, deletePrefix);
				}
				else if (deltaKind == IModuleResourceDelta.NO_CHANGE) {
					IModuleResourceDelta[] children = delta.getAffectedChildren();

					processResourceDeltas(children, zip, deleteEntries, deletePrefix, deltaPrefix, adjustGMTOffset);
				}
			}
		}
	}

	private String _removeArchive(String archive) {
		int index = Math.max(archive.lastIndexOf(".war"), archive.lastIndexOf(".jar"));

		if (index >= 0) {
			return archive.substring(0, index + 5);
		}

		return StringPool.EMPTY;
	}

	private IProject _project;

}