/*******************************************************************************
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
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
package com.liferay.ide.adt.core.model;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.SensitiveData;


/**
 * @author Gregory Amerson
 */
public interface ServerInstance extends Element
{

    ElementType TYPE = new ElementType( ServerInstance.class );


    // *** Url ***

    @Label( standard = "url" )
    @Required
    @DefaultValue( text = "http://localhost:8080/" )
    ValueProperty PROP_URL = new ValueProperty( TYPE, "Url" );

    Value<String> getUrl();
    void setUrl( String value );


    // *** OmniUsername ***

    @Required
    @DefaultValue( text = "test@liferay.com")
    ValueProperty PROP_OMNI_USERNAME = new ValueProperty( TYPE, "OmniUsername" );

    Value<String> getOmniUsername();
    void setOmniUsername( String value );


    // *** OmniPassword ***

    @SensitiveData
    @Required
    @DefaultValue( text = "test" )
    ValueProperty PROP_OMNI_PASSWORD = new ValueProperty( TYPE, "OmniPassword" );

    Value<String> getOmniPassword();
    void setOmniPassword( String value );


    // *** Summary ***

    @Label( standard = "summary" )
    ValueProperty PROP_SUMMARY = new ValueProperty( TYPE, "Summary" );

    Value<String> getSummary();
    void setSummary( String value );

}
