def signApps = properties["signApps"]

if (!"true".equals(signApps)) {
	return
}

def appDir = new File(properties["appDir"]).canonicalFile
def searchPath = properties["signingServerSearchPath"]
def replacePath = properties["signingServerReplacePath"]
def serverURL = properties["signingServerURL"]
def certificate = properties["certificate"]
def createDmg = properties["createDmg"]

println appDir
println serverURL
println certificate

if (appDir.exists() && serverURL != null) {
	println "Initial appDir = ${appDir}"

	if (searchPath != null && searchPath.length() > 0) {
		def absolutePath = appDir.absolutePath

		if (absolutePath.startsWith(searchPath)) {
			absolutePath = absolutePath.replaceAll(searchPath, replacePath)
			appDir = new File(absolutePath)
		}
	}

	println "Modified appDir = ${appDir}"

	println "Calling codesign service..."

	def url = new URL(serverURL)
	def post = url.openConnection()
	def path = appDir.toURI().toASCIIString().replaceAll("^file:","")
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
}