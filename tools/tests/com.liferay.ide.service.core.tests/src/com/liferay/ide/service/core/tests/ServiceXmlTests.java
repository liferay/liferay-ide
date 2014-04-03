package com.liferay.ide.service.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.util.CoreUtil;
import com.liferay.ide.core.util.StringPool;
import com.liferay.ide.project.core.tests.XmlTestsBase;
import com.liferay.ide.service.core.operation.ServiceBuilderDescriptorHelper;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.Status;
import org.junit.Test;


/**
 * @author Kuo Zhang
 */
public class ServiceXmlTests extends XmlTestsBase
{
    // This test needs to set the "liferay.bundles.dir" in the configuration.

    @Test
    public void testAddSampleEntity() throws Exception
    {
        final IProject project =
            importProject( "portlets","com.liferay.ide.service.core.tests", "Add-Sample-Entity-Test-portlet" );

        final ServiceBuilderDescriptorHelper descriptorHelper = new ServiceBuilderDescriptorHelper( project );

        assertEquals( Status.OK_STATUS, descriptorHelper.addDefaultEntity() );

        final IFile serviceXmlFile = descriptorHelper.getDescriptorFile();

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXmlFile.getContents() );

        final String expectedServiceXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/service-sample-6.2.0-add-sample-entity.xml" ) );

        assertEquals(
            expectedServiceXmlContent.replaceAll( "\\s+", StringPool.SPACE ),
            serviceXmlContent.replaceAll( "\\s+", StringPool.SPACE ) );
    }

    @Test
    public void testAddDefaultColumns() throws Exception
    {
        final IProject project =
            importProject( "portlets", "com.liferay.ide.service.core.tests", "Add-Default-Columns-Test-portlet" );

        final ServiceBuilderDescriptorHelper descriptorHelper = new ServiceBuilderDescriptorHelper( project );

        assertEquals( Status.OK_STATUS, descriptorHelper.addEntity( "AddDefaultColumns" ) );
        assertEquals( Status.OK_STATUS, descriptorHelper.addDefaultColumns( "AddDefaultColumns" ) );

        final IFile serviceXmlFile = descriptorHelper.getDescriptorFile();

        final String serviceXmlContent = CoreUtil.readStreamToString( serviceXmlFile.getContents() );

        final String expectedServiceXmlContent =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/service-sample-6.2.0-add-default-columns.xml" ) );

        assertEquals(
            expectedServiceXmlContent.replaceAll( "\\s+", StringPool.SPACE ),
            serviceXmlContent.replaceAll( "\\s+", StringPool.SPACE) );
    }

}
