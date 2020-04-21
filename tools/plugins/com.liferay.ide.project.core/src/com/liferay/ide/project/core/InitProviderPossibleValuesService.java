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

package com.liferay.ide.project.core;

import com.liferay.ide.core.ILiferayProjectProvider;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.core.workspace.LiferayWorkspaceUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Seiphon Wang
 */
public interface InitProviderPossibleValuesService {

	public default List<String> initPossibleValues(String providerName) {
		List<String> possibleValues = new ArrayList<>();

		for (ILiferayProjectProvider provider : LiferayCore.getProviders(providerName)) {
			if (provider instanceof NewLiferayProjectProvider<?>) {
				try {
					String shortName = provider.getShortName();

					if (LiferayWorkspaceUtil.hasGradleWorkspace() && !shortName.startsWith("gradle")) {
						continue;
					}

					if (LiferayWorkspaceUtil.hasMavenWorkspace() && !shortName.startsWith("maven")) {
						continue;
					}

					possibleValues.add(provider.getShortName());
				}
				catch (Exception ce) {
				}
			}
		}

		Collections.sort(possibleValues);

		return possibleValues;
	}

}