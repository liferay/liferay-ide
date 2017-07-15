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

package com.liferay.ide.swtbot.ui.eclipse.page;

import org.eclipse.swtbot.eclipse.finder.SWTWorkbenchBot;

import com.liferay.ide.swtbot.ui.UI;
import com.liferay.ide.swtbot.ui.page.Text;
import com.liferay.ide.swtbot.ui.page.View;

/**
 * @author Terry Jia
 */
public class ProgressView extends View implements UI
{

    private Text noOperationsText;

    public ProgressView( SWTWorkbenchBot bot )
    {
        super( bot, LABEL_PROGRESS );

        noOperationsText = new Text( bot, INDEX_DEFAULT_START );
    }

    public String getNoOperationText()
    {
        return noOperationsText.getText();
    }

    public boolean noRunningProgress()
    {
        return getNoOperationText().equals( TEXT_NO_OPERTAIONS );
    }

}