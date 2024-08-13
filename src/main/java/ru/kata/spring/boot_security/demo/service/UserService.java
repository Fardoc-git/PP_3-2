package ru.kata.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kata.spring.boot_security.demo.model.Role;
import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.repository.UserRepository;
import javax.transaction.Transactional;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService implements UserDetailsService, FaceOfUserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository, User user) {
        this.userRepository = userRepository;
    }

    public User findByLastName(String lastname) {
        return userRepository.findByLastName(lastname);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    @Transactional
    public void saveUser(User user) {
        userRepository.save(user);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public UserDetails loadUserByUsername(String name) throws UsernameNotFoundException {
        User user = findByLastName(name);
        if (user == null) {
            throw new UsernameNotFoundException("%s not found".formatted(name));
        }
        return new org.springframework.security.core.userdetails.User(user.getLastName(),
                user.getPassword(),
                getGrantedAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> getGrantedAuthorities(Collection<Role> roles) {
        return roles
                .stream()
                .map(r -> new SimpleGrantedAuthority(r.getRole())).collect(Collectors.toList());
    }
}
