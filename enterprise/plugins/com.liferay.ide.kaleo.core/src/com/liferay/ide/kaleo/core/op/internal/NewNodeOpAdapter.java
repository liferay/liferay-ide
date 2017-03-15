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

package com.liferay.ide.kaleo.core.op.internal;

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.WorkflowDefinition;
import com.liferay.ide.kaleo.core.op.NewNodeOp;

import java.lang.reflect.Method;

import org.eclipse.sapphire.UniversalConversionService;


/**
 * @author Gregory Amerson
 */
public class NewNodeOpAdapter extends UniversalConversionService
{

    @Override
    public <T> T convert( Object object, Class<T> type )
    {
        if( type.equals( WorkflowDefinition.class ) )
        {
            NewNodeOp op = context().find( NewNodeOp.class );

            if( op.getWorkflowDefinition().content( false ) != null )
            {
                return type.cast( op.getWorkflowDefinition().content( false ) );
            }
        }
        else if( type.equals( CanTransition.class ) )
        {
            final String simpleName = object.getClass().getSimpleName();

            final int index = simpleName.indexOf( "Op$Impl" );

            if( index > -1 )
            {
                try
                {
                    final String simpleNamePrefix = object.getClass().getSimpleName().substring( 0, index );

                    final String methodName = "get" + simpleNamePrefix;

                    final Method method = object.getClass().getMethod( methodName );

                    return type.cast( method.invoke( object ) );
                }
                catch( Exception e )
                {
                }
            }

        }

        return null;
    }

}
