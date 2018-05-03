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

package com.liferay.ide.theme.core.util;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.theme.core.ThemeCore;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.osgi.util.NLS;
import org.eclipse.wst.server.core.internal.Messages;
import org.eclipse.wst.server.core.internal.ProgressUtil;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class BuildHelper {

	// size of the buffer

	/**
	 * Utility method to recursively delete a directory.
	 *
	 * @param dir
	 *            a directory
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public static IStatus[] deleteDirectory(File dir, IProgressMonitor monitor) {
		if (!dir.exists() || !dir.isDirectory()) {
			return new IStatus[] {
				new Status(
					IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorNotADirectory, dir.getAbsolutePath()),
					null)
			};
		}

		List<IStatus> status = new ArrayList<>(2);

		try {
			File[] files = dir.listFiles();

			int size = files.length;

			monitor = ProgressUtil.getMonitorFor(monitor);

			monitor.beginTask(NLS.bind(Messages.deletingTask, new String[] {dir.getAbsolutePath()}), size * 10);

			// cycle through files

			boolean deleteCurrent = true;

			for (int i = 0; i < size; i++) {
				File current = files[i];

				if (current.isFile()) {
					if (!current.delete()) {
						status.add(
							new Status(
								IStatus.ERROR, ThemeCore.PLUGIN_ID, 0,
								NLS.bind(Messages.errorDeleting, files[i].getAbsolutePath()), null));
						deleteCurrent = false;
					}

					monitor.worked(10);
				}
				else if (current.isDirectory()) {
					monitor.subTask(NLS.bind(Messages.deletingTask, new String[] {current.getAbsolutePath()}));
					IStatus[] stat = deleteDirectory(current, ProgressUtil.getSubMonitorFor(monitor, 10));

					if (ListUtil.isNotEmpty(stat)) {
						deleteCurrent = false;
						_addArrayToList(status, stat);
					}
				}
			}

			if (deleteCurrent && !dir.delete()) {
				status.add(
					new Status(
						IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, dir.getAbsolutePath()),
						null));
			}

			monitor.done();
		}
		catch (Exception e) {
			ThemeCore.logError("Error deleting directory " + dir.getAbsolutePath(), e);
			status.add(new Status(IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, e.getLocalizedMessage(), null));
		}

		IStatus[] stat = new IStatus[status.size()];

		status.toArray(stat);

		return stat;
	}

	// the buffer

	/**
	 * Create a new PublishHelper.
	 *
	 * @param tempDirectory
	 *            a temporary directory to use during publishing, or
	 *            <code>null</code> to use the default. If it does not exist,
	 *            the folder will be created
	 */
	public BuildHelper() {
		_tempDir = _defaultTempDir;

		if (!_tempDir.exists()) {
			_tempDir.mkdirs();
		}
	}

	/**
	 * Handle a delta publish.
	 *
	 * @param delta
	 *            a module resource delta
	 * @param path
	 *            the path to publish to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishDelta(IResourceDelta delta, IPath path, IPath[] restorePaths, IProgressMonitor monitor) {
		List<IStatus> status = new ArrayList<>(2);

		IResource resource = delta.getResource();
		int kind2 = delta.getKind();

		if (resource instanceof IFile) {
			IFile file = (IFile)resource;

			try {
				if (kind2 == IResourceDelta.REMOVED) {
					_deleteFile(path, file, restorePaths);
				}
				else {
					IPath diffsRelativePath = _getDiffsRelativePath(file.getProjectRelativePath());

					if (diffsRelativePath != null) {
						IPath path2 = path.append(diffsRelativePath);

						File f = path2.toFile().getParentFile();

						if (!f.exists()) {
							f.mkdirs();
						}

						_copyFile(file, path2);
					}
				}
			}
			catch (CoreException ce) {
				status.add(ce.getStatus());
			}

			IStatus[] stat = new IStatus[status.size()];

			status.toArray(stat);

			return stat;
		}

		if (kind2 == IResourceDelta.ADDED) {

			// find relative path from _diffs and append that to path.

			IPath diffsPath = resource.getProjectRelativePath();

			IPath diffsRelativePath = _getDiffsRelativePath(diffsPath);

			if (diffsRelativePath != null) {
				IPath path2 = path.append(diffsRelativePath);

				File file = path2.toFile();

				if (!file.exists() && !file.mkdirs()) {
					status.add(
						new Status(IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorMkdir, path2), null));
					IStatus[] stat = new IStatus[status.size()];

					status.toArray(stat);

					return stat;
				}
			}
		}

		IResourceDelta[] childDeltas = delta.getAffectedChildren();

		int size = childDeltas.length;

		for (int i = 0; i < size; i++) {
			IStatus[] stat = publishDelta(childDeltas[i], path, restorePaths, monitor);

			_addArrayToList(status, stat);
		}

		if (kind2 == IResourceDelta.REMOVED) {
			IPath diffsRelativePath = _getDiffsRelativePath(resource.getProjectRelativePath());

			if (diffsRelativePath != null) {
				IPath path2 = path.append(diffsRelativePath);

				File file = path2.toFile();

				if (file.exists() && !file.delete()) {
					status.add(
						new Status(
							IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, path2), null));
				}
			}
		}

		IStatus[] stat = new IStatus[status.size()];

		status.toArray(stat);

		return stat;
	}

	/**
	 * Handle a delta publish.
	 *
	 * @param delta
	 *            a module resource delta
	 * @param path
	 *            the path to publish to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishDelta(IResourceDelta[] delta, IPath path, IPath[] restorePaths, IProgressMonitor monitor) {
		if (delta == null) {
			return _EMPTY_STATUS;
		}

		monitor = ProgressUtil.getMonitorFor(monitor);

		List<IStatus> status = new ArrayList<>(2);
		int size2 = delta.length;

		for (int i = 0; i < size2; i++) {
			IStatus[] stat = publishDelta(delta[i], path, restorePaths, monitor);

			_addArrayToList(status, stat);
		}

		IStatus[] stat = new IStatus[status.size()];

		status.toArray(stat);

		return stat;
	}

	/**
	 * Publish the given module resources to the given path.
	 *
	 * @param resources
	 *            an array of module resources
	 * @param path
	 *            a path to publish to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishFull(IResource[] resources, IPath path, IProgressMonitor monitor) {
		if (resources == null) {
			return _EMPTY_STATUS;
		}

		monitor = ProgressUtil.getMonitorFor(monitor);

		List<IStatus> status = new ArrayList<>(2);
		int size = resources.length;

		for (int i = 0; i < size; i++) {
			IStatus[] stat = _copy(resources[i], path, monitor);

			_addArrayToList(status, stat);

			if (monitor.isCanceled()) {
				break;
			}
		}

		IStatus[] stat = new IStatus[status.size()];

		status.toArray(stat);

		return stat;
	}

	/**
	 * Smart copy the given module resources to the given path.
	 *
	 * @param resources
	 *            an array of module resources
	 * @param path
	 *            an external path to copy to
	 * @param ignore
	 *            an array of paths relative to path to ignore, i.e. not delete
	 *            or copy over
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishSmart(IResource[] resources, IPath path, IPath[] ignore, IProgressMonitor monitor) {
		if (resources == null) {
			return _EMPTY_STATUS;
		}

		monitor = ProgressUtil.getMonitorFor(monitor);

		List<IStatus> status = new ArrayList<>(2);
		File toDir = path.toFile();
		int fromSize = resources.length;

		String[] fromFileNames = new String[fromSize];

		for (int i = 0; i < fromSize; i++) {
			fromFileNames[i] = resources[i].getName();
		}

		List<String> ignoreFileNames = new ArrayList<>();

		if (ignore != null) {
			for (int i = 0; i < ignore.length; i++) {
				if (ignore[i].segmentCount() == 1) {
					ignoreFileNames.add(ignore[i].toOSString());
				}
			}
		}

		// cache files and file names for performance

		File[] toFiles = null;
		String[] toFileNames = null;

		boolean foundExistingDir = false;

		if (toDir.exists()) {
			if (toDir.isDirectory()) {
				foundExistingDir = true;
				toFiles = toDir.listFiles();

				int toSize = toFiles.length;

				toFileNames = new String[toSize];

				// check if this exact file exists in the new directory

				for (int i = 0; i < toSize; i++) {
					toFileNames[i] = toFiles[i].getName();
					boolean dir = toFiles[i].isDirectory();
					boolean found = false;

					for (int j = 0; j < fromSize; j++) {
						if (toFileNames[i].equals(fromFileNames[j]) && (dir == (resources[j] instanceof IFolder))) {
							found = true;

							break;
						}
					}

					// delete file if it can't be found or isn't the correct
					// type

					if (!found) {
						boolean delete = true;

						// if should be preserved, don't delete and don't try to
						// copy

						for (String preserveFileName : ignoreFileNames) {
							if (toFileNames[i].equals(preserveFileName)) {
								delete = false;

								break;
							}
						}

						if (delete) {
							if (dir) {
								IStatus[] stat = deleteDirectory(toFiles[i], null);

								_addArrayToList(status, stat);
							}
							else {
								if (!toFiles[i].delete()) {
									status.add(
										new Status(
											IStatus.ERROR, ThemeCore.PLUGIN_ID, 0,
											NLS.bind(Messages.errorDeleting, toFiles[i].getAbsolutePath()), null));
								}
							}
						}

						toFiles[i] = null;
						toFileNames[i] = null;
					}
				}
			}
			else {
				if (!toDir.delete()) {
					status.add(
						new Status(
							IStatus.ERROR, ThemeCore.PLUGIN_ID, 0,
							NLS.bind(Messages.errorDeleting, toDir.getAbsolutePath()), null));
					IStatus[] stat = new IStatus[status.size()];

					status.toArray(stat);

					return stat;
				}
			}
		}

		if (!foundExistingDir && !toDir.mkdirs()) {
			status.add(
				new Status(
					IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorMkdir, toDir.getAbsolutePath()),
					null));
			IStatus[] stat = new IStatus[status.size()];

			status.toArray(stat);

			return stat;
		}

		if (monitor.isCanceled()) {
			return new IStatus[] {Status.CANCEL_STATUS};
		}

		monitor.worked(50);

		// cycle through files and only copy when it doesn't exist
		// or is newer

		if (toFiles == null) {
			toFiles = toDir.listFiles();

			if (toFiles == null) {
				toFiles = new File[0];
			}
		}

		int toSize = toFiles.length;

		int dw = 0;

		if (toSize > 0) {
			dw = 500 / toSize;
		}

		// cache file names and last modified dates for performance

		if (toFileNames == null) {
			toFileNames = new String[toSize];
		}

		long[] toFileMod = new long[toSize];

		for (int i = 0; i < toSize; i++) {
			if (toFiles[i] != null) {
				if (toFileNames[i] != null) {
					toFileNames[i] = toFiles[i].getName();
				}

				toFileMod[i] = toFiles[i].lastModified();
			}
		}

		for (int i = 0; i < fromSize; i++) {
			IResource current = resources[i];
			String name = fromFileNames[i];
			boolean currentIsDir = current instanceof IFolder;

			if (!currentIsDir) {

				// check if this is a new or newer file

				boolean copy = true;
				IFile mf = (IFile)current;

				long mod = -1;
				IFile file = (IFile)mf.getAdapter(IFile.class);

				if (file != null) {
					mod = file.getLocalTimeStamp();
				}
				else {
					File file2 = (File)mf.getAdapter(File.class);

					mod = file2.lastModified();
				}

				for (int j = 0; j < toSize; j++) {
					if (name.equals(toFileNames[j]) && (mod == toFileMod[j])) {
						copy = false;

						break;
					}
				}

				if (copy) {
					try {
						_copyFile(mf, path.append(name));
					}
					catch (CoreException ce) {
						status.add(ce.getStatus());
					}
				}

				monitor.worked(dw);
			}
			else {
				IFolder folder = (IFolder)current;
				IResource[] children = null;

				try {
					children = folder.members();
				}
				catch (CoreException ce) {
					ce.printStackTrace();
				}

				// build array of ignored Paths that apply to this folder

				IPath[] ignoreChildren = null;

				if (ignore != null) {
					List<IPath> ignoreChildPaths = new ArrayList<>();

					for (IPath preservePath : ignore) {
						if (preservePath.segment(0).equals(name)) {
							ignoreChildPaths.add(preservePath.removeFirstSegments(1));
						}
					}

					if (ListUtil.isNotEmpty(ignoreChildPaths)) {
						ignoreChildren = ignoreChildPaths.toArray(new Path[ignoreChildPaths.size()]);
					}
				}

				monitor.subTask(NLS.bind(Messages.copyingTask, new String[] {name, name}));
				IStatus[] stat = publishSmart(
					children, path.append(name), ignoreChildren, ProgressUtil.getSubMonitorFor(monitor, dw));

				_addArrayToList(status, stat);
			}
		}

		if (monitor.isCanceled()) {
			return new IStatus[] {Status.CANCEL_STATUS};
		}

		monitor.worked(500 - dw * toSize);
		monitor.done();

		IStatus[] stat = new IStatus[status.size()];

		status.toArray(stat);

		return stat;
	}

	/**
	 * Smart copy the given module resources to the given path.
	 *
	 * @param resources
	 *            an array of module resources
	 * @param path
	 *            an external path to copy to
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishSmart(IResource[] resources, IPath path, IProgressMonitor monitor) {
		return publishSmart(resources, path, null, monitor);
	}

	/**
	 * Accepts an IModuleResource array which is expected to contain a single
	 * IModuleFile resource and copies it to the specified path, which should
	 * include the name of the file to write. If the array contains more than a
	 * single resource or the resource is not an IModuleFile resource, the file
	 * is not created. Currently no error is returned, but error handling is
	 * recommended since that is expected to change in the future.
	 *
	 * @param resources
	 *            an array containing a single IModuleFile resource
	 * @param path
	 *            the path, including file name, where the file should be
	 *            created
	 * @param monitor
	 *            a progress monitor, or <code>null</code> if progress reporting
	 *            and cancellation are not desired
	 * @return a possibly-empty array of error and warning status
	 */
	public IStatus[] publishToPath(IResource[] resources, IPath path, IProgressMonitor monitor) {
		if (ListUtil.isEmpty(resources)) {

			// should also check if resources consists of all empty directories

			File file = path.toFile();

			if (file.exists()) {
				file.delete();
			}

			return _EMPTY_STATUS;
		}

		monitor = ProgressUtil.getMonitorFor(monitor);

		if ((resources.length == 1) && resources[0] instanceof IFile) {
			try {
				_copyFile((IFile)resources[0], path);
			}
			catch (CoreException ce) {
				return new IStatus[] {ce.getStatus()};
			}
		}

		return _EMPTY_STATUS;
	}

	/**
	 * Returns <code>true</code> if the module file should be copied to the
	 * destination, <code>false</code> otherwise.
	 *
	 * @param moduleFile
	 *            the module file
	 * @param toPath
	 *            destination.
	 * @return <code>true</code>, if the module file should be copied
	 */
	protected boolean isCopyFile(IFile moduleFile, IPath toPath) {
		return true;
	}

	private static void _addArrayToList(List<IStatus> list, IStatus[] a) {
		if ((list == null) || ListUtil.isEmpty(a)) {
			return;
		}

		int size = a.length;

		for (int i = 0; i < size; i++) {
			list.add(a[i]);
		}
	}

	private static void _deleteFile(IPath path, IFile file, IPath[] restorePaths) throws CoreException {
		IPath diffsPath = file.getProjectRelativePath();

		IPath diffsRelativePath = _getDiffsRelativePath(diffsPath);

		if (diffsRelativePath != null) {
			IPath path2 = path.append(diffsRelativePath);

			// restore this file from the first restorePaths that matches

			boolean restored = false;

			for (IPath restorePath : restorePaths) {
				File restoreFile = restorePath.append(diffsRelativePath).toFile();

				if (restoreFile.exists()) {
					try {
						FileUtils.copyFile(restoreFile, path2.toFile());
						restored = true;

						break;
					}
					catch (IOException ioe) {
						throw new CoreException(
							new Status(
								IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind("Error restoring theme file.", path2),
								ioe));
					}
				}
			}

			if (!restored) {
				if (path2.toFile().exists() && !path2.toFile().delete()) {
					throw new CoreException(
						new Status(
							IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, path2), null));
				}
			}
		}
	}

	private static IPath _getDiffsRelativePath(IPath diffsPath) {
		IPath diffsRelativePath = null;

		for (int i = 0; i < diffsPath.segmentCount(); i++) {
			if ("_diffs".equals(diffsPath.segment(i))) {
				diffsRelativePath = diffsPath.removeFirstSegments(i + 1);

				break;
			}
		}

		return diffsRelativePath;
	}

	/**
	 * Safe delete. Tries to delete multiple times before giving up.
	 *
	 * @param f
	 * @return <code>true</code> if it succeeds, <code>false</code> otherwise
	 */
	private static boolean _safeDelete(File f, int retrys) {
		int count = 0;

		while (count < retrys) {
			if (f.delete()) {
				return true;
			}

			count++;

			// delay if we are going to try again

			if (count < retrys) {
				try {
					Thread.sleep(100);
				}
				catch (Exception e) {
				}
			}
		}

		return false;
	}

	/**
	 * Safe rename. Will try multiple times before giving up.
	 *
	 * @param from
	 * @param to
	 * @param retrys
	 *            number of times to retry
	 * @return <code>true</code> if it succeeds, <code>false</code> otherwise
	 */
	private static boolean _safeRename(File from, File to, int retrys) {

		// make sure parent dir exists

		File dir = to.getParentFile();

		if ((dir != null) && !dir.exists()) {
			dir.mkdirs();
		}

		int count = 0;

		while (count < retrys) {
			if (from.renameTo(to)) {
				return true;
			}

			count++;

			// delay if we are going to try again

			if (count < retrys) {
				try {
					Thread.sleep(100);
				}
				catch (Exception e) {
				}
			}
		}

		return false;
	}

	private IStatus[] _copy(IResource resource, IPath path, IProgressMonitor monitor) {
		if ((monitor != null) && monitor.isCanceled()) {
			return new IStatus[0];
		}

		List<IStatus> status = new ArrayList<>(2);

		if (resource instanceof IFolder) {
			IFolder folder = (IFolder)resource;
			IStatus[] stat;

			try {
				stat = publishFull(folder.members(), path, monitor);

				_addArrayToList(status, stat);
			}
			catch (CoreException ce) {
				ce.printStackTrace();
			}
		}
		else {
			IFile mf = (IFile)resource;

			IPath diffsRelativePath = _getDiffsRelativePath(mf.getProjectRelativePath());

			if (diffsRelativePath != null) {
				path = path.append(diffsRelativePath);

				File f = path.toFile().getParentFile();

				if (f.exists()) {
					try {
						_copyFile(mf, path);
					}
					catch (CoreException ce) {
						status.add(ce.getStatus());
					}
				}
				else {

					// Create the parent directory.

					if (f.mkdirs()) {
						try {
							_copyFile(mf, path);
						}
						catch (CoreException ce) {
							status.add(ce.getStatus());
						}
					}
					else {
						status.add(
							new Status(
								IStatus.ERROR, ThemeCore.PLUGIN_ID, 0,
								NLS.bind(Messages.errorMkdir, f.getAbsolutePath()), null));
					}
				}
			}
		}

		IStatus[] stat = new IStatus[status.size()];

		status.toArray(stat);

		return stat;
	}

	private void _copyFile(IFile mf, IPath path) throws CoreException {
		if (!isCopyFile(mf, path)) {
			return;
		}

		IFile file = (IFile)mf.getAdapter(IFile.class);

		if (file != null) {
			try (InputStream inputStream = file.getContents()) {
				_copyFile(inputStream, path, file.getLocalTimeStamp());
			}
			catch (IOException ioe) {
				throw new CoreException(
					new Status(
						IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorReading, file.getLocation()),
						ioe));
			}
		}
		else {
			File file2 = (File)mf.getAdapter(File.class);
			InputStream in = null;

			try {
				in = new FileInputStream(file2);
			}
			catch (IOException ioe) {
				throw new CoreException(
					new Status(
						IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorReading, file2.getAbsolutePath()),
						ioe));
			}

			_copyFile(in, path, file2.lastModified());
		}
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 *
	 * @param in
	 *            an input stream
	 * @param to
	 *            a path to copy to. the directory must already exist
	 * @param ts
	 *            timestamp
	 * @throws CoreException
	 *             if anything goes wrong
	 */
	private void _copyFile(InputStream in, IPath to, long ts) throws CoreException {
		OutputStream out = null;

		File tempFile = null;
		File tempFileParentDir = null;

		try {
			File file = to.toFile();

			// IDE-796 need to make sure temporary file is generated in same
			// directory as file destination so that
			// file.renameTo() will never fail due to source/destination being
			// on two different file systems

			if ((file != null) && file.getParentFile().exists()) {
				tempFileParentDir = to.toFile().getParentFile();
			}
			else {
				tempFileParentDir = _tempDir;
			}

			tempFile = File.createTempFile(_TEMPFILE_PREFIX, "." + to.getFileExtension(), tempFileParentDir);

			out = new FileOutputStream(tempFile);

			int avail = in.read(_buf);

			while (avail > 0) {
				out.write(_buf, 0, avail);
				avail = in.read(_buf);
			}

			out.close();
			out = null;

			_moveTempFile(tempFile, file);

			if ((ts != IResource.NULL_STAMP) && (ts != 0)) {
				file.setLastModified(ts);
			}
		}
		catch (CoreException ce) {
			throw ce;
		}
		catch (Exception e) {
		}
		finally {
			if ((tempFile != null) && tempFile.exists()) {
				tempFile.deleteOnExit();
			}

			try {
				if (in != null) {
					in.close();
				}
			}
			catch (Exception ex) {
			}

			try {
				if (out != null) {
					out.close();
				}
			}
			catch (Exception ex) {
			}
		}
	}

	/**
	 * Copy a file from a to b. Closes the input stream after use.
	 *
	 * @param in
	 *            an InputStream
	 * @param to
	 *            the file to copy to
	 * @return a status
	 */
	private IStatus _copyFile(InputStream in, String to) {
		OutputStream out = null;

		try {
			out = new FileOutputStream(to);

			int avail = in.read(_buf);

			while (avail > 0) {
				out.write(_buf, 0, avail);
				avail = in.read(_buf);
			}

			return Status.OK_STATUS;
		}
		catch (Exception e) {
			ThemeCore.logError("Error copying file", e);

			return new Status(
				IStatus.ERROR, ThemeCore.PLUGIN_ID, 0,
				NLS.bind(Messages.errorCopyingFile, new String[] {to, e.getLocalizedMessage()}), e);
		}
		finally {
			try {
				if (in != null) {
					in.close();
				}
			}
			catch (Exception ex) {
			}

			try {
				if (out != null) {
					out.close();
				}
			}
			catch (Exception ex) {
			}
		}
	}

	/**
	 * Utility method to move a temp file into position by deleting the original
	 * and swapping in a new copy.
	 *
	 * @param tempFile
	 * @param file
	 * @throws CoreException
	 */
	private void _moveTempFile(File tempFile, File file) throws CoreException {
		if (file.exists()) {
			if (!_safeDelete(file, 2)) {

				// attempt to rewrite an existing file with the tempFile
				// contents if
				// the existing file can't be deleted to permit the move

				try {
					InputStream in = new FileInputStream(tempFile);

					IStatus status = _copyFile(in, file.getPath());

					if (!status.isOK()) {
						MultiStatus status2 = new MultiStatus(
							ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorDeleting, file.toString()), null);

						status2.add(status);
						throw new CoreException(status2);
					}

					return;
				}
				catch (FileNotFoundException fnfe) {
				}
				finally {
					tempFile.delete();
				}
			}
		}

		if (!_safeRename(tempFile, file, 10)) {
			throw new CoreException(
				new Status(
					IStatus.ERROR, ThemeCore.PLUGIN_ID, 0, NLS.bind(Messages.errorRename, tempFile.toString()), null));
		}
	}

	private static final int _BUFFER = 65536;

	private static final IStatus[] _EMPTY_STATUS = new IStatus[0];

	private static final String _TEMPFILE_PREFIX = ".tmp-safe-to-delete-";

	private static byte[] _buf = new byte[_BUFFER];
	private static final File _defaultTempDir = ThemeCore.getDefault().getStateLocation().toFile();

	private File _tempDir;

}