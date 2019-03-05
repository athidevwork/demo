package dti.oasis.test.junit5.provider;

import com.jayway.jsonpath.JsonPath;
import dti.oasis.recordset.Record;
import dti.oasis.test.annotations.OasisTestParameter;
import dti.oasis.test.junit5.provider.impl.ArgumentsConfigurationLoader;
import dti.oasis.test.junit5.provider.impl.ArgumentsResolver;
import dti.oasis.util.LogUtils;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;

import java.lang.reflect.Parameter;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   3/7/2018
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
public class OasisArgumentsProvider implements ArgumentsProvider {
    private final Logger l = LogUtils.getLogger(getClass());

    /**
     * Provide a {@link Stream} of {@link Arguments} to be passed to a
     * {@code @ParameterizedTest} method.
     *
     * @param context the current extension context; never {@code null}
     * @return a stream of arguments; never {@code null}
     */
    @Override
    public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
        return Stream.of(ArgumentsResolver.getInstance().resolve(context));
    }
}
