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

import com.liferay.ide.kaleo.core.model.CanTransition;
import com.liferay.ide.kaleo.core.model.Transition;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.sapphire.FilteredListener;
import org.eclipse.sapphire.PropertyContentEvent;

/**
 * @author Gregory Amerson
 * @author Kuo Zhang
 */
public class TransitionTargetListener extends FilteredListener<PropertyContentEvent>
{

    @Override
    protected void handleTypedEvent( final PropertyContentEvent event )
    {
        final Transition transition = event.property().nearest( Transition.class );

        if( transition != null )
        {
            if( transition.getTarget().content() != null && transition.getName().content( false ) == null )
            {
                final String targetName = transition.getTarget().content();

                String defaultName = targetName;

                final Set<String> existingNames = new HashSet<String>();

                for( Transition t : transition.nearest( CanTransition.class ).getTransitions() )
                {
                    if( t.getName().content() != null )
                    {
                        existingNames.add( t.getName().content() );
                    }
                }

                int count = 1;

                while( existingNames.contains( defaultName ) )
                {
                    defaultName = targetName + "_" + count++;
                }

                transition.setName( defaultName );
            }
        }
    }

}
