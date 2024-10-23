package io.hhplus.javaconcerthancil.interfaces.api.interceptor;

import jakarta.annotation.Nullable;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

public class PathMatcherInterceptor implements HandlerInterceptor {

    private final HandlerInterceptor handlerInterceptor;
    private final PathContainer pathContainer;

    public PathMatcherInterceptor(HandlerInterceptor handlerInterceptor) {
        this.handlerInterceptor = handlerInterceptor;
        this.pathContainer = new PathContainer();
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {

        if (pathContainer.notIncludedPath(request.getServletPath(), request.getMethod())) {
            return true;
        }

        return handlerInterceptor.preHandle(request, response, handler);
    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler,
                           @Nullable ModelAndView modelAndView) throws Exception {
        if (pathContainer.notIncludedPath(request.getServletPath(), request.getMethod())) {
            return;
        }

        handlerInterceptor.postHandle(request, response, handler,modelAndView);
    }

    public PathMatcherInterceptor includePathPattern(String pathPattern, PathMethod pathMethod) {
        pathContainer.includePathPattern(pathPattern, pathMethod);
        return this;
    }

    public PathMatcherInterceptor excludePathPattern(String pathPattern, PathMethod pathMethod) {
        pathContainer.excludePathPattern(pathPattern, pathMethod);
        return this;
    }
}