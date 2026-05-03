package com.school.core.service.impl;

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
import com.school.core.service.ParentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class UserServiceImplTest {

	@Mock
	private UserRepository userRepository;
	@Mock
	private PasswordResetTokenRepository passwordResetTokenRepository;
	@Mock
	private PasswordEncoder passwordEncoder;
	@Mock
	private ParentService parentService;
	@Mock
	private StaffService staffService;
	@Mock
	private AcademicService academicService;

	@InjectMocks
	private UserServiceImpl userService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldRegisterNewUser() {
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.empty());
		when(academicService.getStudentByEmail("test@test.com")).thenReturn(Optional.empty());
		when(staffService.findByEmail("test@test.com")).thenReturn(Optional.empty());
		when(parentService.findByEmail("test@test.com")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("Pass123!@")).thenReturn("encodedPassword");

		User user = new User();
		user.setUsername("testuser");
		user.setPassword("encodedPassword");
		user.setRole(Role.STUDENT);
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User u = invocation.getArgument(0);
			u.setId(1L);
			return u;
		});

		User result = userService.registerNewUser("Test", "User", "test@test.com", "testuser", "Pass123!@");

		assertNotNull(result);
		verify(passwordEncoder).encode("Pass123!@");
		assertEquals(Role.STUDENT, result.getRole());
		assertTrue(result.isEnabled());
	}

	@Test
	void shouldRejectDuplicateUsername() {
		when(userRepository.findByUsername("existing")).thenReturn(Optional.of(new User()));

		assertThrows(IllegalArgumentException.class,
				() -> userService.registerNewUser("Test", "User", "test@test.com", "existing", "Pass123!@"));
	}

	@Test
	void shouldRejectDuplicateEmail() {
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());

		Student existingStudent = new Student();
		existingStudent.setUser(new User());
		when(academicService.getStudentByEmail("existing@test.com")).thenReturn(Optional.of(existingStudent));

		assertThrows(IllegalArgumentException.class,
				() -> userService.registerNewUser("Test", "User", "existing@test.com", "newuser", "Pass123!@"));
	}

	@Test
	void shouldRejectWeakPassword() {
		when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
		when(academicService.getStudentByEmail("test@test.com")).thenReturn(Optional.empty());
		when(staffService.findByEmail("test@test.com")).thenReturn(Optional.empty());
		when(parentService.findByEmail("test@test.com")).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class,
				() -> userService.registerNewUser("Test", "User", "test@test.com", "newuser", "weak"));
	}

	@Test
	void shouldCreatePasswordResetToken() {
		User user = new User();
		user.setId(1L);

		when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.empty());
		when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userService.createPasswordResetTokenForUser(user, "token123");

		verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
	}

	@Test
	void shouldDeleteOldTokenBeforeCreatingNew() {
		User user = new User();
		user.setId(1L);
		PasswordResetToken oldToken = new PasswordResetToken();

		when(passwordResetTokenRepository.findByUser(user)).thenReturn(Optional.of(oldToken));
		when(passwordResetTokenRepository.save(any(PasswordResetToken.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userService.createPasswordResetTokenForUser(user, "newToken");

		verify(passwordResetTokenRepository).delete(oldToken);
		verify(passwordResetTokenRepository).save(any(PasswordResetToken.class));
	}

	@Test
	void shouldChangePassword() {
		User user = new User();
		when(passwordEncoder.encode("NewPass123!@")).thenReturn("newEncoded");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

		userService.changeUserPassword(user, "NewPass123!@");

		assertEquals("newEncoded", user.getPassword());
		verify(userRepository).save(user);
	}

	@Test
	void shouldRejectWeakPasswordChange() {
		User user = new User();

		assertThrows(IllegalArgumentException.class, () -> userService.changeUserPassword(user, "weak"));
	}

	@Test
	void shouldCheckValidOldPassword() {
		User user = new User();
		user.setPassword("encodedPassword");
		when(passwordEncoder.matches("oldPass", "encodedPassword")).thenReturn(true);

		assertTrue(userService.checkIfValidOldPassword(user, "oldPass"));
	}

	@Test
	void shouldCheckInvalidOldPassword() {
		User user = new User();
		user.setPassword("encodedPassword");
		when(passwordEncoder.matches("wrongPass", "encodedPassword")).thenReturn(false);

		assertFalse(userService.checkIfValidOldPassword(user, "wrongPass"));
	}

	@Test
	void shouldFindByUsername() {
		User user = new User();
		user.setUsername("testuser");
		when(userRepository.findByUsername("testuser")).thenReturn(Optional.of(user));

		Optional<User> found = userService.findByUsername("testuser");
		assertTrue(found.isPresent());
		assertEquals("testuser", found.get().getUsername());
	}

	@Test
	void shouldFindById() {
		User user = new User();
		user.setId(1L);
		when(userRepository.findById(1L)).thenReturn(Optional.of(user));

		Optional<User> found = userService.findById(1L);
		assertTrue(found.isPresent());
	}

	@Test
	void shouldReturnEmptyForNullId() {
		Optional<User> found = userService.findById(null);
		assertTrue(found.isEmpty());
	}

	@Test
	void shouldRegisterNewUserWithType() {
		when(userRepository.findByUsername("newparent")).thenReturn(Optional.empty());
		when(passwordEncoder.encode("Pass123!@")).thenReturn("encoded");
		when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
			User u = invocation.getArgument(0);
			u.setId(1L);
			return u;
		});

		User result = userService.registerNewUserWithType(
				"Parent", "User", "parent@test.com", "newparent",
				"Pass123!@", "PARENT", "12345678", "555-1234",
				"Address 1", "Padre", null);

		assertNotNull(result);
		assertEquals(Role.PARENT, result.getRole());
		verify(parentService).saveParent(any(Parent.class));
	}
}
