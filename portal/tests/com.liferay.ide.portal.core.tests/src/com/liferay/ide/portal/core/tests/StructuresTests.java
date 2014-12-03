
package com.liferay.ide.portal.core.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import com.liferay.ide.portal.core.structures.model.DynamicElement;
import com.liferay.ide.portal.core.structures.model.DynamicElementMetadata;
import com.liferay.ide.portal.core.structures.model.Entry;
import com.liferay.ide.portal.core.structures.model.Root;
import com.liferay.ide.portal.core.structures.model.Structure;

import java.io.InputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementList;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.modeling.xml.XmlResourceStore;
import org.junit.After;
import org.junit.Test;

/**
 * @author Gregory Amerson
 */
public class StructuresTests extends PortalCoreTests
{

    static final IPath DDM_STRUCTURE_BASIC_DOCUMENT = new Path( "structures/ddm_structure_basic_document.xml" );
    static final IPath DOCUMENT_LIBRARY_STRUCTURES = new Path( "structures/document-library-structures.xml" );
    static final IPath DYNAMIC_DATA_MAPPING_STRUCTURES = new Path(
        "structures/dynamic-data-mapping-structures.xml" );
    static final IPath TEST_DDM_STRUCTURE_ALL_FIELDS =
        new Path( "structures/test-ddm-structure-all-fields.xml" );
    static final IPath TEST_JOURNAL_CONTENT_BOOLEAN_REPEATABLE_FIELD = new Path(
        "structures/test-journal-content-boolean-repeatable-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_DOC_LIBRARY_FIELD = new Path(
        "structures/test-journal-content-doc-library-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_LINK_TO_PAGE_FIELD = new Path(
        "structures/test-journal-content-link-to-page-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_LIST_FIELD = new Path( "structures/test-journal-content-list-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_MULTI_LIST_FIELD = new Path(
        "structures/test-journal-content-multi-list-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_NESTED_FIELDS = new Path(
        "structures/test-journal-content-nested-fields.xml" );
    static final IPath TEST_JOURNAL_CONTENT_TEXT_AREA_FIELD = new Path(
        "structures/test-journal-content-text-area-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_TEXT_BOX_REPEATABLE_FIELD = new Path(
        "structures/test-journal-content-text-box-repeatable-field.xml" );
    static final IPath TEST_JOURNAL_CONTENT_TEXT_FIELD = new Path( "structures/test-journal-content-text-field.xml" );

    private Element currentElement;

    protected Element getElementFromFile( IProject project, IPath filePath, ElementType type ) throws Exception
    {
        final String filePathValue = filePath.toOSString();
        final IFile file = createFile( project, filePathValue, this.getClass().getResourceAsStream( filePathValue ) );

        assertEquals( file.getFullPath().lastSegment(), filePath.lastSegment() );

        final InputStream contents = file.getContents();
        final Element element = type.instantiate( new RootXmlResource( new XmlResourceStore( contents ) ) );

        contents.close();

        return element;
    }

    @After
    public void cleanup() throws Exception
    {
        if( this.currentElement != null )
        {
            if( ! this.currentElement.disposed() )
            {
                this.currentElement.dispose();
            }

            this.currentElement = null;
        }

        super.cleanup();
    }

    @Test
    public void testDDMStructureBasicDocumentRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DDM_STRUCTURE_BASIC_DOCUMENT, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        assertEquals( "en_US", root.getAvailableLocales().content() );

        assertEquals( "en_US", root.getDefaultLocale().content() );

        final ElementList<DynamicElement> dynamicElements = root.getDynamicElements();

        assertEquals( 154, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );

        assertEquals( "string", dynamicElement.getDataType().content( false ) );

        assertEquals( "ClimateForcast_COMMAND_LINE", dynamicElement.getName().content( false ) );

        assertEquals( "text", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metaData = dynamicElement.getMetadata().content( false );

        assertNotNull( metaData );

        assertEquals( "en_US", metaData.getLocale().content( false ) );

        final ElementList<Entry> entries = metaData.getEntries();

        assertNotNull( entries );

        assertEquals( 4, entries.size() );

        final Entry entry = entries.get( 2 );

        assertNotNull( entry );

        assertEquals( "required", entry.getName().content( false ) );

        assertEquals( "false", entry.getValue().content( false ) );
    }

    private void setElement( Element element )
    {
        assertNotNull( element );

        this.currentElement = element;
    }

    @Test
    public void testDocumentLibraryStructuresRead() throws Exception
    {
        final Element element = getElementFromFile( getCurrentProject(), DOCUMENT_LIBRARY_STRUCTURES, Root.TYPE );

        setElement( element );

        final Root root = element.nearest( Root.class );

        assertNotNull( root );

        final ElementList<Structure> structures = root.getStructures();

        assertNotNull( structures );
        assertEquals( 8, structures.size() );

        final Structure structure = structures.get( 2 );

        assertNotNull( structure );
        assertEquals( "Learning Module Metadata", structure.getName().content( false ) );
        assertEquals( "Learning Module Metadata", structure.getDescription().content( false ) );

        final Root structureRoot = structure.getRoot().content( false );

        assertNotNull( structureRoot );
        assertEquals( "[$LOCALE_DEFAULT$]", structureRoot.getAvailableLocales().content( false ) );
        assertEquals( "[$LOCALE_DEFAULT$]", structureRoot.getDefaultLocale().content( false ) );

        final ElementList<DynamicElement> dynamicElements = structureRoot.getDynamicElements();

        assertNotNull( dynamicElements );
        assertEquals( 4, dynamicElements.size() );

        final DynamicElement dynamicElement = dynamicElements.get( 1 );

        assertNotNull( dynamicElement );
        assertEquals( "string", dynamicElement.getDataType().content( false ) );
        assertEquals( "keyword", dynamicElement.getIndexType().content( false ) );
        assertEquals( true, dynamicElement.isMultiple().content( false ) );
        assertEquals( "select3212", dynamicElement.getName().content( false ) );
        assertEquals( false, dynamicElement.isReadOnly().content( false ) );
        assertEquals( false, dynamicElement.isRequired().content( false ) );
        assertEquals( true, dynamicElement.isShowLabel().content( false ) );
        assertEquals( "select", dynamicElement.getType().content( false ) );

        final DynamicElementMetadata metadata = dynamicElement.getMetadata().content( false );

        assertNotNull( metadata );
        assertEquals( "[$LOCALE_DEFAULT$]", metadata.getLocale().content( false ) );

        final ElementList<Entry> entries = metadata.getEntries();

        assertNotNull( entries );
        assertEquals( 3, entries.size() );

        final ElementList<DynamicElement> childDynamicElements = dynamicElement.getDynamicElements();

        assertNotNull( childDynamicElements );
        assertEquals( 3, childDynamicElements.size() );

        final DynamicElement childDynamicElement = childDynamicElements.get( 1 );

        assertNotNull( childDynamicElement );
        assertEquals( "2_0", childDynamicElement.getName().content( false ) );
        assertEquals( "option", childDynamicElement.getType().content( false ) );
        assertEquals( "2", childDynamicElement.getValue().content( false ) );

        final DynamicElementMetadata childMetadata = childDynamicElement.getMetadata().content( false );

        assertNotNull( childMetadata );
        assertEquals( "[$LOCALE_DEFAULT$]", childMetadata.getLocale().content( false ) );

        final ElementList<Entry> childEntries = childMetadata.getEntries();

        assertNotNull( childEntries );
        assertEquals( 1, childEntries.size() );

        final Entry childEntry = childEntries.get( 0 );

        assertNotNull( childEntry );
        assertEquals( "label", childEntry.getName().content( false ) );
        assertEquals( "2.0", childEntry.getValue().content( false ) );
    }

}
