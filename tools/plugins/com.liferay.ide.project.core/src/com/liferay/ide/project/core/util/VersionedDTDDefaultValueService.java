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
package com.liferay.ide.project.core.util;

import com.liferay.ide.core.util.StringPool;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.modeling.xml.RootXmlResource;
import org.eclipse.sapphire.services.DefaultValueService;
import org.eclipse.sapphire.services.DefaultValueServiceData;
import org.w3c.dom.Document;


/**
 * @author Gregory Amerson
 */
public class VersionedDTDDefaultValueService extends DefaultValueService
{
    private Pattern systemIdPattern;

    public VersionedDTDDefaultValueService(Pattern systemIdPattern)
    {
        this.systemIdPattern = systemIdPattern;
    }

    @Override
    protected DefaultValueServiceData compute()
    {
        String defaultVersion = null;

        Document document = this.context( Element.class ).resource().adapt( RootXmlResource.class ).getDomDocument();

        if( document != null && document.getDoctype() != null )
        {
            String systemId = document.getDoctype().getSystemId();
            Matcher matcher = this.systemIdPattern.matcher( systemId );

            if( matcher.matches() )
            {
                defaultVersion = matcher.group( 1 );
            }
        }

        if( defaultVersion == null )
        {
            defaultVersion = "6.0.0"; // default should be 6.0.0 //$NON-NLS-1$
        }

        return new DefaultValueServiceData( defaultVersion.replaceAll( StringPool.UNDERSCORE, "." ) ); //$NON-NLS-1$
    }

}
