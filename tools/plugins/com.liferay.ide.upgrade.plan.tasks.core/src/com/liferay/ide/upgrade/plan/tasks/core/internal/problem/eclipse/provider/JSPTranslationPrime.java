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

import org.eclipse.core.resources.IFile;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslation;
import org.eclipse.jst.jsp.core.internal.java.JSPTranslator;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class JSPTranslationPrime extends JSPTranslation {

	public JSPTranslationPrime(IJavaProject javaProject, JSPTranslator translator, IFile jspFile) {
		super(javaProject, translator);

		_jspFile = jspFile;
	}

	public IFile getJspFile() {
		return _jspFile;
	}

	private IFile _jspFile;

}