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

package com.liferay.ide.portlet.core.model.internal;

import com.liferay.ide.portlet.core.util.PortletUtil;

import java.util.Locale;
import java.util.Set;

import org.eclipse.sapphire.services.PossibleValuesService;

/**
 * @author Kamesh Sampath
 */
public class LocalePossibleValueService extends PossibleValuesService
{
    static final Locale[] locales = Locale.getAvailableLocales();

    /*
     * (non-Javadoc)
     * @see org.eclipse.sapphire.modeling.PossibleValuesService#fillPossibleValues(java.util.SortedSet)
     */
    @Override
    protected void fillPossibleValues( Set<String> values )
    {
        // System.out.println( "LocalePossibleValueService.fillPossibleValues()-1" );
        for( Locale locale : locales )
        {
            values.add( PortletUtil.buildLocaleDisplayString( locale.getDisplayName(), locale ) );
        }

    }
}
