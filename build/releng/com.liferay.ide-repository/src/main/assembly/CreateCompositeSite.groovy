import static groovy.io.FileType.*

def basedir = project.basedir.canonicalPath

// Create composite repository

def compositeDir = new File( basedir, "target/composite" )
def toolsTargetRepository = new File( basedir, "../com.liferay.ide.tools-repository/target/" )
def toolsRepository = new File( toolsTargetRepository, "repository" )
def mavenRepository = new File( basedir, "../com.liferay.ide.maven-repository/target/repository" )
def adtRepository = new File( basedir, "../com.liferay.ide.adt-repository/target/repository" )
//def velocityRepository = new File( basedir, "../com.liferay.ide.velocity-repository/target/repository" )

compositeDir.delete()
compositeDir.mkdirs()

def timestamp = System.currentTimeMillis()

ant.sequential
{
    copy( todir:compositeDir )
    {
        fileset( dir:"${basedir}/composite", includes:"*" )
        filterset()
        {
            filter( token:"timestamp", value:timestamp )
        }
    }

    copy( todir:"${compositeDir}/tools" )
    {
        fileset( dir:toolsRepository )
        {
            include( name:"**/*" )
        }
    }

    copy( todir:"${compositeDir}/maven" )
    {
        fileset( dir:mavenRepository )
        {
            include( name:"**/*" )
        }
    }

    copy( todir:"${compositeDir}/adt" )
    {
        fileset( dir:adtRepository )
        {
            include( name:"**/*" )
        }
    }

    //copy( todir:"${compositeDir}/velocity" )
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
ant.zip( destFile: zipSite, baseDir:compositeDir )
