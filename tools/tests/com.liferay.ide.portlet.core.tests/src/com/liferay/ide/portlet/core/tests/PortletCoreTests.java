
package com.liferay.ide.portlet.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.portlet.core.operation.INewPortletClassDataModelProperties;
import com.liferay.ide.portlet.core.operation.NewPortletClassDataModelProvider;

import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.junit.Test;

@SuppressWarnings( "restriction" )
public class PortletCoreTests extends BaseTests implements INewPortletClassDataModelProperties
{

    @Test
    public void testNewLiferayPortletWizardModel() throws Exception
    {
        IDataModel dataModel = DataModelFactory.createDataModel( new NewPortletClassDataModelProvider() );

        // test the defaults for classname, portletname, displayname
        assertEquals( "NewPortlet", dataModel.getProperty( CLASS_NAME ) );

        assertEquals( "new", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "New", dataModel.getProperty( DISPLAY_NAME ) );

        // check all other defaults

        dataModel.setProperty( CLASS_NAME, "MyNewPortlet" );

        assertEquals( "my-new", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "My New", dataModel.getProperty( DISPLAY_NAME ) );
    }

}
