/**
 * Copyright (c) 2000-2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the Liferay Enterprise
 * Subscription License ("License"). You may not use this file except in
 * compliance with the License. You can obtain a copy of the License by
 * contacting Liferay, Inc. See the License for the specific language governing
 * permissions and limitations under the License, including but not limited to
 * distribution rights of the Software.
 */

package com.liferay.ide.server.ui.cmd;

import org.eclipse.osgi.util.NLS;

import com.liferay.ide.server.core.ILiferayServerWorkingCopy;
import com.liferay.ide.server.core.LiferayServerCommand;

/**
 * @author Greg Amerson
 */
public class SetPasswordCommand extends LiferayServerCommand
{

    protected String oldPassword;
    protected String password;

    public SetPasswordCommand( ILiferayServerWorkingCopy server, String password )
    {
        super( server, Msgs.setPassword );
        this.password = password;
    }

    /**
     * Execute setting the memory args
     */
    public void execute()
    {
        oldPassword = server.getPassword();
        server.setPassword( password );
    }

    /**
     * Restore prior memoryargs
     */
    public void undo()
    {
        server.setPassword( oldPassword );
    }

    private static class Msgs extends NLS
    {
        public static String setPassword;

        static
        {
            initializeMessages( SetPasswordCommand.class.getName(), Msgs.class );
        }
    }
}
