package com.liferay.ide.project.ui.upgrade;

import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;


/**
 * @author Gregory Amerson
 */
public class GetPortalSettingsWizard extends SapphireWizard<GetPortalSettingsOp>
{

    public GetPortalSettingsWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( GetPortalSettingsWizard.class ).wizard() );
    }

    private static GetPortalSettingsOp createDefaultOp()
    {
        return GetPortalSettingsOp.TYPE.instantiate();
    }
}
