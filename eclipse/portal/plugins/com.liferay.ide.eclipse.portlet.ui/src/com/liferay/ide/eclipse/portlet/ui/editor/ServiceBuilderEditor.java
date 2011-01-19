/*******************************************************************************
 * Copyright (c) 2000-2011 Liferay, Inc. All rights reserved.
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

package com.liferay.ide.eclipse.portlet.ui.editor;

import com.liferay.ide.eclipse.portlet.core.PortletCore;
import com.liferay.ide.eclipse.portlet.core.job.BuildServiceJob;
import com.liferay.ide.eclipse.portlet.core.job.BuildWSDDJob;
import com.liferay.ide.eclipse.portlet.core.servicebuilder.ServiceBuilderModel;
import com.liferay.ide.eclipse.portlet.ui.PortletUIPlugin;
import com.liferay.ide.eclipse.ui.toolbar.ToolBarButtonContribution;

import java.lang.reflect.Field;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.resource.CompositeImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.SharedHeaderFormEditor;
import org.eclipse.ui.forms.widgets.Form;
import org.eclipse.ui.internal.forms.widgets.BusyIndicator;
import org.eclipse.ui.internal.forms.widgets.FormHeading;
import org.eclipse.ui.internal.forms.widgets.TitleRegion;
import org.eclipse.wst.sse.ui.StructuredTextEditor;

/**
 * @author Greg Amerson
 */
@SuppressWarnings("restriction")
public class ServiceBuilderEditor extends SharedHeaderFormEditor {

	public static final int LEFT_TOOLBAR_HEADER_TOOLBAR_PADDING = 3;

	protected BusyIndicator busyLabel;

	/**
	 * The XML text editor.
	 */
	protected StructuredTextEditor editor;

	protected Image headerImage;

	protected int lastPageIndex;

	protected ToolBar leftToolBar;

	protected ToolBarManager leftToolBarManager;

	protected ServiceBuilderModel model;

	protected boolean noExtraPadding;

	protected Button submitButton;

	protected Label titleLabel;

	protected IToolBarManager toolBarManager;

	@Override
	public void doSave(IProgressMonitor monitor) {
		getEditor(0).doSave(monitor);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void doSaveAs() {
	}

	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(Class adapterClass) {
		Object adapter = super.getAdapter(adapterClass);

		if (adapter == null) {
			adapter = editor.getAdapter(adapterClass);
		}

		return adapter;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public IFileEditorInput getEditorInput() {
		return (IFileEditorInput) super.getEditorInput();
	}

	public ServiceBuilderModel getServiceModel() {
		if (model == null) {
			model = new ServiceBuilderModel(getEditorInput().getFile());
		}

		return model;
	}

	@Override
	public void init(IEditorSite site, IEditorInput editorInput)
		throws PartInitException {

		Assert.isLegal(editorInput instanceof IFileEditorInput, "Invalid Input: Must be IFileEditorInput");

		super.init(site, editorInput);

		setPartName(editorInput.getName());
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	public void updateHeaderToolBar() {
		if (isHeaderFormDisposed()) {
			return;
		}

		final Form form = getHeaderForm().getForm().getForm();

		toolBarManager = form.getToolBarManager();

		toolBarManager.removeAll();

		// toolBarManager.update(true);

		//		toolBarManager.add(new GroupMarker("repository")); //$NON-NLS-1$
		// for (IFormPage page : getPages()) {
		// if (page instanceof TaskFormPage) {
		// TaskFormPage taskEditorPage = (TaskFormPage) page;
		// taskEditorPage.fillToolBar(toolBarManager);
		// }
		// }

		ToolBarButtonContribution submitButtonContribution =
			new ToolBarButtonContribution("com.liferay.ide.eclipse.portlet.ui.toolbars.buildservices") { //$NON-NLS-1$

				@Override
				protected Control createButton(Composite composite) {
					submitButton = new Button(composite, SWT.FLAT);

					submitButton.setText("Build Services"); //$NON-NLS-1$
					submitButton.setImage(PortletUIPlugin.imageDescriptorFromPlugin(
						PortletUIPlugin.PLUGIN_ID, "/icons/e16/service.png").createImage());
					submitButton.setBackground(null);
					submitButton.addListener(SWT.Selection, new Listener() {

						public void handleEvent(Event e) {
							doBuildServices();
						}
					});

					return submitButton;
				}
			};

		submitButtonContribution.marginLeft = 10;

		toolBarManager.add(submitButtonContribution);

		ToolBarButtonContribution wsddButtonContribution =
			new ToolBarButtonContribution("com.liferay.ide.eclipse.portlet.ui.toolbars.buildwsdd") { //$NON-NLS-1$

				@Override
				protected Control createButton(Composite composite) {
					submitButton = new Button(composite, SWT.FLAT);

					submitButton.setText("Build WSDD "); //$NON-NLS-1$
					submitButton.setImage(PortletUIPlugin.imageDescriptorFromPlugin(
						PortletUIPlugin.PLUGIN_ID, "/icons/e16/service.png").createImage());
					submitButton.setBackground(null);
					submitButton.addListener(SWT.Selection, new Listener() {

						public void handleEvent(Event e) {
							doGenerateWSDD();
						}
					});

					return submitButton;
				}
			};

		wsddButtonContribution.marginLeft = 10;

		toolBarManager.add(wsddButtonContribution);
		toolBarManager.update(true);

		updateLeftHeaderToolBar();

		updateHeader();
	}

	private void addXMLEditorPage() {
		editor = new StructuredTextEditor();

		editor.setEditorPart(this);

		int index;

		try {
			index = addPage(editor, getEditorInput());

			setPageText(index, "XML");
		}
		catch (PartInitException e) {
			PortletUIPlugin.logError(e);
		}

	}

	private BusyIndicator getBusyLabel() {
		if (busyLabel != null) {
			return busyLabel;
		}

		try {
			FormHeading heading = (FormHeading) getHeaderForm().getForm().getForm().getHead();

			// ensure that busy label exists
			heading.setBusy(true);
			heading.setBusy(false);

			Field field = FormHeading.class.getDeclaredField("titleRegion"); //$NON-NLS-1$
			field.setAccessible(true);

			TitleRegion titleRegion = (TitleRegion) field.get(heading);

			for (Control child : titleRegion.getChildren()) {
				if (child instanceof BusyIndicator) {
					busyLabel = (BusyIndicator) child;
				}
			}

			if (busyLabel == null) {
				return null;
			}

			busyLabel.addControlListener(new ControlAdapter() {

				@Override
				public void controlMoved(ControlEvent e) {
					updateSizeAndLocations();
				}

			});

			// the busy label may get disposed if it has no image
			busyLabel.addDisposeListener(new DisposeListener() {

				public void widgetDisposed(DisposeEvent e) {
					busyLabel.setMenu(null);
					busyLabel = null;
				}

			});

			if (leftToolBar != null) {
				leftToolBar.moveAbove(busyLabel);
			}

			if (titleLabel != null) {
				titleLabel.moveAbove(busyLabel);
			}

			updateSizeAndLocations();

			return busyLabel;
		}
		catch (Exception e) {
			// if (!toolBarFailureLogged) {
			// StatusHandler.log(new Status(IStatus.ERROR,
			// TasksUiPlugin.ID_PLUGIN,
			//						"Failed to obtain busy label toolbar", e)); //$NON-NLS-1$
			// }
			busyLabel = null;
		}

		return busyLabel;
	}

	private boolean hasLeftToolBar() {
		return leftToolBar != null && leftToolBarManager != null;
		// && leftToolBarManager.getSize() > initialLeftToolbarSize;
	}

	private boolean isHeaderFormDisposed() {
		return getHeaderForm() == null || getHeaderForm().getForm() == null || getHeaderForm().getForm().isDisposed();
	}

	private void setHeaderImage(final Image image) {
		BusyIndicator busyLabel = getBusyLabel();

		if (busyLabel == null) {
			return;
		}

		final Point size = leftToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

		Point titleSize = titleLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

		size.x += titleSize.x + LEFT_TOOLBAR_HEADER_TOOLBAR_PADDING;

		size.y = Math.max(titleSize.y, size.y);

		// padding between toolbar and image, ensure image is at least one pixel
		// wide to avoid SWT error
		final int padding = (size.x > 0 && !noExtraPadding) ? 10 : 1;

		final Rectangle imageBounds = (image != null) ? image.getBounds() : new Rectangle(0, 0, 0, 0);

		int tempHeight = (image != null) ? Math.max(size.y + 1, imageBounds.height) : size.y + 1;

		// avoid extra padding due to margin added by TitleRegion.VMARGIN
		final int height = (tempHeight > imageBounds.height + 5) ? tempHeight - 5 : tempHeight;

		CompositeImageDescriptor descriptor = new CompositeImageDescriptor() {

			@Override
			protected void drawCompositeImage(int width, int height) {
				if (image != null) {
					drawImage(image.getImageData(), size.x + padding, (height - image.getBounds().height) / 2);
				}
			}

			@Override
			protected Point getSize() {
				return new Point(size.x + padding + imageBounds.width, height);
			}

		};

		Image newHeaderImage = descriptor.createImage();

		// directly set on busyLabel since getHeaderForm().getForm().setImage()
		// does not update
		// the image if a message is currently displayed
		busyLabel.setImage(newHeaderImage);

		if (headerImage != null) {
			headerImage.dispose();
		}

		headerImage = newHeaderImage;

		// avoid extra padding due to large title font
		// leftToolBar.getParent().setFont(JFaceResources.getDefaultFont());
		getHeaderForm().getForm().reflow(true);
	}

	private void updateHeader() {
		IEditorInput input = getEditorInput();

		updateHeaderImage();

		setTitleToolTip(input.getToolTipText());

		setPartName(input.getName());
	}

	private void updateHeaderImage() {
		if (hasLeftToolBar()) {
			setHeaderImage(null);
		}
		else {
			getHeaderForm().getForm().setImage(
				PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID, "/icons/e16/service.png").createImage());
		}
	}

	private void updateLeftHeaderToolBar() {
		leftToolBarManager.removeAll();

		//		leftToolBarManager.add(new Separator("activation")); //$NON-NLS-1$
		// leftToolBarManager.add(new
		// Separator(IWorkbenchActionConstants.MB_ADDITIONS));

		// initialLeftToolbarSize = leftToolBarManager.getSize();

		// leftToolBarManager.add(activateAction);

		// for (IFormPage page : getPages()) {
		// if (page instanceof AbstractTaskEditorPage) {
		// AbstractTaskEditorPage taskEditorPage = (AbstractTaskEditorPage)
		// page;
		// taskEditorPage.fillLeftHeaderToolBar(leftToolBarManager);
		// } else if (page instanceof TaskPlanningEditor) {
		// TaskPlanningEditor taskEditorPage = (TaskPlanningEditor) page;
		// taskEditorPage.fillLeftHeaderToolBar(leftToolBarManager);
		// }
		// }

		// add external contributions
		// menuService = (IMenuService)
		// getSite().getService(IMenuService.class);
		// if (menuService != null && leftToolBarManager instanceof
		// ContributionManager) {
		// TaskRepository outgoingNewRepository =
		// TasksUiUtil.getOutgoingNewTaskRepository(task);
		// TaskRepository taskRepository = (outgoingNewRepository != null) ?
		// outgoingNewRepository
		// : taskEditorInput.getTaskRepository();
		//			menuService.populateContributionManager(leftToolBarManager, "toolbar:" + ID_LEFT_TOOLBAR_HEADER + "." //$NON-NLS-1$ //$NON-NLS-2$
		// + taskRepository.getConnectorKind());
		// }

		leftToolBarManager.update(true);

		if (hasLeftToolBar()) {
			// XXX work around a bug in Gtk that causes the toolbar size to be
			// incorrect if no
			// tool bar buttons are contributed
			// if (leftToolBar != null) {
			// Point size = leftToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT,
			// false);
			// boolean changed = false;
			// for (Control control : leftToolBar.getChildren()) {
			// final Point childSize = control.computeSize(SWT.DEFAULT,
			// SWT.DEFAULT, false);
			// if (childSize.y > size.y) {
			// size.y = childSize.y;
			// changed = true;
			// }
			// }
			// if (changed) {
			// leftToolBar.setSize(size);
			// }
			// }
			//
			// if (PlatformUtil.isToolBarHeightBroken(leftToolBar)) {
			// ToolItem item = new ToolItem(leftToolBar, SWT.NONE);
			// item.setEnabled(false);
			// item.setImage(CommonImages.getImage(CommonImages.BLANK));
			// item.setWidth(1);
			// noExtraPadding = true;
			// } else if (PlatformUtil.needsToolItemToForceToolBarHeight()) {
			// ToolItem item = new ToolItem(leftToolBar, SWT.NONE);
			// item.setEnabled(false);
			// int scaleHeight = 22;
			// if (PlatformUtil.needsCarbonToolBarFix()) {
			// scaleHeight = 32;
			// }
			// final Image image = new Image(item.getDisplay(),
			// CommonImages.getImage(CommonImages.BLANK)
			// .getImageData()
			// .scaledTo(1, scaleHeight));
			// item.setImage(image);
			// item.addDisposeListener(new DisposeListener() {
			// public void widgetDisposed(DisposeEvent e) {
			// image.dispose();
			// }
			// });
			// item.setWidth(1);
			// noExtraPadding = true;
			// }

			// fix size of toolbar on Gtk with Eclipse 3.3
			Point size = leftToolBar.getSize();

			if (size.x == 0 && size.y == 0) {
				size = leftToolBar.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

				leftToolBar.setSize(size);
			}
		}
	}

	private void updateSizeAndLocations() {
		if (busyLabel == null || busyLabel.isDisposed()) {
			return;
		}

		Point leftToolBarSize = new Point(0, 0);

		if (leftToolBar != null && !leftToolBar.isDisposed()) {
			// bottom align tool bar in title region
			leftToolBarSize = leftToolBar.getSize();

			int y = leftToolBar.getParent().getSize().y - leftToolBarSize.y - 2;

			if (!hasLeftToolBar()) {
				// hide tool bar to avoid overlaying busyLabel on windows
				leftToolBarSize.x = 0;
			}

			leftToolBar.setBounds(busyLabel.getLocation().x, y, leftToolBarSize.x, leftToolBarSize.y);
		}

		if (titleLabel != null && !titleLabel.isDisposed()) {
			// center align title text in title region
			Point size = titleLabel.computeSize(SWT.DEFAULT, SWT.DEFAULT, true);

			int y = (titleLabel.getParent().getSize().y - size.y) / 2;

			titleLabel.setBounds(
				busyLabel.getLocation().x + LEFT_TOOLBAR_HEADER_TOOLBAR_PADDING + leftToolBarSize.x, y, size.x, size.y);
		}
	}

	@Override
	protected void addPages() {
		addXMLEditorPage();

		addServiceBuilderFormPage();

		updateHeaderToolBar();

		// getHeaderForm().getForm().setImage(
		// PortletUIPlugin.imageDescriptorFromPlugin(PortletUIPlugin.PLUGIN_ID,
		// "/icons/liferay.png").createImage());
	}

	protected void addServiceBuilderFormPage() {
		try {
			int index = addPage(new ServiceBuilderFormPage(this));

			setPageText(index, "Service Builder");
		}
		catch (PartInitException e) {
			PortletUIPlugin.logError(e);
		}
	}

	@Override
	protected void createHeaderContents(IManagedForm headerForm) {
		getToolkit().decorateFormHeading(headerForm.getForm().getForm());
	}

	@Override
	protected Composite createPageContainer(Composite parent) {
		Composite composite = super.createPageContainer(parent);

		FormHeading heading = (FormHeading) getHeaderForm().getForm().getForm().getHead();

		TitleRegion titleRegion = null;
		try {
			Field field = FormHeading.class.getDeclaredField("titleRegion");
			field.setAccessible(true);

			titleRegion = (TitleRegion) field.get(heading);
		}
		catch (Exception e) {
			PortletUIPlugin.logError(e);
		}

		leftToolBarManager = new ToolBarManager(SWT.FLAT);

		leftToolBar = leftToolBarManager.createControl(titleRegion);

		titleLabel = new Label(titleRegion, SWT.NONE);

		titleLabel.setForeground(heading.getForeground());
		titleLabel.setFont(heading.getFont());
		titleLabel.setText("Service Builder");

		getHeaderForm().getForm().setText(null);

		setHeaderImage(null);

		return composite;
	}

	protected void doBuildServices() {
		doSave(new NullProgressMonitor());

		BuildServiceJob job = PortletCore.createBuildServiceJob(getEditorInput().getFile());

		job.schedule();

	}

	protected void doGenerateWSDD() {
		doSave(new NullProgressMonitor());

		BuildWSDDJob job = PortletCore.createBuildWSDDJob(getEditorInput().getFile());

		job.schedule();

	}

	@Override
	protected void pageChange(int newPageIndex) {
		super.pageChange(newPageIndex);

		if (lastPageIndex == 1 && newPageIndex == 0) {
			try {
				model.load();
			}
			catch (CoreException e) {
				PortletUIPlugin.logError(e);
			}
		}

		this.lastPageIndex = newPageIndex;
	}

}
