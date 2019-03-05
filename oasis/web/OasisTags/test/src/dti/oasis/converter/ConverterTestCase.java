package dti.oasis.converter;

import dti.oasis.test.TestCase;
import dti.oasis.util.DateUtils;
import dti.oasis.busobjs.YesNoFlag;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;

/**
 * <p>(C) 2003 Delphi Technology, inc. (dti)</p>
 * Date:   Oct 17, 2006
 *
 * @author wreeder
 */
/*
 *
 * Revision Date    Revised By  Description
 * ---------------------------------------------------
 *
 * ---------------------------------------------------
 */
public class ConverterTestCase extends TestCase {
    public ConverterTestCase(String string) {
        super(string);
    }

    protected void setUp() throws Exception {
        super.setUp();
    }

    public void testStringConverter() {

        String test = "test";

        // Test String conversion
        Converter converter = ConverterFactory.getInstance().getConverter(String.class);
        assertEquals(test, converter.convert(String.class, test));

        // Test Boolean conversion
        String Y = "Y";
        String N = "N";
        assertEquals(Y, converter.convert(String.class, Boolean.TRUE));
        assertEquals(N, converter.convert(String.class, Boolean.FALSE));

        // Test Object conversion
        Double four = new Double(4.0);
        assertEquals("4.0", converter.convert(String.class, four));

        // Test null conversion
        assertEquals(test, converter.convert(String.class, null, test));
    }

    public void testByteConverter() {

        Byte four = new Byte("4");

        // Test Byte conversion
        Converter converter = ConverterFactory.getInstance().getConverter(Byte.class);
        assertEquals(four, converter.convert(Byte.class, four));

        // Test String conversion
        assertEquals(four, converter.convert(Byte.class, "4"));

        // Test Float conversion
        assertEquals(four, converter.convert(Byte.class, new Float(4)));

        // Test Short conversion
        assertEquals(four, converter.convert(Byte.class, new Short("4")));

        // Test Integer conversion
        assertEquals(four, converter.convert(Byte.class, new Integer(4)));

        // Test Long conversion
        assertEquals(four, converter.convert(Byte.class, new Long(4)));

        // Test Byte conversion
        assertEquals(four, converter.convert(Byte.class, new Byte("4")));

        // Test null conversion
        assertEquals(four, converter.convert(Byte.class, null, four));

        // Test Byte conversion
        converter = ConverterFactory.getInstance().getConverter(Byte.TYPE);
        assertEquals(four, converter.convert(Byte.TYPE, four));

        // Test reflection with Byte conversion
        Method[] methods = Byte.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            int modifiers = method.getModifiers();
            if (method.getName().equals("toString") && Modifier.isStatic(modifiers)) {
                Class[] paramTypes = method.getParameterTypes();
                Class ByteParamType = paramTypes[0];

                assertEquals(ByteParamType, Byte.TYPE);

                try {
                    converter = ConverterFactory.getInstance().getConverter(ByteParamType);
                    assertEquals("4", method.invoke(Byte.TYPE, new Object[]{converter.convert(ByteParamType, "4")}));
                } catch (Exception e) {
                    fail(e);
                }
            }
        }

    }

    public void testShortConverter() {

        Short four = new Short("4");

        // Test Short conversion
        Converter converter = ConverterFactory.getInstance().getConverter(Short.class);
        assertEquals(four, converter.convert(Short.class, four));

        // Test String conversion
        assertEquals(four, converter.convert(Short.class, "4"));

        // Test Float conversion
        assertEquals(four, converter.convert(Short.class, new Float(4)));

        // Test Short conversion
        assertEquals(four, converter.convert(Short.class, new Short("4")));

        // Test Integer conversion
        assertEquals(four, converter.convert(Short.class, new Integer(4)));

        // Test Long conversion
        assertEquals(four, converter.convert(Short.class, new Long(4)));

        // Test Byte conversion
        assertEquals(four, converter.convert(Short.class, new Byte("4")));

        // Test null conversion
        assertEquals(four, converter.convert(Short.class, null, four));

        // Test Short conversion
        converter = ConverterFactory.getInstance().getConverter(Short.TYPE);
        assertEquals(four, converter.convert(Short.TYPE, four));

        // Test reflection with Short conversion
        Method[] methods = Short.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            int modifiers = method.getModifiers();
            if (method.getName().equals("toString") && Modifier.isStatic(modifiers)) {
                Class[] paramTypes = method.getParameterTypes();
                Class ShortParamType = paramTypes[0];

                assertEquals(ShortParamType, Short.TYPE);

                try {
                    converter = ConverterFactory.getInstance().getConverter(ShortParamType);
                    assertEquals("4", method.invoke(Short.TYPE, new Object[]{converter.convert(ShortParamType, "4")}));
                } catch (Exception e) {
                    fail(e);
                }
            }
        }

    }

    public void testIntegerConverter() {

        Integer four = new Integer(4);

        // Test Integer conversion
        Converter converter = ConverterFactory.getInstance().getConverter(Integer.class);
        assertEquals(four, converter.convert(Integer.class, four));

        // Test String conversion
        assertEquals(four, converter.convert(Integer.class, "4"));

        // Test Float conversion
        assertEquals(four, converter.convert(Integer.class, new Float(4)));

        // Test Short conversion
        assertEquals(four, converter.convert(Integer.class, new Short("4")));

        // Test Integer conversion
        assertEquals(four, converter.convert(Integer.class, new Integer(4)));

        // Test Long conversion
        assertEquals(four, converter.convert(Integer.class, new Long(4)));

        // Test Byte conversion
        assertEquals(four, converter.convert(Integer.class, new Byte("4")));

        // Test null conversion
        assertEquals(four, converter.convert(Integer.class, null, four));

        // Test Integer conversion
        converter = ConverterFactory.getInstance().getConverter(Integer.TYPE);
        assertEquals(four, converter.convert(Integer.TYPE, four));

        // Test reflection with Integer conversion
        Method[] methods = Integer.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            int modifiers = method.getModifiers();
            if (method.getName().equals("toString") && Modifier.isStatic(modifiers) && method.getParameterTypes().length == 1)
            {
                Class[] paramTypes = method.getParameterTypes();
                Class IntegerParamType = paramTypes[0];

                assertEquals(IntegerParamType, Integer.TYPE);

                try {
                    converter = ConverterFactory.getInstance().getConverter(IntegerParamType);
                    assertEquals("4", method.invoke(Integer.TYPE, new Object[]{converter.convert(IntegerParamType, "4")}));
                } catch (Exception e) {
                    fail(e);
                }
            }
        }

    }

    public void testLongConverter() {

        Long four = new Long(4);

        // Test Long conversion
        Converter converter = ConverterFactory.getInstance().getConverter(Long.class);
        assertEquals(four, converter.convert(Long.class, four));

        // Test String conversion
        assertEquals(four, converter.convert(Long.class, "4"));

        // Test Float conversion
        assertEquals(four, converter.convert(Long.class, new Float(4)));

        // Test Short conversion
        assertEquals(four, converter.convert(Long.class, new Short("4")));

        // Test Long conversion
        assertEquals(four, converter.convert(Long.class, new Long(4)));

        // Test Long conversion
        assertEquals(four, converter.convert(Long.class, new Long(4)));

        // Test Byte conversion
        assertEquals(four, converter.convert(Long.class, new Byte("4")));

        // Test null conversion
        assertEquals(four, converter.convert(Long.class, null, four));

        // Test Long conversion
        converter = ConverterFactory.getInstance().getConverter(Long.TYPE);
        assertEquals(four, converter.convert(Long.TYPE, four));

        // Test reflection with Long conversion
        Method[] methods = Long.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            int modifiers = method.getModifiers();
            if (method.getName().equals("toString") && Modifier.isStatic(modifiers) && method.getParameterTypes().length == 1)
            {
                Class[] paramTypes = method.getParameterTypes();
                Class LongParamType = paramTypes[0];

                assertEquals(LongParamType, Long.TYPE);

                try {
                    converter = ConverterFactory.getInstance().getConverter(LongParamType);
                    assertEquals("4", method.invoke(Long.TYPE, new Object[]{converter.convert(LongParamType, "4")}));
                } catch (Exception e) {
                    fail(e);
                }
            }
        }

    }

    public void testFloatConverter() {

        Float four = new Float(4);

        // Test Float conversion
        Converter converter = ConverterFactory.getInstance().getConverter(Float.class);
        assertEquals(four, converter.convert(Float.class, four));

        // Test String conversion
        assertEquals(four, converter.convert(Float.class, "4"));

        // Test Float conversion
        assertEquals(four, converter.convert(Float.class, new Float(4)));

        // Test Short conversion
        assertEquals(four, converter.convert(Float.class, new Short("4")));

        // Test Integer conversion
        assertEquals(four, converter.convert(Float.class, new Integer(4)));

        // Test Long conversion
        assertEquals(four, converter.convert(Float.class, new Long(4)));

        // Test Byte conversion
        assertEquals(four, converter.convert(Float.class, new Byte("4")));

        // Test null conversion
        assertEquals(four, converter.convert(Float.class, null, four));

        // Test Float conversion
        converter = ConverterFactory.getInstance().getConverter(Float.TYPE);
        assertEquals(four, converter.convert(Float.TYPE, four));

        // Test reflection with Float conversion
        Method[] methods = Float.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            int modifiers = method.getModifiers();
            if (method.getName().equals("toString") && Modifier.isStatic(modifiers)) {
                Class[] paramTypes = method.getParameterTypes();
                Class FloatParamType = paramTypes[0];

                assertEquals(FloatParamType, Float.TYPE);

                try {
                    converter = ConverterFactory.getInstance().getConverter(FloatParamType);
                    assertEquals("4.0", method.invoke(Float.TYPE, new Object[]{converter.convert(FloatParamType, "4")}));
                } catch (Exception e) {
                    fail(e);
                }
            }
        }

    }

    public void testDoubleConverter() {

        Double four = new Double(4);

        // Test Double conversion
        Converter converter = ConverterFactory.getInstance().getConverter(Double.class);
        assertEquals(four, converter.convert(Double.class, four));

        // Test String conversion
        assertEquals(four, converter.convert(Double.class, "4"));

        // Test Float conversion
        assertEquals(four, converter.convert(Double.class, new Float(4)));

        // Test Short conversion
        assertEquals(four, converter.convert(Double.class, new Short("4")));

        // Test Integer conversion
        assertEquals(four, converter.convert(Double.class, new Integer(4)));

        // Test Long conversion
        assertEquals(four, converter.convert(Double.class, new Long(4)));

        // Test Byte conversion
        assertEquals(four, converter.convert(Double.class, new Byte("4")));

        // Test null conversion
        assertEquals(four, converter.convert(Double.class, null, four));

        // Test double conversion
        converter = ConverterFactory.getInstance().getConverter(Double.TYPE);
        assertEquals(four, converter.convert(Double.TYPE, four));

        // Test reflection with double conversion
        Method[] methods = Double.class.getMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            int modifiers = method.getModifiers();
            if (method.getName().equals("toString") && Modifier.isStatic(modifiers)) {
                Class[] paramTypes = method.getParameterTypes();
                Class doubleParamType = paramTypes[0];

                assertEquals(doubleParamType, Double.TYPE);

                try {
                    converter = ConverterFactory.getInstance().getConverter(doubleParamType);
                    assertEquals("4.0", method.invoke(Double.TYPE, new Object[]{converter.convert(doubleParamType, "4")}));
                } catch (Exception e) {
                    fail(e);
                }
            }
        }

    }

    public void testDateConverter() {

        Date janOne = DateUtils.parseDate("01 Jan 2006");
        long janOneMillis = janOne.getTime();

        // Test Date conversion
        Converter converter = ConverterFactory.getInstance().getConverter(Date.class);
        assertEquals(janOne, converter.convert(Date.class, janOne));

        // Test String conversion
        assertEquals(janOne, converter.convert(Date.class, "01/01/2006"));

        // Test Long conversion
        assertEquals(janOne, converter.convert(Date.class, new Long(janOneMillis)));

        // Test null conversion
        assertEquals(janOne, converter.convert(Date.class, null, janOne));

    }

    public void testDefaultConverter() {

        // Test converting to a class with a public constructor that takes a String parameter
        String stringVal1 = "value1";
        TestData testData1 = new TestData(stringVal1);

        Converter converter = ConverterFactory.getInstance().getConverter(TestData.class);
        assertEquals(testData1.toString(), converter.convert(TestData.class, stringVal1).toString());

        // Test converting to a class with a public getInstance factory method that takes a String parameter and returns the target type
        converter = ConverterFactory.getInstance().getConverter(YesNoFlag.class);
        assertEquals(YesNoFlag.Y, converter.convert(YesNoFlag.class, "Y"));
        assertEquals(YesNoFlag.Y, converter.convert(YesNoFlag.class, "yes"));
        assertEquals(YesNoFlag.Y, converter.convert(YesNoFlag.class, "true"));
        assertEquals(YesNoFlag.N, converter.convert(YesNoFlag.class, "N"));
        assertEquals(YesNoFlag.N, converter.convert(YesNoFlag.class, "no"));
        assertEquals(YesNoFlag.N, converter.convert(YesNoFlag.class, "false"));
    }
}
