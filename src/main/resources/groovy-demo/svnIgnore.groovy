/**
 * 该脚本解决的问题：
 * 每次从svn checkout代码下来，都需要配置一遍ignore，手动将target、bin等逐个添加至ignore list。
 * 有了这个脚本，可以将它放在代码库里面，每次只需要修改一下projectPath，然后执行一下就可以了。
 */

def projectPath = "D:/xxx"

if(projectPath.contains("trunk")){
    println "don't run this on trunk!"
    return null
}

def disk = "d:"
if(projectPath.matches(/^[a-zA-Z]:.*$/)){
    disk = projectPath[0]+":"
}

def projectFile = new File(projectPath)

def ignoreFile = new File(disk, ".ignore")
if (!ignoreFile.exists()) {
    ignoreFile.text = """
.idea
.settings
.classpath
.project
*.iml
*.log
tomcat.*
target
bin
bin-groovy
"""
}

def cmd = """svn propget svn:ignore ${projectPath}"""
println cmd
def process = cmd.execute()
println process.err.text
println process.text

cmd = """svn propset svn:ignore -F $ignoreFile ${projectPath}"""
println cmd
process = cmd.execute()
println process.err.text
println process.text

projectFile.eachDir {
    dir ->
        if (dir.name.startsWith(".")) {
            return null
        }
        if(!new File(dir,"pom.xml").exists()){
            return null
        }

        cmd = """svn propget svn:ignore ${dir}"""
        println cmd
        process = cmd.execute()
//process.waitFor()
        println process.err.text
        println process.text

        cmd = """svn propset svn:ignore -F $ignoreFile ${dir}"""
        println cmd
        process = cmd.execute()
        println process.err.text
        println process.text
}
