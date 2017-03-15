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

import com.liferay.ide.kaleo.core.model.ExecutionType;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import java.util.Set;

import org.eclipse.sapphire.PossibleValuesService;

/**
 * @author Gregory Amerson
 */
public class ExecutionTypePossibleValuesService extends PossibleValuesService
{

    public static final String ON_ENTRY = KaleoModelUtil.getEnumSerializationAnnotation( ExecutionType.ON_ENTRY );
    public static final String ON_EXIT = KaleoModelUtil.getEnumSerializationAnnotation( ExecutionType.ON_EXIT );
    public static final String ON_ASSIGNMENT =
        KaleoModelUtil.getEnumSerializationAnnotation( ExecutionType.ON_ASSIGNMENT );

    private Boolean inTaskHeirarchy = null;

    @Override
    protected void compute( Set<String> values )
    {
        values.add( ON_ENTRY );
        values.add( ON_EXIT );

        if( isInTaskHeirarchy() )
        {
            values.add( ON_ASSIGNMENT );
        }
    }

    protected boolean isInTaskHeirarchy()
    {
        if( inTaskHeirarchy == null )
        {
            this.inTaskHeirarchy = context().find( Task.class ) != null;
        }

        return this.inTaskHeirarchy;
    }

}
