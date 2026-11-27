package backend_ecocode.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.view.RedirectView;

@RestController
public class TesteController {

    @GetMapping("/")
    public RedirectView inicio() {
        return new RedirectView("/login.html");
    }

    @GetMapping("/oi")
    public String oi() {
        return "O back tá vivo!";
    }
}