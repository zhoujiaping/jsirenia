import groovy.sql.Sql

@GrabResolver(name = 'aliyun', root = 'http://maven.aliyun.com/nexus/content/groups/public/')
@GrabConfig(systemClassLoader = true)
@Grab('mysql:mysql-connector-java:5.1.46')
class SqlDatabase {
    static Sql setUpDatabase() {
        def url = 'jdbc:mysql://10.118.242.13:3306/jyd?useUnicode=true&characterEncoding=UTF-8'
        def user = 'jyd'
        def password = 'jyd'
        def driver = 'com.mysql.jdbc.Driver'
        def sql = Sql.newInstance(url, user, password, driver)
        return sql
    }
}

def sql = SqlDatabase.setUpDatabase()
sql.eachRow('show tables',{
    println it[0]
    def table = it[0]
    sql.eachRow("select count(*) from "+table,{c->println c[0]})
    def columnCount = 0
    def needPrint = false
    sql.eachRow("select * from "+table,{
        meta -> columnCount = meta.columnCount
        //needPrint = true
    },{row->
        if(needPrint){
            println row
            needPrint = false
        }
        //println row
        (0..<columnCount).each({i->
            if(row[i]!=null && row[i].class==String.class &&row[i].contains('����')){
                println "${table}->${row}"
            }
        })
    })
})
println '*'*10+'end'+'*'*10
/*
sql.eachRow('SELECT * FROM t_customer limit 0,3') { row ->
  def id = row[0]
  def name = row.cust_name
  println(row.getMetaData())
  println("${id},${name}")
}*/