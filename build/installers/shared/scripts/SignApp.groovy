import java.nio.file.*

def signApps = properties["signApps"]

if (!"true".equals(signApps)) {
	return
}

def appPath = new File(properties["appPath"]).canonicalFile
def searchPath = properties["signingServerSearchPath"]
def replacePath = properties["signingServerReplacePath"]
def serverURL = properties["signingServerURL"]
def certificate = properties["certificate"]
def createDmg = properties["createDmg"]

println "appPath=${appPath}"
println "searchPath=${searchPath}"
println "replacePath=${replacePath}"
println "serverURL=${serverURL}"
println "certificate=${certificate}"

if (appPath.exists() && serverURL != null) {
	println "Initial appPath = ${appPath}"

	Path tempDir = Files.createTempDirectory("zipAppPath")

	def workingAppPath = appPath

	if (appPath.name.endsWith(".app")) {
		println "Zipping appPath..."

		ant.copy(todir: tempDir.resolve(workingAppPath.name).toFile()) {
			fileset(dir: workingAppPath) {
				include(name: "**")
			}
		}

		File zipFile = new File(workingAppPath.parentFile, workingAppPath.name + ".zip")

		ant.zip(destfile: zipFile, basedir: tempDir.toFile())

		workingAppPath = zipFile

		println "New zipped appPath = ${workingAppPath}"
	}

	if (searchPath != null && searchPath.length() > 0) {
		def absolutePath = workingAppPath.absolutePath

		println "absolutePath = ${absolutePath}"

		if (absolutePath.startsWith(searchPath)) {
			absolutePath = absolutePath.replaceAll(searchPath, replacePath)
			workingAppPath = new File(absolutePath)
		}
	}

	println "Modified appPath = ${appPath}"

	println "Calling codesign service..."

	def url = new URL(serverURL)
	def post = url.openConnection()
	def path = workingAppPath.toURI().toASCIIString().replaceAll("^file:","")
	def body = "path=${path}&identity=${certificate}&createDmg=${createDmg}"

	println("Posting to ${url} with body=${body}")

	post.setRequestMethod("POST")
	post.setRequestProperty("Accept-Language", "en-US,en;q=0.5")
	post.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
	post.setDoOutput(true)
	DataOutputStream wr = new DataOutputStream(post.getOutputStream())
	wr.writeBytes(body)
	wr.flush()
	wr.close()

	def postResponseCode = post.getResponseCode()

	println("ResponseCode=${postResponseCode}")

	if (postResponseCode.equals(200)) {
		println(post.getInputStream().getText())
	}

	if (appPath.name.endsWith(".app") && (createDmg == null || createDmg.equals("false"))) {
		ant.delete(dir: appPath)

		ant.unzip(src: workingAppPath, dest: appPath.parentFile)
	}

	ant.delete(dir: tempDir.toFile())

	if (workingAppPath != appPath && workingAppPath.absolutePath.startsWith(replacePath)) {
		ant.delete(file: workingAppPath)
	}
}