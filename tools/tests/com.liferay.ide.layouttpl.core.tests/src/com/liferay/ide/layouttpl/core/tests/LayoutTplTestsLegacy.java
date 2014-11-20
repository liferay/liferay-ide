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
public class LayoutTplTestsLegacy extends LayoutTplCoreTests
{

    @Override
    protected LayoutTplElement createModel_132_nest( boolean isBootstrap, String className )
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( isBootstrapStyle() );
        layoutTpl.setClassName( className );

        final PortletLayoutElement row1 = layoutTpl.getPortletLayouts().insert();

        final PortletColumnElement column11 = row1.getPortletColumns().insert();
        column11.setWeight( 100 );

        final PortletLayoutElement row2 = layoutTpl.getPortletLayouts().insert();

        final PortletColumnElement column21 = row2.getPortletColumns().insert();
        column21.setWeight( 33 );

        final PortletColumnElement column22 = row2.getPortletColumns().insert();
        column22.setWeight( 33 );

        final PortletColumnElement column23 = row2.getPortletColumns().insert();
        column23.setWeight( 33 );

        final PortletLayoutElement row3 = layoutTpl.getPortletLayouts().insert();

        final PortletColumnElement column31 = row3.getPortletColumns().insert();
        column31.setWeight( 66 );

        final PortletLayoutElement row311 = column31.getPortletLayouts().insert();

        final PortletColumnElement column3111 = row311.getPortletColumns().insert();
        column3111.setWeight( 100 );

        final PortletLayoutElement row312 = column31.getPortletLayouts().insert();

        final PortletColumnElement column3121 = row312.getPortletColumns().insert();
        column3121.setWeight( 50 );

        final PortletColumnElement column3122 = row312.getPortletColumns().insert();
        column3122.setWeight( 50 );

        final PortletLayoutElement row31221 = column3122.getPortletLayouts().insert();

        final PortletColumnElement column312211 = row31221.getPortletColumns().insert();
        column312211.setWeight( 100 );

        final PortletColumnElement column32 = row3.getPortletColumns().insert();
        column32.setWeight( 33 );

        return layoutTpl;
    }

    @Test
    public void evalTemplateFromChangedModel_1_3_2_nest_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "1_3_2_nest_changed_columns.tpl" );

        final String className = convertToTplClassName( "1_3_2_nest_changed_columns.tpl" );
        final LayoutTplElement layoutTpl = createModel_132_nest( isBootstrapStyle(), className );

        final PortletLayoutElement row1 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 0 );
        final PortletLayoutElement row2 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 1 );
        final PortletLayoutElement row3 = (PortletLayoutElement) layoutTpl.getPortletLayouts().get( 2 );

        final PortletLayoutElement row311 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 0 );

        final PortletLayoutElement row312 = row3.getPortletColumns().get( 0 ).getPortletLayouts().get( 1 );

        final PortletLayoutElement row31221 = row312.getPortletColumns().get( 1 ).getPortletLayouts().get( 0 );

        row1.getPortletColumns().remove( row1.getPortletColumns().get( 0 ) );
        layoutTpl.getPortletLayouts().remove( row1 );

        PortletColumnElement insertedColumn = row311.getPortletColumns().insert();
        insertedColumn.setWeight( 20 );
        row311.getPortletColumns().get( 0 ).setWeight( 80 );

        row2.getPortletColumns().remove( row2.getPortletColumns().get( 0 ) );
        row2.getPortletColumns().get( 0 ).setWeight( 66 );

        insertedColumn = row31221.getPortletColumns().insert();
        insertedColumn.setWeight( 10 );
        row31221.getPortletColumns().get( 0 ).setWeight( 90 );

        evalModelWithFile( refTplFile, layoutTpl );
    }

    @Override
    protected String getFilesPrefix()
    {
        return "legacy/files/";
    }

    @Override
    protected boolean isBootstrapStyle()
    {
        return false;
    }

    @Test
    public void testPorteltColumnWeightValidationService()
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( false );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
        final PortletColumnElement column = row.getPortletColumns().insert();

        final ValidationService validationService = column.getWeight().service( ValidationService.class );

        column.setWeight( 0 );
        assertEquals( "The weight value is invalid, should be in (0, 100]", validationService.validation().message() );

        column.setWeight( -1 );
        assertEquals( "The weight value is invalid, should be in (0, 100]", validationService.validation().message() );

        column.setWeight( 101 );
        assertEquals( "The weight value is invalid, should be in (0, 100]", validationService.validation().message() );

        column.setWeight( 50 );
        assertEquals( "ok", validationService.validation().message() );
    }

    @Test
    public void testPortletColumnFullWeightDefaultValueService() throws Exception
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

        layoutTpl.setBootstrapStyle( false );

        final PortletColumnElement column = layoutTpl.getPortletLayouts().insert().getPortletColumns().insert();

        assertEquals( 100, column.getFullWeight().content( true ).intValue() );
    }

    // test sum of column weights
    @Test
    public void testPortletColumnsValidationSerive()
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( false );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
        final ElementList<PortletColumnElement> columns = row.getPortletColumns();
        final PortletColumnElement column = columns.insert();

        final ValidationService validationService = columns.service( ValidationService.class );

        assertEquals( "ok", validationService.validation().message() );

        column.setWeight( 0 );
        assertEquals( "The sum of weight of columns should be: 100", validationService.validation().message() );

        column.setWeight( -1 );
        assertEquals( "The sum of weight of columns should be: 100", validationService.validation().message() );

        column.setWeight( 50 );
        assertEquals( "The sum of weight of columns should be: 100", validationService.validation().message() );

        column.setWeight( 101 );
        assertEquals( "The sum of weight of columns should be: 100", validationService.validation().message() );

        column.setWeight( 100 );
        assertEquals( "ok", validationService.validation().message() );
    }

    @Test
    public void testPortletColumnWeightInitialValueService() throws Exception
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();
        layoutTpl.setBootstrapStyle( false );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();
        final ElementList<PortletColumnElement> columns = row.getPortletColumns();

        columns.insert();
        columns.insert();
        columns.insert();
        columns.insert();

        assertEquals( 50, columns.get( 0 ).getWeight().content().intValue() );
        assertEquals( 25, columns.get( 1 ).getWeight().content().intValue() );
        assertEquals( 13, columns.get( 2 ).getWeight().content().intValue() );
        assertEquals( 12, columns.get( 3 ).getWeight().content().intValue() );

        columns.get( 0 ).setWeight( 10 );
        columns.get( 1 ).setWeight( 10 );
        columns.get( 2 ).setWeight( 10 );
        columns.get( 3 ).setWeight( 10 );

        columns.insert();
        assertEquals( 60, columns.get( 4 ).getWeight().content().intValue() );
    }

    @Test
    public void testPortletLayoutClassNameDefaultValueService() throws Exception
    {
        final LayoutTplElement layoutTpl = LayoutTplElement.TYPE.instantiate();

        layoutTpl.setBootstrapStyle( false );

        final PortletLayoutElement row = layoutTpl.getPortletLayouts().insert();

        assertEquals( "portlet-layout", row.getClassName().content( true ) );
    }

}
