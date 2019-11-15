import groovy.transform.Field

/**
 * base64
 * 1、base64编码有多个RFC标准
 * 2、RFC2045 是 52个字母10个数字和+/两个字符，但是+在url中会被转成%2B，%在数据库中又是特殊字符，所以base64有各种变种
 * 3、变种之一，是url安全的base64。采用的是 52个字母10个数字和-_两个字符
 * 4、标准的base64规定每76个字符需要加一个换行，但是有些库不遵循这个标准，比如jdk的实现，apache-commons-codec实现。
 * 5、由于以上原因，建议在加解密的时候不要用base64，用hex。
 */
String encodeToString(byte[] src, chars=('A'..'Z')+('a'..'z')+('0'..'9')+['+', '/']){
    def srcBytes = src as List
    //下标->字节数组被3除之后余数，值->应该填充几个字节
    def pad = [0,2,1]
    //应该填充几个字节
    def padNum = pad[srcBytes.size()%3]
    //填充字节
    (0..padNum).each {
        srcBytes<<0
    }
    //结果
    def encodedChars = []
    //源字节数组每3个字节一组，转换成4个字节
    int groupNum = srcBytes.size()/3
    //处理源字节数组，除了最后一组，最后一组有可能有填充，需要特殊处理
    (0..<groupNum - 1).each {
        int group->
            //收集该组3个字节的所有比特
            int bits = ((srcBytes[group*3]&0xff)<<16) | ((srcBytes[group*3+1]&0xff)<<8) | (srcBytes[group*3+2]&0xff)
            encodedChars << chars[(bits>>>18)]
            encodedChars << chars[(bits>>>12)&0x3f]
            encodedChars << chars[(bits>>>6)&0x3f]
            encodedChars << chars[bits&0x3f]
    }
    //处理最后一组
    int group = groupNum - 1
    int bits = ((srcBytes[group*3]&0xff)<<16) | ((srcBytes[group*3+1]&0xff)<<8) | (srcBytes[group*3+2]&0xff)
    encodedChars << chars[(bits>>>18)]
    encodedChars << chars[(bits>>>12)&0x3f]
    if(padNum==0){
        encodedChars << chars[(bits>>>6)&0x3f]
        encodedChars << chars[bits&0x3f]
    }else if(padNum==1){
        encodedChars << chars[(bits>>>6)&0x3f]
        encodedChars << '='
    }else{
        encodedChars << '='
        encodedChars << '='
    }
    encodedChars.join('')
}

def text = """\
ES加密/解密
 
DES加密/解密
 
RC4加密/解密
 
Rabbit加密/解密
 
TripleDes加密/解密
 
MD5加/解密
 
Base64加/解密
 
Hash加/解密
 
"""
//text = "a"
@Field def chars = ('A'..'Z')+('a'..'z')+('0'..'9')+['-', '_']
println encodeToString(text.bytes)
println URLEncoder.encode(encodeToString(text.bytes),"utf-8")

println Base64.encoder.encodeToString(text.bytes)

def decodeFromString(String base64,chars=('A'..'Z')+('a'..'z')+('0'..'9')+['+', '/']){
    base64 = base64.replaceAll("(%2B)|-","+").replaceAll("(%2F)|_","/")
    def bytes = [:]
    chars.eachWithIndex { Object entry, int i ->
        bytes[entry] = i
    }
    def srcBytes = base64.collect{
        bytes[it]
    }
    def decodedBytes = []
    def groupNum = srcBytes.size()/4
    (0..<groupNum-1).each{
        int group->
        int bits = (srcBytes[group*4]<<18) | (srcBytes[group*4+1]<<12) | (srcBytes[group*4+2]<<6) | srcBytes[group*4+3]
        decodedBytes << (bits>>>16 & 0xff)
        decodedBytes << (bits>>>8 & 0xff)
        decodedBytes << (bits & 0xff)
    }
    def tail2 = base64[-2]
    def tail1 = base64[-1]
    int group = srcBytes.size()/4-1
    if(tail2 != '=' && tail1 != '='){
        int bits = srcBytes[group*4]<<18 | (srcBytes[group*4+1]<<12) | (srcBytes[group*4+2]<<6) | srcBytes[group*4+3]
        decodedBytes << (bits>>>16 & 0xff)
        decodedBytes << (bits>>>8 & 0xff)
        decodedBytes << (bits & 0xff)
    }else if(tail2 != '=' && tail1 == '='){
        int bits = srcBytes[group*4]<<18 | (srcBytes[group*4+1]<<12) | (srcBytes[group*4+2]<<6)
        decodedBytes << (bits>>>16 & 0xff)
        decodedBytes << (bits>>>8 & 0xff)
    }else{
        int bits = srcBytes[group*4]<<18 | (srcBytes[group*4+1]<<12)
        decodedBytes << (bits>>>16 & 0xff)
    }
    decodedBytes as byte[]
}

println new String(decodeFromString(encodeToString(text.bytes,chars)))