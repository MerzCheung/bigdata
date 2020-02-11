package ming.zhang.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * @author merz
 * @Description:
 */
@Controller
public class Test {

    @RequestMapping("/demo")
    public String demo() {
        return "demo";
    }
}
