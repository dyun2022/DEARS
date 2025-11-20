package com.example.dears;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import android.os.Build;

import com.example.dears.data.api.InterfaceAPI;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;

import java.lang.reflect.Method;
import java.time.LocalDate;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import retrofit2.Response;

@RunWith(RobolectricTestRunner.class)
@Config(sdk = Build.VERSION_CODES.R, manifest = Config.NONE)
public class UserSetupWhiteBoxTest {

    private RegisterActivity activity;

    @Before
    public void setUp() {
        activity = new RegisterActivity();
    }

    private LocalDate callParseBirthday(String s) throws Exception {
        Method m = RegisterActivity.class
                .getDeclaredMethod("parseBirthdayFlexible", String.class);
        m.setAccessible(true);
        return (LocalDate) m.invoke(activity, s);
    }

    private String callExtractError(Response<?> resp) throws Exception {
        Method m = RegisterActivity.class
                .getDeclaredMethod("extractError", Response.class);
        m.setAccessible(true);
        return (String) m.invoke(activity, resp);
    }

    /**
     * WB1: parseBirthdayFlexible: ISO format "yyyy-MM-dd" is accepted.
     */
    @Test
    public void parseBirthday_isoFormat_success() throws Exception {
        LocalDate d = callParseBirthday("2024-11-19");
        assertEquals(LocalDate.of(2024, 11, 19), d);
    }

    /**
     * WB2: parseBirthdayFlexible: US format "M/d/yyyy" is accepted.
     */
    @Test
    public void parseBirthday_usSingleDigitMonthDay_success() throws Exception {
        LocalDate d = callParseBirthday("1/2/2020");
        assertEquals(LocalDate.of(2020, 1, 2), d);
    }

    /**
     * WB3: parseBirthdayFlexible: invalid format returns null.
     */
    @Test
    public void parseBirthday_invalidFormat_returnsNull() throws Exception {
        LocalDate d = callParseBirthday("02-01-2020");
        assertNull(d);
    }

    /**
     * WB4: extractError: when errorBody is present, its string is returned.
     */
    @Test
    public void extractError_withErrorBody_returnsBodyString() throws Exception {
        MediaType mediaType = MediaType.parse("text/plain");
        ResponseBody body = ResponseBody.create("Bad request", mediaType);
        Response<?> resp = Response.error(400, body);

        String msg = callExtractError(resp);
        assertEquals("Bad request", msg);
    }

    /**
     * WB5: extractError: when errorBody is null, returns "HTTP <code>".
     */
    @Test
    public void extractError_withoutErrorBody_returnsHttpCode() throws Exception {
        Response<String> resp = Response.success("OK");

        String msg = callExtractError(resp);
        assertEquals("HTTP 200", msg);
    }
}
