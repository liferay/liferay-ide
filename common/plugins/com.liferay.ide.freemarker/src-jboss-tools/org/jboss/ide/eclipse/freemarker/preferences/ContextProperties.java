/*
 * JBoss by Red Hat
 * Copyright 2006-2009, Red Hat Middleware, LLC, and individual contributors as indicated
 * by the @authors tag. See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.ide.eclipse.freemarker.preferences;


import java.util.Properties;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.ui.dialogs.PropertyPage;
import org.jboss.ide.eclipse.freemarker.Messages;
import org.jboss.ide.eclipse.freemarker.Plugin;
import org.jboss.ide.eclipse.freemarker.configuration.ConfigurationManager;
import org.jboss.ide.eclipse.freemarker.configuration.ContextValue;
import org.jboss.ide.eclipse.freemarker.dialogs.ContextValueDialog;

public class ContextProperties extends PropertyPage {

	public ContextProperties() {
		super();
		setDescription(Messages.ContextProperties_Description);
	}
	
    protected Control createContents(Composite parent) {
        return createContextPage(parent);
    }

    private Table contextValuesTable;
    private Button editContextValueButton;
    private Button deleteContextValueButton;
    private Button addContextValueButton;
    private Properties contextValues;
    // private DirectoryEditor rootDirectory;
    
    private Control createContextPage(Composite parent) {
        contextValues = new Properties();
        Composite composite = new Composite(parent, SWT.NULL);
        composite.setLayout(new GridLayout(1, true));
        
        if (getElement() instanceof IProject) {
            IProject project = (IProject) getElement();
            Composite subComp = new Composite(composite, SWT.NULL);
            subComp.setLayout(new GridLayout(2, false));
            subComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
//            try {
//	            javascriptDirectory = new DirectoryEditor(getShell(), subComp, "Javascript Direcotory", project.getPersistentProperty(Constants.newQualifiedName(Constants.DIR_JAVASCRIPT)));
//            }
//            catch (CoreException ce) {}
        }

        Composite subComp = new Composite(composite, SWT.NULL);
        subComp.setLayout(new GridLayout(1, false));
        subComp.setLayoutData(new GridData(GridData.FILL_BOTH));
        GridData gridData = new GridData(GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        contextValuesTable = new Table(subComp, SWT.BORDER | SWT.H_SCROLL
                | SWT.FULL_SELECTION);
        contextValuesTable.setVisible(true);
        contextValuesTable.setLinesVisible(false);
        contextValuesTable.setHeaderVisible(true);
        contextValuesTable.addSelectionListener(new SelectionListener() {
            public void widgetSelected(SelectionEvent e) {
                editContextValueButton.setEnabled(true);
                deleteContextValueButton.setEnabled(true);
            }

            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        contextValuesTable.addKeyListener(new ContextValueDeleteKeyListener());
        contextValuesTable
                .addMouseListener(new EditContextValueButtonListener());

        // create the columns
        TableColumn keyColumn = new TableColumn(contextValuesTable, SWT.LEFT);
        TableColumn valueColumn = new TableColumn(contextValuesTable, SWT.LEFT);
        keyColumn.setText(Messages.ContextProperties_KeyColumn);
        valueColumn.setText(Messages.ContextProperties_ValueColumn);
        ColumnLayoutData keyColumnLayout = new ColumnWeightData(30, false);
        ColumnLayoutData valueColumnLayout = new ColumnWeightData(70, false);

        // set columns in Table layout
        TableLayout tableLayout = new TableLayout();
        tableLayout.addColumnData(keyColumnLayout);
        tableLayout.addColumnData(valueColumnLayout);
        contextValuesTable.setLayout(tableLayout);

        GridData data = new GridData(GridData.FILL_BOTH);
        data.heightHint = 50;
        data.grabExcessHorizontalSpace = true;
        data.grabExcessVerticalSpace = true;
        contextValuesTable.setLayoutData(data);

        Composite buttonComposite = new Composite(subComp, SWT.NONE);
        data = new GridData();
        data.horizontalAlignment = GridData.BEGINNING;
        data.verticalAlignment = GridData.BEGINNING;
        buttonComposite.setLayoutData(data);
        GridLayout gl = new GridLayout(3, true);
        buttonComposite.setLayout(gl);
        buttonComposite.setVisible(true);
        addContextValueButton = new Button(buttonComposite, SWT.NATIVE);
        addContextValueButton.setText(Messages.ContextProperties_NewButton);
        addContextValueButton.setVisible(true);
        addContextValueButton
                .addSelectionListener(new AddContextValueButtonListener());
        data = new GridData();
        data.widthHint = 45;
        data.grabExcessHorizontalSpace = true;
        addContextValueButton.setLayoutData(data);
        editContextValueButton = new Button(buttonComposite, SWT.NATIVE);
        editContextValueButton.setText(Messages.ContextProperties_EditButton);
        editContextValueButton
                .addSelectionListener(new EditContextValueButtonListener());
        data = new GridData();
        data.widthHint = 45;
        data.grabExcessHorizontalSpace = true;
        editContextValueButton.setLayoutData(data);
        deleteContextValueButton = new Button(buttonComposite, SWT.NATIVE);
        deleteContextValueButton.setText(Messages.ContextProperties_DeleteButton);
        deleteContextValueButton
                .addSelectionListener(new ContextValueDeleteKeyListener());
        data = new GridData();
        data.widthHint = 45;
        data.grabExcessHorizontalSpace = true;
        deleteContextValueButton.setLayoutData(data);

        reloadContextValues();
        return composite;
    }

    public void reloadContextValues() {
        try {
            contextValuesTable.removeAll();
            ContextValue[] values = ConfigurationManager.getInstance(getResource().getProject())
                    .getContextValues(getResource(), false);
            for (int i = 0; i < values.length; i++) {
                TableItem item = new TableItem(contextValuesTable, SWT.NULL);
                String[] arr = { values[i].name, values[i].objClass.getName() };
                item.setText(arr);
            }
            editContextValueButton.setEnabled(false);
            deleteContextValueButton.setEnabled(false);
        } catch (Exception e) {
            Plugin.log(e);
        }
        contextValuesTable.redraw();
    }

    public class AddContextValueButtonListener implements SelectionListener {
        public void mouseDoubleClick(MouseEvent e) {
            doWork();
        }

        public void mouseDown(MouseEvent e) {
        }

        public void mouseUp(MouseEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            doWork();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void doWork() {
            ContextValueDialog dialog = new ContextValueDialog(new Shell(),
                    null, getResource());
            if (IDialogConstants.OK_ID == dialog.open()) {
                reloadContextValues();
            }
        }
    }

    public class EditContextValueButtonListener implements SelectionListener,
            MouseListener {
        public void mouseDoubleClick(MouseEvent e) {
            doWork();
        }

        public void mouseDown(MouseEvent e) {
        }

        public void mouseUp(MouseEvent e) {
        }

        public void widgetSelected(SelectionEvent e) {
            doWork();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void doWork() {
            int index = contextValuesTable.getSelectionIndex();
            if (index >= 0) {
                String key = contextValuesTable.getSelection()[0].getText(0);
                ContextValue value = ConfigurationManager.getInstance(getResource().getProject()).getContextValue(key,
                        getResource(), false);
                ContextValueDialog dialog = new ContextValueDialog(new Shell(),
                        value, getResource());
                if (IDialogConstants.OK_ID == dialog.open()) {
                    reloadContextValues();
                }
            }
        }
    }

    public class ContextValueDeleteKeyListener implements SelectionListener,
            KeyListener {
        public void widgetSelected(SelectionEvent e) {
            doWork();
        }

        public void widgetDefaultSelected(SelectionEvent e) {
        }

        public void keyPressed(KeyEvent e) {
            if (e.keyCode == SWT.DEL) {
                doWork();
            }
        }

        public void keyReleased(KeyEvent e) {
        }

        public void doWork() {
            int index = contextValuesTable.getSelectionIndex();
            if (index >= 0) {
                try {
                    boolean confirm = MessageDialog
                            .openConfirm(new Shell(), Messages.ContextProperties_ConfirmationTitle,
                                    Messages.ContextProperties_DeleteValueConfirmation);
                    if (confirm) {
                        String key = contextValuesTable.getSelection()[0]
                                .getText(0);
                        ContextValue value = ConfigurationManager.getInstance(getResource().getProject())
                                .getContextValue(key, getResource(), false);
                        ConfigurationManager.getInstance(getResource().getProject()).removeContextValue(value.name,
                                getResource());
                        reloadContextValues();
                    }
                } catch (Exception e1) {
                    Plugin.log(e1);
                }
            }
        }
    }

    private IResource getResource() {
        return (IResource) getElement();
    }
  
    public boolean performOk() {
        if (getElement() instanceof IProject) {
            IProject project = (IProject) getElement();
            ConfigurationManager.getInstance(project).reload();
        }
        return super.performOk();
    }
}