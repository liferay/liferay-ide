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
import org.eclipse.jdt.ui.JavaElementLabels;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.widgets.Display;

/**
 * @author Gregory Amerson
 */
public class ServiceMethodHyperlink implements IHyperlink {

	public ServiceMethodHyperlink(
		IRegion word, SelectionDispatchAction openAction, IMethod serviceMethod, boolean qualify) {

		_word = word;
		_openAction = openAction;
		_serviceMethod = serviceMethod;
		_qualify = qualify;
	}

	public IRegion getHyperlinkRegion() {
		return _word;
	}

	public String getHyperlinkText() {
		String methodLabel = JavaElementLabels.getElementLabel(_serviceMethod, JavaElementLabels.ALL_FULLY_QUALIFIED);

		return getStaticText() + (_qualify ? (" " + methodLabel) : "");
	}

	public String getTypeLabel() {
		return null;
	}

	public IMethod method() {
		return _serviceMethod;
	}

	public void open() {
		Runnable runnable = new Runnable() {

			public void run() {
				ServiceMethodHyperlink.this._openAction.run(new StructuredSelection(_serviceMethod));
			}

		};

		BusyIndicator.showWhile(Display.getDefault(), runnable);
	}

	public IRegion wordRegion() {
		return _word;
	}

	protected String getStaticText() {
		return "Open Service";
	}

	private SelectionDispatchAction _openAction;
	private boolean _qualify;
	private IMethod _serviceMethod;
	private IRegion _word;

}