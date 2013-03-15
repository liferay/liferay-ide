/*******************************************************************************
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *               Kamesh Sampath - initial implementation
 *******************************************************************************/

package com.liferay.ide.portlet.core.util;

/**
 * @author kamesh
 */
public interface PortletAppModelConstants
{
    public static final String COLON = ":"; //$NON-NLS-1$
    public static final String DEFAULT_QNAME_PREFIX = "x"; //$NON-NLS-1$
    public static final String LOCAL_PART_DEFAULT_VALUE = "LOCAL_PART"; //$NON-NLS-1$
    public static final String NAMESPACE_URI_DEFAULT_VALUE = "NAMESPACE_URI"; //$NON-NLS-1$
    public static final String DEFAULT_DERIVED_QNAME_VALUE = "{NAMESPACE_URI}LOCAL_PART"; //$NON-NLS-1$
    public static final String XMLNS_NS_URI = "http://www.w3.org/2001/XMLSchema"; //$NON-NLS-1$
    public static final String DEFAULT_QNAME_NS_DECL = "xmlns" + COLON + DEFAULT_QNAME_PREFIX; //$NON-NLS-1$
    public static final String NS_DECL = "xmlns:%s"; //$NON-NLS-1$
}
