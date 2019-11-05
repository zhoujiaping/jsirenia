//@Grab('com.jcraft:jsch:0.1.54')
import com.jcraft.jsch.*
import groovy.transform.Field

/**
 * 目标：以最快的速度，从svn checkout代码，编译、打包、测试，上传到linux服务器，重启服务。
 * //checkout to local  ->  check version
 * mvn package(war)
 * mvn deploy(jar)
 * upload war
 * tomcat restart
 * 解决性能问题
 * 1、代码库下载，在本地checkout代码（第一次），该目录只用于自动部署，后续每次都update。
 * 2、如何判断已存在的jar/war包就是最新的package？
 * 3、本地执行war，但是并不上传war，而是上传tar.gz。对于其中的lib，对比本地的和服务器上的，
 * 如果一致，则不用上传。如果是快照版本，则上传。
 *
 *
 */
@Field def tempDir = "d:/.auto-deploy"
@Field def codeRepo = "xx/svn/xx"
@Field def codeUrl = "xx/svn/xxx/xxx"
@Field def codeRelativeUrl = codeUrl - codeRepo
@Field def module = "xx"

@Field def svnUsername = "xx"
@Field def svnPassword = "xx"
@Field def cmd = ""
@Field def process
@Field def out = System.out
@Field def err = System.err

def beginTime = System.currentTimeMillis()
def checkoutOrUpdate(){
    def localCodeFile = new File(tempDir,codeRelativeUrl)
    def cd = new File(tempDir,codeRelativeUrl)
    if(!localCodeFile.exists()){
        localCodeFile.mkdirs()
        cmd = """svn cleanup"""
        process = cmd.execute([],cd)
        println cmd
        process.waitForProcessOutput(out,err)

        cmd = """svn co $codeUrl --username $svnUsername --password $svnPassword"""
        println cmd
        process = cmd.execute([],cd)
        process.waitForProcessOutput(out,err)
    }else{
        cmd = """svn cleanup"""
        process = cmd.execute([],cd)
        println cmd
        process.waitForProcessOutput(out,err)

        cmd = """svn up --username $svnUsername --password $svnPassword"""
        println cmd
        process = cmd.execute([],cd)
        process.waitForProcessOutput(out,err)
    }
}
checkoutOrUpdate()

def packageOrDeploy(){
    cmd = """cmd /c cd $tempDir/$codeRelativeUrl/$module & mvn package"""
    println cmd
    process = cmd.execute()
    process.waitForProcessOutput(out,err)

    TarUtil.tar(new File("$tempDir/$codeRelativeUrl/$module/target/$module"),["WEB-INF/lib"])
}

packageOrDeploy()

def localLib = "$tempDir/$codeRelativeUrl/$module/target/$module/WEB-INF/lib"
List<String> localJars = []
new File(localLib).eachFile{
    file->
        if(file.name.endsWith(".jar")){
            localJars<<file.name
        }
}
localJars.sort()


ChannelSftp sftp = null
ChannelShell shell = null
Session sshSession = null

String username = "xx"
String host = "xx"
String password = "xx"
int port = 22

def jsch = new JSch()
sshSession = jsch.getSession(username, host, port)
sshSession.password = password
def sshConfig = new Properties()
sshConfig.StrictHostKeyChecking = "no"
sshSession.config = sshConfig
sshSession.connect()
shell = sshSession.openChannel("shell")
shell.setPty(true)
shell.connect()

def remoteWebapps = "/tomcat/$module/webapps"
def remoteLib = "$remoteWebapps/$module/WEB-INF/lib"
def printWriter = new PrintWriter(shell.outputStream)
printWriter.println("cd $remoteWebapps;tar -zcf ${module}.tar.gz $module")
printWriter.println("cd $remoteWebapps;tar -zcf $module-lib.tar.gz $module/WEB-INF/lib")
printWriter.println("ls $remoteLib>$remoteWebapps/$module-jarlist.txt")
printWriter.println("rm -r $remoteWebapps/$module")
printWriter.println("rm $remoteWebapps/${module}.tar")
printWriter.println("exit")//为了结束本次交互
printWriter.flush()//把缓冲区的数据强行输出
shell.inputStream.eachLine {
    println it
}


sftp = sshSession.openChannel("sftp")
sftp.connect()

sftp.put("$tempDir/$codeRelativeUrl/$module/target/${module}.tar","$remoteWebapps/${module}.tar")

shell = sshSession.openChannel("shell")
shell.setPty(true)
shell.connect()
printWriter = new PrintWriter(shell.outputStream)
printWriter.println("cd $remoteWebapps;tar xf ${module}.tar")
printWriter.println("mkdir $remoteLib;cd $remoteWebapps;tar -zxf $module-lib.tar.gz $module/WEB-INF/lib")
printWriter.println("exit")//为了结束本次交互
printWriter.flush()//把缓冲区的数据强行输出
shell.inputStream.eachLine {
    println it
}

def remoteJars = sftp.get("$remoteWebapps/$module-jarlist.txt").text.split(/\r?\n/) as ArrayList

def toUploads = localJars - remoteJars.findAll{!(it.contains("SNAPSHOT") && it.contains("xx"))}
def toDeletes = remoteJars - localJars.findAll{!(it.contains("SNAPSHOT") && it.contains("xx"))}
toUploads.each {
    println "put $localLib/$it $remoteLib/$it"
    sftp.put("$localLib/$it","$remoteLib/$it")
}
toDeletes.each {
    println "rm $remoteLib/$it"
    sftp.rm("$remoteLib/$it")
}


shell = sshSession.openChannel("shell")
shell.setPty(true)
shell.connect()

printWriter = new PrintWriter(shell.outputStream)
printWriter.println("/tomcat/$module/bin/shutdown.sh;/tomcat/$module/bin/startup.sh")
printWriter.println("exit")//为了结束本次交互
printWriter.flush()//把缓冲区的数据强行输出
shell.inputStream.eachLine {
    println it
}


sftp.disconnect()
shell.disconnect()
sshSession.disconnect()
def endTime = System.currentTimeMillis()
println "time cost: "+(endTime-beginTime)/1000

