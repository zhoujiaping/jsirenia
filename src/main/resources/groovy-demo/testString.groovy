import java.util.regex.Matcher

println 'hello world'

println "hello world"

println '"hello world"'

println "'hello world'"

def foo = "FOO"
println '$foo'

println "$foo"

println "{->$foo}"

println """
    $foo
"""
println(/hello/)
def str = /\d+/
def pattern = ~/\d+/
def matcher = "12abc34def56" =~ /\d/
println str
println pattern.matcher("12345")
def res = matcher.collect()
println res

println "hello world" - "world"

println "hello world!" * 3

println "hello world!"[-3..-1]
println "hello world!"[-3,-1]
