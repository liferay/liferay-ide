package com.liferay.ide.project.ui.upgrade;

import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 */
public class CopyPortalSettingsWizard extends SapphireWizard<CopyPortalSettingsOp>
{

    public CopyPortalSettingsWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( CopyPortalSettingsWizard.class ).wizard() );
    }

    private static CopyPortalSettingsOp createDefaultOp()
    {
        return CopyPortalSettingsOp.TYPE.instantiate();
    }
}
