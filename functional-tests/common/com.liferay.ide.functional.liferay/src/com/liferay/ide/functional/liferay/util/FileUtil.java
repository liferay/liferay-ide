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

package com.liferay.ide.functional.liferay.util;

import com.liferay.ide.functional.swtbot.util.CoreUtil;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.osgi.util.NLS;

import org.w3c.dom.Document;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

/**
 * @author Greg Amerson
 * @author Cindy Li
 * @author Simon Jiang
 */
public class FileUtil {

	public static void copyDirectiory(String sourceDir, String targetDir) throws IOException {
		(new File(
			targetDir
		)).mkdirs();

		File[] files = (new File(
			sourceDir
		)).listFiles();

		for (File file : files) {
			if (file.isFile()) {
				File sourceFile = file;
				File targetFile = new File(
					new File(
						targetDir
					).getAbsolutePath() + File.separator + file.getName());

				copyFile(sourceFile, targetFile);
			}

			if (file.isDirectory()) {
				String dir1 = sourceDir + "/" + file.getName();
				String dir2 = targetDir + "/" + file.getName();

				copyDirectiory(dir1, dir2);
			}
		}
	}

	public static void copyFile(File src, File dest) {
		if ((src == null) || !src.exists() || (dest == null) || dest.isDirectory()) {
			return;
		}

		byte[] buf = new byte[4096];

		try (InputStream in = Files.newInputStream(src.toPath());
			OutputStream out = Files.newOutputStream(dest.toPath())) {

			int avail = in.read(buf);

			while (avail > 0) {
				out.write(buf, 0, avail);
				avail = in.read(buf);
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void deleteDir(File directory, boolean removeAll) {
		if ((directory == null) || !directory.isDirectory()) {
			return;
		}

		for (File file : directory.listFiles()) {
			if (file.isDirectory() && removeAll) {
				deleteDir(file, removeAll);
			}
			else {
				file.delete();
			}
		}

		directory.delete();
	}

	public static void deleteDirContents(File directory) {
		if ((directory == null) || !directory.isDirectory()) {
			return;
		}

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				deleteDir(file, true);
			}
			else {
				file.delete();
			}
		}
	}

	public static File[] getDirectories(File directory) {
		return directory.listFiles(
			new FileFilter() {

				public boolean accept(File file) {
					return file.isDirectory();
				}

			});
	}

	public static IContainer getWorkspaceContainer(File f) {
		IWorkspace ws = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot wsroot = ws.getRoot();

		IPath path = new Path(f.getAbsolutePath());

		File pathFile = path.toFile();

		IContainer[] wsContainers = wsroot.findContainersForLocationURI(pathFile.toURI());

		if (wsContainers.length > 0) {
			return wsContainers[0];
		}

		return null;
	}

	public static IFile getWorkspaceFile(File f, String expectedProjectName) {
		IWorkspace ws = ResourcesPlugin.getWorkspace();

		IWorkspaceRoot wsroot = ws.getRoot();

		IPath path = new Path(f.getAbsolutePath());

		File pathFile = path.toFile();

		IFile[] wsFiles = wsroot.findFilesForLocationURI(pathFile.toURI());

		if (wsFiles.length > 0) {
			for (IFile wsFile : wsFiles) {
				IProject project = wsFile.getProject();

				String name = project.getName();

				if (name.equals(expectedProjectName)) {
					return wsFile;
				}
			}
		}

		return null;
	}

	public static String readContents(File file) {
		return readContents(file, false);
	}

	public static String readContents(File file, boolean includeNewlines) {
		if (file == null) {
			return null;
		}

		if (!file.exists()) {
			return null;
		}

		StringBuffer contents = new StringBuffer();
		BufferedReader bufferedReader = null;

		try {
			FileReader fileReader = new FileReader(file);

			bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				contents.append(line);

				if (includeNewlines) {
					contents.append(System.getProperty("line.separator"));
				}
			}
		}
		catch (Exception e) {
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				}
				catch (IOException ioe) {
				}
			}
		}

		return contents.toString();
	}

	public static String[] readLinesFromFile(File file) {
		if (file == null) {
			return null;
		}

		if (!file.exists()) {
			return null;
		}

		List<String> lines = new ArrayList<>();
		BufferedReader bufferedReader = null;

		try {
			FileReader fileReader = new FileReader(file);

			bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				lines.add(line);
			}
		}
		catch (Exception e) {
		}
		finally {
			if (bufferedReader != null) {
				try {
					bufferedReader.close();
				}
				catch (Exception e) {

					// no need to log, best effort

				}
			}
		}

		return lines.toArray(new String[lines.size()]);
	}

	public static Document readXML(InputStream inputStream, EntityResolver resolver, ErrorHandler error) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();

			if (resolver != null) {
				db.setEntityResolver(resolver);
			}

			if (error != null) {
				db.setErrorHandler(error);
			}

			return db.parse(inputStream);
		}
		catch (Throwable SWTBot) {
			return null;
		}
	}

	public static Document readXML(String content) {
		return readXML(new ByteArrayInputStream(content.getBytes()), null, null);
	}

	public static Document readXMLFile(File file) {
		return readXMLFile(file, null);
	}

	public static Document readXMLFile(File file, EntityResolver resolver) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;

		try {
			db = dbf.newDocumentBuilder();

			if (resolver != null) {
				db.setEntityResolver(resolver);
			}

			return db.parse(file);
		}
		catch (Throwable SWTBot) {
			return null;
		}
	}

	public static String removeDirPrefix(String dirName) {
		dirName = dirName.trim();

		if (dirName.startsWith("/") || dirName.startsWith("\\")) {
			dirName = dirName.substring(1);
		}

		if (dirName.endsWith("/") || dirName.endsWith("\\")) {
			dirName = dirName.substring(0, dirName.length() - 1);
		}

		return dirName;
	}

	public static boolean searchAndReplace(File file, String search, String replace)
		throws FileNotFoundException, IOException {

		boolean replaced = false;

		if (file.exists()) {
			String searchContents = CoreUtil.readStreamToString(Files.newInputStream(file.toPath()));

			String replaceContents = searchContents.replaceAll(search, replace);

			replaced = !searchContents.equals(replaceContents);

			CoreUtil.writeStreamFromString(replaceContents, Files.newOutputStream(file.toPath()));
		}

		return replaced;
	}

	public static void validateEdit(IFile... files) throws CoreException {
		IWorkspace ws = ResourcesPlugin.getWorkspace();

		IStatus st = ws.validateEdit(files, IWorkspace.VALIDATE_PROMPT);

		if (st.getSeverity() == IStatus.ERROR) {
			throw new CoreException(st);
		}
	}

	public static String validateNewFolder(IFolder folder, String folderValue) {
		if ((folder == null) || (folderValue == null)) {
			return null;
		}

		if (CoreUtil.isNullOrEmpty(folderValue)) {
			return Msgs.folderValueNotEmpty;
		}

		if (!Path.ROOT.isValidPath(folderValue)) {
			return Msgs.folderValueInvalid;
		}

		IWorkspace workspace = ResourcesPlugin.getWorkspace();

		IFolder myFolder = folder.getFolder(folderValue);

		IPath fullPath = myFolder.getFullPath();

		IStatus result = workspace.validatePath(fullPath.toString(), IResource.FOLDER);

		if (!result.isOK()) {
			return result.getMessage();
		}

		myFolder = folder.getFolder(new Path(folderValue));

		if (myFolder.exists()) {
			return Msgs.folderAlreadyExists;
		}

		return null;
	}

	public static int writeFileFromStream(File tempFile, InputStream in) throws IOException {
		byte[] buffer = new byte[1024];

		BufferedOutputStream out = new BufferedOutputStream(Files.newOutputStream(tempFile.toPath()));
		BufferedInputStream bin = new BufferedInputStream(in);

		int bytesRead = 0;
		int bytesTotal = 0;

		// Keep reading from the file while there is any content
		// when the end of the stream has been reached, -1 is returned

		while ((bytesRead = bin.read(buffer)) != -1) {
			out.write(buffer, 0, bytesRead);
			bytesTotal += bytesRead;
		}

		if (bin != null) {
			bin.close();
		}

		if (out != null) {
			out.flush();
			out.close();
		}

		return bytesTotal;
	}

	private static class Msgs extends NLS {

		public static String folderAlreadyExists;
		public static String folderValueInvalid;
		public static String folderValueNotEmpty;

		static {
			initializeMessages(FileUtil.class.getName(), Msgs.class);
		}

	}

}