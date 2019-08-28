package ru.javawebinar.topjava.web;

import org.springframework.stereotype.Component;

@Component
public class ExternalEnvironment {
    private static final String URL_PREFIX_ENV_VAR = "TOPJAVA_URL_PREFIX";

    // Servlet can be running under Nginx (for example) with some prefix name in URL.
    // Used in JSP and so on.
    // For example: if
    //   servlet:  http://localhost:8082/topjava
    //   on nginx: location ^~ /demo/topjava
    //   external: http://example.com/demo/topjava
    //
    // Then function must return "/demo"
    //
    // Pass it by "export TOPJAVA_URL_PREFIX=/demo"
    //
    public String getUrlPrefix() {
        String prefix = System.getenv(URL_PREFIX_ENV_VAR);
        return prefix == null ? "" : prefix;
    }
}
