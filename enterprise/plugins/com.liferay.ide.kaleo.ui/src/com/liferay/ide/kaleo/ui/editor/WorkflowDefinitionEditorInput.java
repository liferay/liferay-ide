
package com.liferay.ide.kaleo.ui.editor;

import com.liferay.ide.kaleo.ui.navigator.WorkflowDefinitionEntry;

import org.eclipse.core.resources.IStorage;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IStorageEditorInput;

/**
 * A storage editor input that can read a sapphire value property.
 *
 * @author <a href="mailto:gregory.amerson@liferay.com">Gregory Amerson</a>
 */
public class WorkflowDefinitionEditorInput extends PlatformObject implements IStorageEditorInput
{

	private WorkflowDefinitionEntry defNode;

	public WorkflowDefinitionEditorInput( WorkflowDefinitionEntry defNode )
	{
		super();
		this.defNode = defNode;
	}

	@Override
    public boolean equals( Object obj )
    {
        if( obj instanceof WorkflowDefinitionEditorInput )
        {
            WorkflowDefinitionEditorInput input = (WorkflowDefinitionEditorInput) obj;

            return this.defNode.equals( input.getWorkflowDefinitionEntry() );
        }

        return false;
    }

	public boolean exists()
	{
		return true;
		// return this.modelElement != null && this.valueProperty != null;
	}

	public ImageDescriptor getImageDescriptor()
	{
		return null;
	}

	public String getName()
	{
		StringBuilder builder = new StringBuilder();

		builder.append( this.defNode.getName() + " [Version: " + this.defNode.getVersion() );

		if( this.defNode.getDraftVersion() != -1 )
		{
			builder.append( ", Draft Version: " + this.defNode.getDraftVersion() + "]" );
		}
		else
		{
			builder.append( "]" );
		}

		return builder.toString();
	}

	public IPersistableElement getPersistable()
	{
		return null;
	}

	public IStorage getStorage()
	{
		return new DefinitionStorage( this.defNode );
	}

	public String getToolTipText()
	{
		return getName();
	}

	public WorkflowDefinitionEntry getWorkflowDefinitionEntry()
	{
		return this.defNode;
	}

    public void setWorkflowDefinitionEntry( WorkflowDefinitionEntry newNode )
	{
		this.defNode = newNode;
	}

}
