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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.liferay.ide.core.util.ReflectionUtil;
import com.liferay.ide.core.util.StringUtil;
import com.liferay.ide.ui.tests.UITestsUtils;
import com.liferay.ide.xml.search.ui.AddResourceKeyMarkerResolution;
import com.liferay.ide.xml.search.ui.editor.CompoundRegion;
import com.liferay.ide.xml.search.ui.editor.InfoRegion;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextHover;
import org.eclipse.jface.text.Region;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.ExtendedConfigurationBuilder;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMAttr;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMElement;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMText;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Some methods are modified from eclipse wst sse tests
 *
 * @author Kuo Zhang
 * @author Terry Jia
 */
@SuppressWarnings( "restriction" )
public class XmlSearchTestsUtils extends UITestsUtils
{

    private static ValManager valManager = ValManager.getDefault();

    public static void buildAndValidate( IFile file ) throws Exception
    {
        valManager.validate( file.getProject(), file, IResourceDelta.CHANGED, ValType.Build,
                             IncrementalProjectBuilder.FULL_BUILD, new ValOperation(), new NullProgressMonitor() );
    }
    public static boolean checkMarkerByMessage( IResource resource, String markerType,
                                                String markerMessage, boolean fullMatch ) throws Exception
    {
        return findMarkerByMessage( resource, markerType, markerMessage, fullMatch ) != null;
    }

    public static boolean checkNoMarker( IResource resource, String markerType ) throws Exception
    {
        return resource.findMarkers( markerType, false, IResource.DEPTH_ZERO ).length == 0;
    }

    // check if the excepted hyperlink is in the given proposals
    public static boolean containHyperlink( IHyperlink[] hyperlinks, String exceptedHyperlinkText, boolean fullMatch )
    {
        for( IHyperlink hyperlink : hyperlinks )
        {
            if( fullMatch && hyperlink.getHyperlinkText().equals( exceptedHyperlinkText ) )
            {
                return true;
            }
            else if( ! fullMatch && hyperlink.getHyperlinkText().contains( exceptedHyperlinkText ) )
            {
                return true;
            }
        }

        return false;
    }

    public static IMarker findMarkerByMessage( IResource resource, String markerType,
                                               String markerMessage, boolean fullMatch ) throws Exception
    {
        final IMarker[] markers = resource.findMarkers( markerType, false, IResource.DEPTH_ZERO );

        for( IMarker marker : markers )
        {
            if( fullMatch && marker.getAttribute( IMarker.MESSAGE ).toString().equals( markerMessage ) )
            {
                return marker;
            }
            else if( ! fullMatch && marker.getAttribute( IMarker.MESSAGE ).toString().matches( markerMessage ) )
            {
                return marker;
            }
        }

        return null;
    }

    public static IMarkerResolution findMarkerResolutionByClass( IMarker marker, Class<? extends IMarkerResolution> clazz )
    {
        IMarkerResolution[] markerResolutions = IDE.getMarkerHelpRegistry().getResolutions( marker );

        for( IMarkerResolution markerResolution : markerResolutions )
        {
            if( markerResolution.getClass().equals( clazz ) )
            {
                return markerResolution;
            }
        }

        return null;
    }

    public static int getAttrValueOffset( IFile file, String elementName, String attrName ) throws Exception
    {
        final IDOMModel domModel = getDOMModel( file, false );
        final Node attrNode = domModel.getDocument().getElementsByTagName( elementName ).
                              item( 0 ).getAttributes().getNamedItem( attrName );

        int retval = getRegion( attrNode ).getOffset();

        domModel.releaseFromRead();

        return retval;

    }

    public static int getElementContentOffset( IFile file, String elementName ) throws Exception
    {
        final IDOMModel model = getDOMModel( file, false );

        final Node element = model.getDocument().getElementsByTagName( elementName ).item( 0 );

        int retval = getRegion( element.getFirstChild() ).getOffset();

        model.releaseFromRead();

        return retval;
    }

    private static IHyperlink[] getHyperLinks( IFile file, int nodeType, String... nodeNames ) throws Exception
    {
        List<IHyperlink> retval = new ArrayList<IHyperlink>();
        IDOMModel domModel = null;
        Node targetNode = null;

        if( nodeType == Node.ELEMENT_NODE )
        {
            String elementName = nodeNames[0];
            domModel = getDOMModel( file, false );

            // the actual node is text node of this.element
            targetNode = domModel.getDocument().getElementsByTagName( elementName ).item( 0 ).getFirstChild();
        }
        else if( nodeType == Node.ATTRIBUTE_NODE )
        {
            String elementName = nodeNames[0];
            String attrName = nodeNames[1];

            domModel = getDOMModel( file, false );
            targetNode = domModel.getDocument().getElementsByTagName( elementName ). item( 0 ).
                         getAttributes().getNamedItem( attrName );
        }
        else
        {
            return new IHyperlink[0];
        }

        assertNotNull( targetNode );

        final StructuredTextViewer viewer = getEditor( file ).getTextViewer();
        final SourceViewerConfiguration conf = getSourceViewerConfiguraionFromExtensionPoint( file );

        final IHyperlinkDetector[] hyperlinkDetectors = conf.getHyperlinkDetectors( viewer );
        final IRegion region = getRegion( targetNode );

        for( IHyperlinkDetector detector : hyperlinkDetectors )
        {
            IHyperlink[] tempHyperlinks = detector.detectHyperlinks( viewer, region, true );

            if( tempHyperlinks != null && tempHyperlinks.length > 0 )
            {
                retval.addAll( Arrays.asList( tempHyperlinks ) );
            }
        }

        domModel.releaseFromRead();

        return retval.toArray( new IHyperlink[0] );
    }

    public static IHyperlink[] getHyperLinksForAttr( IFile file, String elementName, String attrName ) throws Exception
    {
        return getHyperLinks( file, Node.ATTRIBUTE_NODE, elementName, attrName );
    }

    public static IHyperlink[] getHyperLinksForElement( IFile file, String elementName ) throws Exception
    {
        return getHyperLinks( file, Node.ELEMENT_NODE, elementName );
    }

    private static ICompletionProposal[] getProposals( IFile file, int nodeType, String... nodeNames ) throws Exception
    {
        Node targetNode = null;
        IDOMModel domModel = null;

        if( nodeType == Node.ELEMENT_NODE )
        {
            String elementName = nodeNames[0];

            domModel = getDOMModel( file, false );
            targetNode = domModel.getDocument().getElementsByTagName( elementName ).item( 0 ).getFirstChild();
        }
        else if( nodeType == Node.ATTRIBUTE_NODE )
        {
            String elementName = nodeNames[0];
            String attrName = nodeNames[1];

            domModel = getDOMModel( file, false );
            targetNode = domModel.getDocument().getElementsByTagName( elementName ).item( 0 ).
                         getAttributes().getNamedItem( attrName );
        }

        int offset = getRegion( targetNode ).getOffset();

        final StructuredTextViewer viewer = getEditor( file ).getTextViewer();
        final SourceViewerConfiguration srcViewConf = getSourceViewerConfiguraionFromExtensionPoint( file );

        final ContentAssistant contentAssistant = (ContentAssistant) srcViewConf.getContentAssistant( viewer );
        // viewer.configure( srcViewConf );
        // viewer.setSelectedRange( offset, 0 );

        // get the processor
        final String partitionTypeID = viewer.getDocument().getPartition( offset ).getType();
        final IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor( partitionTypeID );

        // get content assist suggestions
        final ICompletionProposal[] proposals = processor.computeCompletionProposals( viewer, offset );

        domModel.releaseFromRead();

        return proposals;
    }

    public static ICompletionProposal[] getProposalsForAttr( IFile file, String elementName ,String attrName ) throws Exception
    {
        return getProposals( file, Node.ATTRIBUTE_NODE, elementName, attrName );
    }

    public static ICompletionProposal[] getProposalsForElement( IFile file, String elementName ) throws Exception
    {
        return getProposals( file, Node.ELEMENT_NODE, elementName );
    }

    private static IRegion getRegion( Node node )
    {
        if( node != null )
        {
            switch( node.getNodeType() )
            {
                case Node.ELEMENT_NODE:

                    IDOMElement element = (IDOMElement) node;
                    int endOffset;

                    if( element.hasEndTag() && element.isClosed() )
                    {
                        endOffset = element.getStartEndOffset();
                    }
                    else
                    {
                        endOffset = element.getEndOffset();
                    }

                    return new Region( element.getStartOffset(), endOffset - element.getStartOffset() );

                case Node.ATTRIBUTE_NODE:

                    IDOMAttr att = (IDOMAttr) node;
                    int regOffset = att.getValueRegionStartOffset();
                    int regLength = att.getValueRegionText().length();
                    String attValue = att.getValueRegionText();
                    if( StringUtil.isQuoted( attValue ) )
                    {
                        regOffset++;
                        regLength -= 2;
                    }

                    return new Region( regOffset, regLength );

                case Node.TEXT_NODE:

                    IDOMText text = (IDOMText) node;
                    int startOffset = text.getStartOffset();
                    int length = text.getLength();

                    return new Region( startOffset, length );
            }
        }

        return null;
    }

    // get the SourceViewerConfiguration from extension point
    public static SourceViewerConfiguration getSourceViewerConfiguraionFromExtensionPoint( IFile file ) throws Exception
    {
        final String contentTypeId = file.getContentDescription().getContentType().getId();

        // get Source Viewer Configuration from content type
        Object viewerConfFromContentType =
            ExtendedConfigurationBuilder.getInstance().getConfiguration(
                ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, contentTypeId );

        // get Source Viewer Configuration from editorId, has a higher priority
        final String editorId = IDE.getEditorDescriptor( file ).getId();
        Object viewerConfFromEditor =
            ExtendedConfigurationBuilder.getInstance().getConfiguration(
                ExtendedConfigurationBuilder.SOURCEVIEWERCONFIGURATION, editorId );

        if( viewerConfFromEditor != null && viewerConfFromEditor instanceof SourceViewerConfiguration )
        {
            return (SourceViewerConfiguration) viewerConfFromEditor;
        }
        else if( viewerConfFromContentType != null && viewerConfFromContentType instanceof SourceViewerConfiguration )
        {
            return (SourceViewerConfiguration) viewerConfFromContentType;
        }

        return null;
    }

    private static String[] getTextHover( IFile file, int nodeType, String... nodeNames ) throws Exception
    {
        List<String> retval = new ArrayList<String>();
        IDOMModel domModel = null;

        Node targetNode = null;

        if( nodeType == Node.ELEMENT_NODE )
        {
            String elementName = nodeNames[0];
            domModel = getDOMModel( file, false );

            // the actual node is text node of this.element
            targetNode = domModel.getDocument().getElementsByTagName( elementName ).item( 0 ).getFirstChild();
        }
        else if( nodeType == Node.ATTRIBUTE_NODE )
        {
            String elementName = nodeNames[0];
            String attrName = nodeNames[1];

            domModel = getDOMModel( file, false );
            targetNode = domModel.getDocument().getElementsByTagName( elementName ). item( 0 ).
                         getAttributes().getNamedItem( attrName );
        }
        else
        {
            return null;
        }

        final StructuredTextEditor editor = getEditor( file );
        final StructuredTextViewer viewer = editor.getTextViewer();

        int offset = getRegion( targetNode ).getOffset();

        final Method getTextHoverMethod =
            ReflectionUtil.getDeclaredMethod( viewer.getClass(), "getTextHover", true, int.class, int.class );
        getTextHoverMethod.setAccessible( true );

        final ITextHover hover = (ITextHover)getTextHoverMethod.invoke( viewer, offset ,0 );
        final IRegion region = hover.getHoverRegion( viewer, offset );

        if( region instanceof CompoundRegion )
        {
            List<IRegion> regions = ( (CompoundRegion) region ).getRegions();

            for( IRegion reg : regions )
            {
                if( reg instanceof InfoRegion )
                {
                    retval.add( ( (InfoRegion) reg ).getInfo() );
                }
            }
        }

        domModel.releaseFromRead();

        return retval.toArray( new String[0] );
    }

    public static String[] getTextHoverForAttr( IFile file, String elementName, String attrName ) throws Exception
    {
        return getTextHover( file, Node.ATTRIBUTE_NODE, elementName, attrName );
    }

    public static String[] getTextHoverForElement( IFile file, String elementName ) throws Exception
    {
        return getTextHover( file, Node.ELEMENT_NODE, elementName );
    }

    // open the editor during test, useless for test, but testers can see what's going on.
    public static void openEditor( IFile file ) throws Exception
    {
        getEditor( file );
    }

    // set the attribute value for the 1st element with the "elementName"
    public static void setAttrValue( IFile file, String elementName, String attrName, String attrValue ) throws Exception
    {
        final IDOMModel domModel = getDOMModel( file, true );

        assertNotNull( domModel );

        final IDOMDocument document = domModel.getDocument();
        final NodeList elements = document.getElementsByTagName( elementName );

        assertEquals( true, elements.getLength() > 0 );

        final Element element = (Element) elements.item( 0 );

        Attr attrNode = element.getAttributeNode( attrName );

        attrNode.setValue( attrValue );

        domModel.save();

        domModel.releaseFromEdit();

        file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }

    // set the content for the 1st element with name of "elementName"
    public static void setElementContent( IFile file, String elementName, String content ) throws Exception
    {
        final IDOMModel domModel = getDOMModel( file, true );

        assertNotNull( domModel );

        final IDOMDocument document = domModel.getDocument();
        final NodeList elements = document.getElementsByTagName( elementName );

        assertEquals( true, elements.getLength() > 0 );

        final Element element = (Element) elements.item( 0 );

        final NodeList childNodes = element.getChildNodes();

        for( int i = 0; i < childNodes.getLength(); i++ )
        {
            element.removeChild( childNodes.item( i ) );
        }

        element.appendChild( document.createTextNode( content ) );

        domModel.save();

        domModel.releaseFromEdit();

        file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }

    // find the marker, use the given resolution to fix it and check if the marker is gone.
    public static void verifyQuickFix( IFile file, String markerType, String markerMessageRegex,
                                       Class<? extends IMarkerResolution> resolutionClazz ) throws Exception
    {
        IMarker exceptedMarker = findMarkerByMessage( file, markerType, markerMessageRegex, false );
        assertNotNull( exceptedMarker );

        IMarkerResolution exceptedMarkerResolution =
            findMarkerResolutionByClass( exceptedMarker, AddResourceKeyMarkerResolution.class );
        assertNotNull( exceptedMarkerResolution );

        exceptedMarkerResolution.run( exceptedMarker );
        buildAndValidate( file );

        exceptedMarker = findMarkerByMessage( file, markerType, markerMessageRegex, false );
        assertNull( exceptedMarker );
    }

}
