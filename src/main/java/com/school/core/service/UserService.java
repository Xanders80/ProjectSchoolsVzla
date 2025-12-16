package com.school.core.service;

import com.school.core.entity.User;
import com.school.core.entity.PasswordResetToken;
import java.util.Optional;

public interface UserService {
    User registerNewUser(String firstName, String lastName, String email, String username, String password);

    User registerNewUserWithType(String firstName, String lastName, String email, String username, String password,
            String userType, String dni, String phoneNumber, String address, String relationship);

    Optional<User> findByEmail(String email);

    void createPasswordResetTokenForUser(User user, String token);

    Optional<PasswordResetToken> getPasswordResetToken(String token);

    void changeUserPassword(User user, String password);

    boolean checkIfValidOldPassword(User user, String oldPassword);

    User updateUserProfile(User user, String firstName, String lastName, String email);

    Optional<User> findById(Long id);

    Optional<User> findByUsername(String username);

    java.util.List<User> findAllUsers();
}
