package com.school.finance.service;

import com.school.finance.entity.Payment;
import com.school.finance.entity.StudentFee;
import com.school.finance.enums.FeeStatus;
import com.school.finance.enums.PaymentMethod;
import com.school.finance.repository.PaymentRepository;
import com.school.finance.repository.StudentFeeRepository;
import com.school.academic.repository.StudentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FinanceServiceTest {

	@Mock
	private StudentFeeRepository studentFeeRepository;
	@Mock
	private StudentRepository studentRepository;
	@Mock
	private PaymentRepository paymentRepository;

	@InjectMocks
	private FinanceService financeService;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
	}

	@Test
	void shouldCreateFee() {
		StudentFee fee = new StudentFee();
		fee.setAmount(BigDecimal.valueOf(500));
		when(studentFeeRepository.save(fee)).thenReturn(fee);

		StudentFee saved = financeService.createFee(fee);
		assertNotNull(saved);
		verify(studentFeeRepository).save(fee);
	}

	@Test
	void shouldRegisterPaymentAndUpdateStatusToPaid() {
		StudentFee fee = new StudentFee();
		fee.setId(1L);
		fee.setAmount(BigDecimal.valueOf(1000));
		fee.setStatus(FeeStatus.PENDING);

		Payment payment = new Payment();
		payment.setStudentFee(fee);
		payment.setAmount(BigDecimal.valueOf(1000));
		payment.setMethod(PaymentMethod.TRANSFER);

		when(studentFeeRepository.findById(1L)).thenReturn(Optional.of(fee));
		when(paymentRepository.save(payment)).thenReturn(payment);
		when(paymentRepository.findByStudentFeeId(1L)).thenReturn(Collections.singletonList(payment));
		when(studentFeeRepository.save(any(StudentFee.class))).thenAnswer(invocation -> invocation.getArgument(0));

		Payment savedPayment = financeService.registerPayment(payment);
		assertNotNull(savedPayment);

		ArgumentCaptor<StudentFee> feeCaptor = ArgumentCaptor.forClass(StudentFee.class);
		verify(studentFeeRepository).save(feeCaptor.capture());
		assertEquals(FeeStatus.PAID, feeCaptor.getValue().getStatus());
		assertNotNull(feeCaptor.getValue().getPaymentDate());
	}

	@Test
	void shouldThrowWhenPaymentForNonexistentFee() {
		StudentFee fee = new StudentFee();
		fee.setId(999L);
		Payment payment = new Payment();
		payment.setStudentFee(fee);

		when(studentFeeRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> financeService.registerPayment(payment));
	}

	@Test
	void shouldSoftDeleteFee() {
		StudentFee fee = new StudentFee();
		fee.setId(1L);
		when(studentFeeRepository.findById(1L)).thenReturn(Optional.of(fee));
		when(studentFeeRepository.save(any(StudentFee.class))).thenAnswer(invocation -> invocation.getArgument(0));

		financeService.deleteFee(1L);

		ArgumentCaptor<StudentFee> captor = ArgumentCaptor.forClass(StudentFee.class);
		verify(studentFeeRepository).save(captor.capture());
		assertTrue(captor.getValue().isDeleted());
		assertNotNull(captor.getValue().getDeletedAt());
	}

	@Test
	void shouldSetPartialStatusOnPartialPayment() {
		StudentFee fee = new StudentFee();
		fee.setId(1L);
		fee.setAmount(BigDecimal.valueOf(1000));
		fee.setStatus(FeeStatus.PENDING);

		Payment payment = new Payment();
		payment.setStudentFee(fee);
		payment.setAmount(BigDecimal.valueOf(400));
		payment.setMethod(PaymentMethod.CASH);

		when(studentFeeRepository.findById(1L)).thenReturn(Optional.of(fee));
		when(paymentRepository.save(payment)).thenReturn(payment);
		when(paymentRepository.findByStudentFeeId(1L)).thenReturn(Collections.singletonList(payment));

		financeService.registerPayment(payment);

		ArgumentCaptor<StudentFee> captor = ArgumentCaptor.forClass(StudentFee.class);
		verify(studentFeeRepository).save(captor.capture());
		assertEquals(FeeStatus.PARTIAL, captor.getValue().getStatus());
	}

	@Test
	void shouldSetPendingStatusWhenNoPayments() {
		StudentFee fee = new StudentFee();
		fee.setId(1L);
		fee.setAmount(BigDecimal.valueOf(1000));
		fee.setStatus(FeeStatus.PARTIAL);

		Payment payment = new Payment();
		payment.setStudentFee(fee);
		payment.setAmount(BigDecimal.ZERO);
		payment.setMethod(PaymentMethod.CASH);

		when(studentFeeRepository.findById(1L)).thenReturn(Optional.of(fee));
		when(paymentRepository.save(payment)).thenReturn(payment);
		when(paymentRepository.findByStudentFeeId(1L)).thenReturn(Collections.singletonList(payment));

		financeService.registerPayment(payment);

		ArgumentCaptor<StudentFee> captor = ArgumentCaptor.forClass(StudentFee.class);
		verify(studentFeeRepository).save(captor.capture());
		assertEquals(FeeStatus.PENDING, captor.getValue().getStatus());
	}

	@Test
	void shouldThrowWhenDeleteNonexistentFee() {
		when(studentFeeRepository.findById(999L)).thenReturn(Optional.empty());

		assertThrows(IllegalArgumentException.class, () -> financeService.deleteFee(999L));
	}
}
