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

package com.liferay.ide.xml.search.ui.descriptors;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.jst.jsp.search.editor.references.filters.AbstractTaglibReferenceFilter;
import org.eclipse.wst.xml.search.editor.references.filters.IXMLReferenceFilter;

/**
 * @author Terry Jia
 */
public class LiferayJspTaglibReferenceFilter extends AbstractTaglibReferenceFilter {

	public static final IXMLReferenceFilter INSTANCE = new LiferayJspTaglibReferenceFilter();

	@Override
	protected Collection<String> getTaglibURIs() {
		Collection<String> uris = new ArrayList<>();

		uris.add("http://liferay.com/tld/ui");
		uris.add("http://liferay.com/tld/aui");

		return uris;
	}

}