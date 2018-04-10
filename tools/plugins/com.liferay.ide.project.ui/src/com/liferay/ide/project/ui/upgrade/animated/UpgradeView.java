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

import com.liferay.ide.project.core.upgrade.BreakingChangeSelectedProject;
import com.liferay.ide.project.core.upgrade.MigrationProblemsContainer;
import com.liferay.ide.project.core.upgrade.UpgradeAssistantSettingsUtil;
import com.liferay.ide.project.ui.ProjectUI;
import com.liferay.ide.ui.util.SWTUtil;
import com.liferay.ide.ui.util.UIUtil;

import java.io.IOException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.Property;
import org.eclipse.sapphire.ValuePropertyContentEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.custom.StackLayout;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.osgi.framework.Bundle;

/**
 * @author Andy Wu
 * @author Simon Jiang
 * @author Joye Luo
 * @author Lovett Li
 */
public class UpgradeView extends ViewPart implements SelectionChangedListener {

	public static final String ID = "com.liferay.ide.project.ui.upgradeView";

	public static final Bundle bundle = ProjectUI.getDefault().getBundle();

	public static void addPage(String pageid) {
		Page targetPage = null;

		for (Page page : _staticPageList) {
			if (page.getPageId().equals(pageid)) {
				targetPage = page;

				break;
			}
		}

		if (targetPage != null) {
			_currentPageList.add(targetPage);
		}
	}

	public static Page getPage(int i) {
		if ((i < 0) || (i > (_pages.length - 1))) {
			return null;
		}
		else {
			return _pages[i];
		}
	}

	@SuppressWarnings("unchecked")
	public static <T> T getPage(String pageId, Class<T> clazz) {
		for (Page page : _pages) {
			if (page.getPageId().equals(pageId)) {
				return (T)page;
			}
		}

		return null;
	}

	public static int getPageNumber() {
		return _pages.length;
	}

	public static void resetPages() {
		_currentPageList.clear();

		addPage(Page.welcomePageId);
		addPage(Page.initConfigureProjectPageId);

		boolean hasMavenProject = _dataModel.getHasMavenProject().content();
		boolean hasPortlet = _dataModel.getHasPortlet().content();
		boolean hasServiceBuilder = _dataModel.getHasServiceBuilder().content();
		boolean hasHook = _dataModel.getHasHook().content();
		boolean hasLayout = _dataModel.getHasLayout().content();
		/*
		 * boolean hasTheme = dataModel.getHasTheme().content(); boolean hasExt =
		 * dataModel.getHasExt().content(); boolean hasWorkspace =
		 * dataModel.getConvertLiferayWorkspace().content();
		 */
		if (hasMavenProject) {
			addPage(Page.upgradePomPageId);
		}

		if (hasPortlet || hasHook || hasServiceBuilder || hasMavenProject) {
			addPage(Page.findbreackingchangesPageId);
		}

		if (hasPortlet || hasHook || hasServiceBuilder || hasLayout) {
			addPage(Page.descriptorsPageId);
		}

		if (hasServiceBuilder) {
			addPage(Page.buildservicePageId);
		}

		if (hasLayout) {
			addPage(Page.layouttemplatePageId);
		}

		if (hasHook) {
			addPage(Page.customjspPageId);
		}

		/*
		 * if( hasExt || hasTheme || hasWorkspace ) { addPage( Page.EXTANDTHEME_PAGE_ID
		 * ); }
		 */
		if (hasPortlet || hasHook || hasServiceBuilder || hasLayout || hasMavenProject) {
			addPage(Page.buildPageId);
			addPage(Page.summaryPageId);
		}

		_pages = _currentPageList.toArray(new Page[0]);

		for (Page page : _pages) {
			String pageActionName = UpgradeSettingsUtil.getProperty(page.getPageId());

			if (pageActionName != null) {
				PageAction pageAction = page.getSelectedAction(pageActionName);

				page.setSelectedAction(pageAction);
			}
		}
	}

	public static void resumePages() {
		_dataModel.setHasMavenProject(true);
		_dataModel.setHasExt(true);
		_dataModel.setHasHook(true);
		_dataModel.setHasLayout(true);
		_dataModel.setHasPortlet(true);
		_dataModel.setHasServiceBuilder(true);
		_dataModel.setHasTheme(true);
		_dataModel.setHasWeb(true);
		_dataModel.setConvertLiferayWorkspace(false);

		_currentPageList.clear();
		_currentPageList.addAll(_staticPageList);
		_pages = _currentPageList.toArray(new Page[0]);
	}

	public static void showAllPages() {
		resumePages();

		PageNavigateEvent event = new PageNavigateEvent();

		event.setTargetPage(0);

		StackLayout stackLayout = (StackLayout)_pagesSwitchControler.getLayout();

		Page currentPage = (Page)stackLayout.topControl;

		for (PageNavigatorListener listener : currentPage.naviListeners) {
			listener.onPageNavigate(event);
		}

		InitConfigureProjectPage importPage = getPage(Page.initConfigureProjectPageId, InitConfigureProjectPage.class);

		importPage.setNextPage(true);

		_dataModel.setImportFinished(true);
	}

	public UpgradeView() {
		_dataModel = _createUpgradeModel();

		_dataModel.attach(new LiferayUpgradeStoreListener(), "*");

		UpgradeSettingsUtil.init(_dataModel);
	}

	@Override
	public void createPartControl(Composite parent) {
		ScrolledComposite scrolledComposite = new ScrolledComposite(
			parent, SWT.DOUBLE_BUFFERED | SWT.H_SCROLL | SWT.V_SCROLL);

		scrolledComposite.setExpandHorizontal(true);
		scrolledComposite.setExpandVertical(true);

		Composite container = SWTUtil.createComposite(scrolledComposite, 1, 0, GridData.FILL_BOTH);

		GridLayout gridLayout = new GridLayout(1, false);

		gridLayout.marginWidth = 0;
		gridLayout.marginTop = 0;
		gridLayout.marginHeight = 0;
		container.setLayout(gridLayout);

		Composite composite = new Composite(container, SWT.NONE);

		composite.setLayout(new GridLayout(1, true));

		GridData grData = new GridData(GridData.FILL_BOTH);

		grData.grabExcessVerticalSpace = true;
		grData.grabExcessHorizontalSpace = true;
		composite.setLayoutData(grData);

		final GearControl gear = new GearControl(composite, SWT.NONE);

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 400;
		gridData.heightHint = 150;

		gear.setLayoutData(gridData);

		gear.setBackground(parent.getBackground());

		StackLayout stackLayout = new StackLayout();

		_pagesSwitchControler = new Composite(composite, SWT.BORDER);

		_pagesSwitchControler.setLayout(stackLayout);

		GridData containerData = new GridData(GridData.FILL_BOTH);

		containerData.grabExcessHorizontalSpace = true;
		containerData.grabExcessVerticalSpace = true;
		containerData.grabExcessHorizontalSpace = true;
		_pagesSwitchControler.setLayoutData(containerData);

		int pageIndex = 0;

		Page welcomePage = new WelcomePage(_pagesSwitchControler, SWT.NONE, _dataModel);

		welcomePage.setIndex(pageIndex++);
		welcomePage.setTitle("Welcome");
		welcomePage.setBackPage(false);
		welcomePage.addPageNavigateListener(gear);

		Page initConfigureProjectPage = new InitConfigureProjectPage(_pagesSwitchControler, SWT.NONE, _dataModel);

		initConfigureProjectPage.setIndex(pageIndex++);
		initConfigureProjectPage.setTitle("Select project(s) to upgrade");
		initConfigureProjectPage.addPageNavigateListener(gear);
		initConfigureProjectPage.addPageValidationListener(gear);
		initConfigureProjectPage.setNextPage(false);

		Page upgradePomPage = new UpgradePomPage(_pagesSwitchControler, SWT.NONE, _dataModel);

		upgradePomPage.setIndex(pageIndex++);
		upgradePomPage.setTitle("Upgrade POM Files");
		upgradePomPage.addPageNavigateListener(gear);
		upgradePomPage.addPageValidationListener(gear);

		Page findBreakingChangesPage = new FindBreakingChangesPage(_pagesSwitchControler, SWT.NONE, _dataModel);

		findBreakingChangesPage.setIndex(pageIndex++);
		findBreakingChangesPage.setTitle("Find Breaking Changes");
		findBreakingChangesPage.addPageNavigateListener(gear);

		Page descriptorsPage = new DescriptorsPage(_pagesSwitchControler, SWT.NONE, _dataModel);

		descriptorsPage.setIndex(pageIndex++);
		descriptorsPage.setTitle("Update Descriptor Files");
		descriptorsPage.addPageNavigateListener(gear);
		descriptorsPage.addPageValidationListener(gear);

		Page buildServicePage = new BuildServicePage(_pagesSwitchControler, SWT.NONE, _dataModel);

		buildServicePage.setIndex(pageIndex++);
		buildServicePage.setTitle("Build Services");
		buildServicePage.addPageNavigateListener(gear);

		Page layoutTemplatePage = new LayoutTemplatePage(_pagesSwitchControler, SWT.NONE, _dataModel);

		layoutTemplatePage.setIndex(pageIndex++);
		layoutTemplatePage.setTitle("Layout Templates");
		layoutTemplatePage.addPageNavigateListener(gear);
		layoutTemplatePage.addPageValidationListener(gear);

		Page customJspPage = new CustomJspPage(_pagesSwitchControler, SWT.NONE, _dataModel);

		customJspPage.setIndex(pageIndex++);
		customJspPage.setTitle("Custom Jsp");
		customJspPage.addPageNavigateListener(gear);
		customJspPage.addPageValidationListener(gear);

		// Page extAndThemePage = new ExtAndThemePage( pagesSwitchControler, SWT.NONE,
		// dataModel );
		// extAndThemePage.setIndex( 7 );
		// extAndThemePage.setTitle( "Ext and Theme" );

		Page buildPage = new BuildPage(_pagesSwitchControler, SWT.NONE, _dataModel);

		buildPage.setIndex(pageIndex++);
		buildPage.setTitle("Build");
		buildPage.addPageNavigateListener(gear);

		Page summaryPage = new SummaryPage(_pagesSwitchControler, SWT.NONE, _dataModel);

		summaryPage.setIndex(pageIndex++);
		summaryPage.setTitle("Summary");
		summaryPage.setNextPage(false);
		summaryPage.addPageNavigateListener(gear);

		_staticPageList.clear();

		_staticPageList.add(welcomePage);
		_staticPageList.add(initConfigureProjectPage);
		_staticPageList.add(upgradePomPage);
		_staticPageList.add(findBreakingChangesPage);
		_staticPageList.add(descriptorsPage);
		_staticPageList.add(buildServicePage);
		_staticPageList.add(layoutTemplatePage);
		_staticPageList.add(customJspPage);

		// staticPageList.add( extAndThemePage );

		_staticPageList.add(buildPage);
		_staticPageList.add(summaryPage);

		resetPages();

		final NavigatorControl navigator = new NavigatorControl(composite, SWT.NONE);

		navigator.addPageNavigateListener(gear);
		navigator.addPageActionListener(gear);
		navigator.setBackground(parent.getBackground());

		_staticPageList.stream().forEach(navigator::addPageActionListener);

		gear.addSelectionChangedListener(navigator);

		gear.addSelectionChangedListener(this);
		gear.addSelectionChangedListener(initConfigureProjectPage);
		gear.addSelectionChangedListener(descriptorsPage);
		gear.addSelectionChangedListener(upgradePomPage);
		gear.addSelectionChangedListener(layoutTemplatePage);
		gear.addSelectionChangedListener(summaryPage);

		GridData navData = new GridData(GridData.FILL_HORIZONTAL);

		navData.grabExcessHorizontalSpace = true;

		navigator.setLayoutData(navData);

		scrolledComposite.setContent(container);

		// scrolledComposite.setMinSize(container.computeSize(SWT.DEFAULT, 670));

		setSelectPage(0);

		parent.addDisposeListener(
			new DisposeListener() {

				@Override
				public void widgetDisposed(DisposeEvent e) {
					int pageNum = getPageNumber();

					for (int i = 0; i < pageNum; i++) {
						Page page = UpgradeView.getPage(i);

						String pageId = page.getPageId();
						PageAction pageAction = page.getSelectedAction();

						if (pageAction != null) {
							UpgradeSettingsUtil.storeProperty(pageId, pageAction.getPageActionName());
						}
					}
				}

			});
		IActionBars actionBars = getViewSite().getActionBars();

		final IToolBarManager mgr = actionBars.getToolBarManager();

		final IAction restart =
			new Action("Restart Upgrade", ImageDescriptor.createFromURL(bundle.getEntry("icons/e16/restart.gif"))) {

				@Override
				public void run() {
					_restartUpgradeTool();
				}

			};

		final IAction showAllPages =
			new Action("Show All Pages", ImageDescriptor.createFromURL(bundle.getEntry("icons/e16/showall.gif"))) {

				@Override
				public void run() {
					StringBuilder desriptors = new StringBuilder(
						"If you fail to import projects, you can skip step 2 by ");

					desriptors.append("doing the following steps:\n");
					desriptors.append("   1.upgrade SDK 6.2 to SDK 7.0 manually\n");
					desriptors.append("      or use blade cli to create a Liferay workspace for your SDK\n");
					desriptors.append("   2.import projects you want to upgrade into Eclipse workspace\n");
					desriptors.append("   3.click \"yes\" to show all the steps");

					Boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
						UIUtil.getActiveShell(), "Show All Pages", desriptors.toString());

					if (openNewLiferayProjectWizard) {
						showAllPages();
					}
				}

			};

		mgr.add(restart);
		mgr.add(showAllPages);
	}

	@Override
	public void onSelectionChanged(int targetSelection) {
		StackLayout stackLayout = (StackLayout)_pagesSwitchControler.getLayout();

		stackLayout.topControl = _pages[targetSelection];

		_pagesSwitchControler.layout();
	}

	@Override
	public void setFocus() {
	}

	public void setSelectPage(int i) {
		StackLayout stackLayout = (StackLayout)_pagesSwitchControler.getLayout();

		stackLayout.topControl = _pages[i];

		_pagesSwitchControler.layout();
	}

	public interface PageActionListener {

		public void onPageAction(PageActionEvent event);

	}

	public interface PageNavigatorListener {

		public void onPageNavigate(PageNavigateEvent event);

	}

	public interface PageValidationListener {

		public void onValidation(PageValidateEvent event);

	}

	private LiferayUpgradeDataModel _createUpgradeModel() {
		return LiferayUpgradeDataModel.TYPE.instantiate();
	}

	private void _restartUpgradeTool() {
		boolean openNewLiferayProjectWizard = MessageDialog.openQuestion(
			UIUtil.getActiveShell(), "Restart code upgrade?",
			"All previous configuration files will be deleted. Do you want to restart the code upgrade tool?");

		if (openNewLiferayProjectWizard) {
			CustomJspConverter.clearConvertResults();

			try {
				MigrationProblemsContainer container = UpgradeAssistantSettingsUtil.getObjectFromStore(
					MigrationProblemsContainer.class);

				if (container != null) {
					UpgradeAssistantSettingsUtil.setObjectToStore(MigrationProblemsContainer.class, null);
				}

				BreakingChangeSelectedProject selectedProject = UpgradeAssistantSettingsUtil.getObjectFromStore(
					BreakingChangeSelectedProject.class);

				if (selectedProject != null) {
					UpgradeAssistantSettingsUtil.setObjectToStore(BreakingChangeSelectedProject.class, null);
				}
			}
			catch (IOException ioe) {
				ProjectUI.logError(ioe);
			}

			IWorkbenchWindow activeWorkbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();

			IWorkbenchPage page = activeWorkbenchWindow.getActivePage();

			UpgradeView view = (UpgradeView)UIUtil.findView(UpgradeView.ID);

			CustomJspConverter.clearConvertResults();

			page.hideView(view);

			UpgradeSettingsUtil.resetStoreProperties();

			try {
				page.showView(UpgradeView.ID);
			}
			catch (PartInitException pie) {
				pie.printStackTrace();
			}
		}
	}

	private static List<Page> _currentPageList = new ArrayList<>();
	private static LiferayUpgradeDataModel _dataModel;
	private static Page[] _pages = null;
	private static Composite _pagesSwitchControler = null;
	private static List<Page> _staticPageList = new ArrayList<>();

	private class LiferayUpgradeStoreListener extends Listener {

		@Override
		public void handle(Event event) {
			if (event instanceof ValuePropertyContentEvent) {
				ValuePropertyContentEvent propertyEvetn = (ValuePropertyContentEvent)event;

				final Property property = propertyEvetn.property();

				UpgradeSettingsUtil.storeProperty(property.name(), property.toString());
			}
		}

	}

}