def text = "IApERVPliqDlr4Yv6Kej5a+GCiAKUkM05Yqg5a+GL+ino+WvhgogClJhYmJpdOWKoOWvhi/op6Plr4YKIApUcmlwbGVEZXPliqDlr4Yv6Kej5a+GCiAK"
/**
 *
 * @param text
 * @return
 */
def isBase64(String text){
    if(!text){
        return false
    }
    //RFC2045里面，每76字符需要加一个换行。
    text = text.replaceAll('\n','')
    return text.matches(~/([a-zA-Z0-9\+\/]{4})+|([a-zA-Z0-9\+\/]{4})*[a-zA-Z0-9\+\/]{3}=|([a-zA-Z0-9\+\/]{4})*[a-zA-Z0-9\+\/]{2}==/)
}
def isBase64URL(String text){
    if(!text){
        return false
    }
    //RFC2045里面，每76字符需要加一个换行。
    text = text.replaceAll('\n','')
    return text.matches(~/([a-zA-Z0-9\-_]{4})+|([a-zA-Z0-9\-_]{4})*[a-zA-Z0-9\-_]{3}=|([a-zA-Z0-9\-_]{4})*[a-zA-Z0-9\-_]{2}==/)
}

println isBase64(text)
println isBase64URL(text)

text = new String(Base64.decoder.decode(text),"utf-8")
println text