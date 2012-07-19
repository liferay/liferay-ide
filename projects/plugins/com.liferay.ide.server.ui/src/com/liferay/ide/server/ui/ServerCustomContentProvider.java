package com.liferay.ide.server.ui;

import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.navigator.ICommonContentExtensionSite;
import org.eclipse.ui.navigator.IPipelinedTreeContentProvider;
import org.eclipse.ui.navigator.PipelinedShapeModification;
import org.eclipse.ui.navigator.PipelinedViewerUpdate;

public abstract class ServerCustomContentProvider implements IPipelinedTreeContentProvider
{

	private ICommonContentExtensionSite config;

	public void init( ICommonContentExtensionSite config )
	{
		this.config = config;
	}

	protected ICommonContentExtensionSite getConfig()
	{
		return this.config;
	}

	public void restoreState( IMemento aMemento )
	{
		// do nothing
	}

	public void saveState( IMemento aMemento )
	{
		// do nothing
	}

	public void inputChanged( Viewer viewer, Object oldInput, Object newInput )
	{
	}

	public PipelinedShapeModification interceptAdd( PipelinedShapeModification anAddModification )
	{
		return null;
	}

	public PipelinedShapeModification interceptRemove( PipelinedShapeModification aRemoveModification )
	{
		return null;
	}

	public Object[] getElements( Object inputElement )
	{
		return null;
	}

	@SuppressWarnings( "rawtypes" )
	public void getPipelinedElements( Object anInput, Set theCurrentElements )
	{
	}

	public boolean interceptRefresh( PipelinedViewerUpdate aRefreshSynchronization )
	{
		return false;
	}

	public boolean interceptUpdate( PipelinedViewerUpdate anUpdateSynchronization )
	{
		return false;
	}
}
