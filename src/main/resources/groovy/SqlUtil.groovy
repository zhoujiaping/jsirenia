package com.xx.util
/**
 */
class SqlUtil {
    //下划线转驼峰命名
    def static underline2camel(String column) {
        if (column == null) {
            return null;
        }
        String[] array = column.toLowerCase().split("_(?=[a-z])");
        if (array.length == 1) {
            return array[0];
        }
        for (int i = 1; i < array.length; i++) {
            array[i] = array[i].substring(0, 1).toUpperCase() + array[i].substring(1)
        }
        return String.join("", array)
    }
    //下划线转驼峰命名
    def static underline2camel(String prefixToIgnore,String column) {
        if (column == null) {
            return null;
        }
        if(prefixToIgnore!=null && column.startsWith(prefixToIgnore)){
            column = column.substring(prefixToIgnore.length())
        }
        return underline2camel(column)
    }

    def static columnType2jdbcType = [
            'bigint':'BIGINT',
            'varchar':'VARCHAR',
            'datetime':'TIMESTAMP',
            'int':'INTEGER',
            'double':'DOUBLE',
            'decimal':'DECIMAL',
            'date':'DATE',
            'float':'FLOAT'
            //TODO
    ]
    def static jdbcType(String columnType){
        columnType = columnType.toLowerCase()
        def i = columnType.indexOf("(")
        if(i>0){
            columnType = columnType.substring(0,i)
        }
        columnType2jdbcType[columnType]
    }

    def static columnType2javaType = [
            'bigint':'Long',
            'varchar':'String',
            'datetime':'java.util.Date',
            'int':'Integer',
            'double':'Double',
            'decimal':'java.math.BigDecimal',
            'date':'java.util.Date',
            'float':'Float'
            //TODO
    ]
    def static javaType(String columnType){
        columnType = columnType.toLowerCase()
        def i = columnType.indexOf("(")
        if(i>0){
            columnType = columnType.substring(0,i)
        }
        def type = columnType2javaType[columnType]
        if(!type){
            throw new RuntimeException("columnType:${columnType} => null")
        }
        return type
    }

}