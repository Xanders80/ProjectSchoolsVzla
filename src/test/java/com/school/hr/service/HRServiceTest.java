package com.school.hr.service;

import com.school.admin.entity.Staff;
import com.school.admin.repository.StaffRepository;
import com.school.hr.entity.Contract;
import com.school.hr.entity.Payroll;
import com.school.hr.entity.StaffAttendance;
import com.school.hr.repository.ContractRepository;
import com.school.hr.repository.PayrollRepository;
import com.school.hr.repository.StaffAttendanceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class HRServiceTest {

	@Mock
	private ContractRepository contractRepository;
	@Mock
	private StaffAttendanceRepository attendanceRepository;
	@Mock
	private PayrollRepository payrollRepository;
	@Mock
	private StaffRepository staffRepository;

	@InjectMocks
	private HRService hrService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		ReflectionTestUtils.setField(hrService, "lateThreshold", "09:00");
	}

	@Test
	void shouldMarkCheckInOnTime() {
		Staff staff = new Staff();
		staff.setId(1L);

		when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
		when(attendanceRepository.findByStaffIdAndDate(1L, LocalDate.now())).thenReturn(Optional.empty());
		when(attendanceRepository.save(any(StaffAttendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

		StaffAttendance result = hrService.markCheckIn(1L, LocalTime.of(8, 0));

		assertEquals(StaffAttendance.AttendanceStatus.PRESENT, result.getStatus());
		assertEquals(staff, result.getStaff());
		assertEquals(LocalDate.now(), result.getDate());
		assertEquals(LocalTime.of(8, 0), result.getCheckInTime());
	}

	@Test
	void shouldMarkCheckInLate() {
		Staff staff = new Staff();
		staff.setId(1L);

		when(staffRepository.findById(1L)).thenReturn(Optional.of(staff));
		when(attendanceRepository.findByStaffIdAndDate(1L, LocalDate.now())).thenReturn(Optional.empty());
		when(attendanceRepository.save(any(StaffAttendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

		StaffAttendance result = hrService.markCheckIn(1L, LocalTime.of(9, 30));

		assertEquals(StaffAttendance.AttendanceStatus.LATE, result.getStatus());
	}

	@Test
	void shouldMarkCheckOut() {
		StaffAttendance attendance = new StaffAttendance();
		when(attendanceRepository.findByStaffIdAndDate(1L, LocalDate.now())).thenReturn(Optional.of(attendance));
		when(attendanceRepository.save(any(StaffAttendance.class))).thenAnswer(invocation -> invocation.getArgument(0));

		StaffAttendance result = hrService.markCheckOut(1L, LocalTime.of(17, 0));
		assertEquals(LocalTime.of(17, 0), result.getCheckOutTime());
	}

	@Test
	void shouldThrowWhenCheckOutWithoutCheckIn() {
		when(attendanceRepository.findByStaffIdAndDate(1L, LocalDate.now())).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> hrService.markCheckOut(1L, LocalTime.of(17, 0)));
	}

	@Test
	void shouldThrowWhenCheckInForNonexistentStaff() {
		when(staffRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> hrService.markCheckIn(999L, LocalTime.of(8, 0)));
	}

	@Test
	void shouldGeneratePayrollForPeriod() {
		Staff staff = new Staff();
		staff.setId(1L);

		Contract contract = new Contract();
		contract.setStaff(staff);
		contract.setActive(true);
		contract.setSalary(BigDecimal.valueOf(3000));

		when(contractRepository.findAll()).thenReturn(List.of(contract));
		when(payrollRepository.existsByStaffIdAndPeriod(1L, "2026-01")).thenReturn(false);
		when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

		hrService.generatePayrollForPeriod("2026-01");

		ArgumentCaptor<Payroll> captor = ArgumentCaptor.forClass(Payroll.class);
		verify(payrollRepository).save(captor.capture());
		assertEquals(Payroll.PaymentStatus.PENDING, captor.getValue().getStatus());
		assertEquals(BigDecimal.valueOf(3000), captor.getValue().getBaseSalary());
		assertEquals("2026-01", captor.getValue().getPeriod());
	}

	@Test
	void shouldNotGenerateDuplicatePayroll() {
		Staff staff = new Staff();
		staff.setId(1L);

		Contract contract = new Contract();
		contract.setStaff(staff);
		contract.setActive(true);

		when(contractRepository.findAll()).thenReturn(List.of(contract));
		when(payrollRepository.existsByStaffIdAndPeriod(1L, "2026-01")).thenReturn(true);

		hrService.generatePayrollForPeriod("2026-01");

		verify(payrollRepository, never()).save(any(Payroll.class));
	}

	@Test
	void shouldSkipInactiveContracts() {
		Contract inactive = new Contract();
		inactive.setActive(false);

		when(contractRepository.findAll()).thenReturn(List.of(inactive));

		hrService.generatePayrollForPeriod("2026-01");

		verify(payrollRepository, never()).save(any(Payroll.class));
	}

	@Test
	void shouldPayPayroll() {
		Payroll payroll = new Payroll();
		payroll.setId(1L);
		payroll.setStatus(Payroll.PaymentStatus.PENDING);

		when(payrollRepository.findById(1L)).thenReturn(Optional.of(payroll));
		when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

		hrService.payPayroll(1L);

		ArgumentCaptor<Payroll> captor = ArgumentCaptor.forClass(Payroll.class);
		verify(payrollRepository).save(captor.capture());
		assertEquals(Payroll.PaymentStatus.PAID, captor.getValue().getStatus());
		assertNotNull(captor.getValue().getPaymentDate());
	}

	@Test
	void shouldThrowWhenPayNonexistentPayroll() {
		when(payrollRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> hrService.payPayroll(999L));
	}

	@Test
	void shouldSaveContract() {
		Contract contract = new Contract();
		contract.setSalary(BigDecimal.valueOf(2000));
		when(contractRepository.save(contract)).thenReturn(contract);

		Contract saved = hrService.saveContract(contract);
		assertNotNull(saved);
		verify(contractRepository).save(contract);
	}

	@Test
	void shouldGetContractByStaffId() {
		Contract contract = new Contract();
		when(contractRepository.findByStaffId(1L)).thenReturn(Optional.of(contract));

		Optional<Contract> found = hrService.getContractByStaffId(1L);
		assertTrue(found.isPresent());
	}

	@Test
	void shouldCalculateNetSalaryInPayroll() {
		Staff staff = new Staff();
		staff.setId(1L);

		Contract contract = new Contract();
		contract.setStaff(staff);
		contract.setActive(true);
		contract.setSalary(BigDecimal.valueOf(3000));

		when(contractRepository.findAll()).thenReturn(List.of(contract));
		when(payrollRepository.existsByStaffIdAndPeriod(1L, "2026-01")).thenReturn(false);
		when(payrollRepository.save(any(Payroll.class))).thenAnswer(invocation -> invocation.getArgument(0));

		hrService.generatePayrollForPeriod("2026-01");

		ArgumentCaptor<Payroll> captor = ArgumentCaptor.forClass(Payroll.class);
		verify(payrollRepository).save(captor.capture());
		Payroll saved = captor.getValue();
		assertEquals(BigDecimal.valueOf(3000), saved.getNetSalary());
	}
}
