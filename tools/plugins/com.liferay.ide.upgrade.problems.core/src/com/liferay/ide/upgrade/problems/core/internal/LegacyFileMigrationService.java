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
import com.liferay.ide.upgrade.problems.core.FileMigrator;
import com.liferay.ide.upgrade.problems.core.LegacyFileMigration;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.List;
import java.util.Objects;
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

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Seiphon Wang
 */
@Component
public class LegacyFileMigrationService implements LegacyFileMigration {

	@Activate
	public void activate(BundleContext context) {
		_context = context;

		_fileMigratorTracker = new ServiceTracker<>(context, FileMigrator.class, null);

		_fileMigratorTracker.open();
	}

	@Override
	public List<UpgradeProblem> findUpgradeProblems(
		File dir, List<String> versions, Set<String> requiredProperties, IProgressMonitor monitor) {

		monitor.beginTask("Searching for migration problems in " + dir, -1);

		List<UpgradeProblem> upgradeProblems = Collections.synchronizedList(new ArrayList<UpgradeProblem>());

		monitor.beginTask("Analyzing files", -1);

		_findLegacyFiles(dir, upgradeProblems, versions, requiredProperties, monitor);

		monitor.done();

		return upgradeProblems;
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