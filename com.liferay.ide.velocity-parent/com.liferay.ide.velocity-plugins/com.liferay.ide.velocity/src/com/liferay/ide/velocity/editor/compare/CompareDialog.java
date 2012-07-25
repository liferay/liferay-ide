/*
 * Created on 30.12.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package com.liferay.ide.velocity.editor.compare;

import java.lang.reflect.InvocationTargetException;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.internal.ICompareContextIds;
import org.eclipse.compare.internal.ResizableDialog;
import org.eclipse.compare.internal.Utilities;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * @author akmal
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class CompareDialog extends ResizableDialog implements IPropertyChangeListener
{

    private CompareEditorInput fCompareEditorInput;
    private Button             fCommitButton;
    private String             _error;

    public CompareDialog(Shell shell, CompareEditorInput input, String error)
    {
        super(shell, null);
        _error = error;
      
        fCompareEditorInput = input;
        fCompareEditorInput.addPropertyChangeListener(this);
        setHelpContextId(ICompareContextIds.COMPARE_DIALOG);
    }

    public boolean close()
    {
        if (super.close())
        {
            if (fCompareEditorInput != null) fCompareEditorInput.addPropertyChangeListener(this);
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected void createButtonsForButtonBar(Composite parent)
    {
        fCommitButton = createButton(parent, IDialogConstants.OK_ID, IDialogConstants.OK_LABEL, true); //$NON-NLS-1$
        fCommitButton.setEnabled(true);
        createButton(parent, IDialogConstants.CANCEL_ID, IDialogConstants.CANCEL_LABEL, false);
    }

    public void propertyChange(PropertyChangeEvent event)
    {
        if (fCommitButton != null && fCompareEditorInput != null) fCommitButton.setEnabled(fCompareEditorInput.isSaveNeeded());
    }

    /*
     * (non-Javadoc) Method declared on Dialog.
     */
    protected Control createDialogArea(Composite parent2)
    {
        Composite parent = (Composite) super.createDialogArea(parent2);
        Control c = fCompareEditorInput.createContents(parent);
        GridData gridData = new GridData(GridData.FILL_BOTH);
        c.setLayoutData(gridData);
        if (_error != null)
        {
            Text editorComposite = new Text(parent, SWT.BORDER | SWT.H_SCROLL | SWT.V_SCROLL);
            editorComposite.setEditable(false);
            editorComposite.setText(_error);
            GridLayout layout = new GridLayout();
            RGB rgb = new RGB(201, 209, 218);
            Color color = new Color(Display.getCurrent(), rgb);
            editorComposite.setBackground(color);
            editorComposite.setLayoutData(gridData);
        }
        Shell shell = c.getShell();
        shell.setText(fCompareEditorInput.getTitle());
        shell.setImage(fCompareEditorInput.getTitleImage());
        applyDialogFont(parent);
        return parent;
    }

    /*
     * (non-Javadoc) Method declared on Window.
     */
    public int open()
    {
        int rc = super.open();
        if (rc == OK && fCompareEditorInput.isSaveNeeded())
        {
            WorkspaceModifyOperation operation = new WorkspaceModifyOperation() {

                public void execute(IProgressMonitor pm) throws CoreException
                {
                    fCompareEditorInput.saveChanges(pm);
                }
            };
            Shell shell = getParentShell();
            ProgressMonitorDialog pmd = new ProgressMonitorDialog(shell);
            try
            {
                operation.run(pmd.getProgressMonitor());
            }
            catch (InterruptedException x)
            {
                // NeedWork
            }
            catch (OperationCanceledException x)
            {
                // NeedWork
            }
            catch (InvocationTargetException x)
            {
                String title = Utilities.getString("CompareDialog.saveErrorTitle"); //$NON-NLS-1$
                String msg = Utilities.getString("CompareDialog.saveErrorMessage"); //$NON-NLS-1$
                MessageDialog.openError(shell, title, msg + x.getTargetException().getMessage());
            }
        }
        return rc;
    }
}