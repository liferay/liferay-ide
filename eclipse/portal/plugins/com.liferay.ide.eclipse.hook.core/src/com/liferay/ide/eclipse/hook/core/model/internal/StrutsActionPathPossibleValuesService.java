package com.liferay.ide.eclipse.hook.core.model.internal;

import com.liferay.ide.eclipse.hook.core.model.IHook;
import com.liferay.ide.eclipse.server.core.ILiferayRuntime;
import com.liferay.ide.eclipse.server.util.ServerUtil;

import java.io.File;
import java.util.SortedSet;

import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.sapphire.services.PossibleValuesService;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;


public class StrutsActionPathPossibleValuesService extends PossibleValuesService
{

	private IPath portalDir;

	@Override
	protected void fillPossibleValues( SortedSet<String> values )
	{
		if ( this.portalDir != null && this.portalDir.toFile().exists() )
		{
			File strutsConfigFile = this.portalDir.append( "WEB-INF/struts-config.xml" ).toFile();

			if ( strutsConfigFile.exists() )
			{
				try
				{
					Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse( strutsConfigFile );
					NodeList actions = doc.getElementsByTagName( "action" );

					if ( actions != null )
					{
						for ( int i = 0; i < actions.getLength(); i++ )
						{
							Node action = actions.item( i );

							Node path = action.getAttributes().getNamedItem( "path" );

							if ( path != null )
							{
								values.add( path.getNodeValue() );
							}
						}
					}
				}
				catch ( Exception e )
				{
				}
			}
		}
	}

	@Override
	protected void init()
	{
		super.init();

		final IProject project = project();

		if ( project != null )
		{
			try
			{
				ILiferayRuntime lr = ServerUtil.getLiferayRuntime( project );

				if ( lr != null )
				{
					this.portalDir = lr.getPortalDir();
				}
			}
			catch ( CoreException e )
			{
			}
		}
	}

	protected IHook hook()
	{
		return this.context().find( IHook.class );
	}

	protected IProject project()
	{
		return this.hook().adapt( IProject.class );
	}
}
