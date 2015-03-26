/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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

package com.liferay.ide.xml.search.ui.tests;

import static com.liferay.ide.ui.tests.UITestsUtils.deleteOtherProjects;
import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.ILiferayConstants;
import com.liferay.ide.core.LiferayCore;
import com.liferay.ide.xml.search.ui.editor.LiferayCustomXmlViewerConfiguration;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.junit.Test;

/**
 * @author Kuo Zhang
 */
public class LiferayHookXmlTests extends XmlSearchTestsBase
{

    protected final static String MARKER_TYPE = XML_REFERENCES_MARKER_TYPE;
    private IFile descriptor;
    private IProject project;

    protected IFile getDescriptorFile() throws Exception
    {
        return descriptor != null ? descriptor : LiferayCore.create( getProject() ).getDescriptorFile(
            ILiferayConstants.LIFERAY_HOOK_XML_FILE );
    }

    private IProject getProject() throws Exception
    {
        if( project == null )
        {
            project = super.getProject( "hooks", "Liferay-Hook-Xml-Test-hook" );
            deleteOtherProjects( project );
        }

        return project;
    }

    // TODO
    protected void testIndexerClassNameContentAssist()
    {
    }

    // TODO
    protected void testIndexerClassNameHyperlink()
    {
    }

    // TODO
    private void testIndexerPostProcesserImplContentAssist()
    {
    }

    // TODO
    private void testIndexerPostProcesserImplHyperlink()
    {
    }

    // TODO
    protected void testLanguagePropertiesContentAssist()
    {
    }

    // TODO
    protected void testLanguagePropertiesHyperlink()
    {
    }

    // TODO
    private void testPortalPropertiesContentAssist()
    {
    }

    // TODO
    private void testPortalPropertiesQuickFix()
    {
    }

    // TODO
    protected void testServiceTypeAndServiceImplContentAssist()
    {
    }

    // TODO
    protected void testServiceTypeAndServiceImplHyperlink()
    {
    }

    // TODO
    private void testServletFilterImplContentAssist()
    {
    }

    // TODO
    private void testServletFilterImplHyperlink()
    {
    }

    @Test
    public void testSourceViewerConfiguration() throws Exception
    {
        if( shouldSkipBundleTests() )
        {
            return;
        }

        final IFile descriptorFile = getDescriptorFile();
        Object sourceViewerConfiguration =
            XmlSearchTestsUtils.getSourceViewerConfiguraionFromOpenedEditor( descriptorFile );

        assertEquals( true, sourceViewerConfiguration instanceof LiferayCustomXmlViewerConfiguration );
    }

    // TODO
    protected void testStrutsActionImplContentAssist()
    {
    }

    // TODO
    protected void testStrutsActionImplHyperlink()
    {
    }

}
