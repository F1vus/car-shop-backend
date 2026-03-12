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

    /**
     * Loads a user from the database using the provided email.
     *
     * @param email the email of the user trying to authenticate
     * @return UserDetails object built from the found user
     * @throws NotFoundException if the user is not found
     */
    @Override
    public UserDetails loadUserByUsername(final String email) throws NotFoundException {
        User user = userRepository.findUserByEmail(email).orElseThrow(RuntimeException::new);
        return UserDetailsImpl.build(user);
    }

    /**
     * Persist the given user.
     *
     * @param user user to save
     */
    @Transactional
    public User save(final User user) throws EntityExistsException {
        if(userRepository.existsUserByEmail(user.getEmail())){
            throw new EntityExistsException("Email already exists");
        }
        return userRepository.save(user);
    }

    /**
     * Update an existing user record.
     *
     * @param user user entity to update
     */
    public void updateUser(final User user) {
        userRepository.save(user);
    }

    /**
     * Find user by email.
     *
     * @param email user's email
     * @return user entity
     * @throws NotFoundException when no user is found
     */
    public User getUserByEmail(final String email) throws NotFoundException {
        return userRepository.findUserByEmail(email)
                .orElseThrow(() -> new NotFoundException("User not found by email: " + email));
    }

    /**
     * Find user by id.
     *
     * @param id user id
     * @return user entity
     * @throws NotFoundException when no user is found
     */
    public User getUserById(final Long id) throws NotFoundException {
        return userRepository.findUserById(id)
                .orElseThrow(() -> new NotFoundException("User not found by id: " + id));
    }
}
