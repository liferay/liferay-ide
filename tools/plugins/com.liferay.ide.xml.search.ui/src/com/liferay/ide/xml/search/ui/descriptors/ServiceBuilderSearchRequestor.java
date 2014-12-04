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

package com.liferay.ide.xml.search.ui.descriptors;

import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.wst.xml.search.core.queryspecifications.requestor.ContentTypeXMLSearchRequestor;
import org.eclipse.wst.xml.search.core.queryspecifications.requestor.IXMLSearchRequestor;



/**
 * @author Kuo Zhang
 */
public class ServiceBuilderSearchRequestor extends ContentTypeXMLSearchRequestor
{

    public static IXMLSearchRequestor INSTANCE = new ServiceBuilderSearchRequestor();

    private static final Collection<String> contentTypeIds =
        Collections.singleton( XMLSearchConstants.SERVICE_BUILDER_CONTENT_TYPE );

    @Override
    protected Collection<String> getSupportedContentTypeIds()
    {
        return contentTypeIds;
    }

}
