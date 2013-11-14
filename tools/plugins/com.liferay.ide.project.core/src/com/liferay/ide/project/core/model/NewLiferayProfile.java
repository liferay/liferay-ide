package com.liferay.ide.project.core.model;

import com.liferay.ide.project.core.model.internal.LiferayVersionDefaultValueService;
import com.liferay.ide.project.core.model.internal.LiferayVersionPossibleValuesService;
import com.liferay.ide.project.core.model.internal.NewLiferayProfileIdDefaultValueService;
import com.liferay.ide.project.core.model.internal.NewLiferayProfileIdValidationService;

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.annotations.DefaultValue;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.annotations.Service;
import org.eclipse.sapphire.modeling.annotations.Services;
import org.eclipse.sapphire.modeling.annotations.Type;


/**
 * @author Gregory Amerson
 */
public interface NewLiferayProfile extends Profile, HasLiferayRuntime
{
    ElementType TYPE = new ElementType( NewLiferayProfile.class );

    // *** Id ***

    @Services
    (
        value =
        {
            @Service( impl = NewLiferayProfileIdDefaultValueService.class ),
            @Service( impl = NewLiferayProfileIdValidationService.class ),
        }
    )
    @Label( standard = "new profile id" )
    ValueProperty PROP_ID = new ValueProperty( TYPE, "Id" );


    // *** ProfileLocation ***

    @Type( base = ProfileLocation.class )
    @Label( standard = "profile location" )
    @DefaultValue( text = "projectPom" )
    ValueProperty PROP_PROFILE_LOCATION = new ValueProperty( TYPE, "ProfileLocation" );

    Value<ProfileLocation> getProfileLocation();
    void setProfileLocation( String value );
    void setProfileLocation( ProfileLocation value );


    // *** LiferayVersion ***

    @Label( standard = "liferay version" )
    @Services
    (
        value =
        {
            @Service
            (
                impl = LiferayVersionPossibleValuesService.class,
                params =
                {
                    @Service.Param( name = "groupId", value = "com.liferay.portal" ),
                    @Service.Param( name = "artifactId", value = "portal-service" ),
                }
            ),
            @Service( impl = LiferayVersionDefaultValueService.class )
        }
    )
    ValueProperty PROP_LIFERAY_VERSION = new ValueProperty( TYPE, "LiferayVersion" );

    Value<String> getLiferayVersion();
    void setLiferayVersion( String value );

}
