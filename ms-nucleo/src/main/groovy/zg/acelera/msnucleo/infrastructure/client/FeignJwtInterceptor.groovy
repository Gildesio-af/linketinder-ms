package zg.acelera.msnucleo.infrastructure.client

import feign.RequestInterceptor
import feign.RequestTemplate
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes

@Component
class FeignJwtInterceptor implements RequestInterceptor {

    @Override
    void apply(RequestTemplate template) {
        ServletRequestAttributes attributes = RequestContextHolder.getRequestAttributes() as ServletRequestAttributes
        if (attributes != null) {
            String authHeader = attributes.request.getHeader("Authorization")
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                template.header("Authorization", authHeader)
            }
        }
    }
}