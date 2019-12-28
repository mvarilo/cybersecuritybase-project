package sec.project.controller;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import sec.project.domain.Account;
import sec.project.domain.Signup;
import sec.project.repository.AccountRepository;
import sec.project.repository.SignupRepository;

@Controller
public class SignupController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm() {
        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(Authentication authentication, @RequestParam String address) {
        Account account = accountRepository.findByUsername(authentication.getName());
        signupRepository.save(new Signup(account.getUsername(), address));
        return "redirect:/done";
    }

    @RequestMapping(value = "/done", method = RequestMethod.GET)
    public String loadSignups(Model model, Authentication auth) {
        List<Signup> signups = signupRepository.findAll();
        List<Signup> valid = new ArrayList<>();

        for (int i = 0; i < signups.size(); i++) {
            if (signups.get(i).getName().equals(auth.getName())) {
                valid.add(signups.get(i));
            }
        }
        model.addAttribute("signups", valid);
        return "done";
    }

    @RequestMapping(value = "/clear/{id}", method = RequestMethod.GET)
    public String submitDelete(@PathVariable(value = "id") Long id, Model model, Authentication auth) {
//        if (signupRepository.findById(id).getName().equals(accountRepository.findByUsername(auth.getName()).getUsername())) {
//            signupRepository.delete(id);
//        }
        signupRepository.delete(id);

        return "redirect:/done";
    }

}
