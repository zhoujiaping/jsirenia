import groovy.sql.Sql
import groovy.transform.Field

Sql getSql(){
    def url = 'jdbc:mysql://localhost:3306/asp?useUnicode=true&characterEncoding=UTF-8'
    def user = 'root'
    def password = ''
    def driver = 'com.mysql.jdbc.Driver'
    def sql = Sql.newInstance(url, user, password, driver)
    return sql
}
@Field Sql sql = getSql()
def stmt = "select * from t_user limit 10;"
def keys = ['user_id','username','pwd','creator_user_id']
sql.eachRow(stmt,[]){
    row->
    println keys.collect {
        row[it]
    }
        //println row
}
