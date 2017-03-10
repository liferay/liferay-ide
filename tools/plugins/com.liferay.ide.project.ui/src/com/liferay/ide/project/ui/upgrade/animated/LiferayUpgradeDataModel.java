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

package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Service;

/**
 * @author Simon Jiang
 * @author Andy Wu
 */
public interface LiferayUpgradeDataModel extends Element
{

    String DEFAULT_BUNDLE_URL = "https://cdn.lfrs.sl/releases.liferay.com/portal/7.0.2-ga3/liferay-ce-portal-tomcat-7.0-ga3-20160804222206210.zip";

    ElementType TYPE = new ElementType( LiferayUpgradeDataModel.class );

    // *** SdkLocation ***

    @Type( base = Path.class )
    @Service( impl = ProjectLocationValidationService.class )
    ValueProperty PROP_SDK_LOCATION = new ValueProperty( TYPE, "SdkLocation" );

    Value<Path> getSdkLocation();
    void setSdkLocation( String sdkLocation );
    void setSdkLocation( Path sdkLocation );

    // *** Layout ***

    ValueProperty PROP_LAYOUT = new ValueProperty( TYPE, "Layout" );

    Value<String> getLayout();
    void setLayout( String Layout );

    // *** Liferay70ServerName ***

    ValueProperty PROP_LIFERAY_70_SERVER_NAME = new ValueProperty( TYPE, "Liferay70ServerName" );

    Value<String> getLiferay70ServerName();
    void setLiferay70ServerName( String value );

    // *** Liferay62ServerLocation ***

    ValueProperty PROP_LIFERAY_62_SERVER_LOCATION = new ValueProperty( TYPE, "Liferay62ServerLocation" );

    Value<String> getLiferay62ServerLocation();
    void setLiferay62ServerLocation( String value );

    // *** BundleName ***

    @Service( impl = BundleNameValidationService.class )
    @DefaultValue( text = "Liferay 7.x" )
    ValueProperty PROP_BUNDLE_NAME = new ValueProperty( TYPE, "BundleName" );

    Value<String> getBundleName();
    void setBundleName( String BundleName );

    // *** BundleUrl ***

    @Service( impl = BundleUrlValidationService.class )
    @DefaultValue( text = DEFAULT_BUNDLE_URL )
    ValueProperty PROP_BUNDLE_URL = new ValueProperty( TYPE, "BundleUrl" );

    Value<String> getBundleUrl();
    void setBundleUrl( String BundleUrl );

    // *** HasMavenProject ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_MAVEN_PROJECT = new ValueProperty( TYPE, "HasMavenProject" );

    Value<Boolean> getHasMavenProject();
    void setHasMavenProject( String HasMavenProject );
    void setHasMavenProject( Boolean HasMavenProject );

    // *** HasHook ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_HOOK = new ValueProperty( TYPE, "HasHook" );

    Value<Boolean> getHasHook();
    void setHasHook( String hasHook );
    void setHasHook( Boolean hasHook );

    // *** HasPortlet ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_PORTLET = new ValueProperty( TYPE, "HasPortlet" );

    Value<Boolean> getHasPortlet();
    void setHasPortlet( String hasPortlet );
    void setHasPortlet( Boolean hasPortlet );

    // *** HasTheme ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_THEME = new ValueProperty( TYPE, "HasTheme" );

    Value<Boolean> getHasTheme();
    void setHasTheme( String hasTheme );
    void setHasTheme( Boolean hasTheme );

    // *** HasExt ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_EXT = new ValueProperty( TYPE, "HasExt" );

    Value<Boolean> getHasExt();
    void setHasExt( String hasExt );
    void setHasExt( Boolean hasExt );

    // *** HasServiceBuilder ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_SERVICE_BUILDER = new ValueProperty( TYPE, "HasServiceBuilder" );

    Value<Boolean> getHasServiceBuilder();
    void setHasServiceBuilder( String hasServiceBuilder );
    void setHasServiceBuilder( Boolean hasServiceBuilder );

    // *** HasLayout ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_LAYOUT = new ValueProperty( TYPE, "HasLayout" );

    Value<Boolean> getHasLayout();
    void setHasLayout( String hasLayout );
    void setHasLayout( Boolean hasLayout );

    // *** HasWeb ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_HAS_WEB = new ValueProperty( TYPE, "HasWeb" );

    Value<Boolean> getHasWeb();
    void setHasWeb( String hasWeb );
    void setHasWeb( Boolean hasWeb );

    // *** ConvertedProjectLocation ***

    @Type( base = Path.class )
    @Service( impl = ConvertedProjectLocationValidationService.class )
    ValueProperty PROP_CONVERTED_PROJECT_LOCATION = new ValueProperty( TYPE, "ConvertedProjectLocation" );

    Value<Path> getConvertedProjectLocation();

    void setConvertedProjectLocation( String convertedProjectLocation );
    void setConvertedProjectLocation( Path convertedProjectLocation );

    // *** ConvertLiferayWorkspace ***

    @Type(base = Boolean.class)
    @DefaultValue(text = "false")
    ValueProperty PROP_CONVERT_LIFERAY_WORKSPACE = new ValueProperty( TYPE, "ConvertLiferayWorkspace" );

    Value<Boolean> getConvertLiferayWorkspace();

    void setConvertLiferayWorkspace(String convertLiferayWorkspace);
    void setConvertLiferayWorkspace(Boolean convertLiferayWorkspace);

    // *** BackupSdk ***

    @Type( base = Boolean.class )
    @DefaultValue(text = "false")
    ValueProperty PROP_DOWNLOAD_BUNDLE = new ValueProperty( TYPE, "downloadBundle" );

    Value<Boolean> getDownloadBundle();

    void setDownloadBundle( String downloadBundle );

    void setDownloadBundle( Boolean downloadBundle );

    @Type( base = Boolean.class )
    ValueProperty PROP_BACKUP_SDK = new ValueProperty( TYPE, "BackupSdk" );
    Value<Boolean> getBackupSdk();

    void setBackupSdk( String backupSdk );
    void setBackupSdk( Boolean backupSdk );

    // *** BackupLocation ***

    @Type( base = Path.class )
    @Service( impl = BackupLocationValidationService.class )
    ValueProperty PROP_BACKUP_LOCATION = new ValueProperty( TYPE, "BackupLocation" );
    Value<Path> getBackupLocation();

    void setBackupLocation( String backupLocation );
    void setBackupLocation( Path backupLocation );

    // *** ImportFinished ***

    @Type( base = Boolean.class )
    @DefaultValue( text = "false" )
    ValueProperty PROP_IMPORT_FINISHED = new ValueProperty( TYPE, "ImportFinished" );
    Value<Boolean> getImportFinished();

    void setImportFinished( String ImportFinished );
    void setImportFinished( Boolean ImportFinished );
}
