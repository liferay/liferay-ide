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
 *
 *******************************************************************************/

package com.liferay.ide.gradle.core.workspace;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Type;

/**
 * @author Gregory Amerso
 */
public interface BaseLiferayWorkspaceOp extends ExecutableElement
{

    ElementType TYPE = new ElementType( BaseLiferayWorkspaceOp.class );

    // *** provision liferay bundle ***

    @DefaultValue( text = "false" )
    @Label( standard = "download liferay bundle" )
    @Type( base = Boolean.class )
    ValueProperty PROP_PROVISION_LIFERAY_BUNDLE = new ValueProperty( TYPE, "provisionLiferayBundle" );

    Value<Boolean> getProvisionLiferayBundle();
    void setProvisionLiferayBundle( String value );
    void setProvisionLiferayBundle( Boolean value );

    // *** serverName ***

    @Service( impl = ServerNameValidationService.class )
    ValueProperty PROP_SERVER_NAME = new ValueProperty( TYPE, "serverName" );

    Value<String> getServerName();
    void setServerName( String value );

}
