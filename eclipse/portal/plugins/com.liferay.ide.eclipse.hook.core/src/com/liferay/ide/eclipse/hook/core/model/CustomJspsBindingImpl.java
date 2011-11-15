package com.liferay.ide.eclipse.hook.core.model;

import com.liferay.ide.eclipse.core.util.CoreUtil;
import com.liferay.ide.eclipse.hook.core.model.internal.CustomJspResource;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.modeling.IModelElement;
import org.eclipse.sapphire.modeling.ListBindingImpl;
import org.eclipse.sapphire.modeling.ModelElementType;
import org.eclipse.sapphire.modeling.ModelProperty;
import org.eclipse.sapphire.modeling.Path;
import org.eclipse.sapphire.modeling.Resource;


public class CustomJspsBindingImpl extends ListBindingImpl
{

	private List<Resource> customJspsResources;

	@Override
	public void init( IModelElement element, ModelProperty property, String[] params )
	{
		super.init( element, property, params );
		this.customJspsResources = new ArrayList<Resource>();

		IFile[] customJspFiles = getCustomJspFiles();

		for ( IFile customJspFile : customJspFiles )
		{
			IPath customJspDirPath = getCustomJspFolder().getProjectRelativePath();
			IPath customJspFilePath = customJspFile.getProjectRelativePath();

			IPath customJspPath =
				customJspFilePath.removeFirstSegments( customJspFilePath.matchingFirstSegments( customJspDirPath ) );

			this.customJspsResources.add( new CustomJspResource( this.element().resource(), customJspPath ) );
		}


	}

	@Override
	public void remove( Resource resource )
	{
		this.customJspsResources.remove( resource );
		this.element().notifyPropertyChangeListeners( this.property() );
	}

	@Override
	public List<Resource> read()
	{
		return this.customJspsResources;
	}

	private IFolder getCustomJspFolder()
	{
		Path customJspDir = this.hook().getCustomJspDir().element().getValue().getContent();
		IFolder docroot = CoreUtil.getDocroot( project() );
		IFolder customJspFolder = docroot.getFolder( customJspDir.toPortableString() );
		return customJspFolder;
	}

	private IHook hook()
	{
		return this.element().nearest( IHook.class );
	}

	private IProject project()
	{
		return this.hook().adapt( IProject.class );
	}

	private IFile[] getCustomJspFiles()
	{
		List<IFile> customJspFiles = new ArrayList<IFile>();

		IFolder customJspFolder = getCustomJspFolder();

		try
		{
			findJspFiles( customJspFolder, customJspFiles );
		}
		catch ( CoreException e )
		{
			e.printStackTrace();
		}

		return customJspFiles.toArray( new IFile[0] );
	}

	@Override
	public Resource add( ModelElementType type )
	{
		if ( type.equals( ICustomJsp.TYPE ) )
		{
			CustomJspResource newCustomJspResource =
				new CustomJspResource( this.element().resource(), new org.eclipse.core.runtime.Path( "" ) );

			this.customJspsResources.add( newCustomJspResource );

			this.element().notifyPropertyChangeListeners( this.property() );

			return newCustomJspResource;
		}
		else
		{
			return null;
		}
	}

	private void findJspFiles( IFolder folder, List<IFile> jspFiles ) throws CoreException
	{
		IResource[] members = folder.members( IResource.FOLDER | IResource.FILE );

		for ( IResource member : members )
		{
			if ( member instanceof IFile && "jsp".equals( member.getFileExtension() ) )
			{
				jspFiles.add( (IFile) member );
			}
			else
			{
				findJspFiles( (IFolder) member, jspFiles );
			}
		}

	}

	@Override
	public ModelElementType type( Resource resource )
	{
		if ( resource instanceof CustomJspResource )
		{
			return ICustomJsp.TYPE;
		}

		return null;
	}

}
