def process = "svn co xxx xxx --username xxx --password xxx".execute()


def oldRoot = "D:/xxx"
def newRoot = "D:/xxxx"

def module = "xxx"
def oldMap = [:]
def newMap = [:]

new File(oldRoot,aspServer+"/src/main/resources/product/properties").eachFile{
    file->{
        oldMap[file.name] = file.text
        //println file
    }
}

new File(newRoot,aspServer+"/src/main/resources/product/properties").eachFile{
    file->{
        newMap[file.name] = file.text
        //println file
    }
}

def diffAdd = newMap.keySet() - oldMap.keySet()
diffAdd.each {
    println "add file $it"
    println newMap[it]
}

def diffDel = oldMap.keySet() - newMap.keySet()
diffDel.each{
    println "del file $it"
    println oldMap[it]
}

oldMap.each {
    String filename,text->
    if(filename.endsWith(".properties") && newMap.containsKey(filename)){
        def oldFile = new File([oldRoot,module,"/src/main/resources/product/properties",filename].join("/"))
        Properties oldProps = new Properties()
        oldProps.load(oldFile.newReader())
        def newFile = new File([newRoot,module,"/src/main/resources/product/properties",filename].join("/"))
        Properties newProps = new Properties()
        newProps.load(newFile.newReader())

        println "at file $newFile"

        def itemAdd = newProps.keySet() - oldProps.keySet()
        itemAdd.each {
            println "add item $it = ${newProps[it]}"
        }

        def itemDel = oldProps.keySet() - newProps.keySet()
        itemDel.each {
            println "del item $it = ${oldProps[it]}"
        }

        def itemUpt = oldProps.keySet() - (oldProps.keySet() - newProps.keySet())
        itemUpt.each {
            if(newProps[it] != oldProps[it]){
                println "upt item $it = ${oldProps[it]} ===> ${newProps[it]}"
            }
        }
    }
}

//println oldMap