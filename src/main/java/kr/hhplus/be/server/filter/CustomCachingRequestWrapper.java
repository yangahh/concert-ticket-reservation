package kr.hhplus.be.server.filter;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import org.apache.tomcat.util.http.fileupload.IOUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class CustomCachingRequestWrapper extends HttpServletRequestWrapper {
    /* ContentCachingRequestWrapper는 request body를 request 객체에 복사해주는 메소드가 별도로 존재하지 않기 때문에 Wrapper 클래스를 직접 구현 */

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    public CustomCachingRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        IOUtils.copy(super.getInputStream(), outputStream);
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        ByteArrayInputStream buffer = new ByteArrayInputStream(outputStream.toByteArray());
        return new ServletInputStream() {
            @Override
            public int read() {
                return buffer.read();
            }

            @Override
            public boolean isFinished() {
                return buffer.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener listener) {
                // no-op
            }
        };
    }

    // request body를 byte[]로 반환
    public byte[] getRequestBody() {
        return outputStream.toByteArray();
    }
}
