/*
 * Created on 30.12.2004
 * 
 * TODO To change the template for this generated file go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
package com.liferay.ide.velocity.editor.compare;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.CompareUI;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

import com.liferay.ide.velocity.vaulttec.ui.VelocityPlugin;


/**
 * @author akmal
 * 
 * TODO To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class VelocityCompare  extends CompareEditorInput
{

    private VelocityInput left;
    private VelocityInput right;
    private String error;

    public VelocityCompare(final VelocityInput left, final VelocityInput right, String error) {
	super(new CompareConfiguration());
	
	this.left=left;
	this.right=right;
	this.error=error;
    }
@Override
public CompareConfiguration getCompareConfiguration()
{
    // TODO Auto-generated method stub
    return super.getCompareConfiguration();
}



    public  int openCompareDialog()
    {
        VelocityPlugin plugin = VelocityPlugin.getDefault();
//        CompareUI.openCompareEditor(this);
        if (plugin != null) return plugin.openCompareDialog(this, error);
        return 0;
    }

    @Override
    protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
    {
	CompareConfiguration cc = getCompareConfiguration();
	cc.setRightEditable(false);
	cc.setLeftEditable(false);
	cc.setLeftLabel("original");
	cc.setRightLabel("formatted Text");
	 return new DiffNode(left, right);
    }
}
