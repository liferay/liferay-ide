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

import com.liferay.ide.service.ui.ServiceUI;
import com.liferay.ide.ui.util.UIUtil;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.internal.ui.javaeditor.JavaEditor;
import org.eclipse.jdt.internal.ui.javaeditor.JavaSourceViewer;
import org.eclipse.jdt.ui.actions.SelectionDispatchAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.IInformationControlCreator;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.information.InformationPresenter;
import org.eclipse.swt.graphics.Rectangle;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class ServiceMethodWrapperLookupHyperlink extends ServiceMethodHyperlink {

	public ServiceMethodWrapperLookupHyperlink(
		JavaEditor editor, IRegion word, SelectionDispatchAction action, IMethod wrapperMethod, boolean qualify) {

		super(word, action, wrapperMethod, qualify);

		_editor = editor;
	}

	@Override
	public void open() {
		JavaSourceViewer sourceViewer = (JavaSourceViewer)_editor.getViewer();

		try {

			// call internal APIs to display quick heirarchy

			Class<?> sourceViewClass = sourceViewer.getClass();

			Field p = sourceViewClass.getSuperclass().getDeclaredField("fHierarchyPresenter");

			p.setAccessible(true);
			InformationPresenter presenter = (InformationPresenter)p.get(sourceViewer);

			Class<? extends InformationPresenter> presenterClass = presenter.getClass();

			Class<?> presenterSuperclass = presenterClass.getSuperclass();

			Method m = presenterSuperclass.getDeclaredMethod(
				"setCustomInformationControlCreator", IInformationControlCreator.class);

			m.setAccessible(true);
			m.invoke(presenter, (IInformationControlCreator)null);
			m = presenterClass.getDeclaredMethod("computeArea", IRegion.class);

			m.setAccessible(true);
			Rectangle bounds = (Rectangle)m.invoke(presenter, wordRegion());
			m = presenterSuperclass.getDeclaredMethod("setInformation", Object.class, Rectangle.class);

			m.setAccessible(true);
			m.invoke(presenter, method(), bounds);
		}
		catch (Exception e) {
			ServiceUI.logError("Could not open service wrapper", e);
			MessageDialog.openError(
				UIUtil.getActiveShell(), "Open Service Wrapper",
				"Could not open service wrapper, see error log for details.");
		}
	}

	@Override
	protected String getStaticText() {
		return "Open Service Wrapper";
	}

	private JavaEditor _editor;

}