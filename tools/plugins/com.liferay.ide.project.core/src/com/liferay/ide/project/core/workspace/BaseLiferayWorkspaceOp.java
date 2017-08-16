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

package com.liferay.ide.project.core.workspace;

import com.liferay.ide.core.ILiferayProjectProvider;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.ExecutableElement;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Gregory Amerson
 */
public interface BaseLiferayWorkspaceOp extends ExecutableElement
{
    public static final String DEFAULT_BUNDLE_URL =
        "https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.3-ga4/liferay-ce-portal-tomcat-7.0-ga4-20170613175008905.zip";

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

    // *** bundleUrl ***

    @DefaultValue( text = DEFAULT_BUNDLE_URL )
    @Service( impl = BundleUrlValidationService.class )
    ValueProperty PROP_BUNDLE_URL = new ValueProperty( TYPE, "bundleUrl" );

    Value<String> getBundleUrl();
    void setBundleUrl( String value );

    // *** ProjectProvider ***

    @Type( base = ILiferayProjectProvider.class )
    @Label( standard = "build type" )
    @Service( impl = WorkspaceProjectProviderPossibleValuesService.class )
    @Service( impl = WorkspaceProjectProviderDefaultValueService.class )
    ValueProperty PROP_PROJECT_PROVIDER = new ValueProperty( TYPE, "ProjectProvider" );

    Value<NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp>> getProjectProvider();
    void setProjectProvider( String value );
    void setProjectProvider( NewLiferayWorkspaceProjectProvider<NewLiferayWorkspaceOp> value );

}
