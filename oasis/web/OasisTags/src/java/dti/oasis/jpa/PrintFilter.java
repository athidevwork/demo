package dti.oasis.jpa;

import dti.oasis.util.LogUtils;
import org.apache.commons.io.IOUtils;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ContainerResponseContext;
import javax.ws.rs.container.ContainerResponseFilter;
import java.io.IOException;
import java.util.logging.Logger;

public class PrintFilter implements ContainerResponseFilter, ContainerRequestFilter {

    Logger l = LogUtils.getLogger(getClass());

    @Override
    public void filter(ContainerRequestContext requestContext, ContainerResponseContext responseContext)
            throws IOException {
        System.out.println("Response body: " + responseContext.getEntity());
        l.finer("Response body: " + responseContext.getEntity());
    }

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String string = IOUtils.toString(requestContext.getEntityStream());
        System.out.println("Request body: " + string);
        l.finer("Request body: " + string);
    }


}
