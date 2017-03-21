/**
 * Copyright (c) 2014 Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.kaleo.ui.diagram;

import com.liferay.ide.kaleo.core.model.Scriptable;
import com.liferay.ide.kaleo.core.model.Task;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.DetailSectionPart;

/**
 * @author Gregory Amerson
 */
public class ScriptableOpenActionHandler extends SapphireActionHandler
{

    @Override
    protected Object run( final Presentation context )
    {
        try
        {
            Scriptable scriptable = scriptable( context );

            if( scriptable != null )
            {
                IKaleoEditorHelper kaleoEditorHelper =
                    KaleoUI.getKaleoEditorHelper( scriptable.getScriptLanguage().text( true ) );

                kaleoEditorHelper.openEditor( context.part(), scriptable, Scriptable.PROP_SCRIPT );
            }
        }
        catch( Exception e )
        {
            KaleoUI.logError( "Could not open script editor.", e );
        }

        return null;
    }

    protected Scriptable scriptable( Presentation context )
    {
        Scriptable retval = null;

        ISapphirePart part = context.part();

        if( part instanceof DetailSectionPart )
        {
            DetailSectionPart pageBook = part.nearest( DetailSectionPart.class );

            retval = pageBook.getCurrentPage().getLocalModelElement().nearest( Scriptable.class );
        }
        else
        {
            final Element modelElement = context.part().getLocalModelElement();

            if( modelElement instanceof Task )
            {
                retval = modelElement.nearest( Task.class ).getScriptedAssignment().content( false );
            }
            else
            {
                retval = modelElement.nearest( Scriptable.class );
            }
        }

        return retval;
    }

}
