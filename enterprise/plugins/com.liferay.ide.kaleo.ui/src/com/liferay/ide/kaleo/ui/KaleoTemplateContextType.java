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

package com.liferay.ide.kaleo.ui;

import org.eclipse.jface.text.templates.GlobalTemplateVariables;
import org.eclipse.jface.text.templates.TemplateContextType;

/**
 * @author Gregory Amerson
 */
public class KaleoTemplateContextType extends TemplateContextType
{

    public KaleoTemplateContextType()
    {
        addResolver( new GlobalTemplateVariables.Cursor() );
        addResolver( new GlobalTemplateVariables.Date() );
        addResolver( new GlobalTemplateVariables.Dollar() );
        addResolver( new GlobalTemplateVariables.LineSelection() );
        addResolver( new GlobalTemplateVariables.Time() );
        addResolver( new GlobalTemplateVariables.User() );
        addResolver( new GlobalTemplateVariables.WordSelection() );
        addResolver( new GlobalTemplateVariables.Year() );
    }
}
