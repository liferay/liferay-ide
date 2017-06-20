/*******************************************************************************
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
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
 *******************************************************************************/

package com.liferay.ide.swtbot.ui.tests.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotPerspective;

/**
 * @author Terry Jia
 */
public class PerspectivePO extends AbstractPO
{

    private final String _lable;

    public PerspectivePO( SWTWorkbenchBot bot, String label )
    {
        super( bot );

        _lable = label;
    }

    public void activate()
    {
        getPerspective().activate();
    }

    protected SWTBotPerspective getPerspective()
    {
        return ( (SWTWorkbenchBot) bot ).perspectiveByLabel( _lable );
    }

}