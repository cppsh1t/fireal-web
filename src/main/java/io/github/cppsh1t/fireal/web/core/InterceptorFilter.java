package io.github.cppsh1t.fireal.web.core;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import io.github.cppsh1t.fireal.web.security.Authenticator;
import io.github.cppsh1t.fireal.web.security.FilterBlock;
import io.github.cppsh1t.fireal.web.security.FilterBlockHolder;

import io.github.cppsh1t.fireal.core.Container;
import io.github.cppsh1t.fireal.exception.BeanNotFoundException;
import io.github.cppsh1t.fireal.web.security.UserDetail;
import io.github.cppsh1t.fireal.web.util.DebugUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class InterceptorFilter extends HttpFilter {

    private final Set<FilterBlock> filterBlocks = new HashSet<>();
    private Authenticator authenticator = null;

    @Override
    public void init() throws ServletException {
        super.init();
        Container container = WebInitializer.getContainer();
        try {
            FilterBlockHolder filterBlockHolder = container.getBean(FilterBlockHolder.class);
            filterBlocks.addAll(filterBlockHolder.getFilterBlocks());
            authenticator = container.getBean(Authenticator.class);
        } catch (BeanNotFoundException e) {
            //nothing
        }
    }

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        if (authenticator != null)  {
            String url = DispatcherServlet.getMappingUrl(req);
            UserDetail userDetail = authenticator.resolve(req);
            for(FilterBlock filterBlock : filterBlocks) {
                if (!filterBlock.checkAuth(url, userDetail)) return;
            }
        }
        chain.doFilter(req, res);
    }

}
