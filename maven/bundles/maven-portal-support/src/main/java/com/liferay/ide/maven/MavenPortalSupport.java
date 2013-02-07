package com.liferay.ide.maven;

import com.liferay.portal.deploy.hot.HookHotDeployListener;


public class MavenPortalSupport
{

    public String[] getHookSupportedProperties()
    {
        return HookHotDeployListener.SUPPORTED_PROPERTIES;
    }

}
