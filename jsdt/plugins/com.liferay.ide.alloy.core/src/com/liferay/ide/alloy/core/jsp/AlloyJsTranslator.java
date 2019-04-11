/**
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
 */

package com.liferay.ide.alloy.core.jsp;

import java.util.Iterator;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.Position;
import org.eclipse.wst.jsdt.web.core.internal.Logger;
import org.eclipse.wst.jsdt.web.core.javascript.JsDataTypes;
import org.eclipse.wst.jsdt.web.core.javascript.JsTranslator;
import org.eclipse.wst.jsdt.web.core.javascript.NodeHelper;
import org.eclipse.wst.jsdt.web.core.javascript.Util;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocumentRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegion;
import org.eclipse.wst.sse.core.internal.provisional.text.ITextRegionList;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class AlloyJsTranslator extends JsTranslator {

	public static final String[] ALLOYATTREVENTS = {
		"onActiveStateChange", "onBlurMethod", "onBoundingBoxChange", "onChange", "onChangeMethod",
		"onClassNamesChange", "onClick", "onContentBoxChange", "onContentUpdate", "onCssClassChange",
		"onDefaultStateChange", "onDepthChange", "onDestroy", "onDestroyedChange", "onDisabledChange",
		"onFocusedChange", "onFocusMethod", "onHandlerChange", "onHeightChange", "onHideClassChange",
		"onHoverStateChange", "onIconChange", "onIconNodeChange", "onIdChange", "onIndexChange", "onInit",
		"onInitializedChange", "onLabelChange", "onLabelNodeChange", "onlyActions", "onParentChange", "onRender",
		"onRenderChange", "onRenderedChange", "onRootChange", "onSelectedChange", "onSrcNodeChange", "onStringsChange",
		"onSubmit", "onTabIndexChange", "onTitleChange", "onTypeChange", "onUseARIAChange", "onVisibleChange",
		"onWidthChange"
	};

	public AlloyJsTranslator(IStructuredDocument doc, String baseLocation, boolean listen) {
		super(doc, baseLocation, listen);
	}

	public void translate() {
		synchronized (finished) {
			if (getCurrentNode() != null) {
				NodeHelper nh = new NodeHelper(getCurrentNode());

				while ((getCurrentNode() != null) && !isCanceled()) {
					nh.setDocumentRegion(getCurrentNode());

					if (getCurrentNode().getType() == DOMRegionContext.XML_TAG_NAME) {
						if ((!nh.isEndTag() || nh.isSelfClosingTag()) &&
							(nh.nameEquals("script") || nh.nameEquals("aui:script"))) {

							/**
							 * Handles the following cases: <script type="javascriptype"> <script
							 * language="javascriptype> <script src='' type=javascriptype> <script src=''
							 * language=javascripttype <script src=''> global js type. <script> (global js
							 * type)
							 */
							if (NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("type")) ||
								NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("language")) ||
								((nh.getAttributeValue("type") == null) && (nh.getAttributeValue("language") == null) &&
								 isGlobalJs())) {

								if (nh.containsAttribute(new String[] {"src"})) {
									translateScriptImportNode(getCurrentNode());
								}

								if (getCurrentNode().getNext() != null) {
									translateJSNode(getCurrentNode().getNext());
								}
							}
						}
						else if (nh.containsAttribute(JsDataTypes.HTMLATREVENTS) ||
								 nh.containsAttribute(ALLOYATTREVENTS)) {

							/* Check for embedded JS events in any tags */
							translateInlineJSNode(getCurrentNode());
						}
						else if (nh.nameEquals("META") && nh.attrEquals("http-equiv", "Content-Script-Type") &&
								 nh.containsAttribute(new String[] {"content"})) {

							setIsGlobalJs(
								NodeHelper.isInArray(JsDataTypes.JSVALIDDATATYPES, nh.getAttributeValue("content")));
						}

						// End big if of JS types

					}

					if (getCurrentNode() != null) {
						advanceNextNode();
					}
				}

				if (getCompUnitBuffer() != null) {
					getCompUnitBuffer().setContents(fScriptText.toString());
				}
			}

			finishedTranslation();
		}
	}

	public void translateInlineJSNode(IStructuredDocumentRegion container) {
		/* start a function header.. will amend later */
		ITextRegionList t = container.getRegions();

		ITextRegion r;

		Iterator<?> regionIterator = t.iterator();

		while (regionIterator.hasNext() && !isCanceled()) {
			r = (ITextRegion)regionIterator.next();

			if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_NAME) {
				r.getStart();
				r.getTextEnd();

				String tagAttrname = container.getText(r);

				/**
				 * Attribute values aren't case sensative, also make sure next region is attrib
				 * value
				 */
				if (NodeHelper.isInArray(JsDataTypes.HTMLATREVENTS, tagAttrname) ||
					NodeHelper.isInArray(ALLOYATTREVENTS, tagAttrname)) {

					if (regionIterator.hasNext()) {
						regionIterator.next();
					}

					if (regionIterator.hasNext()) {
						r = (ITextRegion)regionIterator.next();
					}

					if (r.getType() == DOMRegionContext.XML_TAG_ATTRIBUTE_VALUE) {
						int valStartOffset = container.getStartOffset(r);

						String rawText = container.getText(r);

						if ((rawText == null) || (rawText.length() == 0)) {
							continue;
						}
						/* Strip quotes */
						switch (rawText.charAt(0)) {
							case '\'':
							case '"':
								rawText = rawText.substring(1);
								valStartOffset++;
						}

						if ((rawText == null) || (rawText.length() == 0)) {
							continue;
						}

						switch (rawText.charAt(rawText.length() - 1)) {
							case '\'':
							case '"':
								rawText = rawText.substring(0, rawText.length() - 1);
						}

						new Position(valStartOffset, rawText.length());

						char[] spaces = Util.getPad(
							Math.max(0, valStartOffset - scriptOffset - _EVENT_HANDLER_PRE_LENGTH));

						for (int i = 0; i < spaces.length; i++) {
							try {
								char c = fStructuredDocument.getChar(scriptOffset + i);

								if ((c == '\n') || (c == '\r') || (c == '\t')) {
									spaces[i] = c;
								}
							}
							catch (BadLocationException ble) {
								Logger.logException(ble);
							}
						}

						fScriptText.append(spaces);
						fScriptText.append(_EVENT_HANDLER_PRE);
						appendAndTrack(rawText, valStartOffset);

						if (ADD_SEMICOLON_AT_INLINE) {
							fScriptText.append(";");
						}

						if (r.getLength() > rawText.length()) {
							fScriptText.append(_EVENT_HANDLER_POST);
							spaces = Util.getPad(
								Math.max(0, r.getLength() - rawText.length() - _EVENT_HANDLER_POST_LENGTH));

							fScriptText.append(spaces);
						}

						scriptOffset = container.getEndOffset(r);
					}
				}
			}
		}
	}

	private static final String _EVENT_HANDLER_POST = "})();";

	private static final int _EVENT_HANDLER_POST_LENGTH = _EVENT_HANDLER_POST.length();

	private static final String _EVENT_HANDLER_PRE = "(function(){";

	private static final int _EVENT_HANDLER_PRE_LENGTH = _EVENT_HANDLER_PRE.length();

}