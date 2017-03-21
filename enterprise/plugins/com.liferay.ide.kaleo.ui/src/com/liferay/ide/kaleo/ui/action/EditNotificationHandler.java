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

package com.liferay.ide.kaleo.ui.action;

import com.liferay.ide.kaleo.core.model.Action;
import com.liferay.ide.kaleo.core.model.Notification;
import com.liferay.ide.kaleo.ui.IKaleoEditorHelper;
import com.liferay.ide.kaleo.ui.KaleoUI;

import org.eclipse.sapphire.Element;
import org.eclipse.sapphire.ImageData;
import org.eclipse.sapphire.ui.Presentation;

/**
 * @author Gregory Amerson
 */
public class EditNotificationHandler extends ListSelectionEditHandler
{

    @Override
    public Object edit( final Element modelElement, final Presentation context )
    {
        final Notification notification = modelElement.nearest( Notification.class );

        IKaleoEditorHelper kaleoEditorHelper =
            KaleoUI.getKaleoEditorHelper( notification.getTemplateLanguage().text( true ) );

        kaleoEditorHelper.openEditor( this.getPart(), notification, Notification.PROP_TEMPLATE );

        return null;
    }

    @Override
    protected ImageData typeImage()
    {
        return Action.TYPE.image();
    }

}
