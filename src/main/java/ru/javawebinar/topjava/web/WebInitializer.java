package ru.javawebinar.topjava.web;

import org.springframework.web.WebApplicationInitializer;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

public class WebInitializer implements WebApplicationInitializer {
    @Override
    public void onStartup(ServletContext servletContext) throws ServletException {
        String path = servletContext.getContextPath();
        servletContext.getSessionCookieConfig()
                .setPath(new ExternalEnvironment().getUrlPrefix() + path);
    }
}
