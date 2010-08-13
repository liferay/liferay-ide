package com.liferay.ide.eclipse.project.ui.action;

import com.liferay.ide.eclipse.core.AbstractConfigurationElement;
import com.liferay.ide.eclipse.project.ui.ProjectUIPlugin;
import com.liferay.ide.eclipse.project.ui.wizard.LiferayProjectImportWizard;
import com.liferay.ide.eclipse.ui.action.NewWizardAction;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.InvalidRegistryObjectException;
import org.eclipse.core.runtime.spi.RegistryContributor;


public class ImportLiferayProjectWizardAction extends NewWizardAction {

	protected static class ImportLiferayProjectDescriptionElement extends AbstractConfigurationElement {

		@Override
		public String getValue()
			throws InvalidRegistryObjectException {

			return "Import description";
		}

	}

	protected static class ImportLiferayProjectParameterElement extends AbstractConfigurationElement {

		@Override
		public String getAttribute(String name)
			throws InvalidRegistryObjectException {

			if (NewWizardAction.TAG_NAME.equals(name)) {
				return NewWizardAction.ATT_MENUINDEX;
			}
			else if (NewWizardAction.TAG_VALUE.equals(name)) {
				return "100";
			}

			return null;
		}

	}

	protected static class ImportLiferayProjectClassElement extends AbstractConfigurationElement {

		@Override
		public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {

			if (NewWizardAction.TAG_PARAMETER.equals(name)) {
				return new IConfigurationElement[] {
					new ImportLiferayProjectParameterElement()
				};
			}

			return null;
		}

	}

	protected static class ImportLiferayProjectElement extends AbstractConfigurationElement {

		@Override
		public IConfigurationElement[] getChildren(String name)
			throws InvalidRegistryObjectException {

			if (NewWizardAction.TAG_DESCRIPTION.equals(name)) {
				return new IConfigurationElement[] {
					new ImportLiferayProjectDescriptionElement()
				};
			}
			else if (NewWizardAction.TAG_CLASS.equals(name)) {
				return new IConfigurationElement[] {
					new ImportLiferayProjectClassElement()
				};
			}

			return null;
		}

		@Override
		public String getAttribute(String attr)
			throws InvalidRegistryObjectException {

			if (NewWizardAction.ATT_NAME.equals(attr)) {
				return "";
			}
			else if (NewWizardAction.ATT_ICON.equals(attr)) {
				return "/icons/e16/import.png";
			}

			return null;
		}

		@Override
		public IContributor getContributor()
			throws InvalidRegistryObjectException {

			return new RegistryContributor(null, ProjectUIPlugin.PLUGIN_ID, null, null);
		}

		@Override
		public Object createExecutableExtension(String propertyName)
			throws CoreException {

			if (NewWizardAction.ATT_CLASS.equals(propertyName)) {
				return new LiferayProjectImportWizard();
			}

			return null;
		}
	}

	public ImportLiferayProjectWizardAction() {
		super(new ImportLiferayProjectElement());
		setText("Import Existing Liferay Plug-in Project");
	}

}
