package com.school.hr.service;

import com.school.admin.entity.Staff;
import com.school.admin.repository.StaffRepository;
import com.school.hr.entity.Contract;
import com.school.hr.entity.Payroll;
import com.school.hr.entity.StaffAttendance;
import com.school.hr.repository.ContractRepository;
import com.school.hr.repository.PayrollRepository;
import com.school.hr.repository.StaffAttendanceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class HRService {

    private final ContractRepository contractRepository;
    private final StaffAttendanceRepository attendanceRepository;
    private final PayrollRepository payrollRepository;
    private final StaffRepository staffRepository;

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

    @Transactional(readOnly = true)
    public List<Contract> getAllContracts() {
        return contractRepository.findAll();
    }

    public Contract saveContract(Contract contract) {
        return contractRepository.save(contract);
    }

    public Optional<Contract> getContractByStaffId(Long staffId) {
        return contractRepository.findByStaffId(staffId);
    }

    // --- Attendance Management ---

    public List<StaffAttendance> getAttendanceByDate(LocalDate date) {
        return attendanceRepository.findByDate(date);
    }

    public StaffAttendance markCheckIn(Long staffId, LocalTime time) {
        LocalDate today = LocalDate.now();
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new IllegalArgumentException("Staff not found"));

        StaffAttendance attendance = attendanceRepository.findByStaffIdAndDate(staffId, today)
                .orElse(new StaffAttendance());

        attendance.setStaff(staff);
        attendance.setDate(today);
        attendance.setCheckInTime(time);

        // Simple logic for status: if checkin after 9:00 -> LATE, else PRESENT
        if (time.isAfter(LocalTime.of(9, 0))) {
            attendance.setStatus(StaffAttendance.AttendanceStatus.LATE);
        } else {
            attendance.setStatus(StaffAttendance.AttendanceStatus.PRESENT);
        }

        return attendanceRepository.save(attendance);
    }

    public StaffAttendance markCheckOut(Long staffId, LocalTime time) {
        LocalDate today = LocalDate.now();
        StaffAttendance attendance = attendanceRepository.findByStaffIdAndDate(staffId, today)
                .orElseThrow(() -> new IllegalArgumentException("No check-in record found for today"));

        attendance.setCheckOutTime(time);
        return attendanceRepository.save(attendance);
    }

    // --- Payroll Management ---

    @Transactional(readOnly = true)
    public List<Payroll> getPayrollByPeriod(String period) {
        return payrollRepository.findByPeriod(period);
    }

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
                payroll.setNetSalary(contract.getSalary()
                        .add(payroll.getBonuses())
                        .subtract(payroll.getDeductions()));

                payrollRepository.save(payroll);
            }
        }
    }

    public void payPayroll(Long payrollId) {
        Payroll payroll = payrollRepository.findById(payrollId)
                .orElseThrow(() -> new IllegalArgumentException("Payroll record not found"));
        payroll.setStatus(Payroll.PaymentStatus.PAID);
        payroll.setPaymentDate(LocalDate.now());
        payrollRepository.save(payroll);
    }
}
