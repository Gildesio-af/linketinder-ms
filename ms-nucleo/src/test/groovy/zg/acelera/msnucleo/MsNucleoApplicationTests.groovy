package zg.acelera.msnucleo

import org.springframework.boot.test.context.SpringBootTest
import spock.lang.Specification

@SpringBootTest
class MsNucleoApplicationTests extends Specification {
    void "application context loads"() {
        expect:
        true
    }
}
