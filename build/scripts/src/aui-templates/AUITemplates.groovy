File templatesXml = new File( "src/aui-templates/aui-js-templates-empty.xml" )
XmlParser parser = new XmlParser()
def doc = parser.parse( templatesXml )

File auiExamplesDir = new File( "d:/liferay/alloyui.com/src/documents/examples" )

auiExamplesDir.eachDir
{
    if( it.name == "_sample" )
    {
        return
    }

    File js = new File( it, "basic.js" )
    File index = new File( it, "index.html.eco" )

    if( js.exists() && index.exists() )
    {
        def description = "${it.name}"

        index.eachLine
        {
            if( it.startsWith( "description: " ) )
            {
                description = it.substring( 13, it.length() );
            }
        }

        def attrs =
        [
            name: "aui-${it.name}",
            description: "${description}",
            id: "com.liferay.ide.alloy.ui.templates.${it.name}",
            context: "javaScript",
            enabled: "true",
            autoinsert: "true"
        ]

        Node template = parser.createNode( doc, "template", attrs )

        template.setValue( js.text )
    }
}

def writer = new StringWriter()
new XmlNodePrinter( new PrintWriter( writer ) ).print( doc )
def xmlText = writer.toString()

new File( "../../tools/plugins/com.liferay.ide.alloy.ui/templates/aui-js-templates.xml" ).setText( xmlText )
