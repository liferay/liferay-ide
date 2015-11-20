package com.liferay.ide.project.core.upgrade;

/**
 * @author Lovett Li
 */
public class Liferay7UpgradeAssistantSettings
{

    private PortalSettings portalSettings;
    private String[] javaProjectLocations;

    public Liferay7UpgradeAssistantSettings()
    {
    }

    public PortalSettings getPortalSettings()
    {
        return portalSettings;
    }

    public void setPortalSettings( PortalSettings portalSettings )
    {
        this.portalSettings = portalSettings;
    }

    public String[] getJavaProjectLocations()
    {
        return javaProjectLocations;
    }

    public void setJavaProjectLocations( String[] locations )
    {
        javaProjectLocations = locations;
    }

}
