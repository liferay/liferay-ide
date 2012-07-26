package com.liferay.ide.velocity.preferences;

import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;

/**
 * Dialog used to define a Velocity user directive (name and type [block or
 * line]).
 */
public class DirectiveDialog extends InputDialog
{

    protected static String          PREFIX    = "VelocityPreferences.directive.dialog.";
    protected static IInputValidator VALIDATOR = new DirectiveValidator();
    private Button                   fLineButton;
    private Button                   fBlockButton;
    private boolean                  fIsBlock;

    public DirectiveDialog(Shell aShell)
    {
        super(aShell, VelocityPlugin.getMessage(PREFIX + "title"), VelocityPlugin.getMessage(PREFIX + "message"), null, VALIDATOR);
    }

    protected Control createDialogArea(Composite aParent)
    {
        Composite composite = (Composite) super.createDialogArea(aParent);
        GridLayout layout = new GridLayout();
        layout.horizontalSpacing = 8; // Gap between label and control
        layout.numColumns = 2;
        Group group = new Group(composite, SWT.LEFT);
        group.setFont(aParent.getFont());
        group.setText(VelocityPlugin.getMessage(PREFIX + "typeGroup"));
        group.setLayout(layout);
        fLineButton = createRadioButton(group, VelocityPlugin.getMessage(PREFIX + "typeLine"));
        fLineButton.setSelection(!fIsBlock);
        fLineButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                fIsBlock = false;
            }
        });
        fBlockButton = createRadioButton(group, VelocityPlugin.getMessage(PREFIX + "typeBlock"));
        fBlockButton.setSelection(fIsBlock);
        fBlockButton.addSelectionListener(new SelectionAdapter() {

            public void widgetSelected(SelectionEvent e)
            {
                fIsBlock = true;
            }
        });
        return composite;
    }

    /**
     * Utility method that creates a radio button instance and sets the default
     * layout data.
     * 
     * @param aParent
     *            the parent for the new radio button
     * @param aLabel
     *            the label for the new radio button
     * @return the newly-created radio button
     */
    protected static Button createRadioButton(Composite aParent, String aLabel)
    {
        Button button = new Button(aParent, SWT.RADIO | SWT.LEFT);
        button.setText(aLabel);
        button.setFont(aParent.getFont());
        return button;
    }

    /**
     * Returns the name of the directive typed into this input dialog and the
     * selected type (format '&lt;name&gt;[&lt;B|L&gt;]'.
     * 
     * @return the input string and the selected type
     */
    public String getValue()
    {
        return super.getValue() + " [" + (fIsBlock ? "Block" : "Line") + ']';
    }

    private static class DirectiveValidator implements IInputValidator
    {

        public String isValid(String aText)
        {
            if (aText.length() == 0) { return ""; }
            for (int i = aText.length() - 1; i >= 0; i--)
            {
                if (!Character.isLetterOrDigit(aText.charAt(i))) { return VelocityPlugin.getMessage(PREFIX + "error"); }
            }
            return null;
        }
    }
}
