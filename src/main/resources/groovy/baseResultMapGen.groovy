package com.xx.codegen

import groovy.sql.Sql
import com.xx.util.SqlUtil
import groovy.transform.Field

//可以指定maven仓库
Sql getSql(){
    def url = 'jdbc:mysql://xxx/xxx?useUnicode=true&characterEncoding=UTF-8'
    url = "jdbc:mysql://localhost:3306/xxx?useUnicode=true&characterEncoding=UTF-8"
    def user = 'xx'
    def password = 'xx'
    def driver = 'com.mysql.jdbc.Driver'
    def sql = Sql.newInstance(url, user, password, driver)
    return sql
}
@Field Sql sql = getSql()
def genResults(String tableName){
    def stmt = "select * from information_schema.columns where table_schema = ? and table_name = ?;"
    def results = ''
    sql.eachRow(stmt,['xx',tableName],{
        row ->
            String columnName = row.COLUMN_NAME.toLowerCase()
            String propertyName = SqlUtil.underline2camel(columnName)
            String jdbcType = SqlUtil.jdbcType(row.COLUMN_TYPE)
            if(row.COLUMN_KEY == 'PRI'){
                results = """<id column="${columnName}" jdbcType="${jdbcType}" property="${propertyName}" />\n""" + results
            }else{
                results = results + """<result column="${columnName}" jdbcType="${jdbcType}" property="${propertyName}" />\n"""
            }
    })
    results
}
def findTables(){
    def stmt = 'select table_name from information_schema.tables where table_schema = ?;'
    def tables = []
    sql.eachRow(stmt,['xx'],{
        def table = it.TABLE_NAME
        tables.add(table)
    })
    return tables
}
/*******************useage**************
 * 扫描某个库的所有表，生成mybaits的xml mapper需要的BaseResultMap，
 * 输出到一个文本文件。
 *
 *
 * */
def tables = findTables()
File file = new File("/results.txt")
def text = tables.collect {
    def rs = genResults(it)
    return it+"===>"+"*"*80+"\n"+rs+"\n"
}
file.text = text



