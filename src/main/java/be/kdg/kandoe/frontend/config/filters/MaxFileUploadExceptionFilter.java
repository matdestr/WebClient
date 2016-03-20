package be.kdg.kandoe.frontend.config.filters;

import org.springframework.web.filter.DelegatingFilterProxy;

/**
 * Filter for catching the MaxFileUploadException
 */

public class MaxFileUploadExceptionFilter extends DelegatingFilterProxy {
}
