/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.diagram;

import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.SapphireActionHandlerFilter;
import org.eclipse.sapphire.ui.diagram.actions.DiagramNodeAddActionHandler;

/**
 * @author Gregory Amerson
 */
public class DiagramNodeAddActionHandlerFilter extends SapphireActionHandlerFilter
{

    @Override
    public boolean check( SapphireActionHandler handler )
    {
        if( handler instanceof NewNodeAddActionHandler )
        {
            return true;
        }

        if( handler instanceof DiagramNodeAddActionHandler )
        {
            return false;
        }

        return true;
    }

}
