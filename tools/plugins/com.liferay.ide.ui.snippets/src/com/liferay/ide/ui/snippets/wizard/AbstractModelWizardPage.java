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

package com.liferay.ide.ui.snippets.wizard;

import com.liferay.ide.core.util.ListUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.ui.util.SWTUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.SuperInterfaceSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.CheckedListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IDialogFieldListener;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IListAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.LayoutUtil;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.ListDialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.ViewerComparator;
import org.eclipse.jface.window.Window;
import org.eclipse.jst.jsf.common.util.JDTBeanIntrospector;
import org.eclipse.jst.jsf.common.util.JDTBeanProperty;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings({"restriction", "rawtypes"})
public class AbstractModelWizardPage extends NewTypeWizardPage {

	public static Object[] getTypeProperties(IType type) {
		if (type == null) {
			return null;
		}

		JDTBeanIntrospector beanIntrospector = new JDTBeanIntrospector(type);

		Map<String, JDTBeanProperty> properties = beanIntrospector.getProperties();

		return properties.keySet().toArray();
	}

	public AbstractModelWizardPage(String pageName, IEditorPart editor) {
		super(true, pageName);

		editorPart = editor;

		TypeFieldAdapter adapter = new TypeFieldAdapter();

		modelClassDialogField = new StringButtonDialogField(adapter);

		modelClassDialogField.setLabelText(Msgs.modelClassLabel);
		modelClassDialogField.setButtonLabel(Msgs.newTypeWizardPage_superclass_button);

		String[] buttonLabels = {Msgs.selectAllLabel, Msgs.deselectAllLabel};

		propertyListField = new CheckedListDialogField(adapter, buttonLabels, new LabelProvider());

		propertyListField.setDialogFieldListener(adapter);
		propertyListField.setLabelText(Msgs.propertyColumnsLabel);
		propertyListField.setCheckAllButtonIndex(IDX_SELECT);
		propertyListField.setUncheckAllButtonIndex(IDX_DESELECT);
	}

	public void createControl(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		modelClassDialogField.doFillIntoGrid(topComposite, 3);

		// Text modelClassText = modelClassDialogField.getTextControl(null);

		//

		// JavaTypeCompletionProcessor classCompletionProcessor = new
		// JavaTypeCompletionProcessor(false, false, true);
		// classCompletionProcessor.setCompletionContextRequestor(new
		// CompletionContextRequestor() {

		//

		// @Override
		// public StubTypeContext getStubTypeContext() {
		// return getClassStubTypeContext();
		// }
		// });

		//

		// ControlContentAssistHelper.createTextContentAssistant(modelClassText,
		// classCompletionProcessor);
		// TextFieldNavigationHandler.install(modelClassText);

		propertyListField.doFillIntoGrid(topComposite, 3);

		LayoutUtil.setHorizontalSpan(propertyListField.getLabelControl(null), 1);
		LayoutUtil.setWidthHint(propertyListField.getLabelControl(null), convertWidthInCharsToPixels(40));
		LayoutUtil.setHorizontalGrabbing(propertyListField.getListControl(null));

		propertyListField.getTableViewer().setComparator(new ViewerComparator());

		varNameLabel = new Label(topComposite, SWT.LEFT);

		varNameLabel.setText(Msgs.variableNameLabel);
		varNameLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		varNameText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);

		varNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		varNameText.addModifyListener(
			new ModifyListener() {

				public void modifyText(ModifyEvent e) {
					lastVarName = varNameText.getText();
				}

			});

		setControl(topComposite);
	}

	public IJavaProject getJavaProject() {
		IJavaProject javaProject = null;

		if (editorPart != null) {
			IEditorInput editorInput = editorPart.getEditorInput();

			if (editorInput instanceof IFileEditorInput) {
				IProject project = ((IFileEditorInput)editorInput).getFile().getProject();

				return JavaCore.create(project);
			}
		}

		return javaProject;
	}

	public String getModel() {
		try {
			IType type = getJavaProject().findType(getModelClass());

			return type.getElementName();
		}
		catch (Exception e) {
		}

		return StringPool.EMPTY;
	}

	public String getModelClass() {
		return modelClassDialogField.getText();
	}

	public String[] getPropertyColumns() {
		return (String[])propertyListField.getCheckedElements().toArray(new String[0]);
	}

	public String getTypeName() {
		return modelClassDialogField.getText();
	}

	public String getVarName() {
		return lastVarName;
	}

	protected IType chooseClass() {
		IJavaProject project = getJavaProject();

		if (project == null) {
			return null;
		}

		IJavaElement[] elements = {project};

		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog = new FilteredTypesSelectionDialog(
			getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS_AND_INTERFACE);

		dialog.setTitle(Msgs.modelClassSelection);
		dialog.setMessage(Msgs.newTypeWizardPage_SuperClassDialog_message);
		dialog.setInitialPattern(getSuperClass());

		if (dialog.open() == Window.OK) {
			return (IType)dialog.getFirstResult();
		}

		return null;
	}

	protected void handleBrowseButtonPressed() {
	}

	protected void typeChangeControlPressed(DialogField field) {
		IType type = chooseClass();

		if (type != null) {
			modelClassDialogField.setText(SuperInterfaceSelectionDialog.getNameWithTypeParameters(type));

			updatePropertyList(type);
		}
	}

	protected void updatePropertyList(IType type) {
		List<Object> propNames = new ArrayList<>();

		Object[] props = getTypeProperties(type);

		if (ListUtil.isNotEmpty(props)) {
			Collections.addAll(propNames, props);
		}

		try {
			if (type.isInterface()) {
				String[] superInterfaces = type.getSuperInterfaceNames();

				if (ListUtil.isNotEmpty(superInterfaces)) {
					for (String superInterface : superInterfaces) {
						IType superInterfaceType = type.getJavaProject().findType(superInterface);

						Object[] superInterfaceProps = getTypeProperties(superInterfaceType);

						if (ListUtil.isNotEmpty(superInterfaceProps)) {
							Collections.addAll(propNames, superInterfaceProps);
						}
					}
				}
			}
			else {
				IType superType = type.getJavaProject().findType(type.getSuperclassName());

				Object[] superTypeProps = getTypeProperties(superType);

				if (ListUtil.isNotEmpty(superTypeProps)) {
					Collections.addAll(propNames, superTypeProps);
				}
			}
		}
		catch (Exception e) {

			// no error this is best effort

		}

		propertyListField.setElements(propNames);

		varNameText.setText("a" + getModel());
	}

	protected static final int IDX_DESELECT = 1;

	protected static final int IDX_SELECT = 0;

	// protected StubTypeContext getClassStubTypeContext() {
	// if (fClassStubTypeContext == null) {
	// fClassStubTypeContext =
	// TypeContextChecker.createSuperClassStubTypeContext(getTypeName(), null,
	// null);
	// }
	// return fClassStubTypeContext;
	// }

	protected IEditorPart editorPart;
	protected String lastVarName = StringPool.EMPTY;
	protected StringButtonDialogField modelClassDialogField;
	protected CheckedListDialogField propertyListField;
	protected Label varNameLabel;
	protected Text varNameText;

	protected class TypeFieldAdapter implements IStringButtonAdapter, IDialogFieldListener, IListAdapter {

		// -------- IStringButtonAdapter

		public void changeControlPressed(DialogField field) {
			typeChangeControlPressed(field);
		}

		public void customButtonPressed(ListDialogField field, int index) {

			// doButtonPressed(index);

		}

		public void dialogFieldChanged(DialogField field) {
		}

		public void doubleClicked(ListDialogField field) {
		}

		public void selectionChanged(ListDialogField field) {
		}

	}

	private static class Msgs extends NLS {

		public static String deselectAllLabel;
		public static String modelClassLabel;
		public static String modelClassSelection;
		public static String newTypeWizardPage_superclass_button;
		public static String newTypeWizardPage_SuperClassDialog_message;
		public static String propertyColumnsLabel;
		public static String selectAllLabel;
		public static String variableNameLabel;

		static {
			initializeMessages(AbstractModelWizardPage.class.getName(), Msgs.class);
		}

	}

}