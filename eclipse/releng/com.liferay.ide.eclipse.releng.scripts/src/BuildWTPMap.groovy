def mapFile = new File("../com.liferay.ide.eclipse.releng/maps/wtp-3.2.0.map")
def depsFile = new File("../com.liferay.ide.eclipse.releng/maps/wtp-3.2.0-deps.txt")
def outputFile = new File("../com.liferay.ide.eclipse.releng/maps/wtp-3.2.0-deps.map")

def deps = depsFile.readLines();

mapFile.eachLine {
	def plugin = it.toString() =~ /^plugin@(.*)=.*/
	if (plugin.matches()) {
		if (deps.contains(plugin[0][1])) {
			outputFile.append(it.toString() + "\n")
		}
	}
	
}