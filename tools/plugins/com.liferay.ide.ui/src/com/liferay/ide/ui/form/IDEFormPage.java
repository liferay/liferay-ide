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

package com.liferay.ide.ui.form;

import com.liferay.ide.core.model.IBaseModel;
import com.liferay.ide.core.util.StringUtil;

import java.io.PrintWriter;
import java.io.StringWriter;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Link;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.forms.AbstractFormPart;
import org.eclipse.ui.forms.IFormPart;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Hyperlink;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

/**
 * @author Gregory Amerson
 */
public abstract class IDEFormPage extends FormPage {

	public IDEFormPage(FormEditor editor, String id, String title) {
		super(editor, id, title);

		_fLastFocusControl = null;
	}

	/**
	 * Programatically and recursively add focus listeners to the specified
	 * composite and its children that track the last control to have focus
	 * before a page change or the editor lost focus
	 *
	 * @param composite
	 */
	public void addLastFocusListeners(Composite composite) {
		Control[] controls = composite.getChildren();

		for (Control control : controls) {

			// Add a focus listener if the control is any one of the below types
			// Note that the controls listed below represent all the controls
			// currently in use by all form pages in PDE. In the future,
			// more controls will have to be added.
			// Could not add super class categories of controls because it
			// would include things like tool bars that we don't want to track
			// focus for.

			if (control instanceof Button || control instanceof CCombo || control instanceof Combo ||
				control instanceof CTabFolder || control instanceof FilteredTree || control instanceof Hyperlink ||
				control instanceof Link || control instanceof List || control instanceof Spinner ||
				control instanceof TabFolder || control instanceof Table || control instanceof Text ||
				control instanceof Tree) {

				_addLastFocusListener(control);
			}

			if (control instanceof Composite) {

				// Recursively add focus listeners to this composites children

				addLastFocusListeners((Composite)control);
			}
		}
	}

	/**
	 * Used to align the section client / decriptions of two section headers
	 * horizontally adjacent to each other. The misalignment is caused by one
	 * section header containing toolbar icons and the other not.
	 *
	 * @param masterSection
	 * @param detailsSection
	 */
	public void alignSectionHeaders(Section masterSection, Section detailsSection) {
		detailsSection.descriptionVerticalSpacing += masterSection.getTextClientHeightDifference();
	}

	public void cancelEdit() {
		IFormPart[] parts = getManagedForm().getParts();

		for (IFormPart part : parts) {
			if (part instanceof IContextPart) {
				((IContextPart)part).cancelEdit();
			}
		}
	}

	public boolean canCopy(ISelection selection) {
		AbstractFormPart focusPart = _getFocusSection();

		if (focusPart != null) {
			if (focusPart instanceof IDESection) {
				return ((IDESection)focusPart).canCopy(selection);
			}

			if (focusPart instanceof FormDetails) {
				return ((FormDetails)focusPart).canCopy(selection);
			}
		}

		return false;
	}

	public boolean canCut(ISelection selection) {
		AbstractFormPart focusPart = _getFocusSection();

		if (focusPart != null) {
			if (focusPart instanceof IDESection) {
				return ((IDESection)focusPart).canCut(selection);
			}

			if (focusPart instanceof FormDetails) {
				return ((FormDetails)focusPart).canCut(selection);
			}
		}

		return false;
	}

	public boolean canPaste(Clipboard clipboard) {
		AbstractFormPart focusPart = _getFocusSection();

		if (focusPart != null) {
			if (focusPart instanceof IDESection) {
				return ((IDESection)focusPart).canPaste(clipboard);
			}

			if (focusPart instanceof FormDetails) {
				return ((FormDetails)focusPart).canPaste(clipboard);
			}
		}

		return false;
	}

	public void contextMenuAboutToShow(IMenuManager menu) {
	}

	/**
	 * (non-Javadoc)
	 * @see
	 * FormPage#createPartControl(org.eclipse.swt.
	 * widgets.Composite)
	 */
	public void createPartControl(Composite parent) {
		super.createPartControl(parent);

		// Dynamically add focus listeners to all the forms children in order
		// to track the last focus control

		IManagedForm managedForm = getManagedForm();

		if (managedForm != null) {
			addLastFocusListeners(managedForm.getForm());
		}
	}

	public Section createUISection(Composite parent, String text, String description, int style) {
		FormToolkit formToolkit = getManagedForm().getToolkit();

		Section section = formToolkit.createSection(parent, style);

		section.clientVerticalSpacing = FormLayoutFactory.SECTION_HEADER_VERTICAL_SPACING;
		section.setLayout(FormLayoutFactory.createClearGridLayout(false, 1));
		section.setText(text);
		section.setDescription(description);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		section.setLayoutData(data);

		return section;
	}

	public Composite createUISectionContainer(Composite parent, int columns) {
		FormToolkit formToolkit = getManagedForm().getToolkit();

		Composite container = formToolkit.createComposite(parent);

		container.setLayout(FormLayoutFactory.createSectionClientGridLayout(false, columns));

		return container;
	}

	public void dispose() {
		Control c = getPartControl();

		if ((c != null) && !c.isDisposed()) {
			Menu menu = c.getMenu();

			if (menu != null) {
				_resetMenu(menu, c);
			}
		}

		super.dispose();
	}

	public IDEFormEditor getFormEditor() {
		return (IDEFormEditor)getEditor();
	}

	public Control getLastFocusControl() {
		return _fLastFocusControl;
	}

	public IDEFormEditor getLiferayFormEditor() {
		return (IDEFormEditor)getEditor();
	}

	public IBaseModel getModel() {
		return ((IDEFormEditor)getEditor()).getModel();
	}

	public Shell getShell() {
		return null;
	}

	public boolean performGlobalAction(String actionId) {
		Control focusControl = getFocusControl();

		if (focusControl == null) {
			return false;
		}

		if (canPerformDirectly(actionId, focusControl)) {
			return true;
		}

		AbstractFormPart focusPart = _getFocusSection();

		if (focusPart != null) {
			if (focusPart instanceof IDESection) {
				return ((IDESection)focusPart).doGlobalAction(actionId);
			}

			if (focusPart instanceof FormDetails) {
				return ((FormDetails)focusPart).doGlobalAction(actionId);
			}
		}

		return false;
	}

	/**
	 * @param control
	 */
	public void setLastFocusControl(Control control) {
		_fLastFocusControl = control;
	}

	/**
	 * Set the focus on the last control to have focus before a page change or
	 * the editor lost focus.
	 */
	public void updateFormSelection() {
		if ((_fLastFocusControl != null) && (_fLastFocusControl.isDisposed() == false)) {
			Control lastControl = _fLastFocusControl;

			// Set focus on the control

			lastControl.forceFocus();

			// If the control is a Text widget, select its contents

			if (lastControl instanceof Text) {
				Text text = (Text)lastControl;

				text.setSelection(0, StringUtil.length(text.getText()));
			}
		}
		else {

			// No focus control set
			// Fallback on managed form selection mechanism by setting the
			// focus on this page itself.
			// The managed form will set focus on the first managed part.
			// Most likely this will turn out to be a section.
			// In order for this to work properly, we must override the
			// sections setFocus() method and set focus on a child control
			// (preferrably first) that can practically take focus.

			setFocus();
		}
	}

	protected boolean canPerformDirectly(String id, Control control) {
		if (control instanceof Text) {
			Text text = (Text)control;

			if (id.equals(ActionFactory.CUT.getId())) {
				text.cut();

				return true;
			}

			if (id.equals(ActionFactory.COPY.getId())) {
				text.copy();

				return true;
			}

			if (id.equals(ActionFactory.PASTE.getId())) {
				text.paste();

				return true;
			}

			if (id.equals(ActionFactory.SELECT_ALL.getId())) {
				text.selectAll();

				return true;
			}

			if (id.equals(ActionFactory.DELETE.getId())) {
				int count = text.getSelectionCount();

				if (count == 0) {
					int caretPos = text.getCaretPosition();

					text.setSelection(caretPos, caretPos + 1);
				}

				text.insert("");

				return true;
			}
		}

		return false;
	}

	protected void createFormContent(IManagedForm managedForm) {
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		toolkit.decorateFormHeading(form.getForm());

		IToolBarManager manager = form.getToolBarManager();

		getFormEditor().contributeToToolbar(manager);

		IFormPart[] parts = managedForm.getParts();

		for (IFormPart part : parts) {
			if (part instanceof IAdaptable) {
				IAdaptable adapter = (IAdaptable)part;

				IAction[] actions = (IAction[])adapter.getAdapter(IAction[].class);

				if (actions != null) {
					for (IAction action : actions) {
						IToolBarManager toolBarManager = form.getToolBarManager();

						toolBarManager.add(action);
					}
				}
			}
		}

		form.updateToolBar();
	}

	protected void createFormErrorContent(
		IManagedForm managedForm, String errorTitle, String errorMessage, Exception e) {

		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();

		toolkit.decorateFormHeading(form.getForm());

		Composite parent = form.getBody();
		GridLayout layout = new GridLayout();
		GridData data2 = new GridData(GridData.FILL_BOTH);
		layout.marginWidth = 7;
		layout.marginHeight = 7;
		parent.setLayout(layout);
		parent.setLayoutData(data2);

		// Set the title and image of the form

		form.setText(errorTitle);
		form.setImage(JFaceResources.getImage(Dialog.DLG_IMG_MESSAGE_ERROR));

		int sectionStyle = Section.DESCRIPTION | ExpandableComposite.TITLE_BAR;

		// Create the message section

		Section messageSection = createUISection(parent, Msgs.message, errorMessage, sectionStyle);

		Composite messageClient = createUISectionContainer(messageSection, 1);

		// Bind the widgets

		toolkit.paintBordersFor(messageClient);
		messageSection.setClient(messageClient);

		// Ensure the exception was defined

		if (e == null) {
			return;
		}

		// Create the details section

		Section detailsSection = createUISection(parent, Msgs.details, e.getMessage(), sectionStyle);

		Composite detailsClient = createUISectionContainer(detailsSection, 1);

		// Create text widget holding the exception trace

		int style = SWT.MULTI | SWT.V_SCROLL | SWT.H_SCROLL | SWT.READ_ONLY;

		Text text = toolkit.createText(detailsClient, _getStackTrace(e), style);

		GridData data = new GridData(GridData.FILL_HORIZONTAL);

		data.heightHint = 160;
		data.widthHint = 200;
		text.setLayoutData(data);

		// Bind the widgets

		toolkit.paintBordersFor(detailsClient);
		detailsSection.setClient(detailsClient);

		// Note: The veritical scrollbar fails to appear when text widget is
		// not entirely shown

	}

	protected Control getFocusControl() {
		IManagedForm form = getManagedForm();

		if (form == null) {
			return null;
		}

		Control control = form.getForm();

		if ((control == null) || control.isDisposed()) {
			return null;
		}

		Display display = control.getDisplay();

		Control focusControl = display.getFocusControl();

		if ((focusControl == null) || focusControl.isDisposed()) {
			return null;
		}

		return focusControl;
	}

	protected String getHelpResource() {
		return null;
	}

	/**
	 * Add a focus listener to the specified control that tracks the last
	 * control to have focus on this page. When focus is gained by this control,
	 * it registers itself as the last control to have focus. The last control
	 * to have focus is stored in order to be restored after a page change or
	 * editor loses focus.
	 *
	 * @param control
	 */
	private void _addLastFocusListener(Control control) {
		control.addFocusListener(
			new FocusListener() {

				public void focusGained(FocusEvent e) {

					// NO-OP

				}

				public void focusLost(FocusEvent e) {
					_fLastFocusControl = control;
				}

			});
	}

	private AbstractFormPart _getFocusSection() {
		Control focusControl = getFocusControl();

		if (focusControl == null) {
			return null;
		}

		Composite parent = focusControl.getParent();
		AbstractFormPart targetPart = null;

		while (parent != null) {
			Object data = parent.getData("part");

			if ((data != null) && data instanceof AbstractFormPart) {
				targetPart = (AbstractFormPart)data;

				break;
			}

			parent = parent.getParent();
		}

		return targetPart;
	}

	private String _getStackTrace(Throwable throwable) {
		StringWriter swriter = new StringWriter();

		PrintWriter pwriter = new PrintWriter(swriter);

		throwable.printStackTrace(pwriter);
		pwriter.flush();
		pwriter.close();

		return swriter.toString();
	}

	private void _resetMenu(Menu menu, Control c) {
		if (c instanceof Composite) {
			Composite comp = (Composite)c;

			Control[] children = comp.getChildren();

			for (Control control : children) {
				_resetMenu(menu, control);
			}
		}

		Menu cmenu = c.getMenu();

		if ((cmenu != null) && cmenu.equals(menu)) {
			c.setMenu(null);
		}
	}

	private Control _fLastFocusControl;

	private static class Msgs extends NLS {

		public static String details;
		public static String message;

		static {
			initializeMessages(IDEFormPage.class.getName(), Msgs.class);
		}

	}

}