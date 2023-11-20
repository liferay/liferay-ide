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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.util.ProjectUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.PropertyContentEvent;

import org.osgi.framework.Version;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class TargetPlatformPossibleValuesService extends PossibleValuesService implements SapphireContentAccessor {

	@Override
	public void dispose() {
		NewLiferayWorkspaceOp op = context(NewLiferayWorkspaceOp.class);

		if (op != null) {
			SapphireUtil.detachListener(op.property(NewLiferayWorkspaceOp.PROP_LIFERAY_VERSION), _listener);
		}

		super.dispose();
	}

	@Override
	public boolean ordered() {
		return true;
	}

	@Override
	protected void compute(Set<String> values) {
		if (Objects.isNull(_mavenTargetPlatformVersions)) {
			return;
		}

		List<String> possibleValues = new ArrayList<>();

		_mavenTargetPlatformVersions.forEach(
			(liferayVersion, targetPlatformVersion) -> {
				String version = get(_op.getLiferayVersion());

				if (liferayVersion.equals(version)) {
					Collections.addAll(possibleValues, targetPlatformVersion);
				}
			});

		possibleValues.sort(
			new Comparator<String>() {

				@Override
				public int compare(String versionString1, String versionString2) {
					if (versionString1.contains("-") && versionString2.contains("-")) {
						String versionSubString1 = versionString1.substring(0, versionString1.indexOf("-"));
						String versionSubString2 = versionString2.substring(0, versionString2.indexOf("-"));

						Version subVersion1 = Version.parseVersion(versionSubString1);
						Version subVersion2 = Version.parseVersion(versionSubString2);

						return subVersion2.compareTo(subVersion1);
					}
					else if (versionString1.contains("-")) {
						String versionSubString1 = versionString1.substring(0, versionString1.indexOf("-"));

						Version subVersion1 = Version.parseVersion(versionSubString1);

						Version version2 = Version.parseVersion(versionString2);

						return version2.compareTo(subVersion1);
					}
					else if (versionString2.contains("-")) {
						Version version1 = Version.parseVersion(versionString1);

						String versionSubString2 = versionString2.substring(0, versionString2.indexOf("-"));

						Version subVersion2 = Version.parseVersion(versionSubString2);

						return subVersion2.compareTo(version1);
					}

					Version version1 = Version.parseVersion(versionString1);
					Version version2 = Version.parseVersion(versionString2);

					String version1Qualifier = version1.getQualifier();
					String version2Qualifier = version2.getQualifier();

					if (CoreUtil.isNotNullOrEmpty(version1Qualifier) && CoreUtil.isNotNullOrEmpty(version2Qualifier)) {
						try {
							int qualifier1 = Integer.parseInt(version1Qualifier);
							int qualifier2 = Integer.parseInt(version2Qualifier);

							return qualifier2 - qualifier1;
						}
						catch (NumberFormatException numberFormatException) {
							return -1;
						}
					}
					else if (CoreUtil.isNullOrEmpty(version1Qualifier)) {
						return new Version(
							version2.getMajor(), version2.getMinor(), version2.getMicro()
						).compareTo(
							version1
						);
					}
					else if (CoreUtil.isNullOrEmpty(version2Qualifier)) {
						return version2.compareTo(
							new Version(version1.getMajor(), version1.getMinor(), version1.getMicro()));
					}

					return version2.compareTo(version1);
				}

			});

		values.addAll(possibleValues);
	}

	@Override
	protected void initPossibleValuesService() {
		Job getProductVersions = new Job("Get product versions") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					_mavenTargetPlatformVersions = ProjectUtil.initMavenTargetPlatform();

					refresh();
				}
				catch (Exception exception) {
					ProjectCore.logError("Failed to init product version list.", exception);
				}

				return Status.OK_STATUS;
			}

		};

		getProductVersions.setSystem(true);

		getProductVersions.schedule();

		_listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		_op = context(NewLiferayWorkspaceOp.class);

		SapphireUtil.attachListener(_op.property(NewLiferayWorkspaceOp.PROP_LIFERAY_VERSION), _listener);
	}

	private FilteredListener<PropertyContentEvent> _listener;
	private Map<String, String[]> _mavenTargetPlatformVersions;
	private NewLiferayWorkspaceOp _op;

}