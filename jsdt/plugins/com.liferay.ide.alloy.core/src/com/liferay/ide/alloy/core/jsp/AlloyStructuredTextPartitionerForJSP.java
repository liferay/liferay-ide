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

package com.liferay.ide.alloy.core.jsp;

import org.eclipse.jst.jsp.core.internal.text.StructuredTextPartitionerForJSP;
import org.eclipse.wst.html.core.text.IHTMLPartitions;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class AlloyStructuredTextPartitionerForJSP extends StructuredTextPartitionerForJSP
{

    private final String[] RESOURCE_BUNDLE_ATTRS = new String[]
    {
        "key",
        "message",
        "label",
        "helpMessage",
        "suffix",
        "title",
        "value",
        "placeholder",
        "errorMessage",
        "statusMessage",
        "confirmation"
    };

    @Override
    public String getPartitionType( ITextRegion region, int offset )
    {
        String retval = super.getPartitionType( region, offset );

        final IStructuredDocumentRegion sdRegion = this.fStructuredDocument.getRegionAtCharacterOffset( offset );

        if( isAUIScriptRegion( sdRegion ) )
        {
            retval = IHTMLPartitions.SCRIPT;
        }
        else if( isAUIEventHandlerAttrValue( sdRegion, sdRegion.getRegionAtCharacterOffset( offset ), offset ) )
        {
            retval = IHTMLPartitions.SCRIPT_EVENTHANDLER;
        }

        return retval;
    }

    @Override
    public String getPartitionTypeBetween(
        IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode )
    {
        String retval = super.getPartitionTypeBetween( previousNode, nextNode );

        if( isAUIScriptBetween(previousNode, nextNode ) )
        {
            retval = "org.eclipse.wst.html.SCRIPT";
        }

        return retval;
    }

    private boolean isAUIEventHandlerAttrValue(
        IStructuredDocumentRegion region, ITextRegion childRegion, int offset )
    {
        if( region != null && childRegion != null &&
            "XML_TAG_NAME".equals( region.getType() ) &&
            "XML_TAG_ATTRIBUTE_VALUE".equals( childRegion.getType() ) &&
            region.getText().toLowerCase().startsWith( "<aui:" ) )
        {
            // we have found an attribute value in a AUI tag but now we need to check if the attribteName is a
            // javascript type attribute
            final ITextRegion[] regions = region.getRegions().toArray();

            for( int i = 0; i < regions.length; i++ )
            {
                if( regions[i].equals( childRegion ) )
                {
                    if( i >=2 && regions[i-2].getType().equals( "XML_TAG_ATTRIBUTE_NAME" ) )
                    {
                        final ITextRegion attrNameRegion = regions[i-2];
                        final String attrName = region.getFullText( attrNameRegion );

                        for( String attrEvent : AlloyJsTranslator.ALLOYATTREVENTS )
                        {
                            if( attrName.equals( attrEvent ) )
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    protected boolean isResourceBundleAttrValue(
        IStructuredDocumentRegion region, ITextRegion childRegion, int offset )
    {
        if( region != null &&
            childRegion != null &&
            "XML_TAG_NAME".equals( region.getType() ) &&
            "XML_TAG_ATTRIBUTE_VALUE".equals( childRegion.getType() ) &&
            ( region.getText().toLowerCase().startsWith( "<aui:" ) ||
              region.getText().toLowerCase().startsWith( "<liferay-ui:" ) ) )
        {
            // we have found an attribute value in a liferay tag but now we need to check if the attribteName is a
            // resource bundle attribute
            final ITextRegion[] regions = region.getRegions().toArray();

            for( int i = 0; i < regions.length; i++ )
            {
                if( regions[i].equals( childRegion ) )
                {
                    if( i >=2 && regions[i-2].getType().equals( "XML_TAG_ATTRIBUTE_NAME" ) )
                    {
                        final ITextRegion attrNameRegion = regions[i-2];
                        final String attrName = region.getFullText( attrNameRegion );

                        for( String attr : RESOURCE_BUNDLE_ATTRS )
                        {
                            if( attrName.equals( attr ) )
                            {
                                return true;
                            }
                        }
                    }
                }
            }
        }

        return false;
    }

    private boolean isAUIScriptBetween(
        IStructuredDocumentRegion previousNode, IStructuredDocumentRegion nextNode )
    {
        return previousNode.toString().contains( "aui:script" );
    }

    private boolean isAUIScriptRegion( IStructuredDocumentRegion sdRegion )
    {
        // TODO can this handle content with other regions like
        // <aui:script> function foo() {}; <portletTag/> function bar(){}</aui:script>
        return sdRegion != null && sdRegion.getPrevious() != null &&
            sdRegion.getPrevious().toString().contains( "aui:script" );
    }

    @Override
    protected boolean isDocumentRegionBasedPartition(
        IStructuredDocumentRegion sdRegion, ITextRegion containedChildRegion, int offset )
    {
        if( isAUIEventHandlerAttrValue( sdRegion, containedChildRegion, offset ) )
        {
            return false;
        }

// only used if we need partitioner to not skip over liferay taglib message keys
//        if( isResourceBundleAttrValue( sdRegion, containedChildRegion, offset ) )
//        {
//            return true;
//        }

        return super.isDocumentRegionBasedPartition( sdRegion, containedChildRegion, offset );
    }

}
