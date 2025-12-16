package com.school.library.entity;

import com.school.core.listener.AuditEntityListener;
import com.school.library.enums.BookStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity
@Table(name = "books", indexes = {
        @Index(name = "idx_book_isbn", columnList = "isbn", unique = true)
})
@EntityListeners(AuditEntityListener.class)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    @Column(nullable = false)
    @NotBlank(message = "El título es obligatorio")
    private String title;

    @Column(nullable = false)
    @NotBlank(message = "El autor es obligatorio")
    private String author;

    @Column(nullable = false)
    @NotBlank(message = "La categoría es obligatoria")
    private String category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @NotNull(message = "El estado es obligatorio")
    private BookStatus status;

    public Book() {
        this.status = BookStatus.AVAILABLE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public BookStatus getStatus() {
        return status;
    }

    public void setStatus(BookStatus status) {
        this.status = status;
    }
}
