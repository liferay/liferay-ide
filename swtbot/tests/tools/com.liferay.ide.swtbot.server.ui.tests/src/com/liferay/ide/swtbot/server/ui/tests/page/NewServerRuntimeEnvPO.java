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

package com.liferay.ide.swtbot.server.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.server.ui.tests.ServerRuntimeWizard;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;
import com.liferay.ide.swtbot.ui.tests.page.WizardPO;

/**
 * @author Vicky Wang
 * @author Ying Xu
 */
public class NewServerRuntimeEnvPO extends WizardPO implements ServerRuntimeWizard
{

    private TextPO _portalBundleType;
    private TextPO _serverLocation;
    private TextPO _name;

    public NewServerRuntimeEnvPO( SWTBot bot )
    {
        this( bot, TITLE_NEW_SERVER_RUNTIME_ENVIRONMENT, SPECIFY_PORTAL_BUNDLE_LOCATION_INDEX );
    }

    public NewServerRuntimeEnvPO( SWTBot bot, String title, int validationMessageIndex )
    {
        super( bot, title, BUTTON_CANCEL, BUTTON_FINISH, BUTTON_BACK, BUTTON_NEXT, validationMessageIndex );

        _serverLocation = new TextPO( bot, LABEL_SERVER_LOCATION );
        _portalBundleType = new TextPO( bot, LABEL_BUNDLE_TYPE );
        _name = new TextPO( bot, LABEL_RUNTIME_NAME );
    }

    public TextPO getPortalBundleType()
    {
        return _portalBundleType;
    }

    public TextPO getServerLocation()
    {
        return _serverLocation;
    }

    public TextPO getName()
    {
        return _name;
    }

    public void setName( TextPO name )
    {
        this._name = name;
    }

}
