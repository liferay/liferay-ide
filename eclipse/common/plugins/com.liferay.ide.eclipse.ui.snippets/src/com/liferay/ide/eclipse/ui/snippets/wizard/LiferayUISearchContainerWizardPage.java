
package com.liferay.ide.eclipse.ui.snippets.wizard;

import com.liferay.ide.eclipse.ui.util.SWTUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.search.IJavaSearchConstants;
import org.eclipse.jdt.core.search.IJavaSearchScope;
import org.eclipse.jdt.core.search.SearchEngine;
import org.eclipse.jdt.internal.corext.refactoring.StubTypeContext;
import org.eclipse.jdt.internal.corext.refactoring.TypeContextChecker;
import org.eclipse.jdt.internal.ui.dialogs.FilteredTypesSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.NewWizardMessages;
import org.eclipse.jdt.internal.ui.wizards.SuperInterfaceSelectionDialog;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.DialogField;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.IStringButtonAdapter;
import org.eclipse.jdt.internal.ui.wizards.dialogfields.StringButtonDialogField;
import org.eclipse.jdt.ui.wizards.NewTypeWizardPage;
import org.eclipse.jface.window.Window;
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

@SuppressWarnings("restriction")
public class LiferayUISearchContainerWizardPage extends NewTypeWizardPage {

	protected Text varNameText;

	protected String lastVarName;

	protected StringButtonDialogField modelClassDialogField;

	protected IEditorPart editorPart;

	private StubTypeContext fClassStubTypeContext;

	protected class TypeFieldAdapter implements IStringButtonAdapter {

		// -------- IStringButtonAdapter
		public void changeControlPressed(DialogField field) {
			typeChangeControlPressed(field);
		}

	}

	protected void typeChangeControlPressed(DialogField field) {
		IType type = chooseClass();
		if (type != null) {
			modelClassDialogField.setText(SuperInterfaceSelectionDialog.getNameWithTypeParameters(type));
		}
	}

	protected IType chooseClass() {
		IJavaProject project = getJavaProject();
		if (project == null) {
			return null;
		}

		IJavaElement[] elements = new IJavaElement[] {
			project
		};
		IJavaSearchScope scope = SearchEngine.createJavaSearchScope(elements);

		FilteredTypesSelectionDialog dialog =
			new FilteredTypesSelectionDialog(
				getShell(), false, getWizard().getContainer(), scope, IJavaSearchConstants.CLASS_AND_INTERFACE);
		dialog.setTitle("Model class selection");
		dialog.setMessage(NewWizardMessages.NewTypeWizardPage_SuperClassDialog_message);
		dialog.setInitialPattern(getSuperClass());

		if (dialog.open() == Window.OK) {
			return (IType) dialog.getFirstResult();
		}
		return null;
	}

	public IJavaProject getJavaProject() {
		IJavaProject javaProject = null;

		if (editorPart != null) {
			IEditorInput editorInput = editorPart.getEditorInput();
			if (editorInput instanceof IFileEditorInput) {
				IProject project = ((IFileEditorInput) editorInput).getFile().getProject();
				return JavaCore.create(project);
			}
		}

		return javaProject;
	}

	public LiferayUISearchContainerWizardPage(String pageName, IEditorPart editor) {
		super(true, pageName);
		setTitle("Liferay UI Search Container");
		setDescription("Insert a Liferay UI Search Container JSP tag.");

		editorPart = editor;

		TypeFieldAdapter adapter = new TypeFieldAdapter();

		modelClassDialogField = new StringButtonDialogField(adapter);
		modelClassDialogField.setLabelText("Model class:");
		modelClassDialogField.setButtonLabel(NewWizardMessages.NewTypeWizardPage_superclass_button);
	}

	public String getTypeName() {
		return modelClassDialogField.getText();
	}

	protected StubTypeContext getClassStubTypeContext() {
		if (fClassStubTypeContext == null) {
			fClassStubTypeContext = TypeContextChecker.createSuperClassStubTypeContext(getTypeName(), null, null);
		}
		return fClassStubTypeContext;
	}

	public void createControl(Composite parent) {
		Composite topComposite = SWTUtil.createTopComposite(parent, 3);

		modelClassDialogField.doFillIntoGrid(topComposite, 3);
		// Text modelClassText = modelClassDialogField.getTextControl(null);
		//
		// JavaTypeCompletionProcessor classCompletionProcessor = new JavaTypeCompletionProcessor(false, false, true);
		// classCompletionProcessor.setCompletionContextRequestor(new CompletionContextRequestor() {
		//
		// @Override
		// public StubTypeContext getStubTypeContext() {
		// return getClassStubTypeContext();
		// }
		// });
		//
		// ControlContentAssistHelper.createTextContentAssistant(modelClassText, classCompletionProcessor);
		// TextFieldNavigationHandler.install(modelClassText);

		Label varNameLabel = new Label(topComposite, SWT.LEFT);
		varNameLabel.setText("Variable name:");
		varNameLabel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));

		varNameText = new Text(topComposite, SWT.SINGLE | SWT.BORDER);
		varNameText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		varNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				lastVarName = varNameText.getText();
			}
		});

		setControl(topComposite);
	}

	protected void handleBrowseButtonPressed() {

	}

	public String getModelClass() {
		return modelClassDialogField.getText();
	}

	public String getVarName() {
		return lastVarName;
	}

	public String getModel() {
		try {
			IType type = getJavaProject().findType(getModelClass());
			return type.getElementName();
		}
		catch (Exception e) {

		}
		return "";
	}

}
