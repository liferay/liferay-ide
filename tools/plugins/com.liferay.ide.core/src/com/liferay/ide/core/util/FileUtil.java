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

package com.liferay.ide.core.util;

import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.StringBufferOutputStream;

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
import java.io.RandomAccessFile;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import java.net.URL;

import java.nio.file.Files;

import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.osgi.util.NLS;

import org.w3c.dom.Document;

import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 * @author Simon Jiang
 * @author Terry Jia
 * @author Charles Wu
 */
public class FileUtil {

	public static void clearContents(File versionFile) {
		if (notExists(versionFile)) {
			return;
		}

		try (RandomAccessFile file = new RandomAccessFile(versionFile, "rw")) {
			file.setLength(0);
		}
		catch (Exception ex) {
			LiferayCore.logError("Unable clean contents for " + versionFile.getName(), ex);
		}
	}

	public static void copyFile(File src, File dest) {
		if (notExists(src) || (dest == null) || dest.isDirectory()) {
			return;
		}

		byte[] buf = new byte[4096];

		try (OutputStream out = Files.newOutputStream(dest.toPath());
			InputStream in = Files.newInputStream(src.toPath())) {

			int avail = in.read(buf);

			while (avail > 0) {
				out.write(buf, 0, avail);

				avail = in.read(buf);
			}
		}
		catch (Exception e) {
			LiferayCore.logError("Unable to copy file " + src.getName() + " to " + dest.getAbsolutePath(), e);
		}
	}

	public static void copyFileToDir(File src, File dir) {
		copyFileToDir(src, src.getName(), dir);
	}

	public static void copyFileToDir(File src, String newName, File dir) {
		copyFile(src, new File(dir, newName));
	}

	public static void copyFileToIFolder(File file, IFolder folder, IProgressMonitor monitor) throws CoreException {
		IFile iFile = folder.getFile(file.getName());

		try (InputStream input = Files.newInputStream(file.toPath())) {
			if (exists(iFile)) {
				iFile.setContents(input, true, true, monitor);
			}
			else {
				iFile.create(input, true, monitor);
			}
		}
		catch (Exception e) {
			throw new CoreException(
				LiferayCore.createErrorStatus("Could not copy file to folder " + file.getName(), e));
		}
	}

	public static void deleteDir(File dir, boolean removeAll) {
		if (isNotDir(dir)) {
			return;
		}

		for (File file : dir.listFiles()) {
			if (file.isDirectory() && removeAll) {
				deleteDir(file, removeAll);
			}
			else {
				file.delete();
			}
		}

		dir.delete();
	}

	public static void deleteDirContents(File dir) {
		if (isNotDir(dir)) {
			return;
		}

		for (File file : dir.listFiles()) {
			if (file.isDirectory()) {
				deleteDir(file, true);
			}
			else {
				file.delete();
			}
		}
	}

	public static boolean exists(File file) {
		if ((file != null) && file.exists()) {
			return true;
		}

		return false;
	}

	public static boolean exists(IContainer container) {
		if ((container != null) && container.exists()) {
			return true;
		}

		return false;
	}

	public static boolean exists(IFile file) {
		if ((file != null) && file.exists()) {
			return true;
		}

		return false;
	}

	public static boolean exists(IFolder folder) {
		if ((folder != null) && folder.exists()) {
			return true;
		}

		return false;
	}

	public static boolean exists(IJavaProject project) {
		if ((project != null) && project.exists()) {
			return true;
		}

		return false;
	}

	public static boolean exists(IPath path) {
		if (path != null) {
			File file = path.toFile();

			if (file.exists()) {
				return true;
			}
		}

		return false;
	}

	public static boolean exists(IProject project) {
		if ((project != null) && project.exists()) {
			return true;
		}

		return false;
	}

	public static boolean exists(IResource resource) {
		if ((resource != null) && resource.exists()) {
			return true;
		}

		return false;
	}

	public static File[] getDirectories(File directory) {
		return directory.listFiles(
			new FileFilter() {

				@Override
				public boolean accept(File file) {
					return file.isDirectory();
				}

			});
	}

	public static File getFile(IFile file) {
		if (notExists(file)) {
			return null;
		}

		IPath location = file.getLocation();

		return location.toFile();
	}

	public static File getFile(IPath path) {
		if (notExists(path)) {
			return null;
		}

		return path.toFile();
	}

	public static String toPortableString(IPath path) {
		if (path == null) {
			return null;
		}

		return path.toPortableString();
	}

	public static String toOSString(IPath path) {
		if (path == null) {
			return null;
		}

		return path.toOSString();
	}

	public static File getFile(URL url) {
		if (url == null) {
			return null;
		}

		return new File(url.getFile());
	}

	public static IContainer getWorkspaceContainer(File file) {
		IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

		IPath path = new Path(file.getAbsolutePath());

		File f = path.toFile();

		IContainer[] containers = root.findContainersForLocationURI(f.toURI());

		if (containers.length == 0) {
			return null;
		}

		return containers[0];
	}

	public static IFile getWorkspaceFile(File file, String expectedProjectName) {
		IWorkspaceRoot root = CoreUtil.getWorkspaceRoot();

		IPath path = new Path(file.getAbsolutePath());

		File f = path.toFile();

		IFile[] files = root.findFilesForLocationURI(f.toURI());

		if (files.length == 0) {
			return null;
		}

		for (IFile wsFile : files) {
			IProject project = wsFile.getProject();

			String projectName = project.getName();

			if (projectName.equals(expectedProjectName)) {
				return wsFile;
			}
		}

		return null;
	}

	public static boolean hasChildren(File dir) {
		if (isDir(dir) && ListUtil.isNotEmpty(dir.list())) {
			return true;
		}

		return false;
	}

	public static boolean isDir(File file) {
		if (exists(file) && file.isDirectory()) {
			return true;
		}

		return false;
	}

	public static boolean isFile(File file) {
		if (exists(file) && file.isFile()) {
			return true;
		}

		return false;
	}

	public static boolean isNotDir(File file) {
		if (notExists(file) || !file.isDirectory()) {
			return true;
		}

		return false;
	}

	public static void mkdirs(File f) throws CoreException {
		if (f.exists()) {
			if (f.isFile()) {
				String msg = NLS.bind(Msgs.locationIsFile, f.getAbsolutePath());

				throw new CoreException(LiferayCore.createErrorStatus(msg));
			}
		}
		else {
			mkdirs(f.getParentFile());

			IContainer wsContainer = getWorkspaceContainer(f);

			if (wsContainer != null) {

				// Should be a folder...

				IFolder iFolder = (IFolder)wsContainer;

				iFolder.create(true, true, null);
			}
			else {
				boolean successful = f.mkdir();

				if (!successful) {
					String msg = NLS.bind(Msgs.failedToCreateDirectory, f.getAbsolutePath());

					throw new CoreException(LiferayCore.createErrorStatus(msg));
				}
			}
		}
	}

	public static boolean notExists(File file) {
		if ((file == null) || !file.exists()) {
			return true;
		}

		return false;
	}

	public static boolean notExists(IFile file) {
		if ((file == null) || !file.exists()) {
			return true;
		}

		return false;
	}

	public static boolean notExists(IFolder folder) {
		if ((folder == null) || !folder.exists()) {
			return true;
		}

		return false;
	}

	public static boolean notExists(IJavaProject project) {
		if ((project == null) || !project.exists()) {
			return true;
		}

		return false;
	}

	public static boolean notExists(IPath path) {
		if (path == null) {
			return true;
		}

		File file = path.toFile();

		if (!file.exists()) {
			return true;
		}

		return false;
	}

	public static boolean notExists(IProject project) {
		if ((project == null) || !project.exists()) {
			return true;
		}

		return false;
	}

	public static boolean notExists(IResource resource) {
		if ((resource == null) || !resource.exists()) {
			return true;
		}

		return false;
	}

	public static boolean notExists(org.eclipse.sapphire.modeling.Path path) {
		if (path == null) {
			return true;
		}

		File file = path.toFile();

		if (!file.exists()) {
			return true;
		}

		return false;
	}

	public static IPath pathAppend(IPath path, String... folders) {
		if (path == null) {
			return null;
		}

		for (String folder : folders) {
			path = path.append(folder);
		}

		return path;
	}

	public static String readContents(File file) {
		return readContents(file, false);
	}

	public static String readContents(File file, boolean includeNewlines) {
		if (notExists(file)) {
			return null;
		}

		StringBuffer contents = new StringBuffer();

		try (FileReader fileReader = new FileReader(file);
			BufferedReader bufferedReader = new BufferedReader(fileReader)) {

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				contents.append(line);

				if (includeNewlines) {
					contents.append(System.getProperty("line.separator"));
				}
			}
		}
		catch (Exception e) {
			LiferayCore.logError("Could not read file: " + file.getPath());
		}

		return contents.toString();
	}

	public static String readContents(IFile file) {
		return readContents(getFile(file), false);
	}

	public static String readContents(InputStream contents) throws IOException {
		byte[] buffer = new byte[4096];

		try (BufferedInputStream bin = new BufferedInputStream(contents);
			StringBufferOutputStream out = new StringBufferOutputStream()) {

			int bytesRead = 0;

			// int bytesTotal = 0;

			/*
			 * Keep reading from the file while there is any content when the end of the stream has been reached,
			 * -1 is returned
			 */
			while ((bytesRead = bin.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);

				// bytesTotal += bytesRead;

			}

			return out.toString();
		}
	}

	public static String[] readLinesFromFile(File file) {
		return readLinesFromFile(file, false);
	}

	public static String[] readLinesFromFile(File file, boolean includeNewlines) {
		if (notExists(file)) {
			return null;
		}

		List<String> lines = new ArrayList<>();

		try (FileReader fileReader = new FileReader(file)) {
			BufferedReader bufferedReader = new BufferedReader(fileReader);

			String line;

			while ((line = bufferedReader.readLine()) != null) {
				StringBuffer contents = new StringBuffer(line);

				if (includeNewlines) {
					contents.append(System.getProperty("line.separator"));
				}

				lines.add(contents.toString());
			}
		}
		catch (Exception e) {
			LiferayCore.logError("Could not read file: " + file.getPath());
		}

		return lines.toArray(new String[lines.size()]);
	}

	public static String[] readMainFestProsFromJar(File systemJarFile, String... names) {
		if (systemJarFile.canRead()) {
			String[] strs = new String[names.length];

			try (ZipFile jar = new ZipFile(systemJarFile)) {
				ZipEntry manifest = jar.getEntry("META-INF/MANIFEST.MF");

				Manifest mf = new Manifest(jar.getInputStream(manifest));

				Attributes a = mf.getMainAttributes();

				for (int i = 0; i < names.length; i++) {
					strs[i] = a.getValue(names[i]);
				}

				return strs;
			}
			catch (IOException ioe) {
				return null;
			}
		}

		return null;
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
		catch (Throwable t) {
			return null;
		}
	}

	public static Document readXML(String content) {
		try (InputStream inputStream = new ByteArrayInputStream(content.getBytes())) {
			return readXML(inputStream, null, null);
		}
		catch (Exception e) {
		}

		return null;
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
		catch (Throwable t) {
			return null;
		}
	}

	public static boolean searchAndReplace(File file, String search, String replace)
		throws FileNotFoundException, IOException {

		if (notExists(file)) {
			return false;
		}

		String searchContents = CoreUtil.readStreamToString(Files.newInputStream(file.toPath()));

		String replaceContents = searchContents.replaceAll(search, replace);

		boolean replaced = !searchContents.equals(replaceContents);

		try (OutputStream out = Files.newOutputStream(file.toPath())) {
			CoreUtil.writeStreamFromString(replaceContents, out);
		}

		return replaced;
	}

	public static void validateEdit(IFile... files) throws CoreException {
		IWorkspace ws = CoreUtil.getWorkspace();

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

		IFolder newFolder = folder.getFolder(folderValue);

		IPath path = newFolder.getFullPath();

		IWorkspace workspace = CoreUtil.getWorkspace();

		IStatus result = workspace.validatePath(path.toString(), IResource.FOLDER);

		if (!result.isOK()) {
			return result.getMessage();
		}

		if (exists(folder.getFolder(new Path(folderValue)))) {
			return Msgs.folderAlreadyExists;
		}

		return null;
	}

	public static void writeFile(File f, byte[] contents, String expectedProjectName) throws CoreException {
		try (InputStream inputStream = new ByteArrayInputStream(contents)) {
			writeFile(f, inputStream, expectedProjectName);
		}
		catch (Exception e) {
			throw new CoreException(LiferayCore.createErrorStatus(e));
		}
	}

	public static void writeFile(File f, InputStream contents) throws CoreException {
		writeFile(f, contents, null);
	}

	public static void writeFile(File f, InputStream contents, String expectedProjectName) throws CoreException {
		if (f.exists()) {
			if (f.isDirectory()) {
				String msg = NLS.bind(Msgs.locationIsDirectory, f.getAbsolutePath());

				throw new CoreException(LiferayCore.createErrorStatus(msg));
			}
		}
		else {
			mkdirs(f.getParentFile());
		}

		IFile wsfile = getWorkspaceFile(f, expectedProjectName);

		if (wsfile != null) {
			validateEdit(new IFile[] {wsfile});

			if (wsfile.exists()) {
				wsfile.setContents(contents, true, false, null);
			}
			else {
				wsfile.create(contents, true, null);
			}
		}
		else {
			if (f.exists() && !f.canWrite()) {
				String msg = NLS.bind(Msgs.cannotWriteFile, f.getAbsolutePath());

				throw new CoreException(LiferayCore.createErrorStatus(msg));
			}

			byte[] buffer = new byte[1024];

			try (OutputStream out = Files.newOutputStream(f.toPath())) {
				for (int count; (count = contents.read(buffer)) != -1;) {
					out.write(buffer, 0, count);
				}

				out.flush();
			}
			catch (IOException ioe) {
				String msg = NLS.bind(Msgs.failedWhileWriting, f.getAbsolutePath());

				throw new CoreException(LiferayCore.createErrorStatus(msg, ioe));
			}
		}
	}

	public static void writeFile(File f, String contents, String expectedProjectName) throws CoreException {
		try {
			writeFile(f, contents.getBytes("UTF-8"), expectedProjectName);
		}
		catch (UnsupportedEncodingException uee) {
			throw new RuntimeException(uee);
		}
	}

	public static int writeFileFromStream(File tempFile, InputStream in) throws IOException {
		byte[] buffer = new byte[1024];
		int bytesTotal = 0;

		try (OutputStream outputStream = Files.newOutputStream(tempFile.toPath());
			BufferedOutputStream out = new BufferedOutputStream(outputStream);
			BufferedInputStream bin = new BufferedInputStream(in)) {

			int bytesRead = 0;

			/*
			 * Keep reading from the file while there is any content when the end of the stream has been reached, -1 is returned
			 */
			while ((bytesRead = bin.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
				bytesTotal += bytesRead;
			}
		}

		return bytesTotal;
	}

	public static String writeXml(Document document) throws Exception {
		TransformerFactory tf = TransformerFactory.newInstance();

		Transformer transformer = tf.newTransformer();

		StringWriter writer = new StringWriter();

		transformer.transform(new DOMSource(document), new StreamResult(writer));

		StringBuffer sb = writer.getBuffer();

		return sb.toString();
	}

	private static class Msgs extends NLS {

		public static String cannotWriteFile;
		public static String failedToCreateDirectory;
		public static String failedWhileWriting;
		public static String folderAlreadyExists;
		public static String folderValueInvalid;
		public static String folderValueNotEmpty;
		public static String locationIsDirectory;
		public static String locationIsFile;

		static {
			initializeMessages(FileUtil.class.getName(), Msgs.class);
		}

	}

}