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

package com.liferay.ide.upgrade.problems.ui.internal;

import com.liferay.ide.upgrade.plan.core.UpgradeProblem;
import com.liferay.ide.upgrade.problems.core.AutoFileMigrator;
import com.liferay.ide.upgrade.problems.core.AutoFileMigratorException;

import java.io.File;

import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Optional;
import java.util.stream.Stream;

import org.eclipse.core.resources.IMarker;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.actions.SelectionProviderAction;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.Version;
import org.osgi.framework.VersionRange;

/**
 * @author Terry Jia
 * @author Simon Jiang
 */
public class BaseAutoCorrectAction extends SelectionProviderAction implements UpgradeProblemUISupport {

	protected BaseAutoCorrectAction(ISelectionProvider provider, String text) {
		super(provider, text);
	}

	protected void autoCorrect(File file, UpgradeProblem upgradeProblem, boolean removeMarker) {
		String autoCorrectContext = upgradeProblem.getAutoCorrectContext();

		if (autoCorrectContext == null) {
			return;
		}

		int filterKeyIndex = autoCorrectContext.indexOf(":");

		if (filterKeyIndex > -1) {
			autoCorrectContext = autoCorrectContext.substring(0, filterKeyIndex);
		}

		Bundle bundle = FrameworkUtil.getBundle(BaseAutoCorrectAction.class);

		BundleContext bundleContext = bundle.getBundleContext();

		try {
			Collection<ServiceReference<AutoFileMigrator>> serviceReferences = bundleContext.getServiceReferences(
				AutoFileMigrator.class, "(auto.correct=" + autoCorrectContext + ")");

			Stream<ServiceReference<AutoFileMigrator>> migratorStream = serviceReferences.stream();

			migratorStream.filter(
				ref -> {
					Dictionary<String, Object> serviceProperties = ref.getProperties();

					Version version = new Version(upgradeProblem.getVersion());

					return Optional.ofNullable(
						serviceProperties.get("version")
					).map(
						Object::toString
					).map(
						VersionRange::valueOf
					).filter(
						range -> range.includes(version)
					).isPresent();
				}
			).map(
				bundleContext::getService
			).forEach(
				autoFileMigrator -> {
					try {
						int problemsCorrected = autoFileMigrator.correctProblems(
							file, Collections.singletonList(upgradeProblem));

						if ((problemsCorrected > 0) && removeMarker) {
							IMarker marker = findMarker(upgradeProblem);

							if (marker != null) {
								deleteMarker(marker);
							}
						}
					}
					catch (AutoFileMigratorException afme) {
						UpgradeProblemsUIPlugin.logError(
							"Problem encountered when automatically migrating file " + file, afme);
					}
				}
			);
		}
		catch (InvalidSyntaxException ise) {
		}
	}

}