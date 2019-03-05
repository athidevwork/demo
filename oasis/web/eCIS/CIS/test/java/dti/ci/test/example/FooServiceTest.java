package dti.ci.test.example;

import dti.oasis.recordset.Record;
import dti.oasis.recordset.RecordFilter;
import dti.oasis.recordset.RecordSet;
import dti.oasis.test.annotations.OasisAutoWired;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.app.ApplicationContextHelper;
import dti.oasis.test.junit5.extension.OasisExtension;
import dti.oasis.test.junit5.provider.OasisArgumentsProvider;
import dti.oasis.test.junit5.tag.TestTags;
import dti.oasis.util.DateUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.platform.engine.TestTag;

import java.util.Date;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.mockito.Mockito.*;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/1/2018
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

// Extend with OasisExtension to loading project spring config and init current user.
// See OasisTags/test/config/dti/customApplicationConfig.properties for user and db pool id config.
// See OasisTags/test/config/jndi.properties for config for jdbc data source of weblogic
@ExtendWith({OasisExtension.class})
public class FooServiceTest {
    // Use annotated with OasisAutoWired to get bean from Spring application context.
    @OasisAutoWired
    private FooService fooService;

    // Group tests by tag.
    @Tag(TestTags.INTEGRATION_VALUE)
    // Group tests by tested annotation.
    @Nested
    // We can use DisplayName annotation to display more detailed information of tests.
    @DisplayName("Integration Tests - Custom Display Name")
    class IntegrationTest {
        @Test
        void testFoo() {
            assertEquals("Foo from DAO", fooService.foo());
        }

        @Test
        void testFoo_fromMessageManager() {
            assertEquals("Foo from message manager", fooService.fooFromMessageManager());
        }

        @ParameterizedTest
        @ArgumentsSource(OasisArgumentsProvider.class)
        void testWithParameters(@OasisTestParameter("testWithParameters.messageFromDao") String messageFromDao,
                                @OasisTestParameter("testWithParameters.intValue") int intValue,
                                @OasisTestParameter("testWithParameters.intObject") Integer intObject,
                                @OasisTestParameter("testWithParameters.longValue") long longValue,
                                @OasisTestParameter("testWithParameters.doubleValue") double doubleValue,
                                @OasisTestParameter("testWithParameters.floatValue") float floatValue,
                                @OasisTestParameter("testWithParameters.booleanValue") boolean booleanValue,
                                @OasisTestParameter("testWithParameters.dateValue") Date dateValue,
                                @OasisTestParameter("testWithParameters.record") Record record,
                                @OasisTestParameter("testWithParameters.recordSet") RecordSet rs) {
            assertAll(
                    () -> assertEquals(messageFromDao, fooService.foo()),
                    () -> assertEquals(100, intValue),
                    () -> assertEquals(Integer.valueOf(200), intObject),
                    () -> assertEquals(300000000000L, longValue),
                    () -> assertEquals(1234567890.1234, doubleValue),
                    () -> assertEquals(123456, Math.round(floatValue)),
                    () -> assertEquals(true, booleanValue),
                    () -> assertEquals(DateUtils.parseDate("06/01/2018"), dateValue),
                    () -> {
                        assertNotNull(record);
                        assertEquals("Y", record.getUpdateIndicator());
                        assertEquals("Jordan", record.getStringValue("lastName", ""));
                        assertEquals("Michael", record.getStringValue("firstName", ""));
                    },
                    () -> {
                        assertEquals(2, rs.getSize());

                        assertThat(rs.getSubSet(new RecordFilter("firstName", "Michael"))
                                        .getSubSet(new RecordFilter("lastName", "Jordan"))
                                        .getSize(),
                                greaterThan(0));

                        assertThat(rs.getSubSet(new RecordFilter("firstName", "Kobe"))
                                        .getSubSet(new RecordFilter("lastName", "Bryant"))
                                        .getSize(),
                                greaterThan(0));
                    }
            );
        }

        @ParameterizedTest
        @ArgumentsSource(OasisArgumentsProvider.class)
        void testWithParameters_record(@OasisTestParameter("testWithParameters_record.inputRecord") Record inputRecord) {
            assertEquals(inputRecord.getStringValue("message", ""), fooService.foo());
        }

        @ParameterizedTest
        @ArgumentsSource(OasisArgumentsProvider.class)
        void testFooBar(@OasisTestParameter("testFooBar.foo") String foo, @OasisTestParameter("testFooBar.bar") String bar) {
            String environmentName = ApplicationContextHelper.getInstance().getEnvironmentName();

            assumeTrue(environmentName.equals(""));
            assertEquals("foo", foo);
            assertEquals("bar", bar);
        }

        @ParameterizedTest
        @ArgumentsSource(OasisArgumentsProvider.class)
        void testFooBar_us(@OasisTestParameter("testFooBar.foo") String foo, @OasisTestParameter("testFooBar.bar") String bar) {
            String environmentName = ApplicationContextHelper.getInstance().getEnvironmentName();

            assumeTrue(environmentName.equals("US"));
            assertEquals("fooUS", foo);
            assertEquals("barUS", bar);
        }
    }

    @Tag(TestTags.MOCK_VALUE)
    @Nested
    class MockTest {
        @Test
        void testFoo() {
            // Create a FooDAO mock object.
            FooDAO mockedFooDAO = mock(FooDAO.class);
            // Record the behavior of Mock Object.
            when(mockedFooDAO.foo()).thenReturn("Foo from mock DAO");

            // Create spy on foo service.
            FooServiceImpl spiedFooService = spy((FooServiceImpl) fooService);
            // Return mocked FooDAO when getFooDAO called.
            when(spiedFooService.getFooDAO()).thenReturn(mockedFooDAO);

            // Run test codes.
            assertEquals("Foo from mock DAO", spiedFooService.foo());

            // Verify spied method spiedFooService.foo() is called once.
            verify(spiedFooService, times(1)).foo();
            // Verify mocked mockedFooDAO.foo() called once.
            verify(mockedFooDAO, times(1)).foo();
        }

        @Test
        void testFoo_fromMessageManager() {
            // We cannot mock FooMessageProvider.getInstance directly since it's a static method.
            // Power mock has the ability to mock on static method. But it may has dependencies on internal
            // implementations of some libs. And it's not recommend to use in communities.
            // We will alternatively mock a mock a getter method. See FooServiceImpl and FooMessageProvider for the details.
            FooMessageProvider mockedMessage = mock(FooMessageProvider.class);
            when(mockedMessage.foo()).thenReturn("Foo form mocked message provider");

            FooServiceImpl spiedFooService = spy((FooServiceImpl) fooService);
            when(spiedFooService.getFooMessageProvider()).thenReturn(mockedMessage);

            assertEquals("Foo form mocked message provider", spiedFooService.fooFromMessageManager());

            // Verify spied method spiedFooService.getFooMessageProvider() is called once.
            verify(spiedFooService, times(1)).getFooMessageProvider();
            // Verify mocked mockedMessage.foo() called once.
            verify(mockedMessage, times(1)).foo();
        }
    }
}
