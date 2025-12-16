package com.school.library.repository;

import com.school.library.entity.Book;
import com.school.library.enums.BookStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface BookRepository extends JpaRepository<Book, Long> {
    Optional<Book> findByIsbn(String isbn);

    List<Book> findByStatus(BookStatus status);
}
