def basedir = project.basedir.canonicalPath
def targetDir = basedir + "/target/"
def jarPrefixToRemove = "org.eclipse.justj.openjdk.hotspot.jre.full"

def updateSiteZip = new File(targetDir).listFiles().find { it.name.startsWith("liferay-ide-updatesite") && it.name.endsWith(".zip") }

if (updateSiteZip) {
  def tempDir = new File(targetDir, "temp-update-site")

  tempDir.mkdir()

  try {
    ant.unzip(src: updateSiteZip, dest: tempDir)

    def pluginsDir = new File(tempDir, "plugins")

    pluginsDir.listFiles().each { file ->
      if (file.isFile() && file.getName().startsWith(jarPrefixToRemove)) {
        if (file.delete()) {
          println "Removed: ${file.getName()}"
        } else {
          println "Failed to remove: ${file.getName()}"
        }
      }
    }

    updateSiteZip.delete()

    ant.zip(destfile: updateSiteZip, basedir: tempDir, update: true)
  }
  catch(Exception ex){
  }
  finally {

    ant.delete(dir: tempDir)
  }
} else {
  println "Update site zip file not found."
} 