package com.liferay.ide.eclipse.portlet.core;

import com.liferay.ide.eclipse.project.core.AbstractProjectDefinition;
import com.liferay.ide.eclipse.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.eclipse.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.eclipse.project.core.util.ProjectUtil;

import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;

public class LiferayExtDefinition extends AbstractProjectDefinition implements IPluginProjectDataModelProperties {

	public LiferayExtDefinition() {
		super();
	}

	public void setupNewProject(IDataModel dataModel, IFacetedProjectWorkingCopy facetedProject) {
		ProjectUtil.setGenerateDD(dataModel, true);

		FacetDataModelMap map = (FacetDataModelMap) dataModel.getProperty(FACET_DM_MAP);
		// IDataModel javaFacetModel = map.getFacetDataModel(
		// JavaFacetUtils.JAVA_FACET.getId() );
		IDataModel webFacetModel = map.getFacetDataModel(IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId());
		webFacetModel.setStringProperty(
			IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.EXT_PLUGIN_SDK_CONFIG_FOLDER);
	}

}
