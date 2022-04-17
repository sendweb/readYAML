package http.server;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Server {
    private int port;
    private Service[] services;
}
