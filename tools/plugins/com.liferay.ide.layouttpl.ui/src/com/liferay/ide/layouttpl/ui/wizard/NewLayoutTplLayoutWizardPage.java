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

package com.liferay.ide.layouttpl.ui.wizard;

import com.liferay.ide.layouttpl.core.operation.INewLayoutTplDataModelProperties;
import com.liferay.ide.layouttpl.ui.LayoutTplUI;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.frameworks.internal.datamodel.ui.DataModelWizardPage;

import org.osgi.framework.Bundle;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class NewLayoutTplLayoutWizardPage extends DataModelWizardPage implements INewLayoutTplDataModelProperties {

	public NewLayoutTplLayoutWizardPage(IDataModel dataModel, String pageName) {
		super(
			dataModel, pageName, Msgs.createLayoutTemplate,
			LayoutTplUI.imageDescriptorFromPlugin(LayoutTplUI.PLUGIN_ID, "/icons/wizban/layout_template_wiz.png"));

		setDescription(Msgs.selectInitialTemplate);
	}

	@Override
	public void dispose() {
		super.dispose();

		if ((imagesToDispose != null) && (imagesToDispose.size() > 0)) {
			for (Image img : imagesToDispose) {
				if ((img != null) && !img.isDisposed()) {
					img.dispose();
				}
			}
		}
	}

	protected void createLayoutOption(Composite parent, String property, String text, Image image) {
		Composite optionParent = new Composite(parent, SWT.NONE);

		optionParent.setLayout(new GridLayout(1, false));

		Label imageLabel = new Label(optionParent, SWT.NONE);

		imageLabel.setImage(image);
		imageLabel.addMouseListener(
			new MouseAdapter() {

				@Override
				public void mouseUp(MouseEvent e) {
					getDataModel().setProperty(property, true);
				}

			});

		Button radio = new Button(optionParent, SWT.RADIO);

		radio.setText(text);

		this.synchHelper.synchRadio(radio, property, null);
	}

	protected void createSelectLayoutGroup(Composite parent) {
		SWTUtil.createLabel(parent, Msgs.selectInitialLayout, 1);

		/**
		 * Composite group = SWTUtil.createTopComposite(parent, 4);
		 * group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1,
		 * 1));
		 */
		Composite group = new Composite(parent, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);

		gd.widthHint = 575;
		group.setLayoutData(gd);

		RowLayout rowLayout = new RowLayout();

		rowLayout.wrap = true;
		rowLayout.pack = false;
		group.setLayout(rowLayout);

		if ((LAYOUT_PROPERTIES.length == LAYOUT_OPTIONS_TEXT.length) &&
			(LAYOUT_PROPERTIES.length == layoutTplUIBundleImages.length)) {

			imagesToDispose = new ArrayList<>();

			for (int i = 0; i < LAYOUT_PROPERTIES.length; i++) {
				Image img = layoutTplUIBundleImages[i].createImage();

				createLayoutOption(group, LAYOUT_PROPERTIES[i], LAYOUT_OPTIONS_TEXT[i], img);
				imagesToDispose.add(img);
			}
		}
	}

	@Override
	protected Composite createTopLevelComposite(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 1);

		createSelectLayoutGroup(topComposite);

		return topComposite;
	}

	@Override
	protected String[] getValidationPropertyNames() {
		return LAYOUT_PROPERTIES;
	}

	protected static final String[] LAYOUT_OPTIONS_TEXT = {
		Msgs.oneColumn, Msgs.oneTwoColumns3070, Msgs.oneTwoColumns7030, Msgs.oneTwoOneColumns, Msgs.twoColumns5050,
		Msgs.twoColumns3070, Msgs.twoColumns7030, Msgs.twoTwoColumns, Msgs.threeColumns
	};

	protected static final LayoutTplUI layoutTplUI = LayoutTplUI.getDefault();
	protected static final Bundle layoutTplUIBundle = layoutTplUI.getBundle();
	protected static final ImageDescriptor[] layoutTplUIBundleImages = {
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/1_column.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/1_2_columns_i.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/1_2_columns_ii.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/1_2_1_columns.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/2_columns_i.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/2_columns_ii.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/2_columns_iii.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/2_2_columns.png")),
		ImageDescriptor.createFromURL(layoutTplUIBundle.getEntry("/icons/layouts/3_columns.png"))
	};

	protected List<Image> imagesToDispose;

	private static class Msgs extends NLS {

		public static String createLayoutTemplate;
		public static String oneColumn;
		public static String oneTwoColumns3070;
		public static String oneTwoColumns7030;
		public static String oneTwoOneColumns;
		public static String selectInitialLayout;
		public static String selectInitialTemplate;
		public static String threeColumns;
		public static String twoColumns3070;
		public static String twoColumns5050;
		public static String twoColumns7030;
		public static String twoTwoColumns;

		static {
			initializeMessages(NewLayoutTplLayoutWizardPage.class.getName(), Msgs.class);
		}

	}

}