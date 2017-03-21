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

import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.ui.ISapphirePart;
import org.eclipse.sapphire.ui.Presentation;
import org.eclipse.sapphire.ui.SapphireActionHandler;
import org.eclipse.sapphire.ui.forms.DetailSectionPart;

/**
 * @author Gregory Amerson
 */
public class TemplateOpenActionHandler extends SapphireActionHandler
{

    protected Notification notification( Presentation context )
    {
        ISapphirePart part = context.part();

        if (part instanceof DetailSectionPart)
        {
            DetailSectionPart pageBook = part.nearest( DetailSectionPart.class );

            return pageBook.getCurrentPage().getLocalModelElement().nearest( Notification.class );
        }

        return context.part().getLocalModelElement().nearest( Notification.class );
    }

    @Override
    protected Object run( final Presentation context )
    {
        try
        {
            Notification notification = notification( context );

            IKaleoEditorHelper kaleoEditorHelper =
                KaleoUI.getKaleoEditorHelper( notification.getTemplateLanguage().text( true ) );

            kaleoEditorHelper.openEditor( context.part(), notification, Notification.PROP_TEMPLATE );
        }
        catch( Exception e )
        {
            KaleoUI.logError( "Could not open template editor.", e );
        }

        return null;
    }

}
