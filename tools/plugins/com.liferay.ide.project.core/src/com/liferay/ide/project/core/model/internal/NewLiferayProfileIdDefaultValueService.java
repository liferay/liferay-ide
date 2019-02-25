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

package com.liferay.ide.project.core.model.internal;

import com.liferay.ide.core.util.SapphireContentAccessor;
import com.liferay.ide.core.util.SapphireUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOpMethods;
import com.liferay.ide.project.core.model.NewLiferayProfile;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Gregory Amerson
 */
public class NewLiferayProfileIdDefaultValueService extends DefaultValueService implements SapphireContentAccessor {

	@Override
	protected String compute() {
		NewLiferayProfile profile = _newLiferayProfile();

		String defaultRuntimeName = get(profile.getRuntimeName());

		/**
		 * First try to use this as a runtimeName, but need to check it against existing possible values.
		 * If no existing profiles with this name exist, use it, if not, append a (1)
		 */
		String data = defaultRuntimeName;

		if (data.equals("<None>")) {
			return StringPool.EMPTY;
		}

		data = data.replaceAll(StringPool.SPACE, StringPool.DASH);

		NewLiferayPluginProjectOp op = profile.nearest(NewLiferayPluginProjectOp.class);

		Set<String> possibleValues = NewLiferayPluginProjectOpMethods.getPossibleProfileIds(op, false);

		while (possibleValues.contains(data)) {
			try {
				data = _nextSuffix(data);
			}
			catch (Exception e) {
			}
		}

		return data;
	}

	@Override
	protected void initDefaultValueService() {
		super.initDefaultValueService();

		Listener listener = new FilteredListener<PropertyContentEvent>() {

			@Override
			protected void handleTypedEvent(PropertyContentEvent event) {
				refresh();
			}

		};

		NewLiferayProfile profile = _newLiferayProfile();

		SapphireUtil.attachListener(profile.getRuntimeName(), listener);
	}

	private NewLiferayProfile _newLiferayProfile() {
		return context(NewLiferayProfile.class);
	}

	private String _nextSuffix(String val) {

		// look for an existing ([0-9])

		Matcher matcher = _dup.matcher(val);

		if (matcher.matches()) {
			try {
				int num = Integer.parseInt(matcher.group(2));

				return matcher.group(1) + "(" + (num + 1) + ")";
			}
			catch (NumberFormatException nfe) {
			}
		}

		return val + "(1)";
	}

	private static final Pattern _dup = Pattern.compile("(.*)\\(([0-9]+)\\)$");

}