import java.util.zip.*

def inputDir = properties["inputDir"]
def outputFile = properties["outputFile"]

println "Zipping ${inputDir} into ${outputFile}"

ant.zip(destfile: outputFile, basedir: inputDir)