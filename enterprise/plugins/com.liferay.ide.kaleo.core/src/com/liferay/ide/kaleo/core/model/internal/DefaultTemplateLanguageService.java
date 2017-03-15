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

import com.liferay.ide.kaleo.core.KaleoCore;
import com.liferay.ide.kaleo.core.model.TemplateLanguageType;
import com.liferay.ide.kaleo.core.util.KaleoModelUtil;

import org.eclipse.sapphire.DefaultValueService;
import org.eclipse.sapphire.Element;

/**
 * @author Gregory Amerson
 */
public class DefaultTemplateLanguageService extends DefaultValueService
{

    @Override
    protected String compute()
    {
        return KaleoModelUtil.getDefaultValue(
            context( Element.class ), KaleoCore.DEFAULT_TEMPLATE_LANGUAGE_KEY, TemplateLanguageType.FREEMARKER );
    }

}
