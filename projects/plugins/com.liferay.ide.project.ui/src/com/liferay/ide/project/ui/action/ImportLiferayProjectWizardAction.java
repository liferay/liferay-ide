package com.liferay.ide.project.ui.action;

import com.liferay.ide.project.ui.ProjectUIPlugin;
import com.liferay.ide.project.ui.wizard.NewProjectFromSourceWizard;
import com.liferay.ide.ui.action.AbstractNewProjectWizardProjectElement;
import com.liferay.ide.ui.action.NewWizardAction;


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
				return "/icons/n16/plugin_new.png";
			}

			return null;
		}

		@Override
		protected Object createNewWizard() {
			return new NewProjectFromSourceWizard();
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
		setText("New Liferay Project from Existing Source");
	}

}
