package com.school.communication.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.school.communication.entity.ForumMessage;

@Repository
public interface ForumMessageRepository extends JpaRepository<ForumMessage, Long> {
    List<ForumMessage> findByForumIdOrderByCreatedAtAsc(Long forumId);
}
