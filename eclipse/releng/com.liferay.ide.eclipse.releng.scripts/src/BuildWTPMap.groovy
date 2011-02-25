def mapFile = new File("../com.liferay.ide.eclipse.releng/files/wtp-3.2.3.map")
def depsFile = new File("../com.liferay.ide.eclipse.releng/files/wtp-3.2.3-deps.txt")
def outputFile = new File("../com.liferay.ide.eclipse.releng/files/wtp-3.2.3-deps.map")

def deps = depsFile.readLines();

outputFile.setText('')

mapFile.eachLine {
	def plugin = it.toString() =~ /^plugin@(.*)=.*/
	if (plugin.matches()) {
		if (deps.contains(plugin[0][1])) {
			outputFile.append(it.toString() + "\n")
		}
	}
}