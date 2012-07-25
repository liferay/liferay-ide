package com.liferay.ide.velocity.ui.editor.outline;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.liferay.ide.velocity.vaulttec.ui.VelocityPluginImages;
import com.liferay.ide.velocity.vaulttec.ui.model.Directive;
import com.liferay.ide.velocity.vaulttec.ui.model.ITreeNode;

/**
 * Standard label provider for Velocity template elements.
 */
public class VelocityOutlineLabelProvider extends LabelProvider
{

    /**
     * @see ILabelProvider#getImage(Object)
     */
    public Image getImage(Object anElement)
    {
        if (anElement instanceof Directive)
        {
            int type = ((Directive) anElement).getType();
            String name;
            if (type < Directive.TYPE_MACRO_CALL)
            {
                name = VelocityPluginImages.IMG_OBJ_SYSTEM_DIRECTIVE;
            } else if (type == Directive.TYPE_MACRO_CALL)
            {
                name = VelocityPluginImages.IMG_OBJ_MACRO;
            } else
            {
                name = VelocityPluginImages.IMG_OBJ_USER_DIRECTIVE;
            }
            return VelocityPluginImages.get(name);
        }
        return null;
    }

    /**
     * @see ILabelProvider#getText(Object)
     */
    public String getText(Object anElement)
    {
        return ((ITreeNode) anElement).getName();
    }
}
