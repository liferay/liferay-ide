package com.liferay.ide.eclipse.project.ui.action;

import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.project.ui.wizard.LiferayProjectImportWizard;
import com.liferay.ide.eclipse.ui.action.AbstractNewProjectWizardProjectElement;
import com.liferay.ide.eclipse.ui.action.NewWizardAction;


public class ImportLiferayProjectWizardAction extends NewWizardAction {

	static class ImportLiferayProjectElement extends AbstractNewProjectWizardProjectElement {

		protected String getContributorID() {
			return ProjectUIPlugin.PLUGIN_ID;
		}

		@Override
		protected String getProjectElementAttribute(String attr) {
			if (NewWizardAction.ATT_NAME.equals(attr)) {
				return "";
			}
			else if (NewWizardAction.ATT_ICON.equals(attr)) {
				return "/icons/e16/import.png";
			}

			return null;
		}

		@Override
		protected Object createNewWizard() {
			return new LiferayProjectImportWizard();
		}

		@Override
		protected String getProjectParameterElementAttribute(String name) {
			if (NewWizardAction.TAG_NAME.equals(name)) {
				return NewWizardAction.ATT_MENUINDEX;
			}
			else if (NewWizardAction.TAG_VALUE.equals(name)) {
				return "100";
			}

			return null;
		}

	}

	public ImportLiferayProjectWizardAction() {
		super(new ImportLiferayProjectElement());
		setText("Import Existing Liferay Plug-in Project");
	}

}
