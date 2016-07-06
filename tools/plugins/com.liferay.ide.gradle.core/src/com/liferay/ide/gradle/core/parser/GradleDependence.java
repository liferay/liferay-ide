
package com.liferay.ide.gradle.core.parser;

import java.util.Map;

public class GradleDependence
{

    private String group;
    private String name;
    private String version;

    public GradleDependence( Map<String, String> dep )
    {
        setGroup( dep.get( "group" ) );
        setName( dep.get( "name" ) );
        setVersion( dep.get( "version" ) );
    }

    public String getGroup()
    {
        return group;
    }

    public void setGroup( String group )
    {
        this.group = group;
    }

    public String getName()
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getVersion()
    {
        return version;
    }

    public void setVersion( String version )
    {
        this.version = version;
    }

}
