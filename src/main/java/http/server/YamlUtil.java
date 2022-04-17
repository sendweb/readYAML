package http.server;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class YamlUtil implements HttpHandler {
//    private static final Logger logger = LoggerFactory.getLogger(getClass());
    private static final Logger logger = LoggerFactory.getLogger(Thread.currentThread().getStackTrace()[1].getClassName());
    private static final HashMap<Integer, HttpServer> httpServers = new HashMap<Integer, HttpServer>();

    public static void main(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        log.info("这里是log4j");
        logger.info("这里是log4j");
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("service.txt");
        String str = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        String[] params = str.split("\r\n");

        initServer(params);

        startServers();
    }

    private static void startServers() {
        for (int port : httpServers.keySet()) {
            httpServers.get(port).start();
            System.out.println(port + " 端口的应用全部启动成功.");
        }
    }

    private static void stopServers() {
        for (int port : httpServers.keySet()) {
            httpServers.get(port).stop(0);
            System.out.println(port + " 端口的应用全部停止.");
        }
    }

    private static void initServer(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (String arg : args) {
            JSONObject service = JSONObject.parseObject(arg);
            int port = service.getIntValue("port");
            if (!httpServers.containsKey(port)) {
                HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
                httpServers.put(port, httpServer);
            }
            String url = service.getString("url");
            String cls = service.getString("cls");
            httpServers.get(port).createContext(url, (HttpHandler) Class.forName(cls).newInstance());
            System.out.println("端口 " + port + " 配置路径:" + url);
        }
        System.out.println(httpServers);
    }

    private static void initServer1(String[] args) throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        for (String arg : args) {
            HttpService service = JSONObject.parseObject(arg, HttpService.class);
            int port = service.getPort();
            if (!httpServers.containsKey(port)) {
                HttpServer httpServer = HttpServer.create(new InetSocketAddress(port), 0);
                httpServers.put(port, httpServer);
            }
            httpServers.get(port).createContext(service.getUrl(), (HttpHandler) Class.forName(service.getCls()).newInstance());
            System.out.println(port + " setup link:" + service.getUrl());
        }
        System.out.println(httpServers);
    }


    public static void main4(String[] args) throws IOException {
        //{"port":5000,"url":"/test","cls":"test.Test"} {"port":6000,"url":"/test2","cls":"test.GoHome"} {"port":5000,"url":"/hello","cls":"test2.Test2"}
        String[] parms = new String[]{"{\"port\":5000,\"url\":\"/test\",\"clss\":\"test.Test\"}",
                "{\"port\":6000,\"url\":\"/test2\",\"cls\":\"test.GoHome\"}",
                "{\"port\":5000,\"url\":\"/test2\",\"cls\":\"test.GoHome\"}",
                "{\"port\":7000,\"url\":\"/test2\",\"cls\":\"test.GoHome\"}",
                "{\"port\":5000,\"url\":\"/hello\",\"cls\":\"test2.Test2\"}"};
        System.out.println(parms.length);
        HashMap<Integer, List<JSONObject>> server = new HashMap<Integer, List<JSONObject>>();
        for (String arg : parms) {
            //System.out.println(arg);
            JSONObject json = JSONObject.parseObject(arg);
            HttpService json2 = (HttpService) JSONObject.parseObject(arg, HttpService.class);
            System.out.println("json2:" + json2.getUrl());
            Integer port = (Integer) json.get("port");
            System.out.println(json);
            json.remove("port");
            if (server.get(port) == null) {
                List list = new ArrayList<JSONObject>();
                list.add(json);
                server.put(port, list);
            } else
                server.get(port).add(json);
        }
        System.out.println(server);


    }

    public static void main3(String[] args) throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("server.json");
        //InputStream fis = new FileInputStream("C:\\Users\\xhb\\IdeaProjects\\readYAML\\target\\classes\\contact.yml");
        String str = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
        JSONObject json = JSONObject.parseObject(str);

        System.out.println(json);
        System.out.println(json.get("port"));
        System.out.println(json.get("services"));

        JSONArray array = json.getJSONArray("services");
        System.out.println(array.size());
        System.out.println(array.get(2));

        JSONObject json2 = (JSONObject) array.get(2);
        System.out.println(json2.get("url"));
        System.out.println(json2.get("cls"));

        Service service = JSONObject.parseObject(array.get(2).toString(), Service.class);
        System.out.println(service.isEnabled());

        MyService[] svs = JSONObject.parseObject(JSONObject.parseObject(str).get("services").toString(), MyService[].class);
        System.out.println(svs.length);
        System.out.println(svs[2].isEnabled());
    }

    public static void main2(String[] args) throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader("C:\\Users\\xhb\\IdeaProjects\\readYAML\\target\\classes\\contact.yml"));

        MyHttpServer server = reader.read(MyHttpServer.class);

        System.out.println(server.getServer());
        System.out.println(server.getServer()[1].getServices()[0].getUrl());
        System.out.println(server.getServer()[1].getServices()[0].getCls());
        System.out.println(server.getServer()[1].getServices()[2].isEnabled());
        System.out.println(server.getServer()[1].getServices()[2].getUrl());
        System.out.println(server.getServer()[1].getServices().length);
    }


    public static void main1(String[] args) throws FileNotFoundException, YamlException {
        Yaml yaml = new Yaml();
        InputStream resourceAsStream = YamlUtil.class.getClassLoader().getResourceAsStream("contact.yml");
        Map<String, Object> load = yaml.load(resourceAsStream);
//        http.server.MyHttpServer svr = (http.server.MyHttpServer) load;
//        System.out.println(load);
//        System.out.println(load.getClass().getTypeName());


        List<Map<String, Object>> links = (List<Map<String, Object>>) load.get("http-server");
        System.out.println(links.get(1).get("port"));

        List list = (List) load.get("http-server");
        System.out.println(list.size());
        System.out.println(list.get(1));
        System.out.println(list.get(1).getClass().getTypeName());

        Map<String, Object> o = (Map<String, Object>) list.get(1);
        System.out.println(o.get("port"));
        System.out.println(o.get("service"));

        List<Map<String, String>> val = (List<Map<String, String>>) o.get("service");
        System.out.println(val.get(1).get("url"));

        List<Map<String, Object>> port = (List<Map<String, Object>>) load.get("http-server");
        System.out.println(port.size());
        System.out.println(port.get(1).get("port"));
        System.out.println(port.get(1).get("enabled"));
        System.out.println(port.get(1).get("service"));

        List<Map<String, String>> service = (List<Map<String, String>>) port.get(1).get("service");
        System.out.println(service.size());
        System.out.println(service.get(1).get("class"));


    }


    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        // 响应内容
        byte[] respContents = ("你好！ 正在关机... " + httpExchange.getRequestURI()).getBytes("UTF-8");

        // 设置响应头
        httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        // 设置响应code和内容长度
        httpExchange.sendResponseHeaders(200, respContents.length);

        // 设置响应内容
        httpExchange.getResponseBody().write(respContents);

        // 关闭处理器
        httpExchange.close();
        stopServers();
    }
}


@Setter
@Getter
class MyService {
    private String name;
    private boolean enabled;
    private String url;
    private String cls;
}

class HttpService {
    private int port;
    private String url;
    private String cls;

    public void setPort(int port) {
        this.port = port;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCls(String cls) {
        this.cls = cls;
    }

    public int getPort() {
        return port;
    }

    public String getUrl() {
        return url;
    }

    public String getCls() {
        return cls;
    }
}

class MyHttpHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("addr: " + httpExchange.getRemoteAddress() +     // 客户端IP地址
                "; protocol: " + httpExchange.getProtocol() +               // 请求协议: HTTP/1.1
                "; method: " + httpExchange.getRequestMethod() +            // 请求方法: GET, POST 等
                "; URI: " + httpExchange.getRequestURI());                  // 请求 URI

        // 获取请求头
        String userAgent = httpExchange.getRequestHeaders().getFirst("User-Agent");
        System.out.println("User-Agent: " + userAgent);

        // 响应内容
        byte[] respContents = ("你好！ Hello World from " + httpExchange.getRequestURI()).getBytes("UTF-8");

        // 设置响应头
        httpExchange.getResponseHeaders().add("Content-Type", "text/html; charset=UTF-8");
        // 设置响应code和内容长度
        httpExchange.sendResponseHeaders(200, respContents.length);

        // 设置响应内容
        httpExchange.getResponseBody().write(respContents);

        // 关闭处理器
        httpExchange.close();
    }
}
