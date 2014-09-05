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
 ******************************************************************************/

package com.liferay.ide.portlet.core.lfportlet.model;

import com.liferay.ide.portlet.core.lfportlet.model.internal.NumberValueValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Simon Jiang
 */
public interface CronTriggeValueTrigger extends ICronTrigger
{

    ElementType TYPE = new ElementType( CronTriggeValueTrigger.class );

    // *** Cron Trigger Value ***

    @Label( standard = "Cron Trigger Value" )
    @XmlBinding( path = "" )
    @Required
    @Services
    (
        value =
        {
            @Service
            (
                impl = NumberValueValidationService.class,
                params =
                {
                    @Service.Param( name = "min", value = "1" ),
                    @Service.Param( name = "max", value = "" ),
                }
            )
        }
    )   
    ValueProperty PROP_CRON_TRIGGER_VALUE = new ValueProperty( TYPE, "CronTriggerValue" ); //$NON-NLS-1$

    Value<String> getCronTriggerValue();

    void setCronTriggerValue( String value );



}
