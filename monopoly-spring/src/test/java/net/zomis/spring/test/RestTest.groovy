package net.zomis.spring.test

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import groovy.transform.CompileStatic
import org.slf4j.Logger
import org.slf4j.LoggerFactory

import java.nio.charset.StandardCharsets

@CompileStatic
public class RestTest {

    private static final Logger logger = LoggerFactory.getLogger(RestTest.class);

    private final String prefix;

    public RestTest(String prefix) {
        if (!prefix.endsWith('/')) {
            prefix = prefix + '/';
        }
        this.prefix = prefix;
    }

    public static RestTest localhost(int port) {
        return new RestTest("http://localhost:" + port);
    }

    public Object get(String path) {

    }

    public Object post(String path, Closure<?> data) {
        if (path.startsWith('/')) {
            path = path.substring(1);
        }
        JsonBuilder jsonBuilder = new JsonBuilder();
        jsonBuilder.call(data);
        String sendData = jsonBuilder.toPrettyString();

        byte[] postData = sendData.getBytes(StandardCharsets.UTF_8);
        int postDataLength = postData.length;
        String request = this.prefix + path;
        logger.info("Request to $request: $sendData");
        URL url = new URL(request);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setInstanceFollowRedirects(false);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        conn.setRequestProperty("charset", "utf-8");
        conn.setRequestProperty("Content-Length", Integer.toString(postDataLength));
        conn.setUseCaches(false);

        new DataOutputStream(conn.getOutputStream()).withCloseable {
            it.write(postData);
        }

        String text = conn.inputStream.text
        logger.info("Result: " + text);
        return new JsonSlurper().parseText(text)
    }

}
