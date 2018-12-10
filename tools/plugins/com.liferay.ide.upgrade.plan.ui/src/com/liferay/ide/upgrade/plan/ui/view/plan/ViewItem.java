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

package com.liferay.ide.upgrade.plan.ui.view.plan;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.ui.util.UIUtil;
import com.liferay.ide.upgrade.plan.api.UpgradeTaskStep;
import com.liferay.ide.upgrade.plan.ui.UpgradePlanUI;

import java.net.URL;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.events.HyperlinkAdapter;
import org.eclipse.ui.forms.events.HyperlinkEvent;
import org.eclipse.ui.forms.events.IExpansionListener;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ImageHyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;

/**
 * @author Terry Jia
 */
public class ViewItem {

	public ViewItem(TaskStepsViewer viewer, UpgradeTaskStep step) {
		_viewer = viewer;

		ScrolledForm form = _viewer.getForm();

		_parent = form.getBody();

		_step = step;

		Display display = _parent.getDisplay();

		toolkit = new FormToolkit(display);

		_addItem();
	}

	public void addExpansionListener(IExpansionListener listener) {
		_mainItemComposite.addExpansionListener(listener);
	}

	public void createCompletionComposite(boolean finalItem) {
	}

	public void dispose() {
		if (_checkDoneLabel != null) {
			_checkDoneLabel.dispose();
		}

		if (_bodyText != null) {
			_bodyText.dispose();
		}

		if (buttonComposite != null) {
			buttonComposite.dispose();
		}

		if (completionComposite != null) {
			completionComposite.dispose();
		}

		if (bodyWrapperComposite != null) {
			bodyWrapperComposite.dispose();
		}

		if (_mainItemComposite != null) {
			_mainItemComposite.dispose();
		}

		if (_titleComposite != null) {
			_titleComposite.dispose();
		}

		if (_regularFont != null) {
			_regularFont.dispose();
		}
	}

	public void handleButtons() {
		if (_step == null) {
			return;
		}

		buttonComposite = toolkit.createComposite(bodyWrapperComposite);

		GridLayout buttonlayout = new GridLayout(4, false);

		buttonlayout.marginHeight = 2;
		buttonlayout.marginWidth = 2;
		buttonlayout.verticalSpacing = 2;

		TableWrapData buttonData = new TableWrapData(TableWrapData.FILL);

		buttonComposite.setLayout(buttonlayout);
		buttonComposite.setLayoutData(buttonData);

		Label filllabel = toolkit.createLabel(buttonComposite, null);

		GridData filldata = new GridData();

		filldata.widthHint = 16;
		filllabel.setLayoutData(filldata);

		UpgradePlanUI upgradeUI = UpgradePlanUI.getDefault();

		_performButton = createButtonWithText(
			buttonComposite, upgradeUI.getImage("COMPOSITE_TASK_START"), this, "Perform");

		_performButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		_performButton.addHyperlinkListener(
			new HyperlinkAdapter() {

				@Override
				public void linkActivated(HyperlinkEvent e) {
					_step.execute(new NullProgressMonitor());
				}

			});

		String url = (String)_step.getProperty("step.url");

		if (CoreUtil.isNotNullOrEmpty(url)) {
			_openDowcumentButton = createButtonWithText(
				buttonComposite, upgradeUI.getImage("COMPOSITE_TASK_START"), this, "Open document");

			_openDowcumentButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

			_openDowcumentButton.addHyperlinkListener(
				new HyperlinkAdapter() {

					@Override
					public void linkActivated(HyperlinkEvent e) {
						try {
							UIUtil.openURL(new URL(url));
						}
						catch (Exception ex) {
							UpgradePlanUI.logError("Could not open external browser.", ex);
						}
					}

				});
		}
	}

	public boolean hasCompletionMessage() {
		return false;
	}

	public void initialized() {
		_initialized = true;
	}

	public boolean isBold() {
		return _bold;
	}

	public boolean isCompleted() {
		return _completed;
	}

	public boolean isExpanded() {
		return _mainItemComposite.isExpanded();
	}

	public boolean isSkipped() {
		return _skipped;
	}

	public void redraw() {
		_parent.redraw();
		_mainItemComposite.redraw();
		_titleComposite.redraw();
		bodyWrapperComposite.redraw();
	}

	public void setAsCurrentActiveItem() {
		setButtonsVisible(true);
		setBold(true);
		setExpanded();
		setFocus();
	}

	public void setBold(boolean value) {
		if (value) {
			_mainItemComposite.setFont(_boldFont);

			if (_initialized) {
				_mainItemComposite.layout();
			}
		}
		else {
			_mainItemComposite.setFont(_regularFont);

			if (_initialized) {
				_mainItemComposite.layout();
			}
		}

		_bold = value;
	}

	public void setButtonsVisible(boolean visible) {
		if ((_buttonExpanded != visible) && (buttonComposite != null)) {
			buttonComposite.setVisible(visible);
		}

		if (visible && _initialized) {
			FormToolkit.ensureVisible(_mainItemComposite);
		}

		_buttonExpanded = visible;
	}

	public void setCollapsed() {
		if (_mainItemComposite.isExpanded()) {
			_mainItemComposite.setExpanded(false);

			if (_initialized) {
				_getForm().reflow(true);

				FormToolkit.ensureVisible(_mainItemComposite);
			}
		}
	}

	public void setComplete() {
		_completed = true;
		_checkDoneLabel.setImage(_getCompleteImage());

		if (_initialized) {
			Composite parent = _checkDoneLabel.getParent();

			parent.layout();
		}
	}

	public void setExpanded() {
		if (!_mainItemComposite.isExpanded()) {
			_mainItemComposite.setExpanded(true);

			if (_initialized) {
				_getForm().reflow(true);

				FormToolkit.ensureVisible(_mainItemComposite);
			}
		}
	}

	public void setIncomplete() {
		_checkDoneLabel.setImage(null);
		_completed = false;
	}

	public void setSkipped() {
		_skipped = true;
		_checkDoneLabel.setImage(_getSkipImage());

		if (_initialized) {
			Composite parent = _checkDoneLabel.getParent();

			parent.layout();
		}
	}

	protected ImageHyperlink createButtonWithText(Composite parent, Image image, ViewItem item, String linkText) {
		ImageHyperlink button = toolkit.createImageHyperlink(parent, SWT.NULL);

		button.setImage(image);
		button.setData(item);
		button.setText(linkText);
		button.setToolTipText(linkText);

		return button;
	}

	protected void setCompletionMessageCollapsed() {
		if ((completionComposite != null) && _completionMessageExpanded) {
			completionComposite.dispose();

			completionComposite = null;

			_getForm().reflow(true);
		}

		_completionMessageExpanded = false;
	}

	protected void setFocus() {
		_mainItemComposite.setFocus();

		FormToolkit.ensureVisible(_mainItemComposite);
	}

	protected Composite bodyWrapperComposite;
	protected Composite buttonComposite;
	protected Composite completionComposite;
	protected FormToolkit toolkit;

	private void _addItem() {
		_checkDoneLabel = toolkit.createLabel(_parent, " ");

		_mainItemComposite = toolkit.createExpandableComposite(
			_parent, ExpandableComposite.TWISTIE | ExpandableComposite.COMPACT);

		String title = (String)_step.getProperty("step.title");

		String type = (String)_step.getProperty("step.type");

		if (type != null) {
			title = title + " (" + type + ")";
		}

		_mainItemComposite.setText(title);

		_mainItemComposite.setData("step", _step);

		_titleComposite = toolkit.createComposite(_mainItemComposite);

		int number = 1;

		GridLayout layout = new GridLayout(number, false);

		GridData data = new GridData(GridData.FILL_BOTH);

		_titleComposite.setLayout(layout);
		_titleComposite.setLayoutData(data);

		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.verticalSpacing = 0;

		bodyWrapperComposite = toolkit.createComposite(_mainItemComposite);

		_mainItemComposite.setClient(bodyWrapperComposite);

		bodyWrapperComposite.setLayout(new TableWrapLayout());

		bodyWrapperComposite.setLayoutData(new TableWrapData(TableWrapData.FILL));

		String description = (String)_step.getProperty("step.description");

		_bodyText = toolkit.createLabel(bodyWrapperComposite, description);

		handleButtons();

		_boldFont = _mainItemComposite.getFont();

		FontData[] fontDatas = _boldFont.getFontData();

		for (FontData fontData : fontDatas) {
			fontData.setStyle(fontData.getStyle() ^ SWT.BOLD);
		}

		_regularFont = new Font(_mainItemComposite.getDisplay(), fontDatas);

		setBold(false);
	}

	private Image _getCompleteImage() {
		UpgradePlanUI upgradeUI = UpgradePlanUI.getDefault();

		return upgradeUI.getImage("ITEM_COMPLETE");
	}

	private ScrolledForm _getForm() {
		return _viewer.getForm();
	}

	private Image _getSkipImage() {
		UpgradePlanUI upgradeUI = UpgradePlanUI.getDefault();

		return upgradeUI.getImage("ITEM_SKIP");
	}

	private Label _bodyText;
	private boolean _bold = true;
	private Font _boldFont;
	private boolean _buttonExpanded = true;
	private Label _checkDoneLabel;
	private boolean _completed;
	private boolean _completionMessageExpanded = false;
	private boolean _initialized;
	private ExpandableComposite _mainItemComposite;
	private ImageHyperlink _openDowcumentButton;
	private final Composite _parent;
	private ImageHyperlink _performButton;
	private Font _regularFont;
	private boolean _skipped;
	private UpgradeTaskStep _step;
	private Composite _titleComposite;
	private TaskStepsViewer _viewer;

}