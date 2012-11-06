def mapFile = new File("../../releng/com.liferay.ide.releng/files/wtp-3.3.0.map")
def depsFile = new File("../../releng/com.liferay.ide.releng/files/wtp-3.3.0-deps.txt")
def outputFile = new File("../../releng/com.liferay.ide.releng/files/wtp-3.3.0-deps.map")

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