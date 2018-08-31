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

import com.liferay.ide.project.ui.upgrade.animated.UpgradeView.PageNavigatorListener;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Table;

/**
 * @author Joye Luo
 */
public class SummaryPage extends Page implements SelectionChangedListener {

	public SummaryPage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, summaryPageId, false);

		Composite container = new Composite(this, SWT.NONE);

		container.setLayout(new GridLayout(2, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		_tableViewer = new TableViewer(container);

		_tableViewer.setContentProvider(new TableViewContentProvider());
		_tableViewer.setLabelProvider(new TableViewLabelProvider());

		Control control = _tableViewer.getControl();

		control.setBackground(getDisplay().getSystemColor(SWT.COLOR_WIDGET_BACKGROUND));

		_tableViewer.addSelectionChangedListener(
			new ISelectionChangedListener() {

				@Override
				public void selectionChanged(SelectionChangedEvent event) {
					final IStructuredSelection selection = (IStructuredSelection)event.getSelection();

					if (!selection.isEmpty()) {
						if (selection.getFirstElement() instanceof TableViewElement) {
							final TableViewElement tableViewElement = (TableViewElement)selection.getFirstElement();

							final int pageIndex = tableViewElement._pageIndex;

							PageNavigateEvent navEvent = new PageNavigateEvent();

							navEvent.setTargetPage(pageIndex);

							for (PageNavigatorListener listener : naviListeners) {
								listener.onPageNavigate(navEvent);
							}
						}
					}
				}

			});

		final Table table = _tableViewer.getTable();

		final GridData tableData = new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1);

		tableData.heightHint = 175;

		table.setLayoutData(tableData);

		table.setLinesVisible(false);

		_createImages();
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final StringBuilder descriptors = new StringBuilder("Upgrade results are summarized in the following table.\n");

		descriptors.append("If there are still some failed or incomplete steps, you can go back to finish them.\n");
		descriptors.append(
			"If all the steps are well-done, congratulations! You have finished the whole upgrade process.\n");
		descriptors.append("Now you can try to deploy your projects to the Liferay Portal instance. ");
		descriptors.append("For more upgrade information, please see <a>From Liferay 6 to Liferay 7</a>.");

		String url = "https://dev.liferay.com/develop/tutorials/-/knowledge_base/7-0/from-liferay-6-to-liferay-7";

		Link link = SWTUtil.createHyperLink(this, style, descriptors.toString(), 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	@Override
	public int getGridLayoutCount() {
		return 2;
	}

	@Override
	public boolean getGridLayoutEqualWidth() {
		return false;
	}

	@Override
	public String getPageTitle() {
		return "Summary";
	}

	@Override
	public void onSelectionChanged(int targetSelection) {
		_setInput();
	}

	private void _createImages() {
		ImageDescriptor imageDescriptor = ImageDescriptor.createFromURL(bundle.getEntry("/images/question.png"));

		_imageQuestion = imageDescriptor.createImage();
	}

	private void _setInput() {
		List<TableViewElement> tableViewElementList = new ArrayList<>();
		TableViewElement[] tableViewElements;
		int pageNum = UpgradeView.getPageNumber();

		for (int i = 1; i < pageNum - 1; i++) {
			Page page = UpgradeView.getPage(i);

			String pageTitle = page.getPageTitle();

			int pageIndex = i;

			if (pageTitle.equals("Ext and Theme Project")) {
				continue;
			}

			PageAction pageAction = page.getSelectedAction();

			Image statusImage = null;

			if (pageAction == null) {
				statusImage = _imageQuestion;
			}
			else {
				statusImage = pageAction.getBageImage();
			}

			TableViewElement tableViewElement = new TableViewElement(pageTitle, statusImage, pageIndex);

			tableViewElementList.add(tableViewElement);
		}

		tableViewElements = tableViewElementList.toArray(new TableViewElement[tableViewElementList.size()]);

		_tableViewer.setInput(tableViewElements);
	}

	private Image _imageQuestion;
	private TableViewer _tableViewer;

	private class TableViewContentProvider implements IStructuredContentProvider {

		@Override
		public void dispose() {
		}

		@Override
		public Object[] getElements(Object inputElement) {
			if (inputElement instanceof TableViewElement[]) {
				return (TableViewElement[])inputElement;
			}

			return new Object[] {inputElement};
		}

		@Override
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}

	}

	private class TableViewElement {

		public TableViewElement(String pageTitle, Image image, int pageIndex) {
			_pageTitle = pageTitle;
			_image = image;
			_pageIndex = pageIndex;
		}

		private Image _image;
		private int _pageIndex;
		private String _pageTitle;

	}

	private class TableViewLabelProvider extends LabelProvider {

		@Override
		public Image getImage(Object element) {
			TableViewElement tableViewElement = (TableViewElement)element;

			return tableViewElement._image;
		}

		@Override
		public String getText(Object element) {
			TableViewElement tableViewElement = (TableViewElement)element;

			return tableViewElement._pageTitle;
		}

	}

}