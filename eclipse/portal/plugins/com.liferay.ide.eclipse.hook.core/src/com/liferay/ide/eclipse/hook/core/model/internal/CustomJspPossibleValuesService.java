package com.liferay.ide.eclipse.hook.core.model.internal;

import java.io.File;
import java.io.FileFilter;
import java.util.SortedSet;

import org.eclipse.core.runtime.Path;
import org.eclipse.sapphire.services.PossibleValuesService;


public class CustomJspPossibleValuesService extends PossibleValuesService
{

	final File portalRoot = new File( "/home/greg/dev java/testing/liferay-portal-6.0.6/tomcat-6.0.29/webapps/ROOT" );

	final FileFilter jspfilter = new FileFilter()
	{

		public boolean accept( File pathname )
		{
			return pathname.isDirectory() || pathname.getName().endsWith( ".jsp" );
		}
	};

	@Override
	protected void fillPossibleValues( SortedSet<String> values )
	{
		File[] files = portalRoot.listFiles( jspfilter );
		findJSPFiles( files, values );
		
	}

	private void findJSPFiles( File[] files, SortedSet<String> values )
	{
		for ( File file : files )
		{
			if ( file.isDirectory() )
			{
				findJSPFiles( file.listFiles( jspfilter ), values );
			}
			else
			{
				values.add( new Path( file.getAbsolutePath() ).removeFirstSegments( 8 ).toPortableString() );
			}
		}
	}

}
