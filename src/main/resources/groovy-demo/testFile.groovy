new File("d:/test-groovy-file").text = """
groovy
java
javascript
"""

println new File("d:/test-groovy-file").text

def writer = new File("d:/test-groovy-file").newWriter()
writer << """
    java
    javascript
"""
writer.close()
new File("d:/test-groovy-file2").bytes = "1234".bytes

new File("d:/test-groovy-file").eachLine {
    println it
}
