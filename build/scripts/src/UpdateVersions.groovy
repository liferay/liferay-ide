def oldVersion = "3.5.0"
def newVersion = "3.6.0"

def oldQualVersion = "${oldVersion}.qualifier"
def newQualVersion = "${newVersion}.qualifier"

def oldPomVersion = "${oldVersion}-SNAPSHOT"
def newPomVersion = "${newVersion}-SNAPSHOT"

new File("./").eachDirRecurse
{
	if( it.absolutePath.contains( "target" ) )
	{
		return
	}

    it.eachFile { itFile ->
        if( itFile.isFile() && itFile.absolutePath.endsWith(".xml")
            && itFile.text.contains( "Copyright (c) 2000-2012 Liferay, Inc. All rights reserved." ) )
        {
            println "Replacing copyright ${itFile.name}"
            itFile.text = itFile.text.replaceAll( "Copyright \\(c\\) 2000-2012 Liferay, Inc. All rights reserved.", "Copyright \\(c\\) 2000-present Liferay, Inc. All rights reserved." );
        }
    }


	// check for manifest
	File manifest = new File( it, "META-INF/MANIFEST.MF" )

	if( manifest.exists() )
	{
		String contents = manifest.text

		if( contents.contains( "Bundle-SymbolicName: com.liferay" ) && contents.contains( "Bundle-Version: ${oldQualVersion}" ) )
		{
			println "Found manifest: " + manifest.getAbsolutePath()
			manifest.text = contents.replaceAll("Bundle-Version: ${oldQualVersion}", "Bundle-Version: ${newQualVersion}");
		}
	}

	//check for feature.xml
	File feature = new File( it, "feature.xml" )

	if( feature.exists() )
	{
		String contents = feature.text

		if( contents.contains("id=\"com.liferay") && contents.contains( "version=\"${oldQualVersion}" ) )
		{
			println "Found feature: " + feature.getAbsolutePath()
			feature.text = contents.replaceAll( "version=\"${oldQualVersion}", "version=\"${newQualVersion}" )
		}
	}

	//check for .product files
	it.list( { d, f -> f ==~ /.*.product/ } as FilenameFilter ).each
	{
		filename ->

		File product = new File( it, filename )

		String contents = product.text

		if( contents.contains( "version=\"${oldQualVersion}" ) )
		{
			println "Found product: " + filename
			product.text = contents.replaceAll( "version=\"${oldQualVersion}", "version=\"${newQualVersion}" )
		}
	}

	//check for pom.xml
	File pom = new File( it, "pom.xml" )

	if( pom.exists() )
	{
		String contents = pom.text

		if( ( contents.contains("<artifactId>com.liferay") || contents.contains("<groupId>com.liferay") )
			&& contents.contains( "<version>${oldPomVersion}" ) )
		{
			println "Found pom: " + pom.getAbsolutePath()
			pom.text = contents.replaceAll( "<version>${oldPomVersion}", "<version>${newPomVersion}" )
		}
	}
}
