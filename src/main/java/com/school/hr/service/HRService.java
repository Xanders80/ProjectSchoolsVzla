package com.school.hr.service;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.admin.entity.Staff;
import com.school.admin.repository.StaffRepository;
import com.school.hr.entity.Contract;
import com.school.hr.entity.Payroll;
import com.school.hr.entity.StaffAttendance;
import com.school.hr.repository.ContractRepository;
import com.school.hr.repository.PayrollRepository;
import com.school.hr.repository.StaffAttendanceRepository;

@Service
@Transactional(readOnly = true)
public class HRService {

    private final ContractRepository contractRepository;
    private final StaffAttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;
	private final StaffRepository staffRepository;

	@Value("${app.hr.late-threshold:09:00}")
	private String lateThreshold;

    public HRService(ContractRepository contractRepository,
            StaffAttendanceRepository attendanceRepository,
            PayrollRepository payrollRepository,
            StaffRepository staffRepository) {
        this.contractRepository = contractRepository;
        this.attendanceRepository = attendanceRepository;
        this.payrollRepository = payrollRepository;
        this.staffRepository = staffRepository;
    }

    // --- Contract Management ---

	public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

	@Transactional
	public Contract saveContract(@NonNull Contract contract) {
        return contractRepository.save(contract);
    }

    public Optional<Contract> getContractByStaffId(Long staffId) {
        return contractRepository.findByStaffId(staffId);
    }

    // --- Attendance Management ---

    public List<StaffAttendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

	@Transactional
	public StaffAttendance markCheckIn(@NonNull Long staffId, LocalTime time) {
        LocalDate today = LocalDate.now();
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Personal no encontrado"));

        StaffAttendance attendance = attendanceRepository.findByStaffIdAndDate(staffId, today)
                .orElse(new StaffAttendance());

        attendance.setStaff(staff);
        attendance.setDate(today);
        attendance.setCheckInTime(time);

        // Simple logic for status: if checkin after 9:00 -> LATE, else PRESENT
		if (time.isAfter(LocalTime.parse(lateThreshold))) {
            attendance.setStatus(StaffAttendance.AttendanceStatus.LATE);
        } else {
            attendance.setStatus(StaffAttendance.AttendanceStatus.PRESENT);
        }

        return attendanceRepository.save(attendance);
    }

	@Transactional
	public StaffAttendance markCheckOut(Long staffId, LocalTime time) {
        LocalDate today = LocalDate.now();
        StaffAttendance attendance = attendanceRepository.findByStaffIdAndDate(staffId, today)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro registro de check-in para hoy"));

        attendance.setCheckOutTime(time);
        return attendanceRepository.save(attendance);
    }

    // --- Payroll Management ---

	public List<Payroll> getPayrollByPeriod(String period) {
        return payrollRepository.findByPeriod(period);
    }

	@Transactional
	public void generatePayrollForPeriod(String period) {
        List<Contract> activeContracts = contractRepository.findAll().stream()
                .filter(Contract::isActive)
                .toList();

        for (Contract contract : activeContracts) {
            if (!payrollRepository.existsByStaffIdAndPeriod(contract.getStaff().getId(), period)) {
                Payroll payroll = new Payroll();
                payroll.setStaff(contract.getStaff());
                payroll.setPeriod(period);
                payroll.setBaseSalary(contract.getSalary());
                payroll.setStatus(Payroll.PaymentStatus.PENDING);

                // Calculate Net (simplified: base + bonus - deductions)
                // For now just Base
                java.math.BigDecimal salary = contract.getSalary() != null ? contract.getSalary()
                        : java.math.BigDecimal.ZERO;
                payroll.setNetSalary(salary
                        .add(payroll.getBonuses())
                        .subtract(payroll.getDeductions()));

                payrollRepository.save(payroll);
            }
        }
    }

	@Transactional
	public void payPayroll(@NonNull Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new IllegalArgumentException("No se encontro registro de nomina"));
        payroll.setStatus(Payroll.PaymentStatus.PAID);
        payroll.setPaymentDate(LocalDate.now());
        payrollRepository.save(payroll);
    }
}
