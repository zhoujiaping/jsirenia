package tools

import groovy.sql.Sql
import org.apache.ibatis.jdbc.ScriptRunner
import org.springframework.util.ResourceUtils

//groovy.sql.Sql ==> https://www.jianshu.com/p/a0e301f79f9b
class DbEnv {
    static Sql getSql(){
        def local = [
                url:"jdbc:mysql://localhost:3306/xx?useUnicode=true&characterEncoding=UTF-8",
                user:'xx',
                password:'xx'
        ]
        def dev = [
                url:"jdbc:mysql://xx:3306/xx?useUnicode=true&characterEncoding=UTF-8",
                user:'xx',
                password:'xx'
        ]
        def config = dev;
        def driver = 'com.mysql.jdbc.Driver'
        def sql = Sql.newInstance(config.url, config.user, config.password, driver)
        return sql
    }
    def static reset() {
        Sql sql = getSql()
        def runner = new ScriptRunner(sql.connection)
        runner.sendFullScript = false
        ResourceUtils.getFile("classpath:sql").eachFile {
            file ->
                println "执行sql文件：$file"
                runner.runScript(file.newReader("utf-8"))
        }
    }
    def static runScript(String sqlText){
        Sql sql = getSql()
        def runner = new ScriptRunner(sql.connection)
        runner.sendFullScript = false
        runner.runScript(new StringReader(sqlText))
    }

    static void main(String[] args) {
        reset()
    }
}
