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
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
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

		List<Problem> problems = Collections.synchronizedList(new ArrayList<Problem>());

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
		List<Problem> problems = Collections.synchronizedList(new ArrayList<Problem>());

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

		try {
			if (ListUtil.isNotEmpty(args)) {
				if (args[0] instanceof File) {
					File outputFile = (File)args[0];

					outputFile.getParentFile().mkdirs();
					outputFile.createNewFile();
					fos = Files.newOutputStream(outputFile.toPath());
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
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
		finally {
			try {
				if (fos != null) {
					fos.close();
				}
			}
			catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	protected FileVisitResult analyzeFile(File file, final List<Problem> problems, ProgressMonitor monitor) {
		Path path = file.toPath();

		String fileName = path.getFileName().toString();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

		monitor.setTaskName("Analyzing file " + _count + "/" + _total + " " + fileName);

		ServiceReference<FileMigrator>[] fileMigrators = _fileMigratorTracker.getServiceReferences();

		if (ListUtil.isNotEmpty(fileMigrators)) {
			Stream<ServiceReference<FileMigrator>> serviceStream = Arrays.asList(fileMigrators).stream();

			List<ServiceReference<FileMigrator>> fileMigratorList = serviceStream.filter(
				predicate -> {
					String fileExtensionString = (String)predicate.getProperty("file.extensions");

					List<String> extensionList = Arrays.asList(fileExtensionString.split(","));

					return extensionList.contains(extension);
				}).collect(Collectors.toList());

			if (ListUtil.isNotEmpty(fileMigratorList)) {
				try {
					Stream<ServiceReference<FileMigrator>> migratorStream = fileMigratorList.stream();

					migratorStream.map(
						_context::getService
					).parallel(
					).forEach(
						fm -> {
							List<Problem> fileProlbems = fm.analyze(file);

							if (ListUtil.isNotEmpty(fileProlbems)) {
								problems.addAll(fileProlbems);
							}
						}
					);
				}
				catch (Exception e) {
				}
			}

			_count++;
		}

		return FileVisitResult.CONTINUE;
	}

	private void _countTotal(File startDir) {
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith(".settings")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("target") && dir.startsWith(startDir.getPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/classes")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/service")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				return super.preVisitDirectory(dir, attrs);
			}

			@Override
			public FileVisitResult visitFile(Path path, BasicFileAttributes attrs) throws IOException {
				File file = path.toFile();

				if (file.isFile() && attrs.isRegularFile() && (attrs.size() > 0)) {
					_total++;
				}

				return super.visitFile(path, attrs);
			}

		};

		try {
			Files.walkFileTree(startDir.toPath(), visitor);
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

	private void _walkFiles(File startDir, List<Problem> problems, ProgressMonitor monitor) {
		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith(".settings")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("target") && dir.startsWith(startDir.getPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/classes")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith("WEB-INF/service")) {
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

				if (file.isFile() && attrs.isRegularFile() && (attrs.size() > 0)) {
					FileVisitResult result = analyzeFile(file, problems, monitor);

					if (result.equals(FileVisitResult.TERMINATE)) {
						return result;
					}
				}

				return super.visitFile(path, attrs);
			}

		};

		try {
			Files.walkFileTree(startDir.toPath(), visitor);
		}
		catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	private static int _count = 0;
	private static int _total = 0;

	private BundleContext _context;
	private ServiceTracker<FileMigrator, FileMigrator> _fileMigratorTracker;
	private ServiceTracker<MigrationListener, MigrationListener> _migrationListenerTracker;

}