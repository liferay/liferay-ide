package com.liferay.ide.layouttpl.core.tests;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.core.resources.IFile;
import org.junit.Test;


/**
 * @author Kuo Zhang
 *
 */
public class LayoutTplTestsBootstrap extends LayoutTplCoreTests
{

    @Override
    protected boolean isBootstrapStyle()
    {
        return true;
    }

    @Override
    protected String getFilesPrefix()
    {
        return "bootstrap/files/";
    }

    @Test
    public void evalTemplateFromChangedModel_1_3_2_nest_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "1_3_2_nest_changed_columns.tpl" );

        final String className = convertToTplClassName( "1_3_2_nest_changed_columns.tpl" );
        LayoutTplElement layoutTpl = createModel_132_nest( isBootstrapStyle(), className );

        PortletLayoutElement row1 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 0 );
        PortletLayoutElement row2 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 1 );
        PortletLayoutElement row3 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 2 );

        PortletLayoutElement row311 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 0 );

        PortletLayoutElement row312 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 1 );

        PortletLayoutElement row31221 = row312.getPortletColumns().get( 1 ).getPortletLayouts().get( 0 );

        row1.getPortletColumns().remove( row1.getPortletColumns().get( 0 ) );
        layoutTpl.getPortletLayouts().remove( row1 );

        PortletColumnElement insertedColumn = row311.getPortletColumns().insert();
        insertedColumn.setWeight( 3 );
        row311.getPortletColumns().get( 0 ).setWeight( 9 );

        row2.getPortletColumns().remove( row2.getPortletColumns().get( 0 ) );
        row2.getPortletColumns().get( 0 ).setWeight( 8 );

        insertedColumn = row31221.getPortletColumns().insert();
        insertedColumn.setWeight( 2 );
        row31221.getPortletColumns().get( 0 ).setWeight( 10 );

        evalModelWithFile( refTplFile, layoutTpl );
    }

    protected LayoutTplElement createModel_132_nest( boolean isBootstrapStyle, String className )
    {
        LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( isBootstrapStyle );
        layoutTpl.setClassName( className );

        PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

        PortletColumnElement column11 = row1.getPortletColumns().insert();
        column11.setWeight( 12 );

        PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

        PortletColumnElement column21 = row2.getPortletColumns().insert();
        column21.setWeight( 4 );

        PortletColumnElement column22 = row2.getPortletColumns().insert();
        column22.setWeight( 4 );

        PortletColumnElement column23 = row2.getPortletColumns().insert();
        column23.setWeight( 4 );

        PortletLayoutElement row3 = layoutTpl.getPortletLayouts().insert();

        PortletColumnElement column31 = row3.getPortletColumns().insert();
        column31.setWeight( 8 );

        PortletLayoutElement row311 = column31.getPortletLayouts().insert();

        PortletColumnElement column3111 = row311.getPortletColumns().insert();
        column3111.setWeight( 12 );

        PortletLayoutElement row312 = column31.getPortletLayouts().insert();

        PortletColumnElement column3121 = row312.getPortletColumns().insert();
        column3121.setWeight( 6 );

        PortletColumnElement column3122 = row312.getPortletColumns().insert();
        column3122.setWeight( 6 );

        PortletLayoutElement row31221 = column3122.getPortletLayouts().insert();

        PortletColumnElement column312211 = row31221.getPortletColumns().insert();
        column312211.setWeight( 12 );

        PortletColumnElement column32 = row3.getPortletColumns().insert();
        column32.setWeight( 4 );

        return layoutTpl;
    }

}
