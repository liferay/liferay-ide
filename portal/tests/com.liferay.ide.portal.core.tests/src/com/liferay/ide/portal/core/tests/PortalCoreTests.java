
package com.liferay.ide.portal.core.tests;

import static org.junit.Assert.assertEquals;

import com.liferay.ide.core.tests.BaseTests;
import com.liferay.ide.portal.core.model.StructuresRoot;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.junit.Before;
import org.junit.Test;

public class PortalCoreTests extends BaseTests
{

    private static final String STRUCTURES_TODO_XML = "structures/todo.xml";
    private IProject a;

    @Before
    public void createTestProject() throws Exception
    {
        deleteProject( "a" );
        this.a = createProject( "a" );
    }

    @Test
    public void testStructureModelReading() throws Exception
    {
        final IFile todoFile =
            createFile( this.a, STRUCTURES_TODO_XML, this.getClass().getResourceAsStream( STRUCTURES_TODO_XML ) );

        assertEquals( todoFile.getFullPath().lastSegment(), "todo.xml" );

        final StructuresRoot todo =
            StructuresRoot.TYPE.instantiate( new RootXmlResource( new XmlResourceStore( todoFile.getContents() ) ) );

        assertEquals( todo.getAvailableLocales().content(), "en_US" );
        assertEquals( todo.getDefaultLocale().content(), "en_US" );
    }

}
