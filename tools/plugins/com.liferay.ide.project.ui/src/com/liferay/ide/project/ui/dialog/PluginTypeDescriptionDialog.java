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

package com.liferay.ide.project.ui.dialog;

import com.liferay.ide.ui.util.UIUtil;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.DialogDef;
import org.eclipse.sapphire.ui.forms.swt.SapphireDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Kuo Zhang
 */
public class PluginTypeDescriptionDialog extends SapphireDialog {

	public PluginTypeDescriptionDialog(
		final Shell shell, final Element element, final DefinitionLoader.Reference<DialogDef> definition) {

		super(shell, element, definition);

		setShellStyle(SWT.NO_TRIM);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);

		Display display = Display.getDefault();

		display.addFilter(
			SWT.MouseUp,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					display.removeFilter(SWT.MouseUp, this);

					UIUtil.async(
						new Runnable() {

							@Override
							public void run() {
								close();
							}

						});
				}

			});
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		return new Composite(parent, SWT.NONE);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		final Point parentLocation = getParentShell().getLocation();
		final Point parentSize = getParentShell().getSize();

		return new Point(parentLocation.x + parentSize.x, parentLocation.y);
	}

}