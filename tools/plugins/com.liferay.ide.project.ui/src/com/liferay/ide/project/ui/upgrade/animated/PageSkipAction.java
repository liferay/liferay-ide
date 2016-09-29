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

package com.liferay.ide.project.ui.upgrade.animated;

/**
 * @author Simon Jiang
 */
public class PageSkipAction extends PageAction
{
    private static String pageActionName = "PageSkipAction";

    protected PageSkipAction()
    {
        super( pageActionName );

        images[0] = loadImage( "no.png" );
        images[1] = loadImage( "no_select.png" );
        images[2] = loadImage( "no_hover.png" );
        images[3] = loadImage( "no_big.png" );
        images[4] = loadImage( "no_badge.png" );
    }
}
