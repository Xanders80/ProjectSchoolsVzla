package com.school.communication.repository;

import com.school.communication.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {
    Page<Message> findByReceiverIdOrderBySentAtDesc(Long receiverId, Pageable pageable);

    Page<Message> findBySenderIdOrderBySentAtDesc(Long senderId, Pageable pageable);

    long countByReceiverIdAndIsReadFalse(Long receiverId);
}
