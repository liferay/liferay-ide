package com.liferay.ide.velocity.vaulttec.ui;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.text.TextAttribute;
import org.eclipse.jface.text.rules.IToken;
import org.eclipse.jface.text.rules.Token;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Display;

/**
 * Provider for colors used in the Velocity UI.
 */
public class VelocityColorProvider implements IColorConstants
{

    // Default colors
    private static final RGB RGB_DEFAULT          = new RGB(0, 0, 0);
    private static final RGB RGB_COMMENT          = new RGB(192, 192, 192);
    private static final RGB RGB_DOC_COMMENT      = new RGB(192, 192, 192);
    private static final RGB RGB_DIRECTIVE        = new RGB(0, 0, 255);
    private static final RGB RGB_STRING           = new RGB(128, 64, 0);
    private static final RGB RGB_REFERENCE        = new RGB(220, 0, 0);
    private static final RGB RGB_STRING_REFERENCE = new RGB(250, 10, 240);
    private static final RGB RGB_HTML_ATTRIBUTE   = new RGB(0, 128, 128);
    private static final RGB RGB_HTML_TAG         = new RGB(250, 10, 10);
    private static final RGB RGB_HTML_ENDTAG      = new RGB(253, 132, 132);
    private static final RGB RGB_HTML_String      = new RGB(128, 64, 0);
    private static final RGB RGB_SCRIPT           = new RGB(184, 215, 149);
    private Map              tokenTable           = new HashMap(10);
    protected Map            fColorTable          = new HashMap(10);
    private Map              textAttributeTable   = new HashMap(10);

    /**
     * Set default colors in given preference store.
     */
    public static void initializeDefaults(IPreferenceStore aStore)
    {
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_DEFAULT, RGB_DEFAULT);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_TAG, RGB_DIRECTIVE);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_SCRIPT, RGB_SCRIPT);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_COMMENT, RGB_COMMENT);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_DOC_COMMENT, RGB_DOC_COMMENT);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_DIRECTIVE, RGB_DIRECTIVE);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_STRING, RGB_STRING);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_REFERENCE, RGB_REFERENCE);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_STRING_REFERENCE, RGB_STRING_REFERENCE);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_RGB_HTML_String, RGB_HTML_String);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_RGB_HTML_ENDTAG, RGB_HTML_ENDTAG);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_RGB_HTML_TAG, RGB_HTML_TAG);
        PreferenceConverter.setDefault(aStore, IPreferencesConstants.COLOR_RGB_HTML_ATTRIBUTE, RGB_HTML_ATTRIBUTE);
    }

    /**
     * Returns specified color that is stored in the color table. If color not
     * found in color table then a new instance is created from according
     * preferences value and stored in color table.
     */
    public Color getColor(String aName)
    {
        Color color = (Color) fColorTable.get(aName);
        if (color == null)
        {
            IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
            RGB rgb = PreferenceConverter.getColor(store, aName);
            if (rgb != null)
            {
                color = new Color(Display.getCurrent(), rgb);
            } else
            {
                color = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
                VelocityPlugin.logErrorMessage("Undefined color '" + aName + "'");
            }
            fColorTable.put(aName, color);
        }
        return color;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param name
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public TextAttribute getTextAttribute(String name)
    {
        TextAttribute ta = (TextAttribute) textAttributeTable.get(name);
        Color color = null;
        if (ta == null)
        {
            IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
            // RGB rgb = PreferenceConverter.getColor(store, name);
            color = getColor(name);
            if (color != null)
            {
                // color = new Color(Display.getCurrent(), rgb);
                boolean bold = store.getBoolean(name + IPreferencesConstants.EDITOR_BOLD_SUFFIX);
                if (bold)
                {
                    ta = new TextAttribute(color, null, SWT.BOLD);
                } else
                {
                    ta = new TextAttribute(color);
                }
            } else
            {
                color = Display.getCurrent().getSystemColor(SWT.COLOR_LIST_FOREGROUND);
                ta = new TextAttribute(color);
                VelocityPlugin.logErrorMessage("Undefined color '" + name + "'");
            }
            fColorTable.put(name, color);
            textAttributeTable.put(name, ta);
        }
        return ta;
    }

    /**
     * Release all of the color resources held onto by the color provider.
     */
    public void dispose()
    {
        Iterator colors = fColorTable.values().iterator();
        while (colors.hasNext())
        {
            ((Color) colors.next()).dispose();
        }
    }

    /**
     * @param event
     */
    public void handlePreferenceStoreChanged(PropertyChangeEvent event)
    {
        IPreferenceStore preferenceStore = VelocityPlugin.getDefault().getPreferenceStore();
        IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
        String prefKey = event.getProperty();
        Token token = (Token) tokenTable.get(prefKey);
        if ((token == null) && (prefKey.indexOf(IPreferencesConstants.EDITOR_BOLD_SUFFIX) > -1))
        {
            token = (Token) tokenTable.get(prefKey.substring(0, prefKey.indexOf(IPreferencesConstants.EDITOR_BOLD_SUFFIX)));
            prefKey = prefKey.substring(0, prefKey.indexOf(IPreferencesConstants.EDITOR_BOLD_SUFFIX));
        }
        RGB rgb = null;
        Color color = null;
        if (token != null)
        {
            rgb = PreferenceConverter.getColor(preferenceStore, prefKey);
            if (rgb != null)
            {
                boolean bold = store.getBoolean(prefKey + IPreferencesConstants.EDITOR_BOLD_SUFFIX);
                if (bold)
                {
                    token.setData(new TextAttribute(new Color(Display.getCurrent(), rgb), null, SWT.BOLD));
                } else
                {
                    token.setData(new TextAttribute(new Color(Display.getCurrent(), rgb)));
                }
                color = new Color(Display.getCurrent(), rgb);
                fColorTable.put(prefKey, color);
            }
        } else
        {
            try
            {
                rgb = PreferenceConverter.getColor(preferenceStore, prefKey);
            }
            catch (Exception e)
            {
                return;
            }
            if (rgb != null)
            {
                color = new Color(Display.getCurrent(), rgb);
                fColorTable.put(prefKey, color);
            }
        }
        // /text attribute
        rgb = PreferenceConverter.getColor(preferenceStore, prefKey);
        Object object = textAttributeTable.get(prefKey);
        if (object != null)
        {
            if (rgb != null)
            {
                boolean bold = store.getBoolean(prefKey + IPreferencesConstants.EDITOR_BOLD_SUFFIX);
                if (bold)
                {
                    textAttributeTable.put(prefKey, new TextAttribute(new Color(Display.getCurrent(), rgb), null, SWT.BOLD));
                } else
                {
                    textAttributeTable.put(prefKey, new TextAttribute(new Color(Display.getCurrent(), rgb)));
                }
                color = new Color(Display.getCurrent(), rgb);
                fColorTable.put(prefKey, color);
            }
        }
    }

    /**
     * DOCUMENT ME!
     * 
     * @param prefKey
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public IToken getToken(String prefKey)
    {
        IPreferenceStore store = VelocityPlugin.getDefault().getPreferenceStore();
        Token token = (Token) tokenTable.get(prefKey);
        boolean bold = store.getBoolean(prefKey + IPreferencesConstants.EDITOR_BOLD_SUFFIX);
        if (token == null)
        {
            if (bold)
            {
                token = new Token(new TextAttribute(getColor(prefKey), null, SWT.BOLD));
            } else
            {
                token = new Token(new TextAttribute(getColor(prefKey)));
            }
            tokenTable.put(prefKey, token);
        }
        return token;
    }

    /**
     * DOCUMENT ME!
     * 
     * @param prefKey
     *            DOCUMENT ME!
     * @param textonly
     *            DOCUMENT ME!
     * 
     * @return DOCUMENT ME!
     */
    public IToken getToken(String prefKey, boolean textonly)
    {
        Token token = (Token) tokenTable.get(prefKey);
        if (token == null)
        {
            if (!textonly)
            {
                token = new Token(new TextAttribute(getColor(prefKey)));
            } else
            {
                token = new Token(prefKey);
            }
            tokenTable.put(prefKey, token);
        }
        return token;
    }
}
