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

package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.PlatformObject;

/**
 * @author Gregory Amerson
 */
public class DefinitionStorage extends PlatformObject implements IStorage
{

    private WorkflowDefinitionEntry node;

    public DefinitionStorage( WorkflowDefinitionEntry defNode )
    {
        this.node = defNode;
    }

    public InputStream getContents() throws CoreException
    {
        return new ByteArrayInputStream( node.getContent() != null ? node.getContent().getBytes() : "".getBytes() );
    }

    public IPath getFullPath()
    {
        return new Path( node.getLocation() + "/KaleoWorkflowDefinitions/" + this.node.getName() );
    }

    public String getName()
    {
        return this.node.getName();
    }

    public boolean isReadOnly()
    {
        return false;
    }

}
