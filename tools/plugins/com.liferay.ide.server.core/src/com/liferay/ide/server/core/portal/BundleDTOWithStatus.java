package com.liferay.ide.server.core.portal;

import org.osgi.framework.dto.BundleDTO;


public class BundleDTOWithStatus extends BundleDTO
{

    public String _status;

    public BundleDTOWithStatus( BundleDTO original, String status )
    {
        id = original.id;
        lastModified = original.lastModified;
        state = original.state;
        symbolicName = original.symbolicName;
        version = original.version;
        _status = status;
    }


}
