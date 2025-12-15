package com.school.core.service.impl;

import com.school.core.entity.PasswordResetToken;
import com.school.core.entity.User;
import com.school.core.enums.Role;
import com.school.core.repository.PasswordResetTokenRepository;
import com.school.core.repository.UserRepository;
import com.school.core.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private com.school.core.service.ParentService parentService;

    @Autowired
    private com.school.admin.service.StaffService staffService;

    @Autowired
    private com.school.academic.service.AcademicService academicService;

    @Override
    public User registerNewUser(String firstName, String lastName, String email, String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo electrónico ya existe: " + email);
        }
        if (!isValidPassword(password)) {
            throw new RuntimeException(
                    "La contraseña no cumple con los requisitos de seguridad (Mínimo 8 caracteres, 1 mayúscula, 1 número, 1 carácter especial).");
        }

        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.STUDENT); // Asignar rol por defecto (ej. ESTUDIANTE)
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User registerNewUserWithType(String firstName, String lastName, String email, String username, String password,
            String userType, String dni, String phoneNumber, String address, String relationship) {
        
        if (userRepository.findByUsername(username).isPresent()) {
            throw new RuntimeException("El nombre de usuario ya existe: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo electrónico ya existe: " + email);
        }
        if (!isValidPassword(password)) {
            throw new RuntimeException(
                    "La contraseña no cumple con los requisitos de seguridad (Mínimo 8 caracteres, 1 mayúscula, 1 número, 1 carácter especial).");
        }

        // Crear usuario base
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.valueOf(userType));
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());
        
        User savedUser = userRepository.save(user);
        
        // Crear entidad específica según el tipo
        createSpecificEntity(savedUser, userType, dni, phoneNumber, address, relationship);
        
        return savedUser;
    }
    
    private void createSpecificEntity(User user, String userType, String dni, String phoneNumber, String address, String relationship) {
        Role role = Role.valueOf(userType);
        
        switch (role) {
            case PARENT:
                com.school.core.entity.Parent parent = new com.school.core.entity.Parent();
                parent.setUser(user);
                parent.setFirstName(user.getFirstName());
                parent.setLastName(user.getLastName());
                parent.setEmail(user.getEmail());
                parent.setDni(dni);
                parent.setPhoneNumber(phoneNumber);
                parent.setAddress(address);
                parent.setRelationship(relationship != null ? relationship : "Padre");
                parentService.saveParent(parent);
                break;
                
            case TEACHER:
            case STAFF:
                com.school.admin.entity.Staff staff = new com.school.admin.entity.Staff();
                staff.setUser(user);
                staff.setFirstName(user.getFirstName());
                staff.setLastName(user.getLastName());
                staff.setEmail(user.getEmail());
                staff.setDni(dni);
                staff.setPhoneNumber(phoneNumber);
                staff.setAddress(address);
                staff.setJobTitle(role);
                staff.setHireDate(java.time.LocalDate.now());
                staff.setDepartment("General");
                staffService.saveStaff(staff);
                break;
                
            case STUDENT:
                com.school.academic.entity.Student student = new com.school.academic.entity.Student();
                student.setUser(user);
                student.setFirstName(user.getFirstName());
                student.setLastName(user.getLastName());
                student.setEmail(user.getEmail());
                student.setDni(dni);
                student.setPhoneNumber(phoneNumber);
                student.setAddress(address);
                student.setRegistrationNumber("REG" + System.currentTimeMillis());
                student.setEnrollmentDate(java.time.LocalDate.now());
                academicService.saveStudent(student);
                break;
                
            default:
                break;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        // Eliminar token anterior si existe
        passwordResetTokenRepository.findByUser(user).ifPresent(passwordResetTokenRepository::delete);

        PasswordResetToken myToken = new PasswordResetToken(token, user, LocalDateTime.now().plusHours(24));
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public Optional<PasswordResetToken> getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void changeUserPassword(User user, String password) {
        if (!isValidPassword(password)) {
            throw new RuntimeException("La contraseña no cumple con los requisitos de seguridad.");
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private boolean isValidPassword(String password) {
        String regex = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        return password != null && password.matches(regex);
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public User updateUserProfile(User user, String firstName, String lastName, String email) {
        // Validar si el email cambió y si ya existe
        if (!user.getEmail().equals(email) && userRepository.findByEmail(email).isPresent()) {
            throw new RuntimeException("El correo electrónico ya está en uso por otro usuario.");
        }

        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);

        return userRepository.save(user);
    }
}
