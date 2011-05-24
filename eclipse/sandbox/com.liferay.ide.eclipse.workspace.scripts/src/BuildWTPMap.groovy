def mapFile = new File("../../releng/com.liferay.ide.eclipse.releng/files/wtp-3.2.4.map")
def depsFile = new File("../../releng/com.liferay.ide.eclipse.releng/files/wtp-3.2.4-deps.txt")
def outputFile = new File("../../releng/com.liferay.ide.eclipse.releng/files/wtp-3.2.4-deps.map")

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