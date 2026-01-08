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
import com.school.core.repository.UserRepository;
import com.school.library.entity.Book;
import com.school.library.entity.DigitalResource;
import com.school.library.entity.Loan;
import com.school.library.enums.BookStatus;
import com.school.library.enums.LoanStatus;
import com.school.library.repository.BookRepository;
import com.school.library.repository.DigitalResourceRepository;
import com.school.library.repository.LoanRepository;

@Service
public class LibraryService {

    private final BookRepository bookRepository;
    private final DigitalResourceRepository digitalResourceRepository;
    private final LoanRepository loanRepository;
    private final UserRepository userRepository;

    public LibraryService(BookRepository bookRepository,
            DigitalResourceRepository digitalResourceRepository,
            LoanRepository loanRepository,
            UserRepository userRepository) {
        this.bookRepository = bookRepository;
        this.digitalResourceRepository = digitalResourceRepository;
        this.loanRepository = loanRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<Book> getDigitalBooks() {
        return bookRepository.findAll().stream()
                .filter(b -> b.isDigital() && !b.isDeleted())
                .toList();
    }

    @Transactional(readOnly = true)
    @NonNull
    public List<DigitalResource> getAllDigitalResources() {
        List<DigitalResource> resources = digitalResourceRepository.findByDeletedFalse();
        return resources != null ? resources : List.of();
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<DigitalResource> getAllDigitalResources(@NonNull Pageable pageable) {
        return digitalResourceRepository.findAll(pageable);
    }

    @Transactional
    @NonNull
    public DigitalResource saveDigitalResource(@NonNull DigitalResource resource) {
        return digitalResourceRepository.save(resource);
    }

    @Transactional(readOnly = true)
    @NonNull
    public Optional<DigitalResource> getDigitalResourceById(@NonNull Long id) {
        return digitalResourceRepository.findById(id);
    }

    @Transactional
    public void deleteDigitalResource(@NonNull Long id) {
        DigitalResource resource = digitalResourceRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Recurso no encontrado"));
        resource.setDeleted(true);
        digitalResourceRepository.save(resource);
    }

    @Transactional
    @NonNull
    public Book saveBook(@NonNull Book book) {
        return bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    @NonNull
    public Page<Book> getAllBooks(@NonNull Pageable pageable) {
        return bookRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    @NonNull
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    @Transactional(readOnly = true)
    @NonNull
    public Optional<Book> getBookById(@NonNull Long id) {
        return bookRepository.findById(id);
    }

    @Transactional
    public void deleteBook(@NonNull Long id) {
        Book book = bookRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));
        book.setDeleted(true);
        book.setDeletedAt(java.time.LocalDateTime.now());
        bookRepository.save(book);
    }

    @Transactional(readOnly = true)
    @NonNull
    public List<Loan> getAllLoans() {
        return loanRepository.findAll();
    }

    @Transactional
    public void borrowBook(@NonNull Long bookId, @NonNull Long userId, @NonNull LocalDate dueDate) {
        Book book = bookRepository.findById(bookId)
                .orElseThrow(() -> new IllegalArgumentException("Libro no encontrado"));

        if (book.getStatus() != BookStatus.AVAILABLE) {
            throw new IllegalStateException("El libro no está disponible para préstamo");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado"));

        Loan loan = new Loan();
        loan.setBook(book);
        loan.setBorrower(user);
        loan.setLoanDate(LocalDate.now());
        loan.setDueDate(dueDate);
        loan.setStatus(LoanStatus.ACTIVE);

        book.setStatus(BookStatus.BORROWED);

        loanRepository.save(loan);
        bookRepository.save(book);
    }

    @Transactional
    public void returnBook(@NonNull Long loanId) {
        Loan loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new IllegalArgumentException("Préstamo no encontrado"));

        loan.setReturnDate(LocalDate.now());
        loan.setStatus(LoanStatus.RETURNED);

        Book book = loan.getBook();
        book.setStatus(BookStatus.AVAILABLE);

        loanRepository.save(loan);
        bookRepository.save(book);
    }
}
