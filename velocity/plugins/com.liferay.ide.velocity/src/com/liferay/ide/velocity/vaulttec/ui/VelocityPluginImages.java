package com.liferay.ide.velocity.vaulttec.ui;

import java.net.MalformedURLException;
import java.net.URL;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;

/**
 * Bundle of images used by Velocity plugin.
 */
public class VelocityPluginImages
{

    private static URL                  fgIconBaseURL             = null;
    // Determine display depth. If depth > 4 then we use high color images.
    // Otherwise low color images are used
    static
    {
        // Don't consider the default display since accessing it throws an
        // SWTException anyway.
        Display display = Display.getCurrent();
        String pathSuffix;
        if ((display != null) && (display.getIconDepth() > 4))
        {
            pathSuffix = "icons/full/";
        } else
        {
            pathSuffix = "icons/basic/";
        }
        try
        {
            fgIconBaseURL = new URL(VelocityPlugin.getInstallURL(), pathSuffix);
        }
        catch (MalformedURLException e)
        {
            VelocityPlugin.log(e);
        }
    }
    // The plugin's image registry
    private static final ImageRegistry  IMAGE_REGISTRY            = VelocityPlugin.getDefault().getImageRegistry();
    // Set of predefined Image Descriptors
    private static final String         T_OBJ                     = "obj16";
    // private static final String T_CLCL = "clcl16";
    // private static final String T_CTOOL = "ctool16";
    // Define image names
    public static final String          IMG_OBJ_SYSTEM_DIRECTIVE  = "system_directive_obj.gif";
    public static final String          IMG_OBJ_USER_DIRECTIVE    = "user_directive_obj.gif";
    public static final String          IMG_OBJ_MACRO             = "macro_obj.gif";
    // Define image descriptors
    public static final ImageDescriptor DESC_OBJ_SYSTEM_DIRECTIVE = createManaged(T_OBJ, IMG_OBJ_SYSTEM_DIRECTIVE);
    public static final ImageDescriptor DESC_OBJ_USER_DIRECTIVE   = createManaged(T_OBJ, IMG_OBJ_USER_DIRECTIVE);
    public static final ImageDescriptor DESC_OBJ_MACRO            = createManaged(T_OBJ, IMG_OBJ_MACRO);

    /**
     * Returns the image managed under the given key in this registry.
     * 
     * @param aKey
     *            the image's key
     * @return the image managed under the given key
     */
    public static Image get(String aKey)
    {
        return IMAGE_REGISTRY.get(aKey);
    }

    /**
     * Sets the three image descriptors for enabled, disabled, and hovered to an
     * action. The actions are retrieved from the *tool16 folders.
     */
    public static void setToolImageDescriptors(IAction anAction, String anIconName)
    {
        setImageDescriptors(anAction, "tool16", anIconName);
    }

    /**
     * Sets the three image descriptors for enabled, disabled, and hovered to an
     * action. The actions are retrieved from the *lcl16 folders.
     */
    public static void setLocalImageDescriptors(IAction anAction, String anIconName)
    {
        setImageDescriptors(anAction, "lcl16", anIconName);
    }

    // ---- Helper methods to access icons on the file system -----------------
    private static void setImageDescriptors(IAction anAction, String aType, String aRelPath)
    {
        try
        {
            ImageDescriptor id = ImageDescriptor.createFromURL(makeIconFileURL("d" + aType, aRelPath));
            if (id != null)
            {
                anAction.setDisabledImageDescriptor(id);
            }
        }
        catch (MalformedURLException e)
        {
        }
        try
        {
            ImageDescriptor id = ImageDescriptor.createFromURL(makeIconFileURL("c" + aType, aRelPath));
            if (id != null)
            {
                anAction.setHoverImageDescriptor(id);
            }
        }
        catch (MalformedURLException e)
        {
        }
        anAction.setImageDescriptor(create("e" + aType, aRelPath));
    }

    private static ImageDescriptor createManaged(String aPrefix, String aName)
    {
        ImageDescriptor result = create(aPrefix, aName);
        IMAGE_REGISTRY.put(aName, result);
        return result;
    }

    private static ImageDescriptor create(String aPrefix, String aName)
    {
        ImageDescriptor result;
        try
        {
            result = ImageDescriptor.createFromURL(makeIconFileURL(aPrefix, aName));
        }
        catch (MalformedURLException e)
        {
            result = ImageDescriptor.getMissingImageDescriptor();
        }
        return result;
    }

    private static URL makeIconFileURL(String aPrefix, String aName) throws MalformedURLException
    {
        if (fgIconBaseURL == null) { throw new MalformedURLException(); }
        StringBuffer buffer = new StringBuffer(aPrefix);
        buffer.append('/');
        buffer.append(aName);
        return new URL(fgIconBaseURL, buffer.toString());
    }
}
