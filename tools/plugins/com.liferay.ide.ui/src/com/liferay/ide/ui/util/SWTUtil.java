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

package com.liferay.ide.ui.util;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.ui.LiferayUIPlugin;

import java.net.URL;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

/**
 * @author Gregory Amerson
 */
public class SWTUtil {

	/**
	 * Create a new button with the standard size.
	 *
	 * @param comp
	 *            the component to add the button to
	 * @param label
	 *            the button label
	 * @return a button
	 */
	public static Button createButton(Composite comp, String label) {
		Button b = new Button(comp, SWT.PUSH);

		b.setText(label);

		if (_fontMetrics == null) {
			initializeDialogUnits(comp);
		}

		GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);

		data.widthHint = getButtonWidthHint(b);
		b.setLayoutData(data);

		return b;
	}

	/**
	 * Creates a check box button using the parents' font
	 *
	 * @param parent
	 *            the parent to add the button to
	 * @param label
	 *            the label for the button
	 * @param image
	 *            the image for the button
	 * @param checked
	 *            the initial checked state of the button
	 * @param hspan
	 *            the horizontal span to take up in the parent composite
	 * @return a new checked button set to the initial checked state
	 * @since 3.3
	 */
	public static Button createCheckButton(Composite parent, String label, Image image, boolean checked, int hspan) {
		Button button = new Button(parent, SWT.CHECK);

		button.setFont(parent.getFont());
		button.setSelection(checked);

		if (image != null) {
			button.setImage(image);
		}

		if (label != null) {
			button.setText(label);
		}

		GridData gd = new GridData();

		gd.horizontalSpan = hspan;
		button.setLayoutData(gd);

		if (_fontMetrics == null) {
			initializeDialogUnits(parent);
		}

		setButtonDimensionHint(button);

		return button;
	}

	/**
	 * Creates a Composite widget
	 *
	 * @param parent
	 *            the parent composite to add this composite to
	 * @param columns
	 *            the number of columns within the composite
	 * @param hspan
	 *            the horizontal span the composite should take up on the parent
	 * @param fill
	 *            the style for how this composite should fill into its parent
	 *            Can be one of <code>GridData.FILL_HORIZONAL</code>,
	 *            <code>GridData.FILL_BOTH</code> or
	 *            <code>GridData.FILL_VERTICAL</code>
	 * @return the new group
	 */
	public static Composite createComposite(Composite parent, int columns, int hspan, int fill) {
		Composite g = new Composite(parent, SWT.NONE);

		g.setLayout(new GridLayout(columns, false));
		g.setFont(parent.getFont());

		GridData gd = new GridData(fill);

		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);

		return g;
	}

	/**
	 * Creates a Composite widget
	 *
	 * @param parent
	 *            the parent composite to add this composite to
	 * @param columns
	 *            the number of columns within the composite
	 * @param hspan
	 *            the horizontal span the composite should take up on the parent
	 * @param fill
	 *            the style for how this composite should fill into its parent
	 *            Can be one of <code>GridData.FILL_HORIZONAL</code>,
	 *            <code>GridData.FILL_BOTH</code> or
	 *            <code>GridData.FILL_VERTICAL</code>
	 * @param marginwidth
	 *            the width of the margin to place around the composite (default
	 *            is 5, specified by GridLayout)
	 * @param marginheight
	 *            the height of the margin to place around the composite
	 *            (default is 5, specified by GridLayout)
	 * @return the new group
	 */
	public static Composite createComposite(
		Composite parent, int columns, int hspan, int fill, int marginwidth, int marginheight) {

		Composite g = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout(columns, false);

		layout.marginWidth = marginwidth;
		layout.marginHeight = marginheight;
		g.setLayout(layout);

		g.setFont(parent.getFont());
		GridData gd = new GridData(fill);

		gd.horizontalSpan = hspan;
		g.setLayoutData(gd);

		return g;
	}

	public static Group createGroup(Composite parent, String text, int numColumns) {
		Group group = new Group(parent, SWT.NULL);

		group.setText(text);

		GridLayout layout = new GridLayout(numColumns, false);

		group.setLayout(layout);

		GridData data = new GridData();

		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 300;
		group.setLayoutData(data);

		return group;
	}

	public static void createHorizontalSpacer(Composite comp, int hSpan) {
		Label l = new Label(comp, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = hSpan;
		l.setLayoutData(gd);
	}

	public static Link createHyperLink(Composite parent, int style, String text, int hspan, String url) {
		Link link = createLink(parent, style, text, hspan);

		if (url != null) {
			link.addSelectionListener(
				new SelectionAdapter() {

					public void widgetSelected(SelectionEvent e) {
						try {
							IWorkbench workbench = PlatformUI.getWorkbench();

							IWorkbenchBrowserSupport browserSupport = workbench.getBrowserSupport();

							browserSupport.getExternalBrowser().openURL(new URL(url));
						}
						catch (Exception e1) {
							LiferayUIPlugin.logError("Could not open external browser.", e1);
						}
					}

				});
		}

		return link;
	}

	public static Label createLabel(Composite parent, int style, String text, int hspan) {
		Label l = new Label(parent, style);

		l.setFont(parent.getFont());
		l.setText(text);

		GridData gd = new GridData();

		gd.horizontalSpan = hspan;
		l.setLayoutData(gd);

		return l;
	}

	/**
	 * Creates a new label widget
	 *
	 * @param parent
	 *            the parent composite to add this label widget to
	 * @param text
	 *            the text for the label
	 * @param hspan
	 *            the horizontal span to take up in the parent composite
	 * @return the new label
	 */
	public static Label createLabel(Composite parent, String text, int hspan) {
		return createLabel(parent, SWT.NONE, text, hspan);
	}

	public static Link createLink(Composite parent, int style, String text, int hspan) {
		Link l = new Link(parent, style);

		l.setFont(parent.getFont());
		l.setText(text);

		GridData gd = new GridData();

		gd.horizontalSpan = hspan;
		l.setLayoutData(gd);

		return l;
	}

	/**
	 * Creates and returns a new push button with the given label and/or image.
	 *
	 * @param parent
	 *            parent control
	 * @param label
	 *            button label or <code>null</code>
	 * @param image
	 *            image or <code>null</code>
	 * @return a new push button
	 */
	public static Button createPushButton(Composite parent, String label, Image image) {
		Button button = new Button(parent, SWT.PUSH);

		button.setFont(parent.getFont());

		if (image != null) {
			button.setImage(image);
		}

		if (label != null) {
			button.setText(label);
		}

		GridData gd = new GridData();

		button.setLayoutData(gd);

		if (_fontMetrics == null) {
			initializeDialogUnits(parent);
		}

		setButtonDimensionHint(button);

		return button;
	}

	public static Button createRadioButton(Composite parent, String label, Image image, boolean checked, int hspan) {
		Button button = new Button(parent, SWT.RADIO);

		button.setFont(parent.getFont());
		button.setSelection(checked);

		if (image != null) {
			button.setImage(image);
		}

		if (label != null) {
			button.setText(label);
		}

		GridData gd = new GridData();

		gd.horizontalSpan = hspan;
		button.setLayoutData(gd);

		if (_fontMetrics == null) {
			initializeDialogUnits(parent);
		}

		setButtonDimensionHint(button);

		return button;
	}

	public static void createSeparator(Composite parent, int hspan) {
		Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, hspan, 1);

		label.setLayoutData(gd);
	}

	/**
	 * Creates a new text widget
	 *
	 * @param parent
	 *            the parent composite to add this text widget to
	 * @param hspan
	 *            the horizontal span to take up on the parent composite
	 * @return the new text widget
	 */
	public static Text createSingleText(Composite parent, int hspan) {
		Text t = new Text(parent, SWT.SINGLE | SWT.BORDER);

		t.setFont(parent.getFont());

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = hspan;
		t.setLayoutData(gd);

		return t;
	}

	public static Text createText(Composite parent, int hspan) {
		return createText(parent, SWT.SINGLE | SWT.BORDER, hspan);
	}

	public static Text createText(Composite parent, int style, int hspan) {
		Text text = new Text(parent, style);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = hspan;
		text.setLayoutData(gd);

		return text;
	}

	public static Composite createTopComposite(Composite parent, int numColumns) {
		Composite composite = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();

		layout.numColumns = numColumns;
		composite.setLayout(layout);

		GridData data = new GridData();

		data.verticalAlignment = GridData.FILL;
		data.horizontalAlignment = GridData.FILL;
		data.widthHint = 300;
		composite.setLayoutData(data);

		return composite;
	}

	/**
	 * creates a vertical spacer for separating components
	 *
	 * @param comp
	 * @param numlines
	 */
	public static void createVerticalSpacer(Composite comp, int numlines, int hspan) {
		Label lbl = new Label(comp, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.heightHint = numlines;
		gd.horizontalSpan = hspan;
		lbl.setLayoutData(gd);
	}

	/**
	 * Creates a wrapping label
	 *
	 * @param parent
	 *            the parent composite to add this label to
	 * @param text
	 *            the text to be displayed in the label
	 * @param hspan
	 *            the horizontal span that label should take up in the parent
	 *            composite
	 * @param wrapwidth
	 *            the width hint that the label should wrap at
	 * @return a new label that wraps at a specified width
	 */
	public static Label createWrapLabel(Composite parent, String text, int hspan, int wrapwidth) {
		Label l = new Label(parent, SWT.WRAP);

		l.setFont(parent.getFont());
		l.setText(text);

		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = hspan;
		gd.widthHint = wrapwidth;
		l.setLayoutData(gd);

		return l;
	}

	public static void selectItem(Combo combo, String item) {
		if ((combo == null) || (item == null)) {
			return;
		}

		String[] items = combo.getItems();

		if (ListUtil.isNotEmpty(items)) {
			for (int i = 0; i < items.length; i++) {
				if (item.equals(items[i])) {
					combo.select(i);

					return;
				}
			}
		}
	}

	/**
	 * Sets width and height hint for the button control. <b>Note:</b> This is a
	 * NOP if the button's layout data is not an instance of
	 * <code>GridData</code>.
	 *
	 * @param the
	 *            button for which to set the dimension hint
	 */
	public static void setButtonDimensionHint(Button button) {
		Object gd = button.getLayoutData();

		if (gd instanceof GridData) {
			((GridData)gd).widthHint = getButtonWidthHint(button);
			((GridData)gd).horizontalAlignment = GridData.FILL;
		}
	}

	public static boolean showPreferencePage(String id, Shell shell) {
		IWorkbench workbench = PlatformUI.getWorkbench();

		PreferenceManager manager = workbench.getPreferenceManager();

		IPreferenceNode node = manager.find("org.eclipse.jdt.ui.preferences.JavaBasePreferencePage").findSubNode(id);

		PreferenceManager manager2 = new PreferenceManager();

		manager2.addToRoot(node);
		PreferenceDialog dialog = new PreferenceDialog(shell, manager2);

		dialog.create();

		if (dialog.open() == Window.OK) {
			return true;
		}

		return false;
	}

	/**
	 * Returns a width hint for a button control.
	 */
	protected static int getButtonWidthHint(Button button) {
		int widthHint = Dialog.convertHorizontalDLUsToPixels(_fontMetrics, IDialogConstants.BUTTON_WIDTH);

		return Math.max(widthHint, button.computeSize(SWT.DEFAULT, SWT.DEFAULT, true).x);
	}

	protected static void initializeDialogUnits(Control testControl) {

		// Compute and store a font metric

		GC gc = new GC(testControl);

		gc.setFont(JFaceResources.getDialogFont());
		_fontMetrics = gc.getFontMetrics();
		gc.dispose();
	}

	private static FontMetrics _fontMetrics;

}