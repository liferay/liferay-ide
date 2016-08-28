package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.Value;
import org.eclipse.sapphire.ValueProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.annotations.Required;

public interface LiferayUpgradeDataModel extends Element
{
    ElementType TYPE = new ElementType( LiferayUpgradeDataModel.class );
    
    
    @Type( base = Path.class )
    @Required
    ValueProperty PROP_SDK_LOCATION = new ValueProperty( TYPE, "SdkLocation" );
    Value<Path> getSdkLocation();
    void setSdkLocation( String sdkLocation );
    void setSdkLocation( Path sdkLocation );
    
    @Required
    ValueProperty PROP_PROJECT_NAME = new ValueProperty( TYPE, "ProjectName" );
    Value<String> getProjectName();
    void setProjectName( String ProjectName );
    
    @Required
    ValueProperty PROP_LAYOUT = new ValueProperty( TYPE, "Layout" );
    Value<String> getLayout();
    void setLayout( String Layout );

    @Required
    ValueProperty PROP_LIFERAY_SERVER_NAME = new ValueProperty( TYPE, "LiferayServerName" );
    Value<String> getLiferayServerName();
    void setLiferayServerName( String value );
}
