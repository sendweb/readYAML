package http.server;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Service {
    private String name;
    private boolean enabled;
    private String url;
    private String cls;
}
