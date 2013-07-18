
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
        
        assertEquals( "New", dataModel.getProperty( TITLE ) );
        // check all other defaults

        dataModel.setProperty( CLASS_NAME, "MyNewPortlet" );

        assertEquals( "my-new", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "My New", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "My New", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "ABCNewPortlet" );

        assertEquals( "ABCNewPortlet", dataModel.getProperty( CLASS_NAME ) );

        assertEquals( "abc-new", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "Abc New", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Abc New", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "abcdMyPORTLET" );

        assertEquals( "abcd-my", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "Abcd My", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Abcd My", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "NewPortletD" );

        assertEquals( "new-portlet-d", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "New Portlet D", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "New Portlet D", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "MyAABcPortletPortlet" );

        assertEquals( "my-aa-bc-portlet", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "My Aa Bc Portlet", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "My Aa Bc Portlet", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "Aa12M334yBbCCC" );

        assertEquals( "aa12-m334y-bb-ccc", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "Aa12 M334y Bb Ccc", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Aa12 M334y Bb Ccc", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( CLASS_NAME, "Aa12M334yB2bCCC2C" );

        assertEquals( "aa12-m334y-b2b-cc-c2-c", dataModel.getProperty( PORTLET_NAME ) );

        assertEquals( "Aa12 M334y B2b Cc C2 C", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Aa12 M334y B2b Cc C2 C", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "Aa 12-M334yB_bCC2C" );

        assertEquals( "Aa 12 M334yB BCC2C", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Aa 12 M334yB BCC2C", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "my-n ew-POrtLet" );

        assertEquals( "My N Ew POrtLet", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "My N Ew POrtLet", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "my-^abcd-tao%*tao ---liferay_PORTlet*^-_Display-_ PORTLET" );

        assertEquals( "My Liferay Display PORTLET", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "My Liferay Display PORTLET", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "A^A*B&BcD-R@daf!ffwo_casfe/asdr.cv,ae][asdferf--portlet-my new" );

        assertEquals( "Portlet My New", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Portlet My New", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "" );

        assertEquals( "", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " " );

        assertEquals( "", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " \" \" " );

        assertEquals( "", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " \" " );

        assertEquals( "", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, "\"\"       \" " );

        assertEquals( "", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " Test1 Test2 \"Test1\" " );

        assertEquals( "Test1 Test2", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Test1 Test2", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " Test1 Test2\" \"Te\"st1 " );

        assertEquals( "Test1", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Test1", dataModel.getProperty( TITLE ) );

        dataModel.setProperty( PORTLET_NAME, " Tes \t3 Te\" \" Test2\" \"Te\"st1 " );

        assertEquals( "Tes 3", dataModel.getProperty( DISPLAY_NAME ) );

        assertEquals( "Tes 3", dataModel.getProperty( TITLE ) );
    }
}
