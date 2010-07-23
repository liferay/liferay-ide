package com.liferay.ide.eclipse.project.ui.wizard;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;

public interface IPluginWizardFragment {

	public static final String ID = "com.liferay.ide.eclipse.project.ui.pluginWizardFragment";

	public String getFragmentPluginFacetId();

	public void setDataModel(IDataModel model);

	public IWizardPage getNextPage(IWizardPage page);

	public void addPages();

	public void setFragment(boolean fragment);

	public void setHostPage(IWizardPage firstPage);

}
