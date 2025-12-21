package com.school.library.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.school.core.entity.User;
import com.school.core.service.UserService;
import com.school.library.entity.Book;
import com.school.library.entity.Loan;
import com.school.library.enums.BookStatus;
import com.school.library.enums.LoanStatus;
import com.school.library.repository.BookRepository;
import com.school.library.repository.LoanRepository;

@Service
@Transactional
public class LibraryService {

    private final BookRepository bookRepository;
    private final LoanRepository loanRepository;
    private final UserService userService;

    public LibraryService(BookRepository bookRepository, LoanRepository loanRepository, UserService userService) {
        this.bookRepository = bookRepository;
        this.loanRepository = loanRepository;
        this.userService = userService;
    }

    // Book Operations
    public Page<Book> getAllBooks(@NonNull Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Optional<Book> getBookById(@NonNull Long id) {
        return bookRepository.findById(id);
    }

    public Book saveBook(@NonNull Book book) {
        return bookRepository.save(book);
    }

    public void deleteBook(@NonNull Long id) {
        bookRepository.deleteById(id);
    }

    // Loan Operations
    public Loan borrowBook(@NonNull Long bookId, @NonNull Long userId, @NonNull LocalDate dueDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid book ID"));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("Book is not available for borrowing");
        }

        User borrower = userService.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setBorrower(borrower);
        loan.setDueDate(dueDate);

        // Update Book Status
        book.setStatus(BookStatus.BORROWED);
        bookRepository.save(book);

        return loanRepository.save(loan);
    }

    public void returnBook(@NonNull Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid loan ID"));

        if (loan.getStatus() != LoanStatus.ACTIVE && loan.getStatus() != LoanStatus.OVERDUE) {
            throw new IllegalStateException("Loan is already returned");
        }

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        // Update Book Status
        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);
        bookRepository.save(book);

        loanRepository.save(loan);
    }

    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    public List<Loan> getActiveLoans() {
        return loanRepository.findByStatus(LoanStatus.ACTIVE);
    }

    public Optional<Loan> getLoanById(@NonNull Long id) {
        return loanRepository.findById(id);
    }
}
