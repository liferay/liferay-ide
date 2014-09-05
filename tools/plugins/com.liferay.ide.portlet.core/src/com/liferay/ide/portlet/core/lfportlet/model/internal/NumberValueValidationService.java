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
 *******************************************************************************/
package com.liferay.ide.portlet.core.lfportlet.model.internal;

import com.liferay.ide.core.util.CoreUtil;

import org.apache.commons.lang.StringEscapeUtils;
import org.eclipse.osgi.util.NLS;
import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Simon Jiang
 */
public class NumberValueValidationService extends ValidationService
{

    private String min;
    private String max;
    private Integer value;

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        this.min = this.param( "min" );
        this.max = this.param( "max" );
    }

    @Override
    protected Status compute()
    {
        final Element modelElement = context( Element.class );

        if( !modelElement.disposed() )
        {
            final String triggerValue = (String) modelElement.property( context( ValueProperty.class ) ).content();
            
            if( !CoreUtil.isNullOrEmpty( triggerValue ) )
            {
                value = Integer.valueOf( triggerValue );

                if( !CoreUtil.isNullOrEmpty( min ) )
                {
                    if( value < Integer.valueOf( min ) )
                    {
                        return Status.createErrorStatus( Resources.bind(
                            StringEscapeUtils.unescapeJava( Resources.minNumberValueInvalid ), new Object[] { value,
                                min } ) );
                    }
                }

                if( !CoreUtil.isNullOrEmpty( max ) )
                {
                    if( value > Integer.valueOf( max ) )
                    {
                        return Status.createErrorStatus( Resources.bind(
                            StringEscapeUtils.unescapeJava( Resources.maxNumberValueInvalid ), new Object[] { value,
                                max } ) );
                    }
                }
            }
        }

        return Status.createOkStatus();
    }

    private static final class Resources extends NLS
    {
        public static String minNumberValueInvalid;
        public static String maxNumberValueInvalid;

        static
        {
            initializeMessages( NumberValueValidationService.class.getName(), Resources.class );
        }
    }
}
