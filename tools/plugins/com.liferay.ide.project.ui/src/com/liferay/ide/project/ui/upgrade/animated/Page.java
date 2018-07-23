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

package com.liferay.ide.project.ui.upgrade.animated;

import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageActionListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageValidationListener;
import com.liferay.ide.ui.util.SWTUtil;

import java.net.URL;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.layout.GridDataFactory;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import org.osgi.framework.Bundle;

/**
 * @author Simon Jiang
 * @author Terry Jia
 */
public abstract class Page extends Composite implements PageActionListener, SelectionChangedListener {

	public static final int DEFAULT_PAGE_WIDTH = 500;

	public static String buildPageId = "build";
	public static String buildservicePageId = "buildservice";
	public static String compilePageId = "compile";
	public static String customjspPageId = "customjsp";
	public static String descriptorsPageId = "descriptors";
	public static String extandthemePageId = "extandtheme";
	public static String findbreackingchangesPageId = "findbreackingchanges";
	public static String initConfigureProjectPageId = "initconfigureproject";
	public static String layouttemplatePageId = "layouttemplate";
	public static String summaryPageId = "summary";
	public static String upgradePomPageId = "upgradepom";
	public static String welcomePageId = "welcome";

	public static Control createHorizontalSpacer(Composite comp, int hSpan) {
		Label l = new Label(comp, SWT.NONE);
		GridData gd = new GridData(GridData.FILL_HORIZONTAL);

		gd.horizontalSpan = hSpan;

		l.setLayoutData(gd);

		return l;
	}

	public static Control createSeparator(Composite parent, int hspan) {
		Label label = new Label(parent, SWT.SEPARATOR | SWT.HORIZONTAL);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, hspan, 1);

		label.setLayoutData(gd);

		return label;
	}

	public Page(
		Composite parent, int style, LiferayUpgradeDataModel dataModel, String pageId, boolean hasFinishAndSkipAction) {

		super(parent, style);

		this.dataModel = dataModel;

		setLayout(new GridLayout(getGridLayoutCount(), getGridLayoutEqualWidth()));

		Label title = SWTUtil.createLabel(this, getPageTitle(), getGridLayoutCount());

		title.setFont(new Font(null, "Times New Roman", 14, SWT.NORMAL));

		createSpecialDescriptor(this, style);

		setPageId(pageId);

		if (hasFinishAndSkipAction) {
			setActions(new PageAction[] {_pageFinishAction, _pageSkipAction});
		}
	}

	public void addPageNavigateListener(PageNavigatorListener listener) {
		this.naviListeners.add(listener);
	}

	public void addPageValidationListener(PageValidationListener listener) {
		this.pageValidationListeners.add(listener);
	}

	public void createSpecialDescriptor(Composite parent, int style) {
	}

	@Override
	public boolean equals(Object obj) {
		Page comp = (Page)obj;

		if (_pageId == comp._pageId) {
			return true;
		}

		return false;
	}

	public PageAction[] getActions() {
		return actions;
	}

	public LiferayUpgradeDataModel getDataModel() {
		return dataModel;
	}

	public int getGridLayoutCount() {
		return 1;
	}

	public boolean getGridLayoutEqualWidth() {
		return true;
	}

	public final int getIndex() {
		return _index;
	}

	public String getPageId() {
		return _pageId;
	}

	public abstract String getPageTitle();

	public PageAction getSelectedAction() {
		return _selectedAction;
	}

	public PageAction getSelectedAction(String actionName) {
		if (actionName.equals("PageFinishAction")) {
			return _pageFinishAction;
		}

		if (actionName.equals("PageSkipAction")) {
			return _pageSkipAction;
		}

		return _selectedAction;
	}

	public String getTitle() {
		return _title;
	}

	@Override
	public void onPageAction(PageActionEvent event) {
	}

	@Override
	public void onSelectionChanged(int targetSelection) {
	}

	public final void setActions(PageAction[] actions) {
		this.actions = actions;
	}

	public void setIndex(int index) {
		_index = index;
	}

	public void setPageId(String pageId) {
		_pageId = pageId;
	}

	public void setSelectedAction(PageAction selectedAction) {
		_selectedAction = selectedAction;
	}

	public void setTitle(String title) {
		_title = title;
	}

	protected Label createLabel(Composite composite, String text) {
		Label label = new Label(composite, SWT.NONE);

		label.setText(text);

		GridDataFactory.generate(label, 2, 1);

		return label;
	}

	protected Text createTextField(Composite composite, int style) {
		Text text = new Text(composite, SWT.BORDER | style);

		text.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));

		return text;
	}

	protected boolean doNextOperation() {
		return true;
	}

	protected final Image loadImage(String name) {
		URL url = null;

		try {
			url = bundle.getEntry("images/" + name);
		}
		catch (Exception e) {
		}

		ImageDescriptor imagedesc = ImageDescriptor.createFromURL(url);

		Image image = imagedesc.createImage();

		return image;
	}

	protected void setBackPage(boolean back) {
		canBack = back;
	}

	protected void setNextPage(boolean next) {
		canNext = next;
	}

	protected boolean showBackPage() {
		return canBack;
	}

	protected boolean showNextPage() {
		return canNext;
	}

	protected void triggerValidationEvent(PageValidateEvent pageValidationEvent) {
		pageValidationEvent.setPageId(getPageId());

		for (PageValidationListener listener : pageValidationListeners) {
			listener.onValidation(pageValidationEvent);
		}
	}

	protected PageAction[] actions;
	protected Bundle bundle = ProjectUI.getDefault().getBundle();
	protected boolean canBack = true;
	protected boolean canNext = true;
	protected LiferayUpgradeDataModel dataModel;
	protected final List<PageNavigatorListener> naviListeners = Collections.synchronizedList(
		new ArrayList<PageNavigatorListener>());
	protected final List<PageValidationListener> pageValidationListeners = Collections.synchronizedList(
		new ArrayList<PageValidationListener>());
	protected ProjectUI projectUI = ProjectUI.getDefault();

	private int _index;
	private PageAction _pageFinishAction = new PageFinishAction();
	private String _pageId;
	private PageAction _pageSkipAction = new PageSkipAction();
	private PageAction _selectedAction;
	private String _title = "title";

}