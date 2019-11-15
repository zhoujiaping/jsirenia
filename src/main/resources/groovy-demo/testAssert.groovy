def foo(a,b){
    a+b
}
def bar = {
    a,b->
        a*b
}

assert foo(2,3)+2 == bar(1, 6)
/*
Caught: Assertion failed:

assert foo(2,3) == bar(1, 6)
       |        |  |
       5        |  6
                false

Assertion failed:

assert foo(2,3) == bar(1, 6)
       |        |  |
       5        |  6
                false

	at testAssert.run(testAssert.groovy:9)
 */