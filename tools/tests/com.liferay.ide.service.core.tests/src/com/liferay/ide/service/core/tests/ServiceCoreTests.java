
package com.liferay.ide.service.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.project.core.util.WizardUtil;
import com.liferay.ide.service.core.operation.NewServiceBuilderDataModelProvider;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class ServiceCoreTests extends BaseTests
{

    @Test
    public void testCreateBlankServiceXmlFile() throws Exception
    {
        createServiceXmlFile( "6.2.0", false );
    }

    @Test
    public void testCreateServiceXmlFiles() throws Exception
    {
        createServiceXmlFile( "6.2.0", true );
        createServiceXmlFile( "6.1.0", true );
        createServiceXmlFile( "6.0.0", true );
    }

    private void createServiceXmlFile( String version, boolean useSample ) throws Exception
    {
        final IProject project = createProject( "serviceXmlFiles-" + version + "-" + useSample );

        final IDataModel model = DataModelFactory.createDataModel( new NewServiceBuilderDataModelProvider() );

        model.setProperty( NewServiceBuilderDataModelProvider.AUTHOR, "junit" );
        model.setProperty( NewServiceBuilderDataModelProvider.PACKAGE_PATH, "com.liferay.sample" );
        model.setProperty( NewServiceBuilderDataModelProvider.NAMESPACE, "SAMPLE" );
        model.setBooleanProperty( NewServiceBuilderDataModelProvider.USE_SAMPLE_TEMPLATE, useSample );

        final IFolder folder = project.getFolder( "test" );

        CoreUtil.prepareFolder( folder );

        final IFile serviceXmlFile = folder.getFile( "service.xml" );

        assertEquals( false, serviceXmlFile.exists() );

        WizardUtil.createDefaultServiceBuilderFile(
            serviceXmlFile, version,
            model.getBooleanProperty( NewServiceBuilderDataModelProvider.USE_SAMPLE_TEMPLATE ),
            model.getStringProperty( NewServiceBuilderDataModelProvider.PACKAGE_PATH ),
            model.getStringProperty( NewServiceBuilderDataModelProvider.NAMESPACE ),
            model.getStringProperty( NewServiceBuilderDataModelProvider.AUTHOR ), new NullProgressMonitor() );

        assertEquals( true, serviceXmlFile.exists() );

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXmlFile.getContents() );

        final String expectedServiceXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/service-sample-" + version + "-" + useSample + ".xml" ) );

        assertEquals( stripCarriageReturns( expectedServiceXmlContent ), stripCarriageReturns( serviceXmlContent ) );
    }

}
