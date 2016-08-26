package com.liferay.ide.project.ui.upgrade.animated;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class DescriptionUpgradePage2 extends Page
{
    PageAction[] actions = { new PageFinishAction(), new PageSkipAction() };
    
    public DescriptionUpgradePage2( Composite parent, int style )
    {
        super( parent, style );
        
        this.setLayout( new FillLayout() );
        
        GridLayout layout = new GridLayout(2, false);

        // set the layout to the shell
        this.setLayout(layout);
        
        // create a label and a button
        Label label = new Label(this, SWT.NONE);
        label.setText("A label");
        Button button = new Button(this, SWT.PUSH);
        button.setText("Press Me");

        // create a new label that will span two columns
        label = new Label(this, SWT.BORDER);
        label.setText("This is a label");
        // create new layout data
        GridData data = new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1);
        label.setLayoutData(data);

        // create a new label which is used as a separator
        label = new Label(this, SWT.SEPARATOR | SWT.HORIZONTAL);
        
        // create new layout data
        data = new GridData(SWT.FILL, SWT.TOP, true, false);
        data.horizontalSpan = 2;
        label.setLayoutData(data);

        // create a right aligned button
        Button b = new Button(this, SWT.PUSH);
        b.setText("New Button");

        data = new GridData(SWT.LEFT, SWT.TOP, false, false, 2, 1);
        b.setLayoutData(data);
        
         // create a spinner with min value 0 and max value 1000
        Spinner spinner = new Spinner(this, SWT.READ_ONLY);
        spinner.setMinimum(0);
        spinner.setMaximum(1000);
        spinner.setSelection(500);
        spinner.setIncrement(1);
        spinner.setPageIncrement(100);
        GridData gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.widthHint = SWT.DEFAULT;
        gridData.heightHint = SWT.DEFAULT;
        gridData.horizontalSpan = 2;
        spinner.setLayoutData(gridData);

        Composite composite = new Composite(this, SWT.BORDER);
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        composite.setLayoutData(gridData);
        composite.setLayout(new GridLayout(1, false));

        Text txtTest = new Text(composite, SWT.NONE);
        txtTest.setText("Testing");
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        txtTest.setLayoutData(gridData);

        Text txtMoreTests = new Text(composite, SWT.NONE);
        txtMoreTests.setText("Another test");
        
        Group group = new Group(this, SWT.NONE);
        group.setText("This is my group");
        gridData = new GridData(SWT.FILL, SWT.FILL, true, false);
        gridData.horizontalSpan = 2;
        group.setLayoutData(gridData);
        group.setLayout(new RowLayout(SWT.VERTICAL));
        Text txtAnotherTest = new Text(group, SWT.NONE);
        txtAnotherTest.setText("Another test");

        setActions( actions );
    }

    @Override
    protected boolean showBackPage()
    {
        return true;
    }

    @Override
    protected boolean showNextPage()
    {
        return true;
    }
}
