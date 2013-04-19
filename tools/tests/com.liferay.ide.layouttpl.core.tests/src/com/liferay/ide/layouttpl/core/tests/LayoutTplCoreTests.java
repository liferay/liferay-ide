/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 *******************************************************************************/

package com.liferay.ide.layouttpl.core.tests;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.FileUtil;
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramElement;
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramFactory;
import com.liferay.ide.layouttpl.core.model.PortletColumnElement;
import com.liferay.ide.layouttpl.core.model.PortletLayoutElement;
import com.liferay.ide.layouttpl.core.util.LayoutTplUtil;

import junit.framework.Assert;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.core.internal.provisional.IStructuredModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 * @author Cindy Li
 */
public class LayoutTplCoreTests extends BaseTests
{
    private static final String FILES_PREFIX = "files/";
    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    @Test
    public void evalTemplateFromFile_0_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "0_columns.tpl" );

        evalModelWithFile( "0_columns.tpl", refTplFile, new LayoutTplDiagramElement() );
    }

    @Test
    public void evalTemplateFromFile_1_2_1_columns() throws Exception
    {
        evalTemplateFromFile("1_2_1_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_1_3_1_columns() throws Exception
    {
        evalTemplateFromFile("1_3_1_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_1_3_2_columns() throws Exception
    {
        evalTemplateFromFile("1_3_2_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_1_3_2_nest_columns() throws Exception
    {
        evalTemplateFromFile("1_3_2_nest_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_2_1_2_columns() throws Exception
    {
        evalTemplateFromFile("2_1_2_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_3_2_3_columns() throws Exception
    {
        evalTemplateFromFile("3_2_3_columns.tpl");
    }

    @Test
    public void evalTemplateFromFile_empty() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "0_columns.tpl" );

        IFile tplFile = getFileFromTplName( "empty.tpl" );

        evalModelWithFile( "0_columns.tpl", refTplFile, createModelFromFile( tplFile ) );
    }

    @Test
    public void evalTemplateFromModel_1_3_2_nest_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "1_3_2_nest_columns.tpl" );

        evalModelWithFile( "1_3_2_nest_columns.tpl", refTplFile, createModel_132_nest() );
    }

    @Test
    public void evalTemplateFromChangedModel_1_3_2_nest_columns() throws Exception
    {
        IFile refTplFile = getFileFromTplName( "1_3_2_nest_changed_columns.tpl" );

        LayoutTplDiagramElement model = createModel_132_nest();

        PortletLayoutElement row1 = (PortletLayoutElement) model.getRows().get( 0 );
        PortletLayoutElement row2 = (PortletLayoutElement) model.getRows().get( 1 );
        PortletLayoutElement row3 = (PortletLayoutElement) model.getRows().get( 2 );

        PortletLayoutElement row3_c1_r1 =
            (PortletLayoutElement) ( (PortletColumnElement) row3.getColumns().get( 0 ) ).getRows().get( 0 );

        PortletLayoutElement row3_c1_r2 =
            (PortletLayoutElement) ( (PortletColumnElement) row3.getColumns().get( 0 ) ).getRows().get( 1 );

        PortletLayoutElement row3_c1_r2_c2_r1 =
            (PortletLayoutElement) ( (PortletColumnElement) row3_c1_r2.getColumns().get( 1 ) ).getRows().get( 0 );

        row1.removeColumn( ( PortletColumnElement ) row1.getColumns().get( 0 ) );
        model.removeRow( row1 );

        row3_c1_r1.addColumn( new PortletColumnElement( 20 ) );
        ( ( PortletColumnElement ) row3_c1_r1.getColumns().get( 0 )).setWeight( 80 );

        row2.removeColumn( (PortletColumnElement) row2.getColumns().get( 0 ) );
        ( ( PortletColumnElement ) row2.getColumns().get( 0 ) ).setWeight( 66 );

        row3_c1_r2_c2_r1.addColumn( new PortletColumnElement( 10 ) );
        ( ( PortletColumnElement ) row3_c1_r2_c2_r1.getColumns().get( 0 ) ).setWeight( 90 );

        evalModelWithFile( "1_3_2_nest_columns.tpl", refTplFile, model );
    }

    protected LayoutTplDiagramElement createModel_132_nest()
    {
        LayoutTplDiagramElement tpl = new LayoutTplDiagramElement();

        PortletLayoutElement row1 = new PortletLayoutElement();
        row1.addColumn( new PortletColumnElement( 100 ) );

        PortletLayoutElement row2 = new PortletLayoutElement();
        row2.addColumn( new PortletColumnElement( 33 ) );
        row2.addColumn( new PortletColumnElement( 33 ) );
        row2.addColumn( new PortletColumnElement( 33 ) );

        PortletLayoutElement row3 = new PortletLayoutElement();

        PortletColumnElement column1 = new PortletColumnElement( 66 );

        PortletLayoutElement row31 = new PortletLayoutElement();
        row31.addColumn( new PortletColumnElement( 100 ) );

        column1.addRow( row31  );

        PortletLayoutElement row32 = new PortletLayoutElement();
        row32.addColumn( new PortletColumnElement( 50 ) );

        PortletColumnElement column11 = new PortletColumnElement( 50 );

        PortletLayoutElement row321 = new PortletLayoutElement();
        row321.addColumn( new PortletColumnElement( 100 ) );

        column11.addRow( row321  );

        row32.addColumn( column11 );

        column1.addRow( row32   );
        row3.addColumn( column1 );

        row3.addColumn( new PortletColumnElement( 33 ) );

        tpl.addRow( row1 );
        tpl.addRow( row2 );
        tpl.addRow( row3 );

        return tpl;
    }

    protected LayoutTplDiagramElement createModelFromFile( IFile templateFile ) throws Exception
    {
        IStructuredModel templateXml = StructuredModelManager.getModelManager().getModelForEdit( templateFile );

        Assert.assertEquals( true, templateXml instanceof IDOMModel );

        IDOMModel templateXmlModel = (IDOMModel) templateXml;

        templateXml.releaseFromEdit();

        LayoutTplDiagramElement diagramElement = LayoutTplDiagramElement.createFromModel( templateXmlModel, LayoutTplDiagramFactory.INSTANCE );

        return diagramElement;
    }

    protected void evalModelWithFile( String tplName, IFile refTplFile, LayoutTplDiagramElement diagramElement )
    {
        Assert.assertEquals( true, diagramElement != null );

        //assume file name is "n_n_n_columns.*" and want "columns-n-n-n"
        String templateSource = LayoutTplUtil.getTemplateSource( diagramElement, "columns-" + tplName.replaceAll( "_columns\\..*", "" ).replaceAll( "_", "-" ) );

        Assert.assertEquals( false, templateSource.isEmpty() );

        String inputString = FileUtil.readContents( refTplFile.getLocation().toFile(), true ).trim();

        inputString = inputString.replaceAll( "\r", "" );
        templateSource = templateSource.replaceAll( "\r", "" );

        Assert.assertEquals( true, inputString.equals( templateSource ) );
    }

    protected void evalTemplateFromFile(String tplName) throws Exception
    {
        IFile tplFile = getFileFromTplName( tplName );

        evalModelWithFile( tplName, tplFile, createModelFromFile( tplFile ) );
    }

    protected IFile getFileFromTplName( String tplName ) throws Exception
    {
        final IFile templateFile = createFile( this.a, FILES_PREFIX + tplName, this.getClass().getResourceAsStream( FILES_PREFIX + tplName ) );

        Assert.assertEquals( templateFile.getFullPath().lastSegment(), tplName );

        Assert.assertEquals( true, templateFile.exists() );

        return templateFile;
    }

}
