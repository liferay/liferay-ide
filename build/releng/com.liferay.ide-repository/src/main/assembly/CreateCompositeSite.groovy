import static groovy.io.FileType.*

def basedir = project.basedir.canonicalPath

// Create composite repository

def updatesiteDir = new File( basedir, "target/updatesite" )
def toolsTargetRepository = new File( basedir, "../com.liferay.ide.tools-repository/target/" )
def toolsRepository = new File( toolsTargetRepository, "repository" )
def mavenRepository = new File( basedir, "../com.liferay.ide.maven-repository/target/repository" )
def mobileRepository = new File( basedir, "../com.liferay.mobile.sdk-repository/target/repository" )
//def velocityRepository = new File( basedir, "../com.liferay.ide.velocity-repository/target/repository" )

updatesiteDir.delete()
updatesiteDir.mkdirs()

def timestamp = System.currentTimeMillis()

ant.sequential
{
    copy( todir:updatesiteDir )
    {
        fileset( dir:"${basedir}/updatesite", includes:"*" )
        filterset()
        {
            filter( token:"timestamp", value:timestamp )
        }
    }

    copy( todir:"${updatesiteDir}/tools" )
    {
        fileset( dir:toolsRepository )
        {
            include( name:"**/*" )
        }
    }

    copy( todir:"${updatesiteDir}/maven" )
    {
        fileset( dir:mavenRepository )
        {
            include( name:"**/*" )
        }
    }

    copy( todir:"${updatesiteDir}/mobile" )
    {
        fileset( dir:mobileRepository )
        {
            include( name:"**/*" )
        }
    }

    //copy( todir:"${updatesiteDir}/velocity" )
    //{
    //    fileset( dir:velocityRepository )
    //    {
    //        include( name:"**/*" )
    //    }
    //}
}

def version = ""
toolsTargetRepository.eachFileMatch FILES, ~/Liferay_IDE_Tools_.*-updatesite.zip/, {
    version = ( it.name =~ /Liferay_IDE_Tools_(.*)-updatesite.zip/ )[0][1]
}


println 'Zipping updated site'
File zipSite = new File( basedir + "/target/Liferay_IDE_${version}-updatesite.zip" )
ant.zip( destFile: zipSite, baseDir:updatesiteDir )
