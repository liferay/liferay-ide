package com.liferay.ide.project.ui.wizard;

import com.liferay.ide.project.ui.ProjectUIPlugin;

import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public class NewProjectFromSourceWizard extends LiferayProjectImportWizard {

	public NewProjectFromSourceWizard() {
		this(null);
	}

	public NewProjectFromSourceWizard(IDataModel dataModel) {
		super(dataModel);

		setWindowTitle("New Liferay Project");
		setDefaultPageImageDescriptor(ProjectUIPlugin.imageDescriptorFromPlugin(
			ProjectUIPlugin.PLUGIN_ID, "/icons/wizban/plugin_project.png"));
		setNeedsProgressMonitor(true);
	}

}
