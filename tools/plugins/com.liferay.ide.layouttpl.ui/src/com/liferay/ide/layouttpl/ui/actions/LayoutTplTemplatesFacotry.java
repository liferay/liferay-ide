package com.liferay.ide.layouttpl.ui.actions;

import com.liferay.ide.layouttpl.core.model.CanAddPortletLayouts;
import com.liferay.ide.layouttpl.core.model.LayoutTplElement;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;


/**
 * @author Kuo Zhang
 *
 * create layout templates and row templates
 */
public class LayoutTplTemplatesFacotry
{
    // *** Row Templates ***

    public static void add_2_I_Columns( CanAddPortletLayouts element )
    {
        PortletLayoutElement row = element.getPortletLayouts().insert();
        PortletColumnElement column1 = row.getPortletColumns().insert();
        PortletColumnElement column2 = row.getPortletColumns().insert();

        column1.setWeight( column1.getFullWeight().content() / 2 );
        column2.setWeight( column1.getFullWeight().content() / 2 );
    }

    public static void add_2_II_Columns( CanAddPortletLayouts element )
    {
        boolean isBootstrap = isBootstrapStyle( element );
        PortletLayoutElement row = element.getPortletLayouts().insert();
        PortletColumnElement column1 = row.getPortletColumns().insert();
        PortletColumnElement column2 = row.getPortletColumns().insert();

        if( isBootstrap )
        {
            column1.setWeight( 4 );
            column2.setWeight( 8 );
        }
        else
        {
            column1.setWeight( 30 );
            column2.setWeight( 70 );
        }
    }

    public static void add_2_III_Columns( CanAddPortletLayouts element )
    {
        boolean isBootstrap = isBootstrapStyle( element );
        PortletLayoutElement row = element.getPortletLayouts().insert();
        PortletColumnElement column1 = row.getPortletColumns().insert();
        PortletColumnElement column2 = row.getPortletColumns().insert();

        if( isBootstrap )
        {
            column1.setWeight( 8 );
            column2.setWeight( 4 );
        }
        else
        {
            column1.setWeight( 70 );
            column2.setWeight( 30 );
        }
    }

    public static void add_3_Columns( CanAddPortletLayouts element )
    {
        PortletLayoutElement row = element.getPortletLayouts().insert();
        PortletColumnElement column1 = row.getPortletColumns().insert();
        PortletColumnElement column2 = row.getPortletColumns().insert();
        PortletColumnElement column3 = row.getPortletColumns().insert();

        column1.setWeight( column1.getFullWeight().content() / 3 );
        column2.setWeight( column1.getFullWeight().content() / 3 );
        column3.setWeight( column1.getFullWeight().content() / 3 );
    }


    //*** Layout Templates ***


    public static void add_1_2_I_Rows( LayoutTplElement element )
    {
        boolean isBootstrap = isBootstrapStyle( element );

        PortletLayoutElement row1 = element.getPortletLayouts().insert(); 

        PortletColumnElement column11 = row1.getPortletColumns().insert();
        column11.setWeight( column11.getFullWeight().content() );

        PortletLayoutElement row2 = element.getPortletLayouts().insert();
        PortletColumnElement column21 = row2.getPortletColumns().insert();
        PortletColumnElement column22 = row2.getPortletColumns().insert();

        if( isBootstrap )
        {
            column21.setWeight( 4 );
            column22.setWeight( 8 );
        }
        else
        {
            column21.setWeight( 30 );
            column22.setWeight( 70 );
        }
    }

    public static void add_1_2_II_Rows( LayoutTplElement element )
    {
        boolean isBootstrap = isBootstrapStyle( element );

        PortletLayoutElement row1 = element.getPortletLayouts().insert(); 

        PortletColumnElement column11 = row1.getPortletColumns().insert();
        column11.setWeight( column11.getFullWeight().content() );

        PortletLayoutElement row2 = element.getPortletLayouts().insert();
        PortletColumnElement column21 = row2.getPortletColumns().insert();
        PortletColumnElement column22 = row2.getPortletColumns().insert();

        if( isBootstrap )
        {
            column21.setWeight( 8 );
            column22.setWeight( 4 );
        }
        else
        {
            column21.setWeight( 70 );
            column22.setWeight( 30 );
        }
    }

    public static void add_1_2_1_Rows( LayoutTplElement element )
    {
        PortletLayoutElement row1 = element.getPortletLayouts().insert();
        PortletColumnElement column11 = row1.getPortletColumns().insert();
        column11.setWeight( column11.getFullWeight().content() );

        PortletLayoutElement row2 = element.getPortletLayouts().insert();
        PortletColumnElement column21 = row2.getPortletColumns().insert();
        column21.setWeight( column21.getFullWeight().content() / 2 );
        PortletColumnElement column22 = row2.getPortletColumns().insert();
        column22.setWeight( column22.getFullWeight().content() / 2 );

        PortletLayoutElement row3 = element.getPortletLayouts().insert();
        PortletColumnElement column31 = row3.getPortletColumns().insert();
        column31.setWeight( column31.getFullWeight().content() );
    }

    public static void add_2_2_Rows( LayoutTplElement element )
    {
        boolean isBootstrap = isBootstrapStyle( element );

        PortletLayoutElement row1 = element.getPortletLayouts().insert();
        PortletColumnElement column11 = row1.getPortletColumns().insert();
        PortletColumnElement column12 = row1.getPortletColumns().insert();

        PortletLayoutElement row2 = element.getPortletLayouts().insert();
        PortletColumnElement column21 = row2.getPortletColumns().insert();
        PortletColumnElement column22 = row2.getPortletColumns().insert();

        if( isBootstrap )
        {
            column11.setWeight( 4 );
            column12.setWeight( 8 );
            column21.setWeight( 8 );
            column22.setWeight( 4 );
        }
        else
        {
            column11.setWeight( 30 );
            column12.setWeight( 70 );
            column21.setWeight( 70 );
            column22.setWeight( 30 );
        } 
    }

    private static boolean isBootstrapStyle( CanAddPortletLayouts element )
    {
      return element.nearest( LayoutTplElement.class ).getBootstrapStyle().content();
    }
}