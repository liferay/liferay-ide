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
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramElement;
import com.liferay.ide.layouttpl.core.model.LayoutTplDiagramFactory;

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
 */
public class LayoutTplCoreTests extends BaseTests
{

    private static final String FILE_121_TPL = "files/1_2_1_columns.tpl";
    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    @Test
    public void testLayoutTpl_121() throws Exception
    {
        final IFile templateFile = createFile( this.a, FILE_121_TPL, this.getClass().getResourceAsStream( FILE_121_TPL ) );

        Assert.assertEquals( templateFile.getFullPath().lastSegment(), "1_2_1_columns.tpl" );

        Assert.assertEquals( true, templateFile.exists() );

        IStructuredModel templateXml = StructuredModelManager.getModelManager().getModelForEdit( templateFile );

        Assert.assertEquals( true, templateXml instanceof IDOMModel );

        IDOMModel templateXmlModel = (IDOMModel) templateXml;

        LayoutTplDiagramElement diagramElement = LayoutTplDiagramElement.createFromModel( templateXmlModel, LayoutTplDiagramFactory.INSTANCE );

        Assert.assertEquals( true, diagramElement != null );

        templateXml.releaseFromEdit();
    }

    @Test
    public void testLayoutTpl_121a() throws Exception
    {
        final IFile templateFile = createFile( this.a, FILE_121_TPL, this.getClass().getResourceAsStream( FILE_121_TPL ) );

        Assert.assertEquals( templateFile.getFullPath().lastSegment(), "1_2_1_columns.tpl" );

        Assert.assertEquals( true, templateFile.exists() );

        IStructuredModel templateXml = StructuredModelManager.getModelManager().getModelForEdit( templateFile );

        Assert.assertEquals( true, templateXml instanceof IDOMModel );

        IDOMModel templateXmlModel = (IDOMModel) templateXml;

        LayoutTplDiagramElement diagramElement = LayoutTplDiagramElement.createFromModel( templateXmlModel, LayoutTplDiagramFactory.INSTANCE );

        Assert.assertEquals( true, diagramElement != null );

        templateXml.releaseFromEdit();
    }

}
