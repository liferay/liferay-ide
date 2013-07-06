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

package com.liferay.ide.service.core.model.internal;

import com.liferay.ide.service.core.model.ServiceBuilder;
import com.liferay.ide.service.core.util.ServiceUtil;

import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Cindy Li
 */
public class NamespaceValidationService extends ValidationService
{
    @Override
    public Status validate()
    {
        final Value<String> namespace = context().find( ServiceBuilder.class ).getNamespace();
        String content = namespace.content();

        if( content == null )
        {
            return Status.createErrorStatus( Msgs.namespaceNotEmpty );
        }

        if( ! ServiceUtil.isValidNamespace( content.toString() ) )
        {
            return Status.createErrorStatus( Msgs.namespaceElementValidKeyword );
        }

        return Status.createOkStatus();
    }

    private static class Msgs extends NLS
    {
        public static String namespaceElementValidKeyword;
        public static String namespaceNotEmpty;

        static
        {
            initializeMessages( NamespaceValidationService.class.getName(), Msgs.class );
        }
    }
}
