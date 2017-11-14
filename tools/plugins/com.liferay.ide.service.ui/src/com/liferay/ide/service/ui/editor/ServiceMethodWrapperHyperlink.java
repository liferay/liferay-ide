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

package com.liferay.ide.service.ui.editor;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.text.IRegion;

/**
 * @author Gregory Amerson
 */
public class ServiceMethodWrapperHyperlink extends ServiceMethodHyperlink {

	public ServiceMethodWrapperHyperlink(
		IRegion word, SelectionDispatchAction action, IMethod wrapperMethod, boolean qualify) {

		super(word, action, wrapperMethod, qualify);
	}

	@Override
	protected String getStaticText() {
		return "Open Service Wrapper";
	}

}