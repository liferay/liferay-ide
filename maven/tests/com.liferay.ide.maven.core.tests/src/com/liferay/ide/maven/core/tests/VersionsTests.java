/**
 *
 */

package com.liferay.ide.maven.core.tests;

import com.liferay.ide.core.ILiferayPortal;
import com.liferay.ide.core.ILiferayProject;
import com.liferay.ide.core.LiferayCore;

import org.eclipse.core.resources.IProject;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.project.IMavenProjectFacade;
import org.eclipse.m2e.tests.common.AbstractMavenProjectTestCase;
import org.eclipse.m2e.tests.common.RequireMavenExecutionContext;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
@RequireMavenExecutionContext
public class VersionsTests extends AbstractMavenProjectTestCase
{

    @Test
    public void testFindLiferayVersionByDeps() throws Exception
    {
        IProject project = importProject( "projects/versions/deps-portlet/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        final ILiferayProject lrproject = LiferayCore.create( project );

        assertNotNull( lrproject );

        final ILiferayPortal portal = lrproject.adapt( ILiferayPortal.class );

        assertNotNull( portal );

        assertEquals( "6.2.1", portal.getVersion() );
    }

    @Test
    public void testFindLiferayVersionByProperties() throws Exception
    {
        IProject project = importProject( "projects/versions/properties-portlet/pom.xml" );

        assertNotNull( project );

        IMavenProjectFacade facade = MavenPlugin.getMavenProjectRegistry().create( project, monitor );

        assertNotNull( facade );

        final ILiferayProject lrproject = LiferayCore.create( project );

        assertNotNull( lrproject );

        final ILiferayPortal portal = lrproject.adapt( ILiferayPortal.class );

        assertNotNull( portal );

        assertEquals( "6.2.1", portal.getVersion() );
    }


}
