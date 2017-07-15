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

package com.liferay.ide.swtbot.liferay.ui.page.wizard;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.Wizard;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewLiferay7RuntimeWizard extends Wizard
{

    private Text name;
    private Text portalBundleType;
    private Text serverLocation;

    public NewLiferay7RuntimeWizard( SWTBot bot )
    {
        super( bot, TITLE_NEW_SERVER_RUNTIME_ENVIRONMENT, SPECIFY_PORTAL_BUNDLE_LOCATION_INDEX );

        serverLocation = new Text( bot, LABEL_SERVER_LOCATION );
        portalBundleType = new Text( bot, LABEL_BUNDLE_TYPE );
        name = new Text( bot, LABEL_RUNTIME_NAME );
    }

    public Text getName()
    {
        return name;
    }

    public Text getPortalBundleType()
    {
        return portalBundleType;
    }

    public Text getServerLocation()
    {
        return serverLocation;
    }

}
