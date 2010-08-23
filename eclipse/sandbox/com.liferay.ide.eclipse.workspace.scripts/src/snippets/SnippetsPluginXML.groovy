package snippets;
File workspaceDir = new File("..")
File outputXml = new File(workspaceDir, "com.liferay.ide.eclipse.ui.snippets/plugin.xml")
String contenttypesVal = "org.eclipse.jst.jsp.core.jspsource"
String categoryIdPrefix = "com.liferay.ide.eclipse.ui.snippets.category"
String itemIdPrefix = "com.liferay.ide.eclipse.ui.snippets.item"
String iconPrefix = "icons/snippets"

def searchContainer = '''
<extension
         point="org.eclipse.wst.common.snippets.SnippetContributions">
	<category
    		contenttypes="org.eclipse.jst.jsp.core.jspsource"
        	description="Liferay Search Container"
        	id="com.liferay.ide.eclipse.ui.snippets.category.search"
        	label="Liferay Search Container"
        	smallicon="icons/e16/portlet_tld.png">
        <item
        		class="com.liferay.ide.eclipse.ui.snippets.TaglibSnippetInsertion"
            	description="actionURL"
            	id="com.liferay.ide.eclipse.ui.snippets.item.search"
            	label="actionURL"
            	smallicon="icons/e16/tag-generic.gif">
            <content>
               &lt;portlet:actionURL ${name}${var}/&gt;</content>
            <variable
                  description="Name"
                  id="name"
                  name="name">
            </variable>
            <variable
                  description="Var"
                  id="var"
                  name="var">
            </variable>
         </item>
      </category>
   </extension>
'''


def mappings = [
			aui : [
				desc : "Liferay AUI Taglib",
				label : "Liferay AUI Taglib",
				icon16 : "liferay_aui_tld_16x16.png",
				icon32 : "liferay_aui_tld_32x32.png",
			],
			"liferay-portlet" : [
				desc : "Liferay Portlet Ext Taglib",
				label : "Liferay Portlet Ext Taglib",
				icon16 : "portlet_ext_tld_16x16.png",
				icon32 : "portlet_ext_tld_32x32.png",
			],
			portlet : [
				desc : "Portlet Taglib",
				label : "Portlet Taglib",
				icon16 : "portlet_tld_16x16.png",
				icon32 : "portlet_tld_32x32.png",
			],
			theme : [
				desc : "Liferay Theme Taglib",
				label : "Liferay Theme Taglib",
				icon16 : "liferay_theme_tld_16x16.png",
				icon32 : "liferay_theme_tld_32x32.png",
			],
			"liferay-ui" : [
				desc : "Liferay UI Taglib",
				label : "Lifeary UI Taglib",
				icon16 : "liferay_ui_tld_16x16.png",
				icon32 : "liferay_ui_tld_32x32.png",
			],
			alloy : [
				desc : "Alloy UI Component Taglib",
				label : "Alloy UI Component Taglib",
				icon16 : "alloy_tld_16x16.png",
				icon32 : "alloy_tld_32x32.png",
			]
		]


XmlParser pluginXmlParser = new XmlParser()

File snippetsDir = new File("bin/snippets")
def tldFiles = []
def tldFilenames = [
	"liferay-portlet.tld",
	"liferay-portlet-ext.tld",
	"liferay-ui.tld",
	"liferay-aui.tld",
	"liferay-theme.tld",
	"alloy.tld",
]

tldFilenames.each {
	tldFiles << new File(snippetsDir, it)
}

//find the <plugin> parent in plugin.xml
def extensionNode = pluginXmlParser.createNode(null, "extension", [point:"org.eclipse.wst.common.snippets.SnippetContributions"])

tldFiles.each {
	println it
	XmlParser xmlParser = new XmlParser()
	Node rootNode = xmlParser.parse(it)
	
	//find the taglib name
	def name = rootNode.'short-name'.text()
	
	def categoryAttrs = [
				contenttypes : contenttypesVal,
				description : mappings[name]["desc"],
				id : "${categoryIdPrefix}.${name}",
				label : mappings[name]["label"],
				smallicon : "${iconPrefix}/${mappings[name]['icon16']}",
				largeicon : "${iconPrefix}/${mappings[name]['icon32']}",
			]
	
	def categoryNode = extensionNode.appendNode("category", categoryAttrs)
	
	rootNode.tag.findAll {
		def itemName = it.name.text()
		
		def itemAttrs = [
					class : "com.liferay.ide.eclipse.ui.snippets.TaglibSnippetInsertion",
					description : itemName,
					id : "${itemIdPrefix}.${itemName}",
					label : itemName,
					smallicon : "${iconPrefix}/tag-generic.gif",
				]
		
		def itemNode = categoryNode.appendNode("item", itemAttrs)
		
		def tagAttrs = []		
	
		it.attribute.findAll {
			tagAttrs << it.name.text()
		}
		
		def appendAttr = {
			writer, attr ->
			writer << '${' + attr + '}' 
		}
		
		def contentBody = "<${name}:${itemName}${ writer -> tagAttrs.each { appendAttr(writer, it) }}></${name}:${itemName}>"
		
		itemNode.appendNode("content", contentBody)
		
		tagAttrs.each {
			itemNode.appendNode "variable", [
				description : it,
				id : it,
				name: it,
			]
		}
		
		def writer = new StringWriter()
		new XmlNodePrinter(new PrintWriter(writer)).print(extensionNode)
		def extensionText = writer.toString()
		
		def outputTemplate = """\
<?xml version="1.0" encoding="UTF-8"?>
<?eclipse version="3.4"?>
<plugin>

${extensionText}

</plugin>
"""
		outputXml.setText outputTemplate
		
	}
}