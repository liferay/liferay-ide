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

package com.liferay.ide.project.core.modules;

import java.util.Set;

import org.eclipse.sapphire.Event;
import org.eclipse.sapphire.Listener;
import org.eclipse.sapphire.modeling.Status;
import org.eclipse.sapphire.services.ValidationService;

/**
 * @author Gregory Amerson
 */
public class ProjectTemplateNameValidationService extends ValidationService
{

    private Status templateNameStatus = Status.createErrorStatus( "Downloading templates, please wait..." );

    @Override
    protected void initValidationService()
    {
        super.initValidationService();

        ProjectTemplateNamePossibleValuesService pvs = context( NewLiferayModuleProjectOp.class ).property( NewLiferayModuleProjectOp.PROP_PROJECT_TEMPLATE_NAME ).service( ProjectTemplateNamePossibleValuesService.class );

        Set<String> templateNames = pvs.values();

        if (templateNames.size() > 0) {
            templateNameStatus = Status.createOkStatus();
        }
        else {
            pvs.attach( new Listener(){
                @Override
                public void handle( Event event )
                {
                    templateNameStatus = Status.createOkStatus();

                    refresh();
                }}
            );
        }
    }

    @Override
    protected Status compute()
    {
        return templateNameStatus;
    }

}
