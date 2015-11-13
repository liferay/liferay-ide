
package com.liferay.ide.project.ui.upgrade;

import org.eclipse.sapphire.ui.def.DefinitionLoader;
import org.eclipse.sapphire.ui.forms.swt.SapphireWizard;

/**
 * @author Andy Wu
 */
public class ConvertJspHookWizard extends SapphireWizard<ConvertJspHookOp>
{

    public ConvertJspHookWizard()
    {
        super( createDefaultOp(), DefinitionLoader.sdef( ConvertJspHookWizard.class ).wizard() );
    }

    private static ConvertJspHookOp createDefaultOp()
    {
        return ConvertJspHookOp.TYPE.instantiate();
    }
}
