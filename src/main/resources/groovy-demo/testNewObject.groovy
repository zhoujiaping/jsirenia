import groovy.transform.ToString
import groovy.transform.EqualsAndHashCode

//import groovy.transform.Canonical

//@Canonical(excludes=['execBeginTime'])
import groovy.transform.builder.*

@Builder(builderStrategy=SimpleStrategy)
@ToString
@EqualsAndHashCode
@groovy.util.logging.Slf4j
class Plan{
    Long id
    String name
    Date createTime
    String creator
    Date execBeginTime
    Date execEndTime
    def exec(){
        log.info("every thing is fine!")
    }
}

println new Plan([id:1,name:'a plan',createTime:new Date()])

println new Plan(id:1,name:'a plan',createTime:new Date())

println new Plan().setName("plan b").setId(10086)

new Plan().exec()

println new Plan(id:1,name:'a plan',createTime:new Date(),foo:'bar')