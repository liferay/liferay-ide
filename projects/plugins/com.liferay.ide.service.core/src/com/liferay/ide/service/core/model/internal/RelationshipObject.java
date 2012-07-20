/*******************************************************************************
 * Copyright (c) 2000-2012 Liferay, Inc. All rights reserved.
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
 * Contributors:
 * 		Gregory Amerson - initial implementation and ongoing maintenance
 *******************************************************************************/

package com.liferay.ide.service.core.model.internal;

/**
 * @author Gregory Amerson
 */
public class RelationshipObject
{

    private String fromName;
    private String toName;

    public RelationshipObject()
    {
    }

    public RelationshipObject( String fromName, String toName )
    {
        this.fromName = fromName;
        this.toName = toName;
    }

    public String getFromName()
    {
        return fromName;
    }

    public String getToName()
    {
        return toName;
    }

    public void setFromName( String fromName )
    {
        this.fromName = fromName;
    }

    public void setToName( String toName )
    {
        this.toName = toName;
    }

}
