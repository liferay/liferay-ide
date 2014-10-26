/*******************************************************************************
 * Copyright (c) 2013-2014 Angelo ZERR.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Angelo Zerr <angelo.zerr@gmail.com> - initial API and implementation
 *******************************************************************************/

package com.liferay.ide.xml.search.ui.descriptors;

import com.liferay.ide.xml.search.ui.XMLSearchConstants;

import java.util.Collection;
import java.util.Collections;

import org.eclipse.wst.xml.search.core.queryspecifications.requestor.ContentTypeXMLSearchRequestor;
import org.eclipse.wst.xml.search.core.queryspecifications.requestor.IXMLSearchRequestor;

/**
 * XML Search requestor for liferay-portlet.xml descriptors.
 */
public class LiferayPortletSearchRequestor extends ContentTypeXMLSearchRequestor
{

    public static IXMLSearchRequestor INSTANCE = new LiferayPortletSearchRequestor();

    private static final Collection<String> contentTypeIds =
        Collections.singleton( XMLSearchConstants.LIFERAY_PORTLET_XML_CONTENT_TYPE );

    @Override
    protected Collection<String> getSupportedContentTypeIds()
    {
        return contentTypeIds;
    }

}
