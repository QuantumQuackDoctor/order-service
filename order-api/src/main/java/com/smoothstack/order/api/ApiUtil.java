package com.smoothstack.order.api;

import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public final class ApiUtil {
    private ApiUtil() {
    }

    /**
     * Sets an example response.
     * @param req the native web request
     * @param contentType the content type to be negotiated
     * @param example The example data to be passed in the response
     */
    public static void setExampleResponse(final NativeWebRequest req,
                                          final String contentType,
                                          final String example) {
        try {
            HttpServletResponse res =
                    req.getNativeResponse(HttpServletResponse.class);
            assert res != null;
            res.setCharacterEncoding("UTF-8");
            res.addHeader("Content-Type", contentType);
            res.getWriter().print(example);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
