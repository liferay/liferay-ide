package snippets;


File outputXml = new File("../../common/plugins/com.liferay.ide.ui.snippets/plugin.xml")
String contenttypesVal = "org.eclipse.jst.jsp.core.jspsource"
String categoryIdPrefix = "com.liferay.ide.ui.snippets.category"
String itemIdPrefix = "com.liferay.ide.ui.snippets.item"
String iconPrefix = "icons/snippets"

def mappings = [
			aui : [
				desc : "Liferay AUI Taglib",
				label : "Liferay AUI Taglib",
				icon16 : "liferay_aui_tld_16x16.png",
				icon32 : "liferay_aui_tld_32x32.png",
				url : "http://liferay.com/tld/aui",
				clazz : "com.liferay.ide.ui.snippets.TaglibSnippetInsertion"
			],
			"liferay-portlet" : [
				desc : "Liferay Portlet Ext Taglib",
				label : "Liferay Portlet Ext Taglib",
				icon16 : "portlet_ext_tld_16x16.png",
				icon32 : "portlet_ext_tld_32x32.png",
				url : "http://liferay.com/tld/portlet",
				clazz : "com.liferay.ide.ui.snippets.TaglibSnippetInsertion"
			],
			portlet : [
				desc : "Portlet Taglib",
				label : "Portlet Taglib",
				icon16 : "portlet_tld_16x16.png",
				icon32 : "portlet_tld_32x32.png",
				url : "http://java.sun.com/portlet",
				clazz : "com.liferay.ide.ui.snippets.TaglibSnippetInsertion"
			],
			theme : [
				desc : "Liferay Theme Taglib",
				label : "Liferay Theme Taglib",
				icon16 : "liferay_theme_tld_16x16.png",
				icon32 : "liferay_theme_tld_32x32.png",
				url : "http://liferay.com/tld/theme",
				clazz : "com.liferay.ide.ui.snippets.TaglibSnippetInsertion"
			],
			"liferay-ui" : [
				desc : "Liferay UI Taglib",
				label : "Liferay UI Taglib",
				icon16 : "liferay_ui_tld_16x16.png",
				icon32 : "liferay_ui_tld_32x32.png",
				url : "http://liferay.com/tld/ui",
				clazz : "com.liferay.ide.ui.snippets.TaglibSnippetInsertion"
			],
			alloy : [
				desc : "Alloy Taglib",
				label : "Alloy Taglib",
				icon16 : "alloy_tld_16x16.png",
				icon32 : "alloy_tld_32x32.png",
				url : "http://alloy.liferay.com/tld/alloy",
				clazz : "com.liferay.ide.ui.snippets.AlloyTagSnippetInsertion"
			]
		]



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

XmlParser pluginXmlParser = new XmlParser()
def pluginXmlNode = pluginXmlParser.parse(outputXml)

//find the <plugin> parent in plugin.xml
def existingTaglibExt = pluginXmlNode.extension.findAll { it.'@id'.equals("taglib") }

if (existingTaglibExt?.size() == 1) {
	pluginXmlNode.remove(existingTaglibExt[0])
}

def taglibExtensionNode = pluginXmlParser.createNode(null, "extension", [id:"taglib",point:"org.eclipse.wst.common.snippets.SnippetContributions"])

def importCategoryAttrs = [
	contenttypes : contenttypesVal,
	description : "Taglib imports",
	id : "${categoryIdPrefix}.taglib_imports",
	label :"Taglib imports",
	smallicon : "${iconPrefix}/jsptaglib.gif",
]

def importCategoryNode = taglibExtensionNode.appendNode("category", importCategoryAttrs)

def taglibImportSnippets = []

mappings.each {
	def mapping = it.getValue()
	def taglibImportSnippet = [
		label : "${mapping['label']} Import",
		desc : mapping["desc"],
		url : mapping["url"],
		prefix : it.getKey()
	]

	taglibImportSnippets << taglibImportSnippet
}

taglibImportSnippets.each {
	def importItemAttrs = [
		class : "org.eclipse.wst.common.snippets.ui.DefaultSnippetInsertion",
		description : it["desc"],
		id : "${itemIdPrefix}.taglib-import." + it["prefix"],
		label : it["label"],
		smallicon : "${iconPrefix}/tag-generic.gif",
	]

	def importItemNode = importCategoryNode.appendNode("item", importItemAttrs)

	def importContentBody = "<%@ taglib uri=\"${it['url']}\" prefix=\"${it['prefix']}\" %>"

	importItemNode.appendNode("content", importContentBody)
}

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

	def categoryNode = taglibExtensionNode.appendNode("category", categoryAttrs)

	rootNode.tag.findAll {
		def itemName = it.name.text()

		def itemAttrs = [
					class : mappings[name]["clazz"],
					description : itemName,
					id : "${itemIdPrefix}.${itemName}",
					label : itemName,
					smallicon : "${iconPrefix}/tag-generic.gif",
				]

		def itemNode = categoryNode.appendNode("item", itemAttrs)

		def tagAttrs = []

		it.attribute.findAll {
			tagAttrs << [name : it.name.text(), desc : it.description?.text()]
		}

		def appendAttr = {
			writer, attr ->
			writer << '${' + attr.name + '}'
		}

		def contentBody = "<${name}:${itemName}${ writer -> tagAttrs.each { appendAttr(writer, it) }}></${name}:${itemName}>"

		itemNode.appendNode("content", contentBody)

		tagAttrs.each {
			itemNode.appendNode "variable", [
				description : it.desc,
				id : it.name,
				name: it.name,
			]
		}
	}
}

pluginXmlNode.append(taglibExtensionNode)

def writer = new StringWriter()
new XmlNodePrinter(new PrintWriter(writer)).print(pluginXmlNode)
def pluginText = writer.toString()


outputXml.setText pluginText