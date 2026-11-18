package zg.acelera.msnucleo

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.data.web.config.EnableSpringDataWebSupport

@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@SpringBootApplication
class MsNucleoApplication {

    static void main(String[] args) {
        SpringApplication.run(MsNucleoApplication, args)
    }

}
