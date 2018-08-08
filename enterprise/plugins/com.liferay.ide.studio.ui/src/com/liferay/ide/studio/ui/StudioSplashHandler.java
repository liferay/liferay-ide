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

package com.liferay.ide.studio.ui;

import org.eclipse.core.runtime.IProduct;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.StringConverter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.branding.IProductConstants;
import org.eclipse.ui.internal.util.PrefUtil;
import org.eclipse.ui.splash.BasicSplashHandler;

/**
 * @author Simon Jiang
 */
@SuppressWarnings("restriction")
public class StudioSplashHandler extends BasicSplashHandler {

	@Override
	public void dispose() {
		super.dispose();

		if ((_newFont != null) && !_newFont.isDisposed()) {
			_newFont.dispose();
		}
	}

	public void init(Shell splash) {
		super.init(splash);

		String progressRectString = null;
		String foregroundColorString = null;

		IProduct product = Platform.getProduct();

		if (product != null) {
			progressRectString = product.getProperty(IProductConstants.STARTUP_PROGRESS_RECT);
			foregroundColorString = product.getProperty(IProductConstants.STARTUP_FOREGROUND_COLOR);
		}

		Rectangle progressRect = StringConverter.asRectangle(progressRectString, new Rectangle(10, 10, 300, 15));

		setProgressRect(progressRect);

		int foregroundColorInteger;

		try {
			foregroundColorInteger = Integer.parseInt(foregroundColorString, 16);
		}
		catch (Exception ex) {
			foregroundColorInteger = 0xD2D7FF; // off white
		}

		setForeground(
			new RGB(
				(foregroundColorInteger & 0xFF0000) >> 16, (foregroundColorInteger & 0xFF00) >> 8,
				foregroundColorInteger & 0xFF));

		// the following code will be removed for release time

		IPreferenceStore preferenceStore = PrefUtil.getInternalPreferenceStore();

		if (preferenceStore.getBoolean("SHOW_BUILDID_ON_STARTUP")) {
			String buildId = System.getProperty("eclipse.buildId", "Unknown Build");
			String buildIdRect = product.getProperty("buildIdRect");

			Rectangle buildIdRectangle = StringConverter.asRectangle(buildIdRect, new Rectangle(322, 190, 100, 40));

			GridLayout layout = new GridLayout(1, false);

			layout.marginRight = 0;
			layout.horizontalSpacing = 0;
			layout.marginWidth = 0;

			Composite versionComposite = new Composite(getContent(), SWT.NONE);

			versionComposite.setBounds(buildIdRectangle);
			versionComposite.setLayout(layout);

			Label idLabel = new Label(versionComposite, SWT.NONE);

			idLabel.setLayoutData(new GridData(SWT.TRAIL, SWT.CENTER, true, true, 1, 1));
			idLabel.setForeground(getForeground());

			Font initialFont = idLabel.getFont();

			FontData[] fontData = initialFont.getFontData();

			for (FontData data : fontData) {
				data.setHeight(14);
				data.setStyle(SWT.BOLD);
			}

			_newFont = new Font(idLabel.getDisplay(), fontData);

			idLabel.setFont(_newFont);

			idLabel.setText(buildId);

			versionComposite.layout(true);
		}
		else {
			getContent(); // ensure creation of the progress
		}
	}

	private Font _newFont;

}