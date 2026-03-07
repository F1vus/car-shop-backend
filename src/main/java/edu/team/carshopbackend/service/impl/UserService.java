package edu.team.carshopbackend.service.impl;

import edu.team.carshopbackend.entity.User;
import edu.team.carshopbackend.entity.impl.UserDetailsImpl;
import edu.team.carshopbackend.error.exception.NotFoundException;
import edu.team.carshopbackend.repository.UserRepository;
import jakarta.persistence.EntityExistsException;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NullMarked;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@NullMarked
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(final String email) throws NotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(RuntimeException::new);
        return UserDetailsImpl.build(user);
    }

    @Transactional
    public User save(final User user) throws EntityExistsException {
        if(userRepository.existsUserByEmail(user.getEmail())){
            throw new EntityExistsException("Email already exists");
        }
        return userRepository.save(user);
    }

    public void updateUser(final User user) {
        userRepository.save(user);
    }

    public User getUserByEmail(final String email) throws NotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found by email: " + email));
    }

    public User getUserById(final Long id) throws NotFoundException {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + id));
    }
}
