package com.liferay.ide.eclipse.server.aws.ui.wizard;

import com.amazonaws.eclipse.ec2.ui.views.instances.InstanceSelectionTable;
import com.amazonaws.services.ec2.model.Instance;

import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;


public class BeanstalkInstanceSelectionTable extends InstanceSelectionTable {

	public BeanstalkInstanceSelectionTable(Composite parent, boolean b) {
		super(parent, b);
	}

	@Override
	protected void createColumns() {
		super.createColumns();
		// newColumn("Instance ID", 10);
		// newColumn("Public DNS Name", 15);
		// newColumn("State", 10);
	}

	@Override
	protected void fillContextMenu(IMenuManager manager) {
		super.fillContextMenu(manager);
	}

	@Override
	protected void makeActions() {
		super.makeActions();
	}

	public Instance getSelectedInstance() {
		StructuredSelection selection = (StructuredSelection) viewer.getSelection();
		return (Instance) selection.getFirstElement();
	}
}
