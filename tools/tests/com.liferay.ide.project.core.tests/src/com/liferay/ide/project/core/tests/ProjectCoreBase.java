package com.liferay.ide.project.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.project.core.model.NewLiferayPluginProjectOp;
import com.liferay.ide.project.core.util.ProjectUtil;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;


public class ProjectCoreBase extends BaseTests
{

    protected IProject createProject( NewLiferayPluginProjectOp op )
    {
        Status status = op.execute( ProgressMonitorBridge.create( new NullProgressMonitor() ) );

        assertNotNull( status );

        assertEquals(
            status.toString(),
            Status.createOkStatus().message().toLowerCase(), status.message().toLowerCase() );

        final IProject newLiferayPluginProject = project( op.getFinalProjectName().content() );

        assertNotNull( newLiferayPluginProject );

        assertEquals( true, newLiferayPluginProject.exists() );

        final IFacetedProject facetedProject = ProjectUtil.getFacetedProject( newLiferayPluginProject );

        assertNotNull( facetedProject );

        final IProjectFacet liferayFacet = ProjectUtil.getLiferayFacet( facetedProject );

        assertNotNull( liferayFacet );

        assertEquals( "liferay." + op.getPluginType().content( true ), liferayFacet.getId() );

        return newLiferayPluginProject;
    }

}
