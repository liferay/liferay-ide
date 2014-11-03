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
import static org.junit.Assert.fail;

import com.liferay.ide.xml.search.ui.AddResourceKeyMarkerResolution;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.WeakHashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.IncrementalProjectBuilder;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.hyperlink.IHyperlink;
import org.eclipse.jface.text.hyperlink.IHyperlinkDetector;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.sapphire.ui.swt.xml.editor.SapphireEditorForXml;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IMarkerResolution;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.wst.sse.core.StructuredModelManager;
import org.eclipse.wst.sse.ui.StructuredTextEditor;
import org.eclipse.wst.sse.ui.internal.StructuredTextViewer;
import org.eclipse.wst.validation.internal.ValManager;
import org.eclipse.wst.validation.internal.ValOperation;
import org.eclipse.wst.validation.internal.ValType;
import org.eclipse.wst.validation.internal.operations.ValidatorManager;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMDocument;
import org.eclipse.wst.xml.core.internal.provisional.document.IDOMModel;
import org.eclipse.wst.xml.search.editor.hyperlink.HyperlinkUtils;
import org.eclipse.wst.xml.ui.internal.tabletree.XMLMultiPageEditorPart;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Some methods are modified from eclipse wst sse tests
 * 
 * @author Kuo Zhang
 */
@SuppressWarnings( "restriction" )
public class XmlSearchTestsUtils
{

    private static Map<IFile, IEditorPart> fileToEditorMap = new HashMap<IFile, IEditorPart>();
    private static Map<IFile, IDOMModel> fileToModelMap = new WeakHashMap<IFile, IDOMModel>();

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

    // check if the excepted proposal is in the given proposals
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

    // check if the excepted proposal is in the given proposals
    public static boolean containProposal( ICompletionProposal[] proposals,
                                           String exceptedProposalString, boolean fullMatch )
    {
        for( ICompletionProposal proposal : proposals )
        {
            if( fullMatch && proposal.getDisplayString().equals( exceptedProposalString ) )
            {
                return true;
            }
            else if( ! fullMatch && proposal.getDisplayString().contains( exceptedProposalString ) )
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


    // same as getElementContentOffset, only available for the attributes format: <element attribute=""/>, with quotes
    public static int getAttrValueOffset( ITextViewer textViewer, String findString ) throws Exception
    {
        FindReplaceDocumentAdapter documentAdapter = new FindReplaceDocumentAdapter( textViewer.getDocument() );
        IRegion region = documentAdapter.find( 0, findString, true, true, true, false );

        assertNotNull( region );

        return region.getOffset() + findString.length() + 2;
    }

    protected static IDOMModel getDOMModel( IFile file ) throws Exception
    {
        IDOMModel domModel = fileToModelMap.get( file );

        if( domModel == null )
        {
            domModel = (IDOMModel) StructuredModelManager.getModelManager().getModelForEdit( file );
        }

        return domModel;
    }

    public static StructuredTextEditor getEditor( IFile file )
    {
        StructuredTextEditor editor = (StructuredTextEditor) fileToEditorMap.get( file );

        if( editor == null )
        {
            try
            {
                final IWorkbenchWindow workbenchWindow = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
                final IWorkbenchPage page = workbenchWindow.getActivePage();

                // let eclipse detect the editor id automatically
                final IEditorPart editorPart = IDE.openEditor( page, file, true, true );

                // specify the editor id
                // final EditorPart editorPart = page.openEditor(
                //    new FileEditorInput( file ), "com.liferay.ide.eclipse.portlet.ui.editor.PortletXmlEditor", true );

                assertNotNull( editorPart );

                if( editorPart instanceof SapphireEditorForXml )
                {
                    editor = ( (SapphireEditorForXml) editorPart ).getXmlEditor();
                }
                else if( editorPart instanceof StructuredTextEditor )
                {
                    editor = ( (StructuredTextEditor) editorPart );
                }
                else if( editorPart instanceof XMLMultiPageEditorPart )
                {
                    XMLMultiPageEditorPart xmlEditorPart = (XMLMultiPageEditorPart) editorPart;
                    editor = (StructuredTextEditor) xmlEditorPart.getAdapter( StructuredTextEditor.class );
                }

                assertNotNull( editor );
                standardizeLineEndings( editor );
                fileToEditorMap.put( file, editor );
            }
            catch( Exception e )
            {
                fail( "Could not open editor for " + file + " exception: " + e.getMessage() );
            }
        }

        return editor;
    }

    // This is only available for getting the offset of element content, element without attribute
    // don't use it other places except for the tests.
    // element format: <element></element>
    public static int getElementContentOffset( ITextViewer textViewer, String findString ) throws Exception
    {
        FindReplaceDocumentAdapter documentAdapter = new FindReplaceDocumentAdapter( textViewer.getDocument() );
        IRegion region = documentAdapter.find( 0, findString, true, true, true, false );

        assertNotNull( region );

        return region.getOffset() + findString.length() + 1;
    }

    // TODO add method getHyperLinksForAttrValue
    public static IHyperlink[] getHyperLinksForAttrValue( IFile file, SourceViewerConfiguration sourceViewConf,
                                                          String nodeName, String id ) throws Exception
    {
        return new IHyperlink[0];
    }

    public static IHyperlink[] getHyperLinksForElementContent( IFile file, SourceViewerConfiguration sourceViewConf,
                                                               String elementName ) throws Exception
    {
        StructuredTextEditor editor = getEditor( file );
        StructuredTextViewer viewer = editor.getTextViewer();

        IDOMModel domModel = getDOMModel( file );
        assertNotNull( domModel );

        final Node node = domModel.getDocument().getElementsByTagName( elementName ).item( 0 );
        assertNotNull( node );

        viewer.configure( sourceViewConf );

        IHyperlinkDetector[] hyperlinkDetectors = sourceViewConf.getHyperlinkDetectors( viewer );

        Set<IHyperlink> hyperlinks = new HashSet<IHyperlink>();

        IRegion region = HyperlinkUtils.getHyperlinkRegion( node.getFirstChild() );

        for( IHyperlinkDetector detector : hyperlinkDetectors )
        {
            IHyperlink[] temp = detector.detectHyperlinks( viewer, region, true );

            if( temp != null && temp.length > 0 )
            {
                hyperlinks.addAll( Arrays.asList( temp ) );
            }
        }

        return hyperlinks.toArray( new IHyperlink[0] );
    }

    public static ICompletionProposal[] getProposals( IFile file, SourceViewerConfiguration sourceViewConf,
                                                      String findString, int nodeType ) throws Exception
    {
        StructuredTextEditor editor = getEditor( file );
        StructuredTextViewer viewer = editor.getTextViewer();

        int offset = 0;

        if( nodeType == Node.ELEMENT_NODE )
        {
            offset = getElementContentOffset( viewer, findString );
        }
        else if( nodeType == Node.ATTRIBUTE_NODE )
        {
            offset = getAttrValueOffset( viewer, findString );
        }

        ContentAssistant contentAssistant = (ContentAssistant) sourceViewConf.getContentAssistant( viewer );
        viewer.configure( sourceViewConf );
        viewer.setSelectedRange( offset, 0 );

        // get the processor
        String partitionTypeID = viewer.getDocument().getPartition( offset ).getType();
        IContentAssistProcessor processor = contentAssistant.getContentAssistProcessor( partitionTypeID );

        // fire content assist session about to start
        Method privateFireSessionBeginEventMethod =
            ContentAssistant.class.getDeclaredMethod( "fireSessionBeginEvent", new Class[] { boolean.class } );
        privateFireSessionBeginEventMethod.setAccessible( true );
        privateFireSessionBeginEventMethod.invoke( contentAssistant, new Object[] { Boolean.TRUE } );

        // get content assist suggestions
        ICompletionProposal[] proposals = processor.computeCompletionProposals( viewer, offset );

        // fire content assist session ending
        Method privateFireSessionEndEventMethod = ContentAssistant.class.getDeclaredMethod( "fireSessionEndEvent" );
        privateFireSessionEndEventMethod.setAccessible( true );
        privateFireSessionEndEventMethod.invoke( contentAssistant );

        return proposals;
    }

    // open the editor during test, useless for test, but testers can see what's going on.
    public static void openEditor( IFile file ) throws Exception
    {
        getEditor( file );
    }

    public static String replace( String aString, String source, String target )
    {
        if( aString == null )
            return null;
        String normalString = ""; //$NON-NLS-1$
        int length = aString.length();
        int position = 0;
        int previous = 0;
        int spacer = source.length();
        StringBuffer sb = new StringBuffer( normalString );
        while( position + spacer - 1 < length && aString.indexOf( source, position ) > -1 )
        {
            position = aString.indexOf( source, previous );
            sb.append( normalString );
            sb.append( aString.substring( previous, position ) );
            sb.append( target );
            position += spacer;
            previous = position;
        }
        sb.append( aString.substring( position, aString.length() ) );
        normalString = sb.toString();

        return normalString;
    }

    public static void setElementContent( IFile file, String elementName, String content ) throws Exception
    {
        final IDOMModel domModel = getDOMModel( file );

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
        //domModel.releaseFromEdit();

        file.refreshLocal( IResource.DEPTH_ZERO, new NullProgressMonitor() );
    }

    public static void standardizeLineEndings( StructuredTextEditor editor )
    {
        final IDocument doc = editor.getTextViewer().getDocument();
        String contents = doc.get();
        contents = XmlSearchTestsUtils.replace( contents, "\r\n", "\n" );
        contents = XmlSearchTestsUtils.replace( contents, "\r", "\n" );
        doc.set( contents );
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

    public static void waitForBuildAndValidation() throws Exception
    {
        IWorkspaceRoot root = null;

        try
        {
            ResourcesPlugin.getWorkspace().checkpoint( true );

            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_MANUAL_BUILD, new NullProgressMonitor() );
            Job.getJobManager().join( ValidatorManager.VALIDATOR_JOB_FAMILY, new NullProgressMonitor() );
            Job.getJobManager().join( ResourcesPlugin.FAMILY_AUTO_BUILD, new NullProgressMonitor() );
            Thread.sleep( 200 );
            Job.getJobManager().beginRule( root = ResourcesPlugin.getWorkspace().getRoot(), null );
        }
        catch( InterruptedException e )
        {
        }
        catch( IllegalArgumentException e )
        {
        }
        catch( OperationCanceledException e )
        {
        }
        finally
        {
            if( root != null )
            {
                Job.getJobManager().endRule( root );
            }
        }
    }

    // remove it when we can make sure the buildAndValidate can work really well,
    // this method is really unstable
    public static void waitForBuildAndValidation( IProject project ) throws Exception
    {
        project.build( IncrementalProjectBuilder.CLEAN_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation();
        project.build( IncrementalProjectBuilder.FULL_BUILD, new NullProgressMonitor() );
        waitForBuildAndValidation();
    }

}
