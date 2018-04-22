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

package com.liferay.ide.ui.dialog;

import com.liferay.ide.core.util.StringPool;

import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jface.operation.IRunnableContext;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class FilteredTypesSelectionDialogEx extends FilteredTypesSelectionDialog {

	public FilteredTypesSelectionDialogEx(
		Shell shell, boolean multi, IRunnableContext context, IJavaSearchScope scope, int elementKinds) {

		super(shell, multi, context, scope, elementKinds);

		setInitialPattern(StringPool.DOUBLE_ASTERISK);
	}

	protected boolean ignoreEvent = false;
	protected boolean listenerInstalled = false;

}