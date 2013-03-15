/**
 * Copyright (c) 2000-2013 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.ui;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.wst.server.core.IServerAttributes;
import org.eclipse.wst.server.core.IServerType;

/**
 * @author Cindy Li
 */
public class ServerPropertyTester extends PropertyTester
{
    public boolean test( Object receiver, String property, Object[] args, Object expectedValue )
    {
        if( receiver instanceof IServerAttributes )
        {
            IServerAttributes server = (IServerAttributes) receiver;
            IServerType serverType = server.getServerType();

            if( serverType != null )
            {
                String id = serverType.getId();

                if( "com.liferay.ide.eclipse.server.remote".equals( id ) ) //$NON-NLS-1$
                {
                    return true;
                }
            }
        }

        return false;
    }

}
