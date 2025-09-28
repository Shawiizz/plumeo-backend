package fr.shawiizz.plumeo.repository;

import fr.shawiizz.plumeo.entity.File;
import fr.shawiizz.plumeo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FileRepository extends JpaRepository<File, String> {
    
    List<File> findByAuthor(User author);
    
    Page<File> findByAuthor(User author, Pageable pageable);
    
    List<File> findByAuthorAndIsPrivate(User author, Boolean isPrivate);
    
    Optional<File> findByIdAndAuthor(String id, User author);
    
    boolean existsByIdAndAuthor(String id, User author);
}