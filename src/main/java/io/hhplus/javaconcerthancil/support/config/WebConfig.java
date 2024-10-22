package io.hhplus.javaconcerthancil.support.config;

import io.hhplus.javaconcerthancil.interfaces.api.interceptor.PathMatcherInterceptor;
import io.hhplus.javaconcerthancil.interfaces.api.interceptor.PathMethod;
import io.hhplus.javaconcerthancil.interfaces.api.interceptor.WaitingQueueInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final WaitingQueueInterceptor waitingQueueInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor());
    }

    private HandlerInterceptor authInterceptor() {
        PathMatcherInterceptor interceptor = new PathMatcherInterceptor(waitingQueueInterceptor);

        return interceptor
                .includePathPattern("/v1/concert**", PathMethod.GET)
                .includePathPattern("/v1/reservation/**", PathMethod.POST)
                .includePathPattern("/v1/queue/**", PathMethod.POST)
                .excludePathPattern("/**", PathMethod.OPTIONS)
                ;
    }

}
