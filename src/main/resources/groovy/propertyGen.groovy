package com.xx.codegen
import groovy.sql.Sql
import com.xx.util.SqlUtil
import groovy.transform.Field

import java.time.LocalDateTime

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
@Field String date = LocalDateTime.now().format("yyyy-MM-dd HH:mm")

def genGetterAndSetter(String tableName){
    def stmt = "select * from information_schema.columns where table_schema = ? and table_name = ?;"
    def results = ''
    sql.eachRow(stmt,['xx',tableName],{
        row ->
            String columnName = row.COLUMN_NAME.toLowerCase()
            String propertyName = SqlUtil.underline2camel(columnName)
            String javaType = SqlUtil.javaType(row.COLUMN_TYPE)
            String getterName = SqlUtil.underline2camel("get_"+columnName)
            String setterName = SqlUtil.underline2camel("set_"+columnName)
            String getter = """
public ${javaType} ${getterName}(){
    return ${propertyName};
}
"""
            String setter = """
public void ${setterName}(${javaType} ${propertyName}){
    this.${propertyName} = ${propertyName};
}

"""
            results += getter
            results += setter
    })
    return results
}
def genProps(String tableName){
    def stmt = "select * from information_schema.columns where table_schema = ? and table_name = ?;"
    def results = ''
    sql.eachRow(stmt,['xx',tableName],{
        row ->
            String columnName = row.COLUMN_NAME.toLowerCase()
            String propertyName = SqlUtil.underline2camel(columnName)
            String columnComment = row.COLUMN_COMMENT
            String comment = "/** ${columnComment} */\n"
            String javaType = SqlUtil.javaType(row.COLUMN_TYPE)
            if(row.COLUMN_KEY == 'PRI'){
                results = """${comment}private ${javaType} ${propertyName};\n""" + results
            }else{
                results = results + """${comment}private ${javaType} ${propertyName};\n"""
            }
    })
    results
}

def genJavaClass(String tableName){
    def packageName = "com.xx.model"
//给定表名，生成BaseResultMap文件
//println System.getProperty("user.dir")
    String props = genProps(tableName)
    String getterAndSetter = genGetterAndSetter(tableName)
    String modelName = SqlUtil.underline2camel(tableName)
    modelName = modelName.substring(1)
//println genProps(tableName)
//println genGetterAndSetter(tableName)
    String javaCode = """\
package ${packageName};
/**
@date ${date}
*/
public class ${modelName} implements java.io.Serializable{
    private static final long serialVersionUID = 1L;
    $props
    $getterAndSetter
    @Override
    public String toString(){
        return com.alibaba.fastjson.JSON.toJSONString(this);
    }
}
"""
    def parent = System.getProperty("user.dir")
    parent = "/models"
    File parentFile = new File(parent)
    if(!parentFile.exists()){
        parentFile.mkdirs()
    }
    File file = new File(parentFile,modelName+".java")
    println file
    file.write(javaCode)
}
//查询表名集合
def findTables(){
    def stmt = 'select table_name from information_schema.tables where table_schema = ?;'
    def tables = []
    sql.eachRow(stmt,['xx'],{
        def table = it.TABLE_NAME
        tables.add(table)
    })
    return tables
}
/************************ useage **************************
 * 扫描某个库的所有表，生成实体类，实体类中的字段，
 * 会有数据库字段的注释。
 * 输出到一个目录。
 *
 * */
//生成所有model
def tables = findTables()
tables.each {
    genJavaClass(it)
}
//生成某个model的props
//genProps('')

//生成某个model的getter和setter
//genGetterAndSetter('')

//生成某个model
//genJavaClass('')







