package com.school.web.controller.library;

import com.school.core.service.UserService;
import com.school.library.entity.Book;
import com.school.library.entity.Loan;
import com.school.library.enums.BookStatus;
import com.school.library.service.LibraryService;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/library")
public class LibraryController {

    private final LibraryService libraryService;
    private final UserService userService;

    public LibraryController(LibraryService libraryService, UserService userService) {
        this.libraryService = libraryService;
        this.userService = userService;
    }

    // Books
    @GetMapping("/books")
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
    public String saveBook(@Valid @ModelAttribute Book book, BindingResult result,
            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "library/book-form";
        }
        try {
            libraryService.saveBook(book);
            redirectAttributes.addFlashAttribute("successMessage", "Libro guardado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al guardar el libro: " + e.getMessage());
            return "library/book-form";
        }
        return "redirect:/library/books";
    }

    @GetMapping("/books/edit/{id}")
    public String editBookForm(@PathVariable Long id, Model model) {
        libraryService.getBookById(id).ifPresent(book -> model.addAttribute("book", book));
        return "library/book-form";
    }

    // Books
    @RequestMapping(value = "/books/delete/{id}", method = { RequestMethod.POST, RequestMethod.DELETE })
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.deleteBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Libro eliminado exitosamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al eliminar el libro: " + e.getMessage());
        }
        return "redirect:/library/books";
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
            @RequestParam Long bookId,
            @RequestParam Long userId,
            @RequestParam String dueDate,
            RedirectAttributes redirectAttributes) {
        try {
            libraryService.borrowBook(bookId, userId, java.time.LocalDate.parse(dueDate));
            redirectAttributes.addFlashAttribute("successMessage", "Préstamo registrado correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al registrar préstamo: " + e.getMessage());
        }
        return "redirect:/library/loans";
    }

    @PostMapping("/loans/return/{id}")
    public String returnBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            libraryService.returnBook(id);
            redirectAttributes.addFlashAttribute("successMessage", "Libro devuelto correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error al devolver libro: " + e.getMessage());
        }
        return "redirect:/library/loans";
    }
}
