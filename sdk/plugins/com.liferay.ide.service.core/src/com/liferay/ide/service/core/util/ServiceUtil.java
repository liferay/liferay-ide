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
 *******************************************************************************/
package com.liferay.ide.service.core.util;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;


/**
 * @author Gregory Amerson
 */
public class ServiceUtil
{

    public static String getDTDVersion( Document document )
    {
        String dtdVersion = null;
        DocumentType docType = document.getDoctype();

        if( docType != null )
        {
            String publicId = docType.getPublicId();
            String systemId = docType.getSystemId();

            if( publicId != null && systemId != null )
            {
                if( publicId.contains( "6.0.0" ) || systemId.contains( "6.0.0" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    dtdVersion = "6.0.0" ; //$NON-NLS-1$
                }
                else if( publicId.contains( "6.1.0" ) || systemId.contains( "6.1.0" ) ) //$NON-NLS-1$ //$NON-NLS-2$
                {
                    dtdVersion = "6.1.0"; //$NON-NLS-1$
                }
            }
        }

        return dtdVersion;
    }
}
