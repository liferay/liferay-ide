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

package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.ui.util.SWTUtil;

import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelSynchHelper;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class LiferaySDKField {

	public LiferaySDKField(
		Composite parent, IDataModel model, SelectionAdapter selectionAdapter, String fieldPropertyName,
		DataModelSynchHelper synchHelper) {

		this(parent, model, selectionAdapter, fieldPropertyName, synchHelper, Msgs.liferayPluginsSDK);
	}

	public LiferaySDKField(
		Composite parent, IDataModel model, SelectionAdapter selectionAdapter, String fieldPropertyName,
		DataModelSynchHelper synchHelper, String labelName) {

		this.model = model;
		propertyName = fieldPropertyName;
		this.selectionAdapter = selectionAdapter;
		this.synchHelper = synchHelper;

		SWTUtil.createLabel(parent, labelName, 1);

		Combo sdkCombo = new Combo(parent, SWT.DROP_DOWN | SWT.READ_ONLY);

		sdkCombo.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));
		sdkCombo.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					LiferaySDKField.this.selectionAdapter.widgetSelected(e);
					LiferaySDKField.this.synchHelper.synchAllUIWithModel();
				}

			});

		synchHelper.synchCombo(sdkCombo, fieldPropertyName, null);
	}

	protected IDataModel model;
	protected String propertyName;
	protected SelectionAdapter selectionAdapter;
	protected DataModelSynchHelper synchHelper;

	private static class Msgs extends NLS {

		public static String liferayPluginsSDK;

		static {
			initializeMessages(LiferaySDKField.class.getName(), Msgs.class);
		}

	}

}