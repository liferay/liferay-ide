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

package com.liferay.ide.upgrade.problems.core.internal;

import com.google.common.collect.ImmutableSet;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.FileMigration;
import com.liferay.ide.upgrade.problems.core.FileMigrator;

import java.io.File;
import java.io.IOException;

import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Gregory Amerson
 * @author Terry Jia
 * @author Simon Jiang
 * @author Seiphon Wang
 */
@Component
public class FileMigrationService implements FileMigration {

	@Activate
	public void activate(BundleContext context) {
		_context = context;

		_fileMigratorTracker = new ServiceTracker<>(context, FileMigrator.class, null);

		_fileMigratorTracker.open();
	}

	@Override
	public List<UpgradeProblem> findLegacyFileUpgradeProblems(
		File dir, List<String> versions, Set<String> requiredProperties, IProgressMonitor monitor) {

		monitor.beginTask("Searching for migration problems in " + dir, -1);

		List<UpgradeProblem> upgradeProblems = Collections.synchronizedList(new ArrayList<UpgradeProblem>());

		monitor.beginTask("Analyzing files", -1);

		_findLegacyFiles(dir, upgradeProblems, versions, requiredProperties, monitor);

		monitor.done();

		return upgradeProblems;
	}

	@Override
	public List<UpgradeProblem> findUpgradeProblems(
		File dir, List<String> versions, Set<String> requiredProperties, IProgressMonitor monitor) {

		monitor.beginTask("Searching for migration problems in " + dir, -1);

		List<UpgradeProblem> upgradeProblems = Collections.synchronizedList(new ArrayList<UpgradeProblem>());

		monitor.beginTask("Analyzing files", -1);

		_countTotal(dir);

		_walkFiles(dir, upgradeProblems, versions, requiredProperties, monitor);

		monitor.done();

		_count = 0;
		_total = 0;

		return upgradeProblems;
	}

	protected FileVisitResult analyzeFile(
		File file, List<UpgradeProblem> upgradeProblems, List<String> versions, Set<String> requiredProperties,
		IProgressMonitor monitor) {

		Path path = file.toPath();

		Path pathFileName = path.getFileName();

		String fileName = pathFileName.toString();

		String extension = fileName.substring(fileName.lastIndexOf('.') + 1);

		monitor.setTaskName("Analyzing file " + _count + "/" + _total + " " + fileName);

		ServiceReference<FileMigrator>[] fileMigratorsServiceReferences = _fileMigratorTracker.getServiceReferences();

		if (ListUtil.isNotEmpty(fileMigratorsServiceReferences)) {
			List<ServiceReference<FileMigrator>> fileMigrators = Stream.of(
				fileMigratorsServiceReferences
			).filter(
				serviceReference -> {
					String fileExtensions = (String)serviceReference.getProperty("file.extensions");

					List<String> extensions = Arrays.asList(fileExtensions.split(","));

					return extensions.contains(extension);
				}
			).filter(
				ref -> {
					if (ListUtil.isNotEmpty(versions)) {
						Dictionary<String, Object> serviceProperties = ref.getProperties();

						return Optional.ofNullable(
							serviceProperties.get("version")
						).map(
							Object::toString
						).map(
							Version::valueOf
						).filter(
							version -> versions.stream(
							).filter(
								v -> version.equals(new Version(v))
							).findFirst(
							).isPresent()
						).isPresent();
					}

					return true;
				}
			).filter(
				serviceReference -> {
					if (requiredProperties == null) {
						return true;
					}

					Dictionary<String, Object> properties = serviceReference.getProperties();

					for (String key : requiredProperties) {
						Object value = properties.get(key);

						if (value == null) {
							return false;
						}
					}

					return true;
				}
			).collect(
				Collectors.toList()
			);

			if (ListUtil.isNotEmpty(versions) && (versions.size() > 1)) {
				versions.sort(
					(v1, v2) -> {
						Version version1 = new Version(v1);
						Version version2 = new Version(v2);

						return version2.compareTo(version1);
					});

				String version = versions.get(0);

				Stream<ServiceReference<FileMigrator>> stream = fileMigrators.stream();

				List<String> serviceReferencesWithVersion = stream.filter(
					serviceReference -> version.equals(serviceReference.getProperty("version"))
				).map(
					reference -> (String)reference.getProperty("component.name")
				).map(
					componentName -> {
						String[] s = componentName.split("\\.");

						return s[s.length - 1];
					}
				).collect(
					Collectors.toList()
				);

				stream = fileMigrators.stream();

				List<ServiceReference<FileMigrator>> serviceReferencesWithoutVersion = stream.filter(
					fileMigrator -> !version.equals(fileMigrator.getProperty("version"))
				).collect(
					Collectors.toList()
				);

				for (ServiceReference<FileMigrator> serviceReferenceWithoutVersion : serviceReferencesWithoutVersion) {
					String componentName = (String)serviceReferenceWithoutVersion.getProperty("component.name");

					String[] s = componentName.split("\\.");

					String className = s[s.length - 1];

					if (serviceReferencesWithVersion.contains(className)) {
						fileMigrators.remove(serviceReferenceWithoutVersion);
					}
				}
			}

			if (ListUtil.isNotEmpty(fileMigrators)) {
				
//					System.out.println(" Total migrator count is  " + fileMigrators.size());
//					
//					for(int i=0;i<fileMigrators.size();i++) {
//						try {
//							ServiceReference<FileMigrator> service = fileMigrators.get(i);
//							
//							FileMigrator migrator = _context.getService(service);
//							
//							List<UpgradeProblem> upgradeProblem = migrator.analyze(file);
//							
//							System.out.println("(" + i + ")" + migrator.getClass().toString() + " problems count is " + upgradeProblem.size());
//							
//							upgradeProblems.addAll(upgradeProblem);
//						}
//						catch (Exception e) {
//							e.printStackTrace();
//						}
//					}

				try {
					Stream<ServiceReference<FileMigrator>> migratorStream = fileMigrators.parallelStream();

					upgradeProblems.addAll(
						migratorStream.map(
							_context::getService
						).unordered(
						).collect(
							new ProblemsCollector(file)
						));
				}
				catch (Exception e) {
					e.printStackTrace();
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

				if (dir.equals(new File(startDir, "bin").toPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.equals(new File(startDir, "out").toPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.equals(new File(startDir, "build").toPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.equals(new File(startDir, "target").toPath())) {
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

	private void _findLegacyFiles(
		File startDir, List<UpgradeProblem> upgradeProblems, List<String> versions, Set<String> requiredProperties,
		IProgressMonitor monitor) {

		ServiceReference<FileMigrator>[] fileMigratorsServiceReferences = _fileMigratorTracker.getServiceReferences();

		monitor.setTaskName("Analyzing legacy file from " + startDir);

		if (ListUtil.isNotEmpty(fileMigratorsServiceReferences)) {
			List<ServiceReference<FileMigrator>> fileMigrators = Stream.of(
				fileMigratorsServiceReferences
			).filter(
				serviceReference -> {
					String version = (String)serviceReference.getProperty("version");

					return versions.contains(version);
				}
			).filter(
				serviceReference -> {
					String problemType = (String)serviceReference.getProperty("problem.type");

					return CoreUtil.isNotNullOrEmpty(problemType) && Objects.equals(problemType, "legacy");
				}
			).filter(
				serviceReference -> {
					if (requiredProperties == null) {
						return true;
					}

					Dictionary<String, Object> properties = serviceReference.getProperties();

					for (String key : requiredProperties) {
						Object value = properties.get(key);

						if (value == null) {
							return false;
						}
					}

					return true;
				}
			).collect(
				Collectors.toList()
			);

			if (ListUtil.isNotEmpty(fileMigrators)) {
				try {
					Stream<ServiceReference<FileMigrator>> migratorStream = fileMigrators.parallelStream();

					upgradeProblems.addAll(
						migratorStream.map(
							_context::getService
						).unordered(
						).collect(
							new ProblemsCollector(startDir)
						));
				}
				catch (Exception e) {
				}
			}
		}
	}

	private void _walkFiles(
		File startDir, List<UpgradeProblem> upgradeProblems, List<String> versions, Set<String> requiredProperties,
		IProgressMonitor monitor) {

		SubMonitor progressMonitor = SubMonitor.convert(monitor, _total);

		FileVisitor<Path> visitor = new SimpleFileVisitor<Path>() {

			@Override
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
				if (dir.endsWith(".git")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.endsWith(".settings")) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.equals(new File(startDir, "bin").toPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.equals(new File(startDir, "out").toPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.equals(new File(startDir, "build").toPath())) {
					return FileVisitResult.SKIP_SUBTREE;
				}

				if (dir.equals(new File(startDir, "target").toPath())) {
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

				progressMonitor.split(1);

				if (file.isFile() && attrs.isRegularFile() && (attrs.size() > 0)) {
					FileVisitResult result = analyzeFile(file, upgradeProblems, versions, requiredProperties, monitor);

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

	private class ProblemsCollector implements Collector<FileMigrator, Set<UpgradeProblem>, Set<UpgradeProblem>> {

		public ProblemsCollector(File file) {
			_file = file;
		}

		@Override
		public BiConsumer<Set<UpgradeProblem>, FileMigrator> accumulator() {
			return (container, migrator) -> container.addAll(migrator.analyze(_file));
		}

		@Override
		public Set<Characteristics> characteristics() {
			return ImmutableSet.of(
				Collector.Characteristics.CONCURRENT, Collector.Characteristics.UNORDERED,
				Collector.Characteristics.IDENTITY_FINISH);
		}

		@Override
		public BinaryOperator<Set<UpgradeProblem>> combiner() {
			return (left, right) -> {
				left.addAll(right);

				return left;
			};
		}

		@Override
		public Function<Set<UpgradeProblem>, Set<UpgradeProblem>> finisher() {
			return Function.identity();
		}

		@Override
		public Supplier<Set<UpgradeProblem>> supplier() {
			return CopyOnWriteArraySet<UpgradeProblem>::new;
		}

		private File _file;

	}

}