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

import java.util.Comparator;

import org.eclipse.sapphire.CollationService;

/**
 * @author Simon Jiang
 */
public class ProdcutCategoryCollationService extends CollationService {

	@Override
	protected Comparator<String> compute() {
		return new CategoryCompartor();
	}

	private class CategoryCompartor implements Comparator<String> {

		@Override
		public int compare(String a, String b) {
			if (a.startsWith("dxp") && !b.startsWith("dxp")) {
				return -1;
			}
			else if (a.startsWith("portal") && b.startsWith("dxp")) {
				return 1;
			}
			else if (a.startsWith("portal") && b.startsWith("commerce")) {
				return -1;
			}
			else if (a.startsWith("commerce") && !b.startsWith("commerce")) {
				return 1;
			}

			return 0;
		}

	}

}