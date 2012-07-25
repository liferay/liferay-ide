package com.liferay.ide.velocity.vaulttec.ui;

/**
 * Defines constants which are used to refer to values in the plugin's
 * preference bundle.
 */
public interface IPreferencesConstants
{

    public static final String EDITOR_BOLD_SUFFIX       = "_bold";
    String                     PREFIX                   = VelocityPlugin.PLUGIN_ID + ".";
    String                     EDITOR_SHOW_SEGMENTS     = PREFIX + "editor.showSegments";
    String                     VELOCITY_COUNTER_NAME    = PREFIX + "velocity.countName";
    String                     VELOCITY_USER_DIRECTIVES = PREFIX + "velocity.userDirectives";
    String                     LIBRARY_PATH             = PREFIX + "library.path";
    String                     LIBRARY_LIST             = PREFIX + "library.list";
    String                     COLOR_DEFAULT            = IColorConstants.DEFAULT;
    String                     COLOR_TAG                = IColorConstants.TAG;
    String                     COLOR_COMMENT            = IColorConstants.COMMENT;
    String                     COLOR_DOC_COMMENT        = IColorConstants.DOC_COMMENT;
    String                     COLOR_DIRECTIVE          = IColorConstants.DIRECTIVE;
    String                     COLOR_STRING             = IColorConstants.STRING;
    String                     COLOR_REFERENCE          = IColorConstants.REFERENCE;
    String                     COLOR_STRING_REFERENCE   = IColorConstants.STRING_REFERENCE;
    String                     COLOR_RGB_HTML_String    = IColorConstants.HTML_String;
    String                     COLOR_RGB_HTML_ENDTAG    = IColorConstants.HTML_ENDTAG;
    String                     COLOR_RGB_HTML_TAG       = IColorConstants.HTML_TAG;
    String                     COLOR_RGB_HTML_ATTRIBUTE = IColorConstants.HTML_ATTRIBUTE;
    String                     COLOR_SCRIPT             = IColorConstants.SCRIPT;
    String                     VELOCITY_RESOURCE_DIR    = PREFIX + "velocity.resourcedir";
}
