package dti.oasis.error;

import dti.oasis.messagemgr.MessageManager;
import dti.oasis.test.annotations.OasisParameterizedTest;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.util.LogUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import java.sql.SQLException;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   4/24/2018
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

public class DBExceptionTest {
    private final Logger l = LogUtils.getLogger(getClass());

    @Mock
    private MessageManager messageManager;

    @Spy
    private ExceptionHelper exceptionHelper = ExceptionHelper.getInstance();

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.initMocks(this);
        doReturn(messageManager).when(exceptionHelper).getMessageManager();
    }

    @OasisParameterizedTest("testHandleUserDefinedDBException")
    void testHandleUserDefinedDBException(
            @OasisTestParameter("sqlErrorMessage") String sqlErrorMessage,
            @OasisTestParameter("convertedErrorMessage") String convertedErrorMessage) {
        // Assert that UserDefinedDBException is thrown.
        assertThrows(UserDefinedDBException.class, () -> {
            try {
                someMethodThrowMockSQLException(sqlErrorMessage);
            } catch (SQLException e) {
                throw exceptionHelper.handleSQLException("Test get user defined exception", e);
            }
        });

        // Verify that error message is added to message manager.
        verify(messageManager, times(1)).addErrorMessage(
                eq(ExceptionHelper.GENERIC_DB_ERROR_MESSAGE_KEY),
                argThat((ArgumentMatcher<Object[]>) argument -> {
                    String message = (String) argument[0];
                    return message.contains(convertedErrorMessage);
                })
        );
    }

    @OasisParameterizedTest("testHandleUnexpectedDBException")
    void testHandleUnexpectedDBException(
            @OasisTestParameter("sqlErrorMessage") String sqlErrorMessage,
            @OasisTestParameter("convertedErrorMessage") String convertedErrorMessage) {
        // Assert that UnexpectedDBException is thrown.
        assertThrows(UnexpectedDBException.class, () -> {
            try {
                someMethodThrowMockSQLException(sqlErrorMessage);
            } catch (SQLException e) {
                throw exceptionHelper.handleSQLException("Test get unexpected exception", e);
            }
        });

        // Verify that error message is added to message manager.
        verify(messageManager, times(1)).addErrorMessage(
                eq(ExceptionHelper.GENERIC_DB_ERROR_MESSAGE_KEY),
                argThat((ArgumentMatcher<Object[]>) argument -> {
                    String message = (String) argument[0];
                    return message.contains(convertedErrorMessage);
                })
        );

    }

    private void someMethodThrowMockSQLException(String errorMessage) throws SQLException {
        SQLException e = mock(SQLException.class);
        doReturn(errorMessage).when(e).getMessage();

        throw e;
    }
}
