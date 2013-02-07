def basedir = project.basedir.canonicalPath
def repositoryDir = basedir + "/target/repository"
def contentJar = repositoryDir  + "/content.jar"
def contentDir = basedir  + "/target/content.jar/"

// Add associate sites

println 'Unzipping content.jar'

def ant = new AntBuilder();   // create an antbuilder

ant.unzip( src: contentJar, dest:contentDir,  overwrite:"true" )

println 'Modify content.xml to add associate sites for Liferay IDE'

File contentXml =  new File( contentDir, "content.xml" )
def contentXmlText = contentXml.text

def parser = new XmlParser()
def root = parser.parseText( contentXmlText )

def props = root.properties

addAssociateSite( root, "http://download.eclipse.org/sapphire/0.6.1.201301281155/repository/" )

println 'Overwriting content.xml'
def writer = new StringWriter()
def printer = new XmlNodePrinter( new PrintWriter( writer ) )
printer.setPreserveWhitespace( true )
printer.print( root )
def result = writer.toString()

contentXml.text = result

println 'Zipping back customized content.jar'
ant.zip( destFile: contentJar, baseDir:contentDir )

def addAssociateSite( root, siteUrl )
{
    def refs = root.references

    if( !refs || refs.size() == 0 )
    {
        def newRefs = new Node( root, 'references' )
        newRefs.@size = "2"
        root.children().add( 1, newRefs )
        refs = root.references
    }

    new Node( refs.get( 0 ), 'repository', [ uri:siteUrl, url:siteUrl, type:'1', options:'1'] )
    new Node( refs.get( 0 ), 'repository', [ uri:siteUrl, url:siteUrl, type:'0', options:'1'] )
}


// Create composite repository

def compositeDir = new File( basedir, "target/composite" )
def toolsRepository = new File( basedir, "target/repository" )
def mavenRepository = new File( basedir, "../com.liferay.ide.maven-repository/target/repository" )

compositeDir.delete()
compositeDir.mkdirs()

def timestamp = System.currentTimeMillis()

ant.sequential
{
    copy( todir:compositeDir )
    {
        fileset( dir:"${basedir}/composite", includes:"*" )
        filterset()
        {
            filter( token:"timestamp", value:timestamp )
        }
    }

    copy( todir:"${compositeDir}/tools" )
    {
        fileset( dir:toolsRepository )
        {
            include( name:"**/*" )
        }
    }

    copy( todir:"${compositeDir}/maven" )
    {
        fileset( dir:mavenRepository )
        {
            include( name:"**/*" )
        }
    }
}

def unqualifiedVersion = project.properties.getProperty("unqualifiedVersion")
def buildQualifier = project.properties.getProperty("buildQualifier")

println 'Zipping updated site'
File zipSite = new File( basedir + "/target/Liferay_IDE_${unqualifiedVersion}.${buildQualifier}-updatesite.zip" )
ant.zip( destFile: zipSite, baseDir:compositeDir )

