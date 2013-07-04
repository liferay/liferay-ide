package com.liferay.ide.sdk.core;

import com.liferay.ide.core.util.CoreUtil;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.variables.IDynamicVariable;
import org.eclipse.core.variables.IDynamicVariableResolver;


public class SDKProjectVariableResolver implements IDynamicVariableResolver
{

    public String resolveValue( IDynamicVariable variable, String argument ) throws CoreException
    {
        String retval = null;
        SDK sdk = null;

        if( CoreUtil.isNullOrEmpty( argument ) )
        {
            sdk = SDKManager.getInstance().getDefaultSDK();
        }
        else
        {
            sdk = SDKUtil.getSDK( CoreUtil.getProject( argument ) );
        }

        if( sdk != null )
        {
            retval = sdk.getLocation().toOSString();
        }

        return retval;
    }

}
