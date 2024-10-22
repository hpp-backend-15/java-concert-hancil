package io.hhplus.javaconcerthancil.interfaces.api.interceptor;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;

import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
public class PathContainer {

    private final PathMatcher pathMatcher = new AntPathMatcher();
    private final List<RequestPath> includePathPattern = new ArrayList<>();
    private final List<RequestPath> excludePathPattern = new ArrayList<>();;


    public boolean notIncludedPath(String targetPath, String pathMethod) {
        boolean excludePattern = excludePathPattern.stream()
                .anyMatch(requestPath -> anyMatchPathPattern(targetPath, pathMethod, requestPath));

        boolean includePattern = includePathPattern.stream()
                .anyMatch(requestPath -> anyMatchPathPattern(targetPath, pathMethod, requestPath));

        return excludePattern || !includePattern;
    }

    private boolean anyMatchPathPattern(String targetPath, String pathMethod, RequestPath requestPath) {
        return pathMatcher.match(requestPath.getPathPattern(), targetPath) &&
                requestPath.matchesMethod(pathMethod);
    }

    public void includePathPattern(String targetPath, PathMethod pathMethod) {
        this.includePathPattern.add(new RequestPath(targetPath, pathMethod));
    }

    public void excludePathPattern(String targetPath, PathMethod pathMethod) {
        this.excludePathPattern.add(new RequestPath(targetPath, pathMethod));
    }
}