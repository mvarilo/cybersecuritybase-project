package sec.project.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() {
        // this data would typically be retrieved from a database

        Account account = new Account();
        account.setUsername("ted");
        account.setPassword(passwordEncoder.encode("1234"));
        accountRepository.save(account);

        account = new Account();
        account.setUsername("roger");
        account.setPassword(passwordEncoder.encode("qwerty"));
        accountRepository.save(account);
        
        account = new Account();
        account.setUsername("admin");
        account.setPassword(passwordEncoder.encode("admin"));
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        List<SimpleGrantedAuthority> permissions = new ArrayList<>();
        if (username.equals("admin")) {
            permissions.add(new SimpleGrantedAuthority("ADMIN"));
        } else {
            permissions.add(new SimpleGrantedAuthority("USER"));
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                permissions);
    }
}
