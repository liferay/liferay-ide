/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 *    Kamesh Sampath - initial implementation
 ******************************************************************************/

package com.liferay.ide.project.core;

import static com.liferay.ide.sdk.ISDKConstants.EXT_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.ISDKConstants.HOOK_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.ISDKConstants.LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.ISDKConstants.PORTLET_PLUGIN_PROJECT_SUFFIX;
import static com.liferay.ide.sdk.ISDKConstants.THEME_PLUGIN_PROJECT_SUFFIX;

import java.io.File;

/**
 * @author <a href="mailto:kamesh.sampath@hotmail.com">Kamesh Sampath</a>
 */
public class BinaryProjectRecord
{

    private String binaryName;
    private File binaryFile;
    private String displayName;
    private String filePath;
    private String liferayVersion;
    boolean conflicts;
    boolean isHook;
    boolean isTheme;
    boolean isPortlet;
    boolean isLayoutTpl;
    boolean isExt;

    public BinaryProjectRecord( File binaryFile )
    {
        this.binaryFile = binaryFile;
        setNames();
    }

    private void setNames()
    {
        if( binaryFile != null )
        {
            binaryName = binaryFile.getName();
            filePath = binaryFile.getAbsolutePath();
            setPluginProperties();
        }

    }

    private void setPluginProperties()
    {
        if( binaryName != null )
        {
            int index = -1;
            if( binaryName.contains( HOOK_PLUGIN_PROJECT_SUFFIX ) )
            {
                index = binaryName.indexOf( HOOK_PLUGIN_PROJECT_SUFFIX );
                isHook = index != -1 ? true : false;
            }
            else if( binaryName.contains( THEME_PLUGIN_PROJECT_SUFFIX ) )
            {
                index = binaryName.indexOf( THEME_PLUGIN_PROJECT_SUFFIX );
                isTheme = index != -1 ? true : false;
            }
            else if( binaryName.contains( PORTLET_PLUGIN_PROJECT_SUFFIX ) )
            {
                index = binaryName.indexOf( PORTLET_PLUGIN_PROJECT_SUFFIX );
                isPortlet = index != -1 ? true : false;
            }
            else if( binaryName.contains( LAYOUTTPL_PLUGIN_PROJECT_SUFFIX ) )
            {
                index = binaryName.indexOf( LAYOUTTPL_PLUGIN_PROJECT_SUFFIX );
                isLayoutTpl = index != -1 ? true : false;
            }
            else if( binaryName.contains( EXT_PLUGIN_PROJECT_SUFFIX ) )
            {
                index = binaryName.indexOf( EXT_PLUGIN_PROJECT_SUFFIX );
                isExt = index != -1 ? true : false;
            }
            if( index != -1 )
            {
                displayName = binaryName.substring( 0, index );
            }
            index = binaryName.lastIndexOf( "-" ); //$NON-NLS-1$
            if( index != -1 )
            {
                liferayVersion = binaryName.substring( index + 1, binaryName.lastIndexOf( "." ) ); //$NON-NLS-1$
            }
        }

    }

    /**
     * @return the filePath
     */
    public String getFilePath()
    {
        return filePath;
    }

    /**
     * @param filePath
     *            the filePath to set
     */
    public void setFilePath( String label )
    {
        this.filePath = label;
    }

    /**
     * @return the binaryName
     */
    public String getBinaryName()
    {
        return binaryName;
    }

    /**
     * @param binaryName
     *            the binaryName to set
     */
    public void setBinaryName( String binaryName )
    {
        this.binaryName = binaryName;
    }

    /**
     * @return the binaryFile
     */
    public File getBinaryFile()
    {
        return binaryFile;
    }

    /**
     * @param binaryFile
     *            the binaryFile to set
     */
    public void setBinaryFile( File binaryFile )
    {
        this.binaryFile = binaryFile;
    }

    /**
     * @return the displayName
     */
    public String getDisplayName()
    {
        return displayName;
    }

    /**
     * @param displayName
     *            the displayName to set
     */
    public void setDisplayName( String liferayPluginName )
    {
        this.displayName = liferayPluginName;
    }

    /**
     * @return the conflicts
     */
    public boolean isConflicts()
    {
        return conflicts;
    }

    /**
     * @param conflicts
     *            the conflicts to set
     */
    public void setConflicts( boolean hasConflicts )
    {
        this.conflicts = hasConflicts;
    }

    /**
     * @return the liferayVersion
     */
    public String getLiferayVersion()
    {
        return liferayVersion;
    }

    /**
     * @return the isHook
     */
    public boolean isHook()
    {
        return isHook;
    }

    /**
     * @return the isTheme
     */
    public boolean isTheme()
    {
        return isTheme;
    }

    /**
     * @return the isPortlet
     */
    public boolean isPortlet()
    {
        return isPortlet;
    }

    /**
     * @return the isLayoutTpl
     */
    public boolean isLayoutTpl()
    {
        return isLayoutTpl;
    }

    public String getLiferayPluginName()
    {
        if( isHook )
        {
            return getDisplayName() + HOOK_PLUGIN_PROJECT_SUFFIX;
        }
        else if( isLayoutTpl )
        {
            return getDisplayName() + LAYOUTTPL_PLUGIN_PROJECT_SUFFIX;
        }
        else if( isPortlet )
        {
            return getDisplayName() + PORTLET_PLUGIN_PROJECT_SUFFIX;
        }
        else if( isTheme )
        {
            return getDisplayName() + THEME_PLUGIN_PROJECT_SUFFIX;
        }
        else if( isExt )
        {
            return getDisplayName() + EXT_PLUGIN_PROJECT_SUFFIX;
        }
        return null;
    }

    public boolean isExt()
    {
        return isExt;
    }

}
