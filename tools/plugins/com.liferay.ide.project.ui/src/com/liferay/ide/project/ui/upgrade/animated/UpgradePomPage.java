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

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.ProjectCore;
import com.liferay.ide.project.core.upgrade.ILiferayLegacyProjectUpdater;
import com.liferay.ide.project.core.util.ProjectUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.File;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ColorRegistry;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider;
import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;

/**
 * @author Andy Wu
 * @author Simon Jiang
 */
public class UpgradePomPage extends Page {

	public UpgradePomPage(Composite parent, int style, LiferayUpgradeDataModel dataModel) {
		super(parent, style, dataModel, upgradePomPageId, true);

		GridLayout layout = new GridLayout(2, false);

		layout.marginHeight = 0;
		layout.marginWidth = 0;

		setLayout(layout);

		final GridData descData = new GridData(GridData.FILL_BOTH);

		descData.grabExcessVerticalSpace = true;
		descData.grabExcessHorizontalSpace = true;

		setLayoutData(descData);

		_fTableViewer = CheckboxTableViewer.newCheckList(this, SWT.BORDER);

		_fTableViewer.setContentProvider(new ArrayContentProvider());
		_fTableViewer.setLabelProvider(new DelegatingStyledCellLabelProvider(new ProjectLabelProvider()));

		_fTableViewer.addDoubleClickListener(
			new IDoubleClickListener() {

				@Override
				public void doubleClick(DoubleClickEvent event) {
					_handleCompare((IStructuredSelection)event.getSelection());
				}

			});

		final Table table = _fTableViewer.getTable();

		final GridData tableData = new GridData(GridData.FILL_BOTH);

		tableData.grabExcessVerticalSpace = true;
		tableData.grabExcessHorizontalSpace = true;
		tableData.horizontalAlignment = SWT.FILL;
		table.setLayoutData(tableData);

		Composite buttonContainer = new Composite(this, SWT.NONE);

		buttonContainer.setLayout(new GridLayout(1, false));
		buttonContainer.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));

		final Button selectAllButton = new Button(buttonContainer, SWT.NONE);

		selectAllButton.setText("Select All");
		selectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		selectAllButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					int i = 0;

					Object object = _fTableViewer.getElementAt(i++);

					while (object != null) {
						UpgradePomElement element = (UpgradePomElement)object;

						if (!element.finished) {
							_fTableViewer.setChecked(element, true);
						}

						object = _fTableViewer.getElementAt(i++);
					}
				}

			});

		final Button disSelectAllButton = new Button(buttonContainer, SWT.NONE);

		disSelectAllButton.setText("Deselect All");
		disSelectAllButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false, 1, 1));
		disSelectAllButton.addSelectionListener(
			new SelectionAdapter() {

				@Override
				public void widgetSelected(SelectionEvent e) {
					_fTableViewer.setAllChecked(false);
				}

			});

		_upgradeButton = new Button(buttonContainer, SWT.NONE);

		_upgradeButton.setText("Upgrade Selected");
		_upgradeButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false, false));
		_upgradeButton.addListener(
			SWT.Selection,
			new Listener() {

				@Override
				public void handleEvent(Event event) {
					_handleUpgradeEvent();
				}

			});

		_fTableViewer.addCheckStateListener(
			new ICheckStateListener() {

				@Override
				public void checkStateChanged(CheckStateChangedEvent event) {
					UpgradePomElement element = (UpgradePomElement)event.getElement();

					if (element.finished && event.getChecked()) {
						_fTableViewer.setChecked(element, false);
					}
				}

			});
	}

	public void createSpecialDescriptor(Composite parent, int style) {
		final StringBuilder descriptors = new StringBuilder(
			"This step will guide you to upgrade maven pom.xml files for the following aspects.\n");

		descriptors.append("  1. Convert and add dependencies for 7.x.\n");
		descriptors.append("  2. Remove the legacy 6.2 plugins.\n");
		descriptors.append("  3. Add 7.x maven plugins accroding to project type:\n");
		descriptors.append("  com.liferay.css.builder -> portlet, com.liferay.portal.tools.theme.builder -> theme,");
		descriptors.append("com.liferay.portal.tools.service.builder -> service-builder\n");
		descriptors.append(
			"Double clicking a file will bring up the compare editor of the original file and the upgraded file.");

		String url = "";

		Link link = SWTUtil.createHyperLink(this, style, descriptors.toString(), 1, url);

		link.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false, 2, 1));
	}

	@Override
	public String getPageTitle() {
		return "Upgrade POM Files";
	}

	@Override
	public void onSelectionChanged(int targetSelection) {
		Page selectedPage = UpgradeView.getPage(targetSelection);

		String selectedPageId = selectedPage.getPageId();

		if (!selectedPageId.equals(getPageId())) {
			return;
		}

		_handleFindEvent();
	}

	public class UpgradePomElement {

		public UpgradePomElement(IProject project, boolean fixed) {
			this.project = project;
			finished = fixed;
		}

		public boolean finished;
		public IProject project;

	}

	private List<UpgradePomElement> _getSelectedElements() {
		Object[] selectedElements = _fTableViewer.getCheckedElements();

		if (selectedElements == null) {
			return Collections.emptyList();
		}

		List<UpgradePomElement> upgradePomElements = new ArrayList<>();

		for (Object element : selectedElements) {
			if (element instanceof UpgradePomElement) {
				UpgradePomElement ele = (UpgradePomElement)element;

				upgradePomElements.add(ele);
			}
		}

		return upgradePomElements;
	}

	private ILiferayLegacyProjectUpdater _getUpdater() {
		if (_updater == null) {
			ProjectCore projectCore = ProjectCore.getDefault();

			_updater = projectCore.getLiferayLegacyProjectUpdater();
		}

		return _updater;
	}

	private void _handleCompare(IStructuredSelection selection) {
		UpgradePomElement element = (UpgradePomElement)selection.getFirstElement();

		IProject project = element.project;

		if (project.exists()) {
			IPath stataLocation = projectUI.getStateLocation();

			IPath tmpDirPath = stataLocation.append("tmp");

			File tmpDir = tmpDirPath.toFile();

			tmpDir.mkdirs();

			File tempPomFile = new File(tmpDir, "pom.xml");

			_getUpdater().upgradePomFile(project, tempPomFile);

			IFile pomfile = project.getFile("pom.xml");

			final LiferayUpgradeCompare lifeayDescriptorUpgradeCompre = new LiferayUpgradeCompare(
				pomfile.getLocation(), tmpDirPath.append("pom.xml"), "pom.xml");

			lifeayDescriptorUpgradeCompre.openCompareEditor();
		}
		else {
			MessageDialog.openInformation(getShell(), "Confirm", "project " + project.getName() + " does not exist");
		}
	}

	private void _handleFindEvent() {
		IProject[] projectArrys = CoreUtil.getAllProjects();

		List<UpgradePomElement> upgradePomElements = new ArrayList<>();

		for (IProject project : projectArrys) {
			if (ProjectUtil.isMavenProject(project) && _getUpdater().isNeedUpgrade(project)) {
				upgradePomElements.add(new UpgradePomElement(project, false));
			}
		}

		UIUtil.async(
			new Runnable() {

				@Override
				public void run() {
					String message = "ok";

					_upgradePomElementsArray = upgradePomElements.toArray(new UpgradePomElement[0]);

					_fTableViewer.setInput(_upgradePomElementsArray);

					if (_upgradePomElementsArray.length < 1) {
						message = "No pom file needs to be upgraded";
					}

					PageValidateEvent pe = new PageValidateEvent();

					pe.setMessage(message);
					pe.setType(PageValidateEvent.warning);

					triggerValidationEvent(pe);
				}

			});
	}

	private void _handleUpgradeEvent() {
		try {
			List<UpgradePomElement> upgradePomElements = _getSelectedElements();

			for (UpgradePomElement element : upgradePomElements) {
				_getUpdater().upgradePomFile(element.project, null);
				element.finished = true;
			}

			_fTableViewer.setInput(_upgradePomElementsArray);
			_fTableViewer.setAllChecked(false);
		}
		catch (Exception e) {
			ProjectUI.logError(e);
		}
	}

	private CheckboxTableViewer _fTableViewer;
	private ILiferayLegacyProjectUpdater _updater;
	private Button _upgradeButton;
	private UpgradePomElement[] _upgradePomElementsArray = null;

	private class ProjectLabelProvider extends LabelProvider implements IStyledLabelProvider, IColorProvider {

		@Override
		public Color getBackground(Object element) {
			return null;
		}

		@Override
		public Color getForeground(Object element) {
			Display display = Display.getCurrent();

			if (element instanceof UpgradePomElement) {
				UpgradePomElement ele = (UpgradePomElement)element;

				if (ele.finished) {
					return display.getSystemColor(SWT.COLOR_BLUE);
				}
			}

			return display.getSystemColor(SWT.COLOR_BLACK);
		}

		@Override
		public Image getImage(Object element) {
			ProjectUI projectUI = ProjectUI.getDefault();

			return projectUI.getImage("pom_file.gif");
		}

		@Override
		public StyledString getStyledText(Object element) {
			UpgradePomElement upgradePomElement = (UpgradePomElement)element;

			String projectName = upgradePomElement.project.getName();

			StringBuilder text = new StringBuilder("pom.xml");

			text.append(" (");
			text.append(projectName);
			text.append(")");

			StyledString retVal = new StyledString();

			ColorRegistry colorReg = JFaceResources.getColorRegistry();

			String upgradePomFrontColor = "UPGRADE_POM_FRONT_COLOR";

			Color frontColor = null;

			if (!colorReg.hasValueFor(upgradePomFrontColor)) {
				Display display = Display.getCurrent();

				frontColor = display.getSystemColor(SWT.COLOR_BLUE);

				colorReg.put(upgradePomFrontColor, frontColor.getRGB());
			}
			else {
				frontColor = colorReg.get(upgradePomFrontColor);
			}

			if (upgradePomElement.finished) {
				text.append("( Finished )");

				retVal.append(text.toString(), StyledString.createColorRegistryStyler(upgradePomFrontColor, null));
			}
			else {
				retVal.append(text.toString());
			}

			return retVal;
		}

	}

}