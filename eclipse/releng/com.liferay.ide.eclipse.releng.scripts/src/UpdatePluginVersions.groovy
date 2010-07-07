File workspaceDir = new File("..")
def oldVersion = "1.0.0.qualifier"
def newVersion = "1.0.1.qualifier"

workspaceDir.eachDir { 
	File manifest = new File(it, "META-INF/MANIFEST.MF")
	
	if (manifest.exists()) {
		String fileText = manifest.text;
		if (fileText.contains("Bundle-Vendor: Liferay")) {
			manifest.text = fileText.replaceAll("Bundle-Version: ${oldVersion}", "Bundle-Version: ${newVersion}");
		}
	}
}