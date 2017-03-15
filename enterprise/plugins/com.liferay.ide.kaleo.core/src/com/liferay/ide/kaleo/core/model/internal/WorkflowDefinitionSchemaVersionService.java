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
import com.liferay.ide.project.core.util.VersionedSchemaDefaultValueService;

import java.util.regex.Pattern;

/**
 * @author Gregory Amerson
 */
public class WorkflowDefinitionSchemaVersionService extends VersionedSchemaDefaultValueService
{

    static final Pattern pattern = Pattern.compile( "urn:liferay.com:liferay-workflow_(.*)" );

    public WorkflowDefinitionSchemaVersionService()
    {
        super( pattern, KaleoCore.DEFAULT_KALEO_VERSION );
    }

}
