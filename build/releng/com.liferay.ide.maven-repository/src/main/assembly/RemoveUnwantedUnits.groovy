def basedir = project.basedir.canonicalPath
def repositoryDir = basedir + "/target/repository"
def contentJar = repositoryDir  + "/content.jar"

def contentDir = basedir  + "/target/content.jar/"

println 'Unzipping content.jar'

def ant = new AntBuilder();   // create an antbuilder

ant.unzip( src: contentJar, dest:contentDir,  overwrite:"true" )

println 'Modify content.xml to remove default category'

File contentXml =  new File( contentDir, "content.xml" )
def parser = new XmlParser()
parser.setTrimWhitespace( false )
def root = parser.parseText( contentXml.text )

def defaultCategory = root.units.unit.findAll{ it.'@id'.endsWith('.Default') }.get( 0 )
defaultCategory.parent().remove( defaultCategory )

def hiddenCategory = root.units.unit.findAll{ it.'@id'.equals('com.liferay.ide.maven-repository.hidden.features') }.get( 0 )
hiddenCategory.parent().remove( hiddenCategory )

println 'Overwriting content.xml'
def writer = new StringWriter()
def printer = new XmlNodePrinter( new PrintWriter( writer ) )
printer.setPreserveWhitespace( true )
printer.print( root )
def result = writer.toString()

contentXml.text = result

println 'Zipping back customized content.jar'
ant.zip( destFile: contentJar, baseDir:contentDir )

def zipSite = basedir  + "/target/${project.artifactId}-${project.version}.zip"

println 'Deleting old zip site'
new File( zipSite ).delete()

def unqualifiedVersion = project.properties.getProperty("unqualifiedVersion")
def buildQualifier = project.properties.getProperty("buildQualifier")

println 'Zipping updated site'
ant.zip( destFile: zipSite, baseDir:repositoryDir )

println 'Copying to final version'
File newZipSite = new File( basedir + "/target/Liferay_IDE_Maven_${unqualifiedVersion}.${buildQualifier}-updatesite.zip" )
ant.copy( file:zipSite, tofile:newZipSite )