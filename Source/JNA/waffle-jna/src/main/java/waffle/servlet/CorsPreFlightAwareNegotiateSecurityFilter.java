package waffle.servlet;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import waffle.util.CorsPreFlightHeaders;

public class CorsPreFlightAwareNegotiateSecurityFilter extends NegotiateSecurityFilter implements Filter {
    /** The Constant LOGGER. */
    private static final Logger LOGGER = LoggerFactory.getLogger(NegotiateSecurityFilter.class);

    /**
     * Instantiates a new negotiate security filter.
     */
    public CorsPreFlightAwareNegotiateSecurityFilter() {
        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] loaded");
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        super.init(filterConfig);
        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Loaded");

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Filtering");
        
        final HttpServletRequest sreq = (HttpServletRequest) request;

        if (isPreFlightRequest(sreq)) {
            CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Authentication Skipped");
            chain.doFilter(request, response);
        } else {
            super.doFilter(request, response, chain);
                    CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] Authentication Completed");
        }
    }

    @Override
    public void destroy() {
        super.destroy();
        CorsPreFlightAwareNegotiateSecurityFilter.LOGGER
                .debug("[waffle.servlet.CorsPreFlightAwareNegotiateSecurityFilter] unloaded");

    }

    private boolean isPreFlightRequest(HttpServletRequest request) {

        final String preflightAttributeValue = "PRE_FLIGHT";
        final String corsRequestType = (String) request.getAttribute("cors.request.type");

        // it has to be an OPTIONS Method to be a PreFlight Request
        if (!request.getMethod().equalsIgnoreCase("OPTIONS")) {
            return false;
        }
        // let an HttpServletFilter already add the Attribute "PRE_FLIGHT" for the value cors.request.type
        if (corsRequestType != null && corsRequestType.equalsIgnoreCase(preflightAttributeValue)) {
            return true;
        } else {
            return CorsPreFlightHeaders.containsAllPreFlightHeaders(request);
        }
    }
}
