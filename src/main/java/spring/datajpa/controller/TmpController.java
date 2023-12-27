package spring.datajpa.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TmpController {

    @GetMapping("/tmp")
    public String tmp() {
        return "tmp";
    }

}
