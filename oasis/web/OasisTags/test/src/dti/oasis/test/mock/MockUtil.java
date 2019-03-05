package dti.oasis.test.mock;

import dti.oasis.app.ApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   2/27/2018
 *
 * @author kshen
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class MockUtil {
    private final static String DEFAULT_OASIS_TEST_USER_PROPERTY = "oasis.test.user";

    public static HttpServletRequest mockRequest() {
        String userId = ApplicationContext.getInstance().getProperty(DEFAULT_OASIS_TEST_USER_PROPERTY);
        return mockRequest(userId);
    }

    public static HttpServletRequest mockRequest(String userId) {
        Map<String, Object> sessionMap = new HashMap<>();
        HttpSession session = mock(HttpSession.class);

        HttpServletRequest request = mock(HttpServletRequest.class);
        Principal principal = mock(Principal.class);

        // Session getAttribute/setAttribute stub
        doAnswer(
                invocation -> sessionMap.get((String) invocation.getArgument(0))
        ).when(session).getAttribute(anyString());

        doAnswer(
                invocation -> sessionMap.put(invocation.getArgument(0),
                        invocation.getArgument(1))
        ).when(session).setAttribute(anyString(), any());

        // Mock session Id.
        when(session.getId()).thenReturn(UUID.randomUUID().toString());

        // Mock request methods
        when(request.getRemoteUser()).thenReturn(userId);
        when(request.getUserPrincipal()).thenReturn(principal);
        when(request.getSession()).thenReturn(session);
        // TODO Replace with another URI?
        when(request.getRequestURI()).thenReturn("<DUMMY_URI>");

        // Mock principal methods.
        when(principal.getName()).thenReturn(userId);

        return request;
    }
}
