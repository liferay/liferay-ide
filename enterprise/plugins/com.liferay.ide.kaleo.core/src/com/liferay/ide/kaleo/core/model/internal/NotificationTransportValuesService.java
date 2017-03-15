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

package com.liferay.ide.kaleo.core.model.internal;

import com.liferay.ide.kaleo.core.model.WorkflowDefinition;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;
import org.eclipse.sapphire.Version;

/**
 * @author Gregory Amerson
 */
public class NotificationTransportValuesService extends PossibleValuesService
{

    static final Version v62 = new Version( "6.2" );

    @Override
    protected void compute( Set<String> values )
    {
        values.add( "email" );
        values.add( "im" );
        values.add( "private-message" );

        final Version version = context( WorkflowDefinition.class ).getSchemaVersion().content();

        if( v62.compareTo( version ) <= 0 ) // if version is bigger than 6.2
        {
            values.add( "user-notification" );
        }
    }

}
