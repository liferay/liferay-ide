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

package com.liferay.ide.swtbot.service.ui.tests.page;

import org.eclipse.swtbot.swt.finder.SWTBot;

import com.liferay.ide.swtbot.service.ui.tests.ServiceBuilderWizard;
import com.liferay.ide.swtbot.ui.tests.page.TextPO;

/**
 * @author Ying Xu
 */
public class ServiceBuilderEntitiesPO extends TextPO implements ServiceBuilderWizard
{

    private TextPO _entityName;

    public ServiceBuilderEntitiesPO( SWTBot bot )
    {
        this( bot, TEXT_BLANK );
    }

    public ServiceBuilderEntitiesPO( SWTBot bot, String title )
    {
        super( bot, title );

        _entityName = new TextPO( bot, LABEL_ENTITY_NAME );
    }

    public void SetServiceBuilderEntitiesName( String name )
    {
        _entityName.setText( name );
    }
}
