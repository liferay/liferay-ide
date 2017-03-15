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

import org.eclipse.sapphire.ElementHandle;
import org.eclipse.sapphire.ElementProperty;
import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Type;
import org.eclipse.sapphire.modeling.annotations.Label;
import org.eclipse.sapphire.modeling.xml.annotations.XmlBinding;

/**
 * @author Gregory Amerson
 */
public interface TaskActionNotification extends ActionNotification
{
    ElementType TYPE = new ElementType( TaskActionNotification.class );

    // *** Assignees ***

    @Type( base = Assignees.class )
    @Label( standard = "assignees" )
    @XmlBinding( path = "recipients/assignees" )
    ElementProperty PROP_ASSIGNEES = new ElementProperty( TYPE, "Assignees" );

    ElementHandle<Assignees> getAssignees();

}
