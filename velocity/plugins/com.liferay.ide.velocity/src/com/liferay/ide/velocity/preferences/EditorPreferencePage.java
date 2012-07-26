package com.liferay.ide.velocity.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferenceConverter;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.texteditor.AbstractTextEditor;

import com.liferay.ide.velocity.vaulttec.ui.IPreferencesConstants;
import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;

/**
 * The BadWordsColorPreferencePage is a preference page that handles setting the
 * colors used by the editors.
 */
public class EditorPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{

    public static final String                               BOLD                  = "_bold";                                                             //$NON-NLS-1$
    private static final RGB                                 RGB_DEFAULT           = new RGB(0, 0, 0);
    private static final RGB                                 RGB_COMMENT           = new RGB(192, 192, 192);
    private static final RGB                                 RGB_DOC_COMMENT       = new RGB(192, 192, 192);
    private static final RGB                                 RGB_DIRECTIVE         = new RGB(0, 0, 255);
    private static final RGB                                 RGB_STRING            = new RGB(128, 64, 0);
    private static final RGB                                 RGB_REFERENCE         = new RGB(220, 0, 0);
    private static final RGB                                 RGB_STRING_REFERENCE  = new RGB(250, 10, 240);
    private static final RGB                                 RGB_HTML_ATTRIBUTE    = new RGB(0, 128, 128);
    private static final RGB                                 RGB_HTML_TAG          = new RGB(250, 10, 10);
    private static final RGB                                 RGB_HTML_ENDTAG       = new RGB(253, 132, 132);
    private static final RGB                                 RGB_HTML_String       = new RGB(128, 64, 0);
    private static final RGB                                 RGB_SCRIPT            = new RGB(184, 215, 149);
    private static final String                              PREFIX                = "EditorPreferences.";
    public final VelocityOverlayPreferenceStore.OverlayKey[] fKeys                 = new VelocityOverlayPreferenceStore.OverlayKey[] {
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_RGB_HTML_String),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_RGB_HTML_String + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_RGB_HTML_ENDTAG),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_RGB_HTML_ENDTAG + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_RGB_HTML_TAG),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_RGB_HTML_TAG + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_RGB_HTML_ATTRIBUTE),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_RGB_HTML_ATTRIBUTE + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_SCRIPT),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_SCRIPT + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_DEFAULT),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_DEFAULT + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_COMMENT),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_COMMENT + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_DOC_COMMENT),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_DOC_COMMENT + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_DIRECTIVE),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_DIRECTIVE + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_STRING),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_STRING + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_REFERENCE),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_REFERENCE + BOLD),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.STRING, IPreferencesConstants.COLOR_STRING_REFERENCE),
            new VelocityOverlayPreferenceStore.OverlayKey(VelocityOverlayPreferenceStore.BOOLEAN, IPreferencesConstants.COLOR_STRING_REFERENCE + BOLD), };
    private final String[][]                                 fSyntaxColorListModel = new String[][] {
            { VelocityPlugin.getMessage(PREFIX + "htmlstring"), IPreferencesConstants.COLOR_RGB_HTML_String },
            { VelocityPlugin.getMessage(PREFIX + "htmlendtag"), IPreferencesConstants.COLOR_RGB_HTML_ENDTAG },
            { VelocityPlugin.getMessage(PREFIX + "htmltag"), IPreferencesConstants.COLOR_RGB_HTML_TAG },
            { VelocityPlugin.getMessage(PREFIX + "htmlattrib"), IPreferencesConstants.COLOR_RGB_HTML_ATTRIBUTE },
            { VelocityPlugin.getMessage(PREFIX + "script"), IPreferencesConstants.COLOR_SCRIPT },
            { VelocityPlugin.getMessage(PREFIX + "default"), IPreferencesConstants.COLOR_DEFAULT },
            { VelocityPlugin.getMessage(PREFIX + "comment"), IPreferencesConstants.COLOR_COMMENT },
            { VelocityPlugin.getMessage(PREFIX + "docComment"), IPreferencesConstants.COLOR_DOC_COMMENT },
            { VelocityPlugin.getMessage(PREFIX + "directive"), IPreferencesConstants.COLOR_DIRECTIVE },
            { VelocityPlugin.getMessage(PREFIX + "string"), IPreferencesConstants.COLOR_STRING },
            { VelocityPlugin.getMessage(PREFIX + "reference"), IPreferencesConstants.COLOR_REFERENCE },
            { VelocityPlugin.getMessage(PREFIX + "stringReference"), IPreferencesConstants.COLOR_STRING_REFERENCE }, };
    private VelocityOverlayPreferenceStore                   fOverlayStore;
    private List                                             fSyntaxColorList;
    private VelocityColorEditor                              fSyntaxForegroundVelocityColorEditor;
    private Button                                           fBoldCheckBox;
    private SourceViewer                                     fPreviewViewer;

    /**
     * Constructor for JSPColorPreferencePage.
     */
    public EditorPreferencePage()
    {
        setDescription(VelocityPlugin.getResourceString(PREFIX + "description")); //$NON-NLS-1$
        setPreferenceStore(VelocityPlugin.getDefault().getPreferenceStore());
        fOverlayStore = new VelocityOverlayPreferenceStore(getPreferenceStore(), fKeys);
    }

    protected Control createContents(Composite parent)
    {
        fOverlayStore.load();
        fOverlayStore.start();
        Composite colorComposite = new Composite(parent, SWT.NULL);
        colorComposite.setLayout(new GridLayout());
        Label label = new Label(colorComposite, SWT.LEFT);
        label.setText(VelocityPlugin.getResourceString(PREFIX + "foreground")); //$NON-NLS-1$
        label.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        Composite editorComposite = new Composite(colorComposite, SWT.NONE);
        GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        editorComposite.setLayout(layout);
        GridData gd = new GridData(GridData.FILL_BOTH);
        editorComposite.setLayoutData(gd);
        fSyntaxColorList = new List(editorComposite, SWT.SINGLE | SWT.V_SCROLL | SWT.BORDER);
        gd = new GridData(GridData.FILL_BOTH);
        gd.heightHint = convertHeightInCharsToPixels(5);
        fSyntaxColorList.setLayoutData(gd);
        Composite stylesComposite = new Composite(editorComposite, SWT.NONE);
        layout = new GridLayout();
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        layout.numColumns = 2;
        stylesComposite.setLayout(layout);
        stylesComposite.setLayoutData(new GridData(GridData.FILL_BOTH));
        label = new Label(stylesComposite, SWT.LEFT);
        label.setText(VelocityPlugin.getResourceString(PREFIX + "color")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.BEGINNING;
        label.setLayoutData(gd);
        fSyntaxForegroundVelocityColorEditor = new VelocityColorEditor(stylesComposite);
        Button foregroundColorButton = fSyntaxForegroundVelocityColorEditor.getButton();
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        foregroundColorButton.setLayoutData(gd);
        label = new Label(stylesComposite, SWT.LEFT);
        label.setText(VelocityPlugin.getResourceString(PREFIX + "bold")); //$NON-NLS-1$
        gd = new GridData();
        gd.horizontalAlignment = GridData.BEGINNING;
        label.setLayoutData(gd);
        fBoldCheckBox = new Button(stylesComposite, SWT.CHECK);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalAlignment = GridData.BEGINNING;
        fBoldCheckBox.setLayoutData(gd);
        fSyntaxColorList.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // do nothing
            }

            public void widgetSelected(SelectionEvent e)
            {
                handleSyntaxColorListSelection();
            }
        });
        foregroundColorButton.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // do nothing
            }

            public void widgetSelected(SelectionEvent e)
            {
                int i = fSyntaxColorList.getSelectionIndex();
                String key = fSyntaxColorListModel[i][1];
                PreferenceConverter.setValue(fOverlayStore, key, fSyntaxForegroundVelocityColorEditor.getColorValue());
            }
        });
        fBoldCheckBox.addSelectionListener(new SelectionListener() {

            public void widgetDefaultSelected(SelectionEvent e)
            {
                // do nothing
            }

            public void widgetSelected(SelectionEvent e)
            {
                int i = fSyntaxColorList.getSelectionIndex();
                String key = fSyntaxColorListModel[i][1];
                fOverlayStore.setValue(key + BOLD, fBoldCheckBox.getSelection());
            }
        });
        initialize();
        return colorComposite;
    }

    private void handleSyntaxColorListSelection()
    {
        int i = fSyntaxColorList.getSelectionIndex();
        String key = fSyntaxColorListModel[i][1];
        RGB rgb = PreferenceConverter.getColor(fOverlayStore, key);
        fSyntaxForegroundVelocityColorEditor.setColorValue(rgb);
        fBoldCheckBox.setSelection(fOverlayStore.getBoolean(key + BOLD));
    }

    /**
     * 
     */
    public static void initDefaults(IPreferenceStore aStore)
    {
        PreferenceConverter.setDefault(aStore, AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND, Display.getDefault().getSystemColor(SWT.COLOR_LIST_FOREGROUND).getRGB());
        aStore.setDefault(AbstractTextEditor.PREFERENCE_COLOR_FOREGROUND_SYSTEM_DEFAULT, true);
        PreferenceConverter.setDefault(aStore, AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND, Display.getDefault().getSystemColor(SWT.COLOR_LIST_BACKGROUND).getRGB());
        aStore.setDefault(AbstractTextEditor.PREFERENCE_COLOR_BACKGROUND_SYSTEM_DEFAULT, true);
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
     * 
     */
    private void initialize()
    {
        for (int i = 0; i < fSyntaxColorListModel.length; i++)
        {
            fSyntaxColorList.add(fSyntaxColorListModel[i][0]);
        }
        fSyntaxColorList.getDisplay().asyncExec(new Runnable() {

            public void run()
            {
                fSyntaxColorList.select(0);
                handleSyntaxColorListSelection();
            }
        });
    }

    /*
     * @see IWorkbenchPreferencePage#init(IWorkbench)
     */
    public void init(IWorkbench workbench)
    {
    }

    /*
     * @see PreferencePage#performDefaults()
     */
    protected void performDefaults()
    {
        fOverlayStore.loadDefaults();
        // initializeFields();
        handleSyntaxColorListSelection();
        super.performDefaults();
        fPreviewViewer.invalidateTextPresentation();
    }

    /**
     * @see org.eclipse.jface.preference.IPreferencePage#performOk()
     */
    public boolean performOk()
    {
        fOverlayStore.propagate();
        VelocityPlugin.getDefault().savePluginPreferences();
        return true;
    }
}
