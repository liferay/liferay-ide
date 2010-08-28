package com.liferay.ide.eclipse.ui.snippets.wizard;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IEditorPart;

public class LiferayUISearchContainerWizard extends Wizard {

	protected LiferayUISearchContainerWizardPage wizardPage;

	protected IEditorPart editorPart;

	public LiferayUISearchContainerWizard(IEditorPart fEditorPart) {
		super();
		setWindowTitle("Insert Search Container");
		editorPart = fEditorPart;
	}

	@Override
	public void addPages() {
		wizardPage = new LiferayUISearchContainerWizardPage("liferayUISearchContainerWizardPage", editorPart);
		addPage(wizardPage);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	public String getModelClass() {
		return wizardPage.getModelClass();
	}

	public String getVarName() {
		return wizardPage.getVarName();
	}

	public String getModel() {
		return wizardPage.getModel();
	}

}
