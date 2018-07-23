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

package com.liferay.ide.project.ui;

import java.util.stream.Stream;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public class BreakingChangeVersion {

	public static String[] getBreakingChangeVersionNames(boolean upgradeLiferayWorkspace) {
		String[] selectedBreakingChangeNameArray = Stream.of(
			_breakingChangeItems
		).filter(
			breakingChange -> breakingChange.isUpgradleLiferayWorkspace() == upgradeLiferayWorkspace
		).map(
			result -> result.getName()
		).toArray(
			size -> new String[size]
		);

		return selectedBreakingChangeNameArray;
	}

	public static BreakingChangeVersion[] getBreakingChangeVersions(String itemName, boolean upgradeLiferayWorkspace) {
		BreakingChangeVersion[] selectedBreakingChangeArray = Stream.of(
			_breakingChangeItems
		).filter(
			breakingChange -> itemName.equals(breakingChange.getName())
		).filter(
			breakingChange -> breakingChange.isUpgradleLiferayWorkspace() == upgradeLiferayWorkspace
		).toArray(
			size -> new BreakingChangeVersion[size]
		);

		return selectedBreakingChangeArray;
	}

	public static String[] getBreakingChangeVersionValues(boolean upgradeLiferayWorkspace) {
		String[] selectedBreakingChangeValueArray = Stream.of(
			_breakingChangeItems
		).filter(
			breakingChange -> breakingChange.isUpgradleLiferayWorkspace() == upgradeLiferayWorkspace
		).map(
			result -> result.getName()
		).toArray(
			size -> new String[size]
		);

		return selectedBreakingChangeValueArray;
	}

	public static String[] getBreakingChangeVersionValues(String itemName, boolean upgradeLiferayWorkspace) {
		String[] selectedBreakingChangeArray = Stream.of(
			_breakingChangeItems
		).filter(
			breakingChange -> itemName.equals(breakingChange.getName())
		).filter(
			breakingChange -> breakingChange.isUpgradleLiferayWorkspace() == upgradeLiferayWorkspace
		).map(
			result -> result.getName()
		).toArray(
			size -> new String[size]
		);

		return selectedBreakingChangeArray;
	}

	public BreakingChangeVersion(String name, String values) {
		_name = name;
		_values = values;

		_upgradleLiferayWorkspace = false;
	}

	public BreakingChangeVersion(String name, String values, boolean upgradleLiferayWorkspace) {
		_name = name;
		_values = values;
		_upgradleLiferayWorkspace = upgradleLiferayWorkspace;
	}

	public String getName() {
		return _name;
	}

	public String getValues() {
		return _values;
	}

	public boolean isUpgradleLiferayWorkspace() {
		return _upgradleLiferayWorkspace;
	}

	public void setName(String name) {
		_name = name;
	}

	public void setUpgradleLiferayWorkspace(boolean value) {
		_upgradleLiferayWorkspace = value;
	}

	public void setValues(String values) {
		_values = values;
	}

	private static BreakingChangeVersion[] _breakingChangeItems = {
		new BreakingChangeVersion("7.0", "7.0"), new BreakingChangeVersion("7.1", "7.0,7.1"),
		new BreakingChangeVersion("7.1", "7.1", true)
	};

	private String _name;
	private boolean _upgradleLiferayWorkspace;
	private String _values;

}