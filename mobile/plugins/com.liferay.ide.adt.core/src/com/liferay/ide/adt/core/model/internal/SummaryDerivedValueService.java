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
package com.liferay.ide.adt.core.model.internal;

import com.liferay.ide.adt.core.model.GenerateCustomServicesOp;
import com.liferay.mobile.sdk.core.MobileSDKCore;


/**
 * @author Gregory Amerson
 */
public class SummaryDerivedValueService extends StatusDerivedValueService
{
    @Override
    protected String checkValue()
    {
        String retval = null;

        final GenerateCustomServicesOp op = op();

        final Object serverStatus = MobileSDKCore.checkServerStatus(
            op.getUrl().content(), op.getOmniUsername().content(), op.getOmniPassword().content() );

        if( serverStatus instanceof String )
        {
            retval = "N/A";
        }
        else if( serverStatus instanceof Integer )
        {
            retval = "Liferay Portal Build: " + serverStatus;
        }

        return retval;
    }
}
