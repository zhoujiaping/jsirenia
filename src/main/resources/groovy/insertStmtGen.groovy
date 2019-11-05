package com.xx.codegen

import groovy.sql.Sql
import com.xx.util.SqlUtil
import groovy.transform.Field

Sql getSql(){
    def url = 'jdbc:mysql://xx:3306/xx?useUnicode=true&characterEncoding=UTF-8'
    url = "jdbc:mysql://localhost:3306/xx?useUnicode=true&characterEncoding=UTF-8"
    def user = 'xx'
    def password = 'xx'
    def driver = 'com.mysql.jdbc.Driver'
    def sql = Sql.newInstance(url, user, password, driver)
    return sql
}
@Field Sql sql = getSql()
def genInsertStmt(String tableName){
    def stmt = "select * from information_schema.columns where table_schema = ? and table_name = ?;"
    def columns = []
    sql.eachRow(stmt,['xx',tableName],{
        row ->
            String columnName = row.COLUMN_NAME.toLowerCase()
            columns<<columnName
    })
    columns = columns.join(", ")
    """
insert into ${tableName}($columns)values
($columns);
"""
}
/*******************useage**************
 * 扫描某个库的所有表，生成mybaits的xml mapper需要的BaseResultMap，
 * 输出到一个文本文件。
 *
 *
 * */
def insertStmt = genInsertStmt("xx")
println insertStmt



