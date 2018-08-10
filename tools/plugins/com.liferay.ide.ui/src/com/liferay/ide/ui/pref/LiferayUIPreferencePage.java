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

package com.liferay.ide.ui.pref;

import com.liferay.ide.ui.LiferayUIPlugin;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import org.osgi.service.prefs.BackingStoreException;

/**
 * @author Gregory Amerson
 */
public class LiferayUIPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	public LiferayUIPreferencePage() {
		LiferayUIPlugin plugin = LiferayUIPlugin.getDefault();

		setImageDescriptor(plugin.getImageDescriptor(LiferayUIPlugin.IMG_LIFERAY_ICON_SMALL));
	}

	public LiferayUIPreferencePage(String title) {
		super(title);
	}

	public LiferayUIPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite pageParent = new Composite(parent, SWT.NONE);

		GridLayoutFactory gridLayoutFactory = GridLayoutFactory.swtDefaults();

		pageParent.setLayout(gridLayoutFactory.create());

		Group group = new Group(pageParent, SWT.NONE);

		group.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		group.setText(Msgs.messageDialogs);
		group.setLayout(new GridLayout(2, false));

		Label label = new Label(group, SWT.NONE);

		label.setText(Msgs.clearAllSettings);

		Button button = new Button(group, SWT.PUSH);

		button.setText(Msgs.clear);
		button.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					try {
						LiferayUIPlugin.clearAllPersistentSettings();
					}
					catch (BackingStoreException bse) {
						MessageDialog.openError(button.getShell(), "Liferay Preferences", "Unable to reset settings.");
					}
				}

			});

		return pageParent;
	}

	private static class Msgs extends NLS {

		public static String clear;
		public static String clearAllSettings;
		public static String messageDialogs;

		static {
			initializeMessages(LiferayUIPreferencePage.class.getName(), Msgs.class);
		}

	}

}