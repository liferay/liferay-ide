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

package com.liferay.ide.upgrade.plan.core;

import com.liferay.ide.core.util.ListUtil;

import java.io.File;

import java.util.Collection;

import org.eclipse.core.resources.IResource;

/**
 * @author Simon Jiang
 */
public interface Problem {

	public default <T extends Problem> boolean checkList(Collection<T> source, Collection<T> target) {
		if (source == null) {
			if (target == null) {
				return true;
			}

			return false;
		}

		if (target == null) {
			return false;
		}

		if (source.size() != target.size()) {
			return false;
		}

		if (ListUtil.isEmpty(source) && ListUtil.isEmpty(target)) {
			return true;
		}

		return true;
	}

	public default boolean isEqual(File original, File target) {
		if (original != null) {
			return original.equals(target);
		}

		if (target == null) {
			return true;
		}

		return false;
	}

	public default boolean isEqual(IResource original, IResource target) {
		if (original != null) {
			return original.equals(target);
		}

		if (target == null) {
			return true;
		}

		return false;
	}

	public default boolean isEqualIgnoreCase(String original, String target) {
		if (original != null) {
			return original.equalsIgnoreCase(target);
		}

		if (target == null) {
			return true;
		}

		return false;
	}

}