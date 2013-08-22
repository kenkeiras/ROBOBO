package es.udc.robot_control.gui.http_server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created with IntelliJ IDEA.
 * User: kerry
 * Date: 7/08/13
 * Time: 19:34
 * This class implements a light http server for debug purposes.
 * It will server allways a static document form the application root folder
 */
public class HttpServerProcess {
    public static String DOCNAME = "programa";
    public static int PORT = 8001;
    private HttpServer server;

    public HttpServerProcess() throws IOException {
        super();
        server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new MyHandler());
        server.setExecutor(null); // creates a default executor
        server.start();
    }

    public String getURL() {
        return "http://" + server.getAddress().getAddress() + ":" + server.getAddress().getPort() + "/";
    }

    static class MyHandler implements HttpHandler {
        public void handle(HttpExchange t) throws IOException {
            String response = readFile(DOCNAME, Charset.defaultCharset());
            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static String readFile(String path, Charset encoding)  throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return encoding.decode(ByteBuffer.wrap(encoded)).toString();
    }


}
