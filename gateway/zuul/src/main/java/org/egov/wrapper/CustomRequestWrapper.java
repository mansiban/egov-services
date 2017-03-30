package org.egov.wrapper;

import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import org.apache.commons.io.IOUtils;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class CustomRequestWrapper
        extends HttpServletRequestWrapper {

    private String payload;

    public CustomRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        payload = IOUtils.toString(request.getInputStream());
    }

    public String getPayload() {
        return payload;
    }

    public void setPayload(String payload){
        this.payload = payload;
    }

    @Override
    public ServletInputStream getInputStream() {
        return new ServletInputStreamWrapper(payload.getBytes());
    }
}