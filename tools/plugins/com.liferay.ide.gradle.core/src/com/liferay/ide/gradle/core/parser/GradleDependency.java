
package com.liferay.ide.gradle.core.parser;

import java.util.Map;

/**
 * @author Lovett Li
 */
public class GradleDependency
{

    private String group;
    private String name;
    private String version;

    public GradleDependency( Map<String, String> dep )
    {
        setGroup( dep.get( "group" ) );
        setName( dep.get( "name" ) );
        setVersion( dep.get( "version" ) );
    }

    public GradleDependency( String group, String name, String version )
    {
        this.group = group;
        this.name = name;
        this.version = version;
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
