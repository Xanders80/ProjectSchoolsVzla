package com.school.communication.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.school.communication.entity.CourseForum;

@Repository
public interface CourseForumRepository extends JpaRepository<CourseForum, Long> {
    Optional<CourseForum> findByCourseId(Long courseId);
}
