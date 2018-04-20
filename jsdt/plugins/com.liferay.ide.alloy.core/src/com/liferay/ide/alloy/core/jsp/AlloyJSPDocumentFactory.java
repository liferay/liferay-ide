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

import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.content.IContentType;
import org.eclipse.core.runtime.content.IContentTypeManager;
import org.eclipse.jface.text.IDocument;
import org.eclipse.wst.sse.core.internal.document.IDocumentLoader;
import org.eclipse.wst.sse.core.internal.filebuffers.BasicStructuredDocumentFactory;
import org.eclipse.wst.sse.core.internal.ltk.modelhandler.IModelHandler;
import org.eclipse.wst.sse.core.internal.modelhandler.ModelHandlerRegistry;
import org.eclipse.wst.sse.core.internal.text.JobSafeStructuredDocument;

/**
 * @author Gregory Amerson
 */
@SuppressWarnings("restriction")
public class AlloyJSPDocumentFactory extends BasicStructuredDocumentFactory {

	@Override
	public IDocument createDocument() {
		IModelHandler handler = null;

		IContentTypeManager manager = Platform.getContentTypeManager();

		IContentType contentType = manager.getContentType("com.liferay.ide.alloy.core.alloyjspsource");

		while ((handler == null) && !IContentTypeManager.CT_TEXT.equals(contentType.getId())) {
			ModelHandlerRegistry registry = ModelHandlerRegistry.getInstance();

			handler = registry.getHandlerForContentTypeId(contentType.getId());

			contentType = contentType.getBaseType();
		}

		if (handler != null) {
			IDocumentLoader loader = handler.getDocumentLoader();

			return loader.createNewStructuredDocument();
		}
		else {
			return new JobSafeStructuredDocument();
		}
	}

}