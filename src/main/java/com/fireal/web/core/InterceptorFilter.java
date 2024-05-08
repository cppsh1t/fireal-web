package com.fireal.web.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import com.fireal.web.security.FilterBlock;
import com.fireal.web.security.FilterBlockHolder;

import fireal.core.Container;
import fireal.exception.BeanNotFoundException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InterceptorFilter extends HttpFilter {

    private final Set<FilterBlock> filterBlocks = new HashSet<>();

    @Override
    public void init() throws ServletException {
        super.init();
        Container container = WebInitializer.getContainer();
        try {
            FilterBlockHolder filterBlockHolder = container.getBean(FilterBlockHolder.class);
            filterBlocks.addAll(filterBlockHolder.getFilterBlocks());
        } catch (BeanNotFoundException e) {
            //nohting
        }
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        String url = DispatcherServlet.getMappingUrl(req);
        for(FilterBlock filterBlock : filterBlocks) {
            if (!filterBlock.checkUrl(req, url)) return;
        }
        chain.doFilter(req, res);
    }

}
