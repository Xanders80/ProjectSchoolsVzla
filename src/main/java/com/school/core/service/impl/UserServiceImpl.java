package com.school.core.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.academic.entity.Student;
import com.school.academic.service.AcademicService;
import com.school.admin.entity.Staff;
import com.school.admin.service.StaffService;
import com.school.core.entity.Parent;
import com.school.core.entity.PasswordResetToken;
import com.school.core.entity.User;
import com.school.core.enums.Role;
import com.school.core.repository.PasswordResetTokenRepository;
import com.school.core.repository.UserRepository;
import com.school.core.service.UserService;
import com.school.core.service.ParentService;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final ParentService parentService;
    private final StaffService staffService;
    private final AcademicService academicService;

    private static final String PASSWORD_REGEX = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(PASSWORD_REGEX);

    public UserServiceImpl(UserRepository userRepository,
            PasswordResetTokenRepository passwordResetTokenRepository,
            PasswordEncoder passwordEncoder,
            ParentService parentService,
            StaffService staffService,
            AcademicService academicService) {
        this.userRepository = userRepository;
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.parentService = parentService;
        this.staffService = staffService;
        this.academicService = academicService;
    }

    @Override
    public User registerNewUser(String firstName, String lastName, String email, String username, String password) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + username);
        }
        if (this.findByEmail(email).isPresent()) {
            throw new IllegalArgumentException("El correo electrónico ya existe: " + email);
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(
                    "La contraseña no cumple con los requisitos de seguridad (Mínimo 8 caracteres, 1 mayúscula, 1 número, 1 carácter especial).");
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(Role.STUDENT); // Asignar rol por defecto (ej. ESTUDIANTE)
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        return userRepository.save(user);
    }

    @Override
    public User registerNewUserWithType(String firstName, String lastName, String email, String username,
            String password,
            String userType, String dni, String phoneNumber, String address, String relationship) {

        if (userRepository.findByUsername(username).isPresent()) {
            throw new IllegalArgumentException("El nombre de usuario ya existe: " + username);
        }
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(
                    "La contraseña no cumple con los requisitos de seguridad (Mínimo 8 caracteres, 1 mayúscula, 1 número, 1 carácter especial).");
        }

        // Crear usuario base
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        Role role = Role.valueOf(userType);
        user.setRole(role);
        user.setEnabled(true);
        user.setCreatedAt(LocalDateTime.now());

        User savedUser = userRepository.save(user);

        // Crear entidad específica según el tipo
        createSpecificEntity(savedUser, role, dni, phoneNumber, address, relationship, firstName, lastName, email);

        return savedUser;
    }

    private void createSpecificEntity(User user, Role role, String dni, String phoneNumber, String address,
            String relationship, String firstName, String lastName, String email) {

        switch (role) {
            case PARENT:
                Parent parent = new Parent();
                parent.setUser(user);
                parent.setFirstName(firstName);
                parent.setLastName(lastName);
                parent.setEmail(email);
                parent.setDni(dni);
                parent.setPhoneNumber(phoneNumber);
                parent.setAddress(address);
                parent.setRelationship(relationship != null ? relationship : "Padre");
                parentService.saveParent(parent);
                break;

            case TEACHER:
            case STAFF:
                Staff staff = new Staff();
                staff.setUser(user);
                staff.setFirstName(firstName);
                staff.setLastName(lastName);
                staff.setEmail(email);
                staff.setDni(dni);
                staff.setPhoneNumber(phoneNumber);
                staff.setAddress(address);
                staff.setJobTitle(role);
                staff.setHireDate(LocalDate.now());
                staff.setDepartment("General");
                staffService.saveStaff(staff);
                break;

            case STUDENT:
                Student student = new Student();
                student.setUser(user);
                student.setFirstName(firstName);
                student.setLastName(lastName);
                student.setEmail(email);
                student.setDni(dni);
                student.setPhoneNumber(phoneNumber);
                student.setAddress(address);
                student.setRegistrationNumber("REG" + System.currentTimeMillis());
                student.setEnrollmentDate(LocalDate.now());
                academicService.saveStudent(student);
                break;

            default:
                break;
        }
    }

    @Override
    public Optional<User> findByEmail(String email) {
        // Buscar en estudiantes
        Optional<Student> student = academicService.getStudentByEmail(email);
        if (student.isPresent())
            return Optional.ofNullable(student.get().getUser());

        // Buscar en personal
        Optional<Staff> staff = staffService.findByEmail(email);
        if (staff.isPresent())
            return Optional.ofNullable(staff.get().getUser());

        // Buscar en padres
        Optional<Parent> parent = parentService.findByEmail(email);
        if (parent.isPresent())
            return Optional.ofNullable(parent.get().getUser());

        return Optional.empty();
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
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
            throw new IllegalArgumentException("La contraseña no cumple con los requisitos de seguridad.");
        }
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private boolean isValidPassword(String password) {
        return password != null && PASSWORD_PATTERN.matcher(password).matches();
    }

    @Override
    public boolean checkIfValidOldPassword(User user, String oldPassword) {
        return passwordEncoder.matches(oldPassword, user.getPassword());
    }

    @Override
    public User updateUserProfile(User user, String firstName, String lastName, String email, String dni,
            String phoneNumber, String address, String relationship, String department, String specialization) {
        switch (user.getRole()) {
            case PARENT:
                parentService.getParentByUserId(user.getId())
                        .ifPresent(parent -> {
                            parent.setFirstName(firstName);
                            parent.setLastName(lastName);
                            parent.setEmail(email);
                            if (dni != null)
                                parent.setDni(dni);
                            if (phoneNumber != null)
                                parent.setPhoneNumber(phoneNumber);
                            if (address != null)
                                parent.setAddress(address);
                            if (relationship != null)
                                parent.setRelationship(relationship);
                            parentService.saveParent(parent);
                        });
                break;
            case ADMIN:
            case DIRECTOR:
            case TEACHER:
            case STAFF:
                staffService.getStaffByUserId(user.getId())
                        .ifPresent(staff -> {
                            staff.setFirstName(firstName);
                            staff.setLastName(lastName);
                            staff.setEmail(email);
                            if (dni != null)
                                staff.setDni(dni);
                            if (phoneNumber != null)
                                staff.setPhoneNumber(phoneNumber);
                            if (address != null)
                                staff.setAddress(address);
                            if (department != null)
                                staff.setDepartment(department);
                            if (specialization != null)
                                staff.setSpecialization(specialization);
                            staffService.saveStaff(staff);
                        });
                break;
            case STUDENT:
                academicService.getStudentByUserId(user.getId())
                        .ifPresent(student -> {
                            student.setFirstName(firstName);
                            student.setLastName(lastName);
                            student.setEmail(email);
                            if (dni != null)
                                student.setDni(dni);
                            if (phoneNumber != null)
                                student.setPhoneNumber(phoneNumber);
                            if (address != null)
                                student.setAddress(address);
                            academicService.saveStudent(student);
                        });
                break;
            default:
                break;
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    @Override
    public List<User> findAllUsers() {
        return userRepository.findAll();
    }
}
