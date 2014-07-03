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

package com.liferay.ide.xml.search.core.descriptors;

import org.eclipse.wst.xml.search.core.queryspecifications.requestor.IXMLSearchRequestor;

/**
 * Query specification to search portlet.xml descriptors.
 */
public class PortletQuerySpecification extends AbstractWebInfQuerySpecification
{

    /*
     * (non-Javadoc)
     * @see org.eclipse.wst.xml.search.core.resource.IResourceRequestorProvider# getRequestor()
     */
    public IXMLSearchRequestor getRequestor()
    {
        return PortletSearchRequestor.INSTANCE;
    }

}
