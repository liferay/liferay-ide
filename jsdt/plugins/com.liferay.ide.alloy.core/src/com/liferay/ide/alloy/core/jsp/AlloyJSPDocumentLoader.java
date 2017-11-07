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

import org.eclipse.jface.text.IDocumentPartitioner;
import org.eclipse.jst.jsp.core.internal.encoding.JSPDocumentLoader;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockMarker;
import org.eclipse.wst.sse.core.internal.ltk.parser.BlockTagParser;
import org.eclipse.wst.sse.core.internal.ltk.parser.RegionParser;
import org.eclipse.wst.sse.core.internal.provisional.document.IEncodedDocument;
import org.eclipse.wst.sse.core.internal.provisional.text.IStructuredDocument;
import org.eclipse.wst.xml.core.internal.regions.DOMRegionContext;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class AlloyJSPDocumentLoader extends JSPDocumentLoader {

	@Override
	public IDocumentPartitioner getDefaultDocumentPartitioner() {
		return new AlloyStructuredTextPartitionerForJSP();
	}

	@Override
	protected IEncodedDocument newEncodedDocument() {
		IEncodedDocument retval = super.newEncodedDocument();

		if (retval instanceof IStructuredDocument) {
			IStructuredDocument doc = (IStructuredDocument)retval;

			RegionParser parser = doc.getParser();

			if (parser instanceof BlockTagParser) {
				BlockTagParser blockParser = (BlockTagParser)parser;
				BlockMarker bm = new BlockMarker("aui:script", null, DOMRegionContext.BLOCK_TEXT, false);

				blockParser.addBlockMarker(bm);
			}
		}

		return retval;
	}

}