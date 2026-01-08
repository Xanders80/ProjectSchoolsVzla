package com.school.web.controller.library;

import java.time.LocalDate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.school.core.service.UserService;
import com.school.core.validation.ValidId;
import com.school.library.entity.Book;
import com.school.library.entity.Loan;
import com.school.library.enums.BookStatus;
import com.school.library.service.LibraryService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/library")
@Validated
public class LibraryController {

    private static final Logger logger = LoggerFactory.getLogger(LibraryController.class);
    private final LibraryService libraryService;
    private final UserService userService;

    public LibraryController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }

    // Books
    @GetMapping("/books")
    @SuppressWarnings("null")
    public String listBooks(Model model, @PageableDefault(size = 10, sort = "title") Pageable pageable) {
        model.addAttribute("books", libraryService.getAllBooks(pageable));
        return "library/book-list";
    }

    @GetMapping("/books/new")
    public String newBookForm(Model model) {
        model.addAttribute("book", new Book());
        return "library/book-form";
    }

    @PostMapping("/books")
    public String saveBook(@Valid @ModelAttribute @org.springframework.lang.NonNull Book book,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "library/book-form";
        }
        libraryService.saveBook(book);
        return "redirect:/library/books";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable @org.springframework.lang.NonNull Long id, Model model) {
        model.addAttribute("book",
                libraryService.getBookById(id)
                        .orElseThrow(() -> new IllegalArgumentException("Invalid book Id:" + id)));
        return "library/book-form";
    }

    @RequestMapping(value = "/books/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    public String deleteBook(@PathVariable @ValidId String id,
            RedirectAttributes redirectAttributes,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Long bookId;

        try {
            bookId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            logger.warn("Invalid book ID format: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute("errorMessage", "ID de libro inválido");
            return "redirect:/library/books";
        }

        try {
            libraryService.deleteBook(bookId);
            logger.info("Book {} deleted successfully by IP: {}", bookId, clientIp);
            redirectAttributes.addFlashAttribute("successMessage", "Libro eliminado exitosamente");
        } catch (EntityNotFoundException e) {
            logger.warn("Attempt to delete non-existent book ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute("errorMessage", "Libro no encontrado");
        } catch (IllegalStateException e) {
            logger.warn("Business rule violation deleting book ID: {} from IP: {}", id, clientIp);
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            logger.error("Unexpected error deleting book ID: {} from IP: {}", id, clientIp, e);
            redirectAttributes.addFlashAttribute("errorMessage", "Error interno del sistema");
        }
        return "redirect:/library/books";
    }

    @DeleteMapping("/books/api/{id}")
    @org.springframework.security.access.prepost.PreAuthorize("hasRole('ADMIN')")
    @ResponseBody
    public ResponseEntity<?> deleteBookApi(@PathVariable @ValidId String id,
            HttpServletRequest request) {
        String clientIp = request.getRemoteAddr();
        Long bookId;

        try {
            bookId = Long.parseLong(id);
        } catch (NumberFormatException e) {
            logger.warn("Invalid book ID format in API call: {} from IP: {}", id, clientIp);
            return ResponseEntity.badRequest().body(java.util.Map.of("error", "ID de libro inválido"));
        }

        try {
            libraryService.deleteBook(bookId);
            logger.info("Book {} deleted via API by IP: {}", bookId, clientIp);
            return ResponseEntity.ok(java.util.Map.of("message", "Libro eliminado exitosamente"));
        } catch (EntityNotFoundException e) {
            logger.warn("API call to delete non-existent book ID: {} from IP: {}", id, clientIp);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(java.util.Map.of("error", "Libro no encontrado"));
        } catch (Exception e) {
            logger.error("API delete error for book ID: {} from IP: {}", id, clientIp, e);
            return ResponseEntity.status(500).body(java.util.Map.of("error", "Error eliminando libro"));
        }
    }

    // Loans
    @GetMapping("/loans")
    public String listLoans(Model model) {
        model.addAttribute("loans", libraryService.getAllLoans());
        return "library/loan-list";
    }

    @GetMapping("/loans/new")
    public String newLoanForm(Model model) {
        model.addAttribute("loan", new Loan());
        // Find available books only
        // Ideally checking status in stream or query, for now simple filter
        // This might be inefficient if many books, but okay for MVP
        model.addAttribute("books", libraryService.getAllBooks().stream()
                .filter(b -> b.getStatus() == BookStatus.AVAILABLE)
                .toList());
        model.addAttribute("users", userService.findAllUsers()); // Assuming findAllUsers exists or similar
        return "library/loan-form";
    }

    @PostMapping("/loans")
    public String createLoan(
            @RequestParam @NonNull Long bookId,
            @RequestParam @NonNull Long userId,
            @RequestParam @NonNull String dueDate) {

        try {
            LocalDate parsedDate = java.time.LocalDate.parse(dueDate);
            if (parsedDate != null) {
                libraryService.borrowBook(bookId, userId, parsedDate);
            } else {
                // Esto no debería ocurrir con LocalDate.parse(), pero para el warning
                throw new IllegalArgumentException("Fecha inválida: " + dueDate);
            }
        } catch (java.time.format.DateTimeParseException e) {
            throw new IllegalArgumentException("Formato de fecha inválido: " + dueDate, e);
        }

        return "redirect:/library/loans";
    }

    @PostMapping("/loans/return/{id}")
    public String returnBook(@PathVariable @org.springframework.lang.NonNull Long id) {
        libraryService.returnBook(id);
        return "redirect:/library/loans";
    }
}
