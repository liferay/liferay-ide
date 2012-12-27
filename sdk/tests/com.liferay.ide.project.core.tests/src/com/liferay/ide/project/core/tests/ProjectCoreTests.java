
package com.liferay.ide.project.core.tests;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.project.core.IPortletFrameworkWizardProvider;
import com.liferay.ide.project.core.ProjectCorePlugin;
import com.liferay.ide.project.core.facet.IPluginFacetConstants;
import com.liferay.ide.project.core.facet.IPluginProjectDataModelProperties;
import com.liferay.ide.project.core.facet.PluginFacetProjectCreationDataModelProvider;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jst.common.project.facet.IJavaFacetInstallDataModelProperties;
import org.eclipse.jst.common.project.facet.core.JavaFacet;
import org.eclipse.jst.j2ee.project.facet.IJ2EEFacetConstants;
import org.eclipse.jst.j2ee.web.project.facet.IWebFacetInstallDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties;
import org.eclipse.wst.common.componentcore.datamodel.properties.IFacetProjectCreationDataModelProperties.FacetDataModelMap;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProjectWorkingCopy;
import org.junit.Before;
import org.junit.Test;

public class ProjectCoreTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }
    
    @Before
    public void setupSDK() throws Exception
    {
        
    }


    @Test
    public void createNewPortletProject() throws Exception
    {
        IDataModel dataModel = DataModelFactory.createDataModel(new PluginFacetProjectCreationDataModelProvider());

        dataModel.setProperty(IFacetProjectCreationDataModelProperties.FACET_PROJECT_NAME, "TestWeb24"); //$NON-NLS-1$

        FacetDataModelMap map = (FacetDataModelMap) dataModel.getProperty( IPluginProjectDataModelProperties.FACET_DM_MAP );
        IDataModel webFacetModel = map.getFacetDataModel( IJ2EEFacetConstants.DYNAMIC_WEB_FACET.getId() );

        webFacetModel.setStringProperty(
            IWebFacetInstallDataModelProperties.CONFIG_FOLDER, IPluginFacetConstants.PORTLET_PLUGIN_SDK_CONFIG_FOLDER );
        webFacetModel.setStringProperty(
            IWebFacetInstallDataModelProperties.SOURCE_FOLDER, IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER );
        IDataModel javaFacetModel = map.getFacetDataModel( JavaFacet.FACET.getId() );
        javaFacetModel.setStringProperty(
            IJavaFacetInstallDataModelProperties.SOURCE_FOLDER_NAME,
            IPluginFacetConstants.PORTLET_PLUGIN_SDK_SOURCE_FOLDER );
        javaFacetModel.setStringProperty(
            IJavaFacetInstallDataModelProperties.DEFAULT_OUTPUT_FOLDER_NAME,
            IPluginFacetConstants.PORTLET_PLUGIN_SDK_DEFAULT_OUTPUT_FOLDER );

        // need to allow portlet framework to do any additional configuration

        String portletFrameworkId = dataModel.getStringProperty( IPluginProjectDataModelProperties.PORTLET_FRAMEWORK_ID );

        IPortletFrameworkWizardProvider portletFramework = ProjectCorePlugin.getPortletFramework( portletFrameworkId );

        portletFramework.configureNewProject( dataModel, getFacetedProjectWorkingCopy( dataModel ) );


        IStatus status  = dataModel.getDefaultOperation().execute(new NullProgressMonitor(), null);
        System.out.println(status);
    }

    protected IFacetedProjectWorkingCopy getFacetedProjectWorkingCopy(IDataModel model)
    {
        return (IFacetedProjectWorkingCopy) model.getProperty( IPluginProjectDataModelProperties.FACETED_PROJECT_WORKING_COPY );
    }

}
