package test;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import org.yaml.snakeyaml.Yaml;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.Map;

public class TestCustomer {
    public static void main(String[] args) throws FileNotFoundException, YamlException {
        Yaml yaml = new Yaml();
        InputStream inputStream = TestCustomer.class.getClassLoader().getResourceAsStream("customer.yaml");
        Map<String, Object> obj = (Map<String, Object>) yaml.load(inputStream);
        System.out.println(obj);

//        Customer customer = yaml.load(inputStream);
//        System.out.println(customer);
//

        // 将list中的数据转成json字符串
        String jsonObject = JSON.toJSONString(obj);
        System.out.println(jsonObject);
        //将json转成需要的对象
        Customer customer = JSONObject.parseObject(jsonObject, Customer.class);

        System.out.println(customer);

        System.out.println(customer.getLastName());


        YamlReader reader = new YamlReader(new FileReader("C:\\Users\\xhb\\IdeaProjects\\readYAML\\target\\classes\\customer.yaml"));

        customer = reader.read(Customer.class);

        System.out.println(customer.getFirstName());


    }

}



