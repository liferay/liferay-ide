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

import org.eclipse.wst.jsdt.web.core.javascript.JsDataTypes;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslator;
import org.eclipse.wst.jsdt.web.core.javascript.NodeHelper;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;


/**
 * @author Gregory Amerson
 */
@SuppressWarnings( "restriction" )
public class AlloyJsTranslator extends JsTranslator
{

    public AlloyJsTranslator( IStructuredDocument doc, String baseLocation, boolean listen )
    {
        super( doc, baseLocation, listen );
    }

    /*
     * copied from org.eclipse.wst.jsdt.web.core.javascript.JsTranslator.translate()
     * modified hardcoded section for "script" tags and added "aui:script"
     */
    public void translate() {
        //setCurrentNode(fStructuredDocument.getFirstStructuredDocumentRegion());

        synchronized(finished) {
            if(getCurrentNode() != null) {
            NodeHelper nh = new NodeHelper(getCurrentNode());
            while (getCurrentNode() != null && !isCanceled()) {
                nh.setDocumentRegion(getCurrentNode());

                // System.out.println("Translator Looking at Node
                // type:"+getCurrentNode().getType()+"---------------------------------:");
                // System.out.println(new NodeHelper(getCurrentNode()));
                // i.println("/---------------------------------------------------");
                if (getCurrentNode().getType() == DOMRegionContext.XML_TAG_NAME) {
                    if ((!nh.isEndTag() || nh.isSelfClosingTag()) && (nh.nameEquals("script") || nh.nameEquals("aui:script"))) { //$NON-NLS-1$
                        /*
                         * Handles the following cases: <script
                         * type="javascriptype"> <script language="javascriptype>
                         * <script src='' type=javascriptype> <script src=''
                         * language=javascripttype <script src=''> global js type.
                         * <script> (global js type)
                         */
                        if (NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("type")) || NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("language")) || (nh.getAttributeValue("type")==null && nh.getAttributeValue("language")==null && isGlobalJs())) { //$NON-NLS-1$ //$NON-NLS-2$
                            if (nh.containsAttribute(new String[] { "src" })) { //$NON-NLS-1$
                                // Handle import
                                translateScriptImportNode(getCurrentNode());
                            }
                            // } else {
                            // handle script section

                            if (getCurrentNode().getNext() != null /*&& getCurrentNode().getNext().getType() == DOMRegionContext.BLOCK_TEXT*/) {
                                translateJSNode(getCurrentNode().getNext());
                            }
                        } // End search for <script> sections
                    } else if (nh.containsAttribute(JsDataTypes.HTMLATREVENTS)) {
                        /* Check for embedded JS events in any tags */
                        translateInlineJSNode(getCurrentNode());
                    } else if (nh.nameEquals("META") && nh.attrEquals("http-equiv", "Content-Script-Type") && nh.containsAttribute(new String[] { "content" })) { //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                        // <META http-equiv="Content-Script-Type" content="type">
                        setIsGlobalJs( NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("content"))); //$NON-NLS-1$
                    } // End big if of JS types
                }
                if (getCurrentNode() != null) {
                    advanceNextNode();
                }
            } // end while loop
            if(getCompUnitBuffer()!=null) getCompUnitBuffer().setContents(fScriptText.toString());
        }
        finishedTranslation();
        }
    }
}
