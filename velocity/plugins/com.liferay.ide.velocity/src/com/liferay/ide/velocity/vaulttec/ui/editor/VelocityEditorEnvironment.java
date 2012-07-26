package com.liferay.ide.velocity.vaulttec.ui.editor;

import org.eclipse.jface.text.DefaultTextDoubleClickStrategy;
import org.eclipse.jface.text.ITextDoubleClickStrategy;
import org.eclipse.jface.text.rules.RuleBasedScanner;

import com.liferay.ide.velocity.vaulttec.ui.VelocityColorProvider;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;
import com.liferay.ide.velocity.vaulttec.ui.editor.parser.VelocityParser;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityCodeScanner;
import com.liferay.ide.velocity.vaulttec.ui.editor.text.VelocityStringScanner;

/**
 * This class maintains resources used by multiple instances of the editor.
 * <p>
 * To use this environment an editor has to <code>connect()</code> first.
 * Before disposing an editor a call to <code>disconnect()</code> is
 * necessary.
 */
public class VelocityEditorEnvironment
{

    private static VelocityParser           fgVelocityParser;
    private static VelocityColorProvider    fgColorProvider;
    private static RuleBasedScanner         fgCodeScanner;
    private static RuleBasedScanner         fgStringScanner;
    private static ITextDoubleClickStrategy fgDoubleClickStrategy;
    private static int                      fgRefCount = 0;

    /**
     * A connection has occured - initialize all resources if it is the first
     * activation.
     */
    public static void connect()
    {
        if (++fgRefCount == 1)
        {
            createVelocityParser();
            fgColorProvider = new VelocityColorProvider();
            fgCodeScanner = new VelocityCodeScanner(fgColorProvider);
            fgStringScanner = new VelocityStringScanner(fgColorProvider);
            fgDoubleClickStrategy = new DefaultTextDoubleClickStrategy();
        }
    }

    /**
     * If an editor is connected then creates a new instance of the Velocity
     * parser and initializes it with data specified in Velocity preferences.
     */
    public static void createVelocityParser()
    {
        if (fgRefCount > 0)
        {
            fgVelocityParser = new VelocityParser();
            try
            {
                fgVelocityParser.init();
            }
            catch (Exception e)
            {
                VelocityPlugin.log(e);
            }
        }
    }

    /**
     * A disconnection has occured - clear all resources if it is the last
     * deactivation.
     */
    public static void disconnect()
    {
        if (--fgRefCount == 0)
        {
            fgDoubleClickStrategy = null;
            fgStringScanner = null;
            fgCodeScanner = null;
            fgColorProvider.dispose();
            fgColorProvider = null;
            fgVelocityParser = null;
        }
    }

    /**
     * Returns the singleton color provider.
     */
    public static VelocityColorProvider getColorProvider()
    {
        return fgColorProvider;
    }

    /**
     * Returns the singleton code scanner.
     */
    public static RuleBasedScanner getCodeScanner()
    {
        return fgCodeScanner;
    }

    /**
     * Returns the singleton string scanner.
     */
    public static RuleBasedScanner getStringScanner()
    {
        return fgStringScanner;
    }

    /**
     * Returns the singleton double-click strategy.
     */
    public static ITextDoubleClickStrategy getDoubleClickStrategy()
    {
        return fgDoubleClickStrategy;
    }

    /**
     * Returns the singleton Velocity parser.
     */
    public static VelocityParser getParser()
    {
        return fgVelocityParser;
    }
}
