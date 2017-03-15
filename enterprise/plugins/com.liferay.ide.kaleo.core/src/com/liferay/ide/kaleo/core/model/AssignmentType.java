/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay IDE ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.core.model;

import org.eclipse.sapphire.modeling.annotations.Label;

/**
 * @author Gregory Amerson
 */
public enum AssignmentType
{

    @Label( standard = "assign to asset creator" )
    CREATOR,

    @Label( standard = "assign to several role types" )
    ROLE_TYPE,

    @Label( standard = "assign to a specific role by id" )
    ROLE,

    @Label( standard = "write a script to determine assignments" )
    SCRIPTED_ASSIGNMENT,

    @Label( standard = "assign to a specific user" )
    USER,

    @Label( standard = "specify resource actions" )
    RESOURCE_ACTIONS

}
