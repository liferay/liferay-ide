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

import org.eclipse.sapphire.ElementType;
import org.eclipse.sapphire.Length;
import org.eclipse.sapphire.ListProperty;

/**
 * @author Gregory Amerson
 */
public interface MustTransition extends CanTransition
{

    ElementType TYPE = new ElementType( MustTransition.class );

    // *** Transitions ***

    @Length( min = 1 )
    ListProperty PROP_TRANSITIONS = new ListProperty( TYPE, CanTransition.PROP_TRANSITIONS );
}
