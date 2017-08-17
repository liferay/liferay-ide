/**
 * Copyright (c) 2000-present Liferay, Inc. All rights reserved.
 *
 * The contents of this file are subject to the terms of the End User License
 * Agreement for Liferay Developer Studio ("License"). You may not use this file
 * except in compliance with the License. You can obtain a copy of the License
 * by contacting Liferay, Inc. See the License for the specific language
 * governing permissions and limitations under the License, including but not
 * limited to distribution rights of the Software.
 */

package com.liferay.ide.studio.ui;

import com.liferay.ide.project.core.workspace.ImportLiferayWorkspaceOp;
import com.liferay.ide.project.ui.workspace.ImportLiferayWorkspaceWizard;

import java.io.File;
import java.util.Properties;

import org.eclipse.core.resources.WorkspaceJob;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.sapphire.platform.ProgressMonitorBridge;
import org.eclipse.sapphire.platform.StatusBridge;
import org.eclipse.ui.internal.intro.impl.IntroPlugin;
import org.eclipse.ui.intro.IIntroSite;
import org.eclipse.ui.intro.config.IIntroAction;

/**
 * @author Andy Wu
 */
@SuppressWarnings( "restriction" )
public class ImportLiferayWorkspaceFromInstallerAction implements IIntroAction
{

    @Override
    public void run( IIntroSite site, Properties params )
    {
        Job job = new WorkspaceJob( "Importing Liferay Workspace..." )
        {

            @Override
            public IStatus runInWorkspace( IProgressMonitor monitor ) throws CoreException
            {
                ImportLiferayWorkspaceWizard wizard = new ImportLiferayWorkspaceWizard();

                File location = new File( Platform.getInstallLocation().getURL().getFile() );

                if( Platform.getOS().equals( Platform.OS_MACOSX ) )
                {

                    location = location.getParentFile().getParentFile();
                }

                IPath path = new Path( location.getAbsolutePath() );

                ImportLiferayWorkspaceOp op = wizard.element().nearest( ImportLiferayWorkspaceOp.class );

                op.setWorkspaceLocation( path.append( "../liferay-workspace" ).toPortableString() );

                op.setProvisionLiferayBundle( true );

                if( op.validation().ok() )
                {
                    op.execute( ProgressMonitorBridge.create( monitor ) );

                    return Status.OK_STATUS;
                }
                else
                {
                    return StatusBridge.create( op.validation() );
                }
            }
        };

        job.schedule();

        IntroPlugin.closeIntro();
    }
}
