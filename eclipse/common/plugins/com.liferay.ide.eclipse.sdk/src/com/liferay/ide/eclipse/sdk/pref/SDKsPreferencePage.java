/*******************************************************************************
 * Copyright (c) 2000-2010 Liferay, Inc. All rights reserved.
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
 *
 *******************************************************************************/

package com.liferay.ide.eclipse.sdk.pref;

import com.liferay.ide.eclipse.sdk.SDK;
import com.liferay.ide.eclipse.sdk.SDKManager;
import com.liferay.ide.eclipse.sdk.SDKPlugin;
import com.liferay.ide.eclipse.ui.pref.LiferayUIPreferencePage;
import com.liferay.ide.eclipse.ui.util.SWTUtil;

import org.eclipse.jface.dialogs.IMessageProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

/**
 * @author Greg Amerson
 */
public class SDKsPreferencePage extends LiferayUIPreferencePage implements IWorkbenchPreferencePage {

	public static final String ID = "com.liferay.ide.eclipse.sdk.preferences.installedSDKs";
	
	protected InstalledSDKsCompostite installedSDKsComposite;

	public SDKsPreferencePage() {
		setImageDescriptor(SDKPlugin.imageDescriptorFromPlugin(SDKPlugin.PLUGIN_ID, "/icons/e16/sdk.png"));
	}

	@Override
	public void init(IWorkbench workbench) {
		noDefaultAndApplyButton();
	}

	@Override
	public boolean isValid() {
		return this.installedSDKsComposite != null &&
			(this.installedSDKsComposite.getSDKs().length == 0 || this.installedSDKsComposite.getCheckedSDK() != null);
	}

	@Override
	public boolean performOk() {
		if (isValid()) {
			SDK[] sdks = installedSDKsComposite.getSDKs();

			if (sdks != null) {
				SDKManager.getInstance().saveSDKs(sdks);
			}

			return true;
		}
		else {
			setMessage("Must have at least SDK checked as default", IMessageProvider.ERROR);

			return false;
		}
	}

	@Override
	protected Control createContents(Composite parent) {
		initializeDialogUnits(parent);

		// GridLayout layout= new GridLayout();
		// layout.numColumns= 1;
		// layout.marginHeight = 0;
		// layout.marginWidth = 0;
		// parent.setLayout(layout);

		SWTUtil.createWrapLabel(
			parent,
			"Add, remove or edit SDK definitions. By default, the checked SDK is used for new Liferay Plug-in projects.",
			1, 100);
		
		SWTUtil.createVerticalSpacer(parent, 1, 1);

		installedSDKsComposite = new InstalledSDKsCompostite(parent, SWT.NONE);
		installedSDKsComposite.setPreferencePage(this);

		GridData data = new GridData(GridData.FILL_BOTH);
		data.horizontalSpan = 1;

		installedSDKsComposite.setLayoutData(data);
		installedSDKsComposite.setSDKs(SDKManager.getInstance().getSDKs());

		return parent;
	}

	@Override
	protected void performDefaults() {
		if (installedSDKsComposite != null && !installedSDKsComposite.isDisposed()) {
			installedSDKsComposite.setSDKs(SDKManager.getInstance().getSDKs());
		}
	}
}
