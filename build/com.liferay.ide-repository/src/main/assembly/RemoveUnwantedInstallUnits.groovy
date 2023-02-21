import groovy.ant.AntBuilder
import groovy.xml.XmlNodePrinter
import groovy.xml.XmlParser

def basedir = project.basedir.canonicalPath
def repositoryDir = basedir + "/target/repository"
def contentJar = repositoryDir  + "/content.jar"
def contentDir = basedir  + "/target/content.jar/"

// Remove hidden feature

println 'Unzipping content.jar'

def ant = new AntBuilder();   // create an antbuilder

ant.unzip( src: contentJar, dest:contentDir,  overwrite:"true" )

println 'Modify content.xml to remove unwanted IUs from Liferay IDE site'

File contentXml =  new File( contentDir, "content.xml" )
def contentXmlText = contentXml.text

def parser = new XmlParser()
parser.setTrimWhitespace( false )
def root = parser.parseText( contentXmlText )

def hiddenCategory = root.units.unit.findAll{ it.'@id'.equals('com.liferay.ide-repository.hidden.features') }.get( 0 )
hiddenCategory.parent().remove( hiddenCategory )

/*
def addAssociateSite( root, siteUrl )
{
    def refs = root.references

    if( !refs || refs.size() == 0 )
    {
        def newRefs = new Node( root, 'references' )
        newRefs.@size = "2"
        refs = root.references
    }

    new Node( refs.get( 0 ), 'repository', [ uri:siteUrl, url:siteUrl, type:'1', options:'1'] )
    new Node( refs.get( 0 ), 'repository', [ uri:siteUrl, url:siteUrl, type:'0', options:'1'] )
}


def props = root.properties

def sapphireSite = project.properties.getProperty("sapphire-site")
addAssociateSite( root, sapphireSite )
*/
/*
class MyXmlNodePrinter extends XmlNodePrinter
{
    MyXmlNodePrinter(PrintWriter out)
    {
       super(out)
    }

    void printSimpleItem(Object value)
    {
       value = value.replaceAll("\n", "&#xA;")
       out.print(value)
    }
}
*/

println 'Overwriting content.xml'
def writer = new StringWriter()
def printer = new XmlNodePrinter( new PrintWriter( writer ) )
printer.setPreserveWhitespace( true )
printer.print( root )
def result = writer.toString()

contentXml.text = result

println 'Zipping back customized content.jar'
ant.zip( destFile: contentJar, baseDir:contentDir )
