package com.liferay.ide.layouttpl.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;

import org.eclipse.core.resources.IFile;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.services.ValidationService;
import org.junit.Test;


/**
 * @author Kuo Zhang
 *
 */
public class LayoutTplTestsBootstrap extends LayoutTplCoreTests
{

    protected LayoutTplElement createModel_132_nest( boolean isBootstrapStyle, String className, boolean is62)
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( isBootstrapStyle );
        layoutTpl.setClassName( className );
        layoutTpl.setIs62( is62 );

        final PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

        final PortletColumnElement column11 = row1.getPortletColumns().insert();
        column11.setWeight( 12 );

        final PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

        final PortletColumnElement column21 = row2.getPortletColumns().insert();
        column21.setWeight( 4 );

        final PortletColumnElement column22 = row2.getPortletColumns().insert();
        column22.setWeight( 4 );

        final PortletColumnElement column23 = row2.getPortletColumns().insert();
        column23.setWeight( 4 );

        final PortletLayoutElement row3 = layoutTpl.getPortletLayouts().insert();

        final PortletColumnElement column31 = row3.getPortletColumns().insert();
        column31.setWeight( 8 );

        final PortletLayoutElement row311 = column31.getPortletLayouts().insert();

        final PortletColumnElement column3111 = row311.getPortletColumns().insert();
        column3111.setWeight( 12 );

        final PortletLayoutElement row312 = column31.getPortletLayouts().insert();

        final PortletColumnElement column3121 = row312.getPortletColumns().insert();
        column3121.setWeight( 6 );

        final PortletColumnElement column3122 = row312.getPortletColumns().insert();
        column3122.setWeight( 6 );

        final PortletLayoutElement row31221 = column3122.getPortletLayouts().insert();

        final PortletColumnElement column312211 = row31221.getPortletColumns().insert();
        column312211.setWeight( 12 );

        final PortletColumnElement column32 = row3.getPortletColumns().insert();
        column32.setWeight( 4 );

        return layoutTpl;
    }

    @Test
    public void evalTemplateFromChangedModel_1_3_2_nest_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "1_3_2_nest_changed_columns.tpl" );

        final String className = convertToTplClassName( "1_3_2_nest_changed_columns.tpl" );
        final LayoutTplElement layoutTpl = createModel_132_nest( isBootstrapStyle(), className, is62());

        final PortletLayoutElement row1 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 0 );
        final PortletLayoutElement row2 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 1 );
        final PortletLayoutElement row3 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 2 );

        final PortletLayoutElement row311 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 0 );

        final PortletLayoutElement row312 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 1 );

        final PortletLayoutElement row31221 = row312.getPortletColumns().get( 1 ).getPortletLayouts().get( 0 );

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

    @Override
    protected String getFilesPrefix()
    {
        return "bootstrap/files/";
    }

    @Override
    protected boolean isBootstrapStyle()
    {
        return true;
    }

    @Override
    protected boolean is62()
    {
        return true;
    }

    @Test
    public void testPorteltColumnWeightValidationService()
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( true );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
        final PortletColumnElement column = row.getPortletColumns().insert();

        final ValidationService validationService = column.getWeight().service( ValidationService.class );

        column.setWeight( 0 );
        assertEquals( "The weight value is invalid, should be in (0, 12]", validationService.validation().message() );

        column.setWeight( -1 );
        assertEquals( "The weight value is invalid, should be in (0, 12]", validationService.validation().message() );

        column.setWeight( 13 );
        assertEquals( "The weight value is invalid, should be in (0, 12]", validationService.validation().message() );

        column.setWeight( 6 );
        assertEquals( "ok", validationService.validation().message() );
    }

    @Test
    public void testPortletColumnFullWeightDefaultValueService() throws Exception
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

        layoutTpl.setBootstrapStyle( true );

        final PortletColumnElement column = layoutTpl.getPortletLayouts().insert().getPortletColumns().insert();

        assertEquals( 12, column.getFullWeight().content( true ).intValue() );
    }

    // test sum of column weights
    @Test
    public void testPortletColumnsValidationSerive()
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( true );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
        final ElementList<PortletColumnElement> columns = row.getPortletColumns();
        final PortletColumnElement column = columns.insert();

        final ValidationService validationService = columns.service( ValidationService.class );

        assertEquals( "ok", validationService.validation().message() );

        column.setWeight( 0 );
        assertEquals( "The sum of weight of columns should be: 12", validationService.validation().message() );

        column.setWeight( -1 );
        assertEquals( "The sum of weight of columns should be: 12", validationService.validation().message() );

        column.setWeight( 6 );
        assertEquals( "The sum of weight of columns should be: 12", validationService.validation().message() );

        column.setWeight( 13 );
        assertEquals( "The sum of weight of columns should be: 12", validationService.validation().message() );

        column.setWeight( 12 );
        assertEquals( "ok", validationService.validation().message() );
    }

    @Test
    public void testPortletColumnWeightInitialValueService() throws Exception
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( true );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
        final ElementList<PortletColumnElement> columns = row.getPortletColumns();

        columns.insert();
        columns.insert();
        columns.insert();
        columns.insert();

        assertEquals( 6, columns.get( 0 ).getWeight().content().intValue() );
        assertEquals( 3, columns.get( 1 ).getWeight().content().intValue() );
        assertEquals( 2, columns.get( 2 ).getWeight().content().intValue() );
        assertEquals( 1, columns.get( 3 ).getWeight().content().intValue() );

        columns.get( 0 ).setWeight( 2 );
        columns.get( 1 ).setWeight( 2 );
        columns.get( 2 ).setWeight( 2 );
        columns.get( 3 ).setWeight( 2 );

        columns.insert();
        assertEquals( 4, columns.get( 4 ).getWeight().content().intValue() );
    }

    @Test
    public void testPortletLayoutClassNameDefaultValueService() throws Exception
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

        layoutTpl.setBootstrapStyle( true );
        layoutTpl.setIs62( true );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();

        assertEquals( "portlet-layout row-fluid", row.getClassName().content( true ) );
    }

}
