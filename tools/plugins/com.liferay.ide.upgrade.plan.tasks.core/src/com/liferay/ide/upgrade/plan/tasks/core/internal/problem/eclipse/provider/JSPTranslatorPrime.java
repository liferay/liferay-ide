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

package com.liferay.ide.upgrade.plan.tasks.core.internal.problem.eclipse.provider;

import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class JSPTranslatorPrime extends JSPTranslator {

	@Override
	protected void handleIncludeFile(String filename) {

		// don't process include files to avoid redundant results and wrong line
		// number

	}

}