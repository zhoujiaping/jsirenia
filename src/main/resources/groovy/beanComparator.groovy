package com.xx.cmp
/**
 * 有些dto和model字段定义重复了。在model被重构的时候（对应的表重构了），
 * 接下来我们需要重构dto。但是dto中有一些额外的属性，我们需要比较dto和model的差异属性。
 * 这个脚本就是用来实现两个java bean属性对比的。
 * */
def basedir = "D:/xxx/src/main/java/"
def dto = new File("${basedir}com/xxxDto.java").text
def model = new File("${basedir}com/xxx.java").text
String compare(String dto,String model){
    //private Integer countByOrder;
    def reg = /private\s+[\.a-zA-Z]+\s+([a-zA-Z0-9]+)\s*;/
    def dtoProps = []
    dto.eachMatch(reg){
        dtoProps << it[1]
    }
    def modelProps = []
    model.eachMatch(reg){
        modelProps << it[1]
    }
    dtoProps.sort()
    modelProps.sort()
    def diffDto = dtoProps - modelProps
    def diffModel = modelProps - dtoProps
    println "dto and model has =============>"
    def dtoPropsCopy = dtoProps.collect(){it}
    dtoPropsCopy.retainAll(modelProps)
    println dtoPropsCopy
    println "dto has, model has not =============>"
    println diffDto
    println "model has, dto has not =============>"
    println diffModel
}
compare(dto,model)