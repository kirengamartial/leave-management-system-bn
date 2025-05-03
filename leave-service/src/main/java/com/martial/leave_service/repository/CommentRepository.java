package com.martial.leave_service.repository;

import com.martial.leave_service.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, String> {
    // Basic CRUD operations are provided by JpaRepository
}