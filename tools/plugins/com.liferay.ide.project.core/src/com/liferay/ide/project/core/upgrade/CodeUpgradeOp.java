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

package com.liferay.ide.project.core.upgrade;

import com.liferay.ide.project.core.upgrade.service.CheckSDKLocationDerivedValueService;
import com.liferay.ide.project.core.upgrade.service.LayoutPossibleValuesService;
import com.liferay.ide.project.core.upgrade.service.LiferayServerNameDefaultValueService;
import com.liferay.ide.project.core.upgrade.service.LiferayServerNamePossibleValuesService;
import com.liferay.ide.project.core.upgrade.service.LiferayServerNameValidationService;
import com.liferay.ide.project.core.upgrade.service.SdkLocationDefaultValueService;
import com.liferay.ide.project.core.upgrade.service.SdkLocationValidationService;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.AbsolutePath;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Derived;
import org.eclipse.sapphire.modeling.annotations.FileSystemResourceType;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Required;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.ValidFileSystemResourceType;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

@XmlBinding( path = "CodeUpgrade" )
public interface CodeUpgradeOp extends Element
{
    ElementType TYPE = new ElementType( CodeUpgradeOp.class );

    @XmlBinding( path = "SdkLocation" )
    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Required
    @Label( standard = "SDK Location" )
    @Service( impl = SdkLocationDefaultValueService.class )
    @Service( impl = SdkLocationValidationService.class )
    ValueProperty PROP_SDK_LOCATION = new ValueProperty( TYPE, "SdkLocation" );

    Value<Path> getSdkLocation();
    void setSdkLocation( String sdkLocation );
    void setSdkLocation( Path sdkLocation );

    @XmlBinding( path = "NewLocation" )
    @Type( base = Path.class )
    @AbsolutePath
    @ValidFileSystemResourceType( FileSystemResourceType.FOLDER )
    @Required
    ValueProperty PROP_NewLOCATION = new ValueProperty( TYPE, "NewLocation" );

    Value<Path> getNewLocation();
    void setNewLocation( String newLocation );
    void setNewLocation( Path newLocation );

    @XmlBinding( path = "ProjectName" )
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );

    Value<String> getProjectName();
    void setProjectName( String ProjectName );

    @DefaultValue( text = "Use plugin sdk in liferay workspace" )
    @Service( impl = LayoutPossibleValuesService.class )
    ValueProperty PROP_LAYOUT = new ValueProperty( TYPE, "Layout" );

    Value<String> getLayout();
    void setLayout( String Layout );

    @Service( impl = LiferayServerNamePossibleValuesService.class )
    @Service( impl = LiferayServerNameDefaultValueService.class )
    @Service( impl = LiferayServerNameValidationService.class )
    @Required
    @XmlBinding( path = "ServerName" )
    ValueProperty PROP_LIFERAY_SERVER_NAME = new ValueProperty( TYPE, "LiferayServerName" );

    Value<String> getLiferayServerName();
    void setLiferayServerName( String value );

    @Derived
    @Service( impl = CheckSDKLocationDerivedValueService.class )
    ValueProperty PROP_LIFERAY_62SERVER_LOCATION = new ValueProperty( TYPE, "Liferay62ServerLocation" );

    Value<String> getLiferay62ServerLocation();
    void setLiferay62ServerLocation( String value );

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    @Label( standard = "Yes,I am confirm" )
    ValueProperty PROP_CONFIRM = new ValueProperty( TYPE, "Confirm" );

    Value<Boolean> getConfirm();
    void setConfirm( String confirm );
    void setConfirm( Boolean confirm );

    @XmlBinding( path = "HasHook" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_HOOK = new ValueProperty( TYPE, "HasHook" );

    Value<Boolean> getHasHook();
    void setHasHook( String hasHook );
    void setHasHook( Boolean hasHook );

    @XmlBinding( path = "HasPortlet" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_PORTLET = new ValueProperty( TYPE, "HasPortlet" );

    Value<Boolean> getHasPortlet();
    void setHasPortlet( String hasPortlet );
    void setHasPortlet( Boolean hasPortlet );

    @XmlBinding( path = "HasTheme" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_THEME = new ValueProperty( TYPE, "HasTheme" );

    Value<Boolean> getHasTheme();
    void setHasTheme( String hasTheme );
    void setHasTheme( Boolean hasTheme );

    @XmlBinding( path = "HasExt" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_EXT = new ValueProperty( TYPE, "HasExt" );

    Value<Boolean> getHasExt();
    void setHasExt( String hasExt );
    void setHasExt( Boolean hasExt );

    @XmlBinding( path = "HasServiceBuilder" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_SERVICE_BUILDER = new ValueProperty( TYPE, "HasServiceBuilder" );

    Value<Boolean> getHasServiceBuilder();
    void setHasServiceBuilder( String hasServiceBuilder );
    void setHasServiceBuilder( Boolean hasServiceBuilder );

    @XmlBinding( path = "HasLayout" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_LAYOUT = new ValueProperty( TYPE, "HasLayout" );

    Value<Boolean> getHasLayout();
    void setHasLayout( String hasLayout );
    void setHasLayout( Boolean hasLayout );

    @XmlBinding( path = "HasWeb" )
    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_WEB = new ValueProperty( TYPE, "HasWeb" );

    Value<Boolean> getHasWeb();
    void setHasWeb( String hasWeb );
    void setHasWeb( Boolean hasWeb );

}
