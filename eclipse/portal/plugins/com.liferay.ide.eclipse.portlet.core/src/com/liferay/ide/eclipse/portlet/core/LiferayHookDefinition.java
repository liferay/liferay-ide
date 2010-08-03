package com.liferay.ide.eclipse.portlet.core;

import com.liferay.ide.eclipse.project.core.AbstractProjectDefinition;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.JavaFacetUtils;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

@SuppressWarnings("deprecation")
public class LiferayHookDefinition extends AbstractProjectDefinition implements IPluginProjectDataModelProperties {

	public LiferayHookDefinition() {
		super();
	}

	public void setupNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject) {
		ProjectUtil.setGenerateDD(dataModel, false);

		FacetDataModelMap map = (FacetDataModelMap) dataModel.getProperty(FACET_DM_MAP);
		IDataModel javaFacetModel = map.getFacetDataModel(JavaFacetUtils.JAVA_FACET.getId());
		IDataModel webFacetModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());

		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.HOOK_PLUGIN_SDK_CONFIG_FOLDER);
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.SOURCE_FOLDER, IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER);
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
			IPluginFacetConstants.HOOK_PLUGIN_SDK_SOURCE_FOLDER);
		javaFacetModel.setStringProperty(
			IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
			IPluginFacetConstants.HOOK_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER);
	}

}
