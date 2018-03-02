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

package com.liferay.blade.eclipse.provider;

import com.liferay.blade.api.FileMigrator;
import com.liferay.blade.api.Migration;
import com.liferay.blade.api.MigrationListener;
import com.liferay.blade.api.Problem;
import com.liferay.blade.api.ProgressMonitor;
import com.liferay.blade.api.Reporter;
import com.liferay.blade.util.FileHelper;
import com.liferay.ide.core.util.ListUtil;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 */
@Component
public class ProjectMigrationService implements Migration {

	@Activate
	public void activate(BundleContext context) {
		_context = context;

		_migrationListenerTracker = new ServiceTracker<>(context, MigrationListener.class, null);

		_migrationListenerTracker.open();

		_fileMigratorTracker = new ServiceTracker<>(context, FileMigrator.class, null);

		_fileMigratorTracker.open();
	}

	@Override
	public List<Problem> findProblems(File projectDir, ProgressMonitor monitor) {
		monitor.beginTask("Searching for migration problems in " + projectDir, -1);

		List<Problem> problems = new ArrayList<>();

		monitor.beginTask("Analyzing files", -1);

		_countTotal(projectDir);

		_walkFiles(projectDir, problems, monitor);

		_updateListeners(problems);

		monitor.done();

		_count = 0;
		_total = 0;

		return problems;
	}

	@Override
	public List<Problem> findProblems(Set<File> files, ProgressMonitor monitor) {
		List<Problem> problems = new ArrayList<>();

		monitor.beginTask("Analyzing files", -1);

		_total = files.size();

		for (File file : files) {
			_count++;

			if (monitor.isCanceled()) {
				return Collections.emptyList();
			}

			analyzeFile(file, problems, monitor);
		}

		_updateListeners(problems);

		monitor.done();

		_count = 0;
		_total = 0;

		return problems;
	}

	@Override
	public void reportProblems(List<Problem> problems, int detail, String format, Object... args) {
		Reporter reporter = null;

		try {
			Collection<ServiceReference<Reporter>> references = _context.getServiceReferences(
				Reporter.class, "(format=" + format + ")");

			if (ListUtil.isNotEmpty(references)) {
				reporter = _context.getService(references.iterator().next());
			}
			else {
				ServiceReference<Reporter> sr = _context.getServiceReference(Reporter.class);

				reporter = _context.getService(sr);
			}
		}
		catch (InvalidSyntaxException ise) {
			ise.printStackTrace();
		}

		OutputStream fos = null;

		if (ListUtil.isNotEmpty(args)) {
			if (args[0] instanceof File) {
				File outputFile = (File)args[0];

				try {
					outputFile.getParentFile().mkdirs();
					outputFile.createNewFile();
					fos = Files.newOutputStream(outputFile.toPath());
				}
				catch (IOException ioe) {
					ioe.printStackTrace();
				}
			}
			else if (args[0] instanceof OutputStream) {
				fos = (OutputStream)args[0];
			}
		}

		if (ListUtil.isNotEmpty(problems)) {
			reporter.beginReporting(detail, fos);

			for (Problem problem : problems) {
				reporter.report(problem);
			}

			reporter.endReporting();
		}
	}

	protected FileVisitResult analyzeFile(File file, List<Problem> problems, ProgressMonitor monitor) {
		try {
			String fileContent = _fileHelper.readFile(file);

			if ((fileContent == null) || (fileContent.trim().length() < 1)) {
				return FileVisitResult.CONTINUE;
			}
		}
		catch (Exception e) {
			return FileVisitResult.CONTINUE;
		}

		Path path = file.toPath();

		String fileName = path.getFileName().toString();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

		monitor.setTaskName("Analyzing file " + _count + "/" + _total + " " + fileName);

		ServiceReference<FileMigrator>[] fileMigrators = _fileMigratorTracker.getServiceReferences();

		if (ListUtil.isNotEmpty(fileMigrators)) {
			for (ServiceReference<FileMigrator> fm : fileMigrators) {
				if (monitor.isCanceled()) {
					return FileVisitResult.TERMINATE;
				}

				List<String> fileExtensions = Arrays.asList(((String)fm.getProperty("file.extensions")).split(","));

				if ((fileExtensions != null) && fileExtensions.contains(extension)) {
					FileMigrator fmigrator = _context.getService(fm);

					try {
						List<Problem> fileProblems = fmigrator.analyze(file);

						if (ListUtil.isNotEmpty(fileProblems)) {
							problems.addAll(fileProblems);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}

					_context.ungetService(fm);
				}
			}

			_count++;
		}

		return FileVisitResult.CONTINUE;
	}

	private void _countTotal(File dir) {
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				File file = path.toFile();

				if (file.isFile()) {
					_total++;
				}

				return super.visitFile(path, attrs);
			}

		};

		try {
			Files.walkFileTree(dir.toPath(), visitor);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private void _updateListeners(List<Problem> problems) {
		if (ListUtil.isNotEmpty(problems)) {
			MigrationListener[] listeners = _migrationListenerTracker.getServices(new MigrationListener[0]);

			for (MigrationListener listener : listeners) {
				try {
					listener.problemsFound(problems);
				}
				catch (Exception e) {

					// ignore

				}
			}
		}
	}

	private void _walkFiles(File dir, List<Problem> problems, ProgressMonitor monitor) {
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				if (monitor.isCanceled()) {
					return FileVisitResult.TERMINATE;
				}

				File file = path.toFile();

				if (file.isFile()) {
					FileVisitResult result = analyzeFile(file, problems, monitor);

					if (result.equals(FileVisitResult.TERMINATE)) {
						return result;
					}
				}

				return super.visitFile(path, attrs);
			}

		};

		try {
			Files.walkFileTree(dir.toPath(), visitor);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static int _count = 0;
	private static int _total = 0;

	private BundleContext _context;
	private FileHelper _fileHelper = new FileHelper();
	private ServiceTracker<FileMigrator, FileMigrator> _fileMigratorTracker;
	private ServiceTracker<MigrationListener, MigrationListener> _migrationListenerTracker;

}