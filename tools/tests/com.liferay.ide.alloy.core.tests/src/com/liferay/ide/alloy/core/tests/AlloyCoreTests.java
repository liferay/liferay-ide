
package com.liferay.ide.alloy.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.alloy.core.AlloyCore;
import com.liferay.ide.alloy.core.LautRunner;
import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.core.util.CoreUtil;

import java.io.ByteArrayInputStream;
import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class AlloyCoreTests extends BaseTests
{

    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }


    @Test
    public void testLautInstallation() throws Exception
    {
        final LautRunner lautRunner = AlloyCore.getLautRunner();

        assertNotNull( lautRunner );

        final String execPath = lautRunner.getExecPath();

        assertNotNull( execPath );

        final File exec = new File( execPath );

        assertEquals( true, exec.exists() );

        lautRunner.exec( a, new NullProgressMonitor() );
    }

    @Test
    public void testUpgrade() throws Exception
    {
        final IFolder cssFolder = this.a.getFolder( new Path( "docroot/css/" ) );

        CoreUtil.prepareFolder( cssFolder );

        assertEquals( true, cssFolder.exists() );

        final IFile cssFile = cssFolder.getFile( "main.css" );

        final String originalMainCss = CoreUtil.readStreamToString(
            this.getClass().getResourceAsStream( "files/01_events-display-portlet/docroot/css/main.css" ) );

        final NullProgressMonitor npm = new NullProgressMonitor();

        cssFile.create( new ByteArrayInputStream( originalMainCss.getBytes() ), true, npm );

        assertEquals( true, cssFile.exists() );

        AlloyCore.getLautRunner().exec( a, npm );

        final String expectedMainCss =
            CoreUtil.readStreamToString( this.getClass().getResourceAsStream(
                "files/02_events-display-portlet/docroot/css/main.css" ) );

        assertEquals( expectedMainCss, CoreUtil.readStreamToString( cssFile.getContents() ) );
    }

}
