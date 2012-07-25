package com.liferay.ide.velocity.editor;

import com.liferay.ide.velocity.ui.VeloContextType;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;

import java.io.IOException;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.ui.editors.text.templates.ContributionContextTypeRegistry;
import org.eclipse.ui.editors.text.templates.ContributionTemplateStore;

/**
 * The main plugin class to be used in the desktop.
 */
public class TemplateEditorUI
{

    /** Key to store custom templates. */
	private static final String CUSTOM_TEMPLATES_KEY = "com.liferay.ide..velocity.customtemplates"; //$NON-NLS-1$
    /** The shared instance. */
    private static TemplateEditorUI         fInstance;
    /** The template store. */
    private TemplateStore                   fStore;
    /** The context type registry. */
    private ContributionContextTypeRegistry fRegistry;

    private TemplateEditorUI()
    {
    }

    /**
     * Returns the shared instance.
     * 
     * @return the shared instance
     */
    public static TemplateEditorUI getDefault()
    {
        if (fInstance == null) fInstance = new TemplateEditorUI();
        return fInstance;
    }

    /**
     * Returns this plug-in's template store.
     * 
     * @return the template store of this plug-in instance
     */
    public TemplateStore getTemplateStore()
    {
        if (fStore == null)
        {
            fStore = new ContributionTemplateStore(getContextTypeRegistry(), VelocityPlugin.getDefault().getPreferenceStore(), CUSTOM_TEMPLATES_KEY);
            try
            {
                fStore.load();
            }
            catch (IOException e)
            {
                VelocityPlugin.getDefault().getLog().log(new Status(IStatus.ERROR, "org.eclipse.ui.examples.javaeditor", IStatus.OK, "", e)); //$NON-NLS-1$ //$NON-NLS-2$
            }
        }
        return fStore;
    }

    /**
     * Returns this plug-in's context type registry.
     * 
     * @return the context type registry for this plug-in instance
     */
    public ContextTypeRegistry getContextTypeRegistry()
    {
        if (fRegistry == null)
        {
            // create an configure the contexts available in the template editor
            fRegistry = new ContributionContextTypeRegistry();
            fRegistry.addContextType(VeloContextType.XML_CONTEXT_TYPE);
        }
        return fRegistry;
    }

    /* Forward plug-in methods to javaeditor example plugin default instance */
    public ImageRegistry getImageRegistry()
    {
        return VelocityPlugin.getDefault().getImageRegistry();
    }

    public static ImageDescriptor imageDescriptorFromPlugin(String string, String default_image)
    {
        return VelocityPlugin.imageDescriptorFromPlugin(string, default_image);
    }

    public IPreferenceStore getPreferenceStore()
    {
        return VelocityPlugin.getDefault().getPreferenceStore();
    }

    public void savePluginPreferences()
    {
        VelocityPlugin.getDefault().savePluginPreferences();
    }
}
