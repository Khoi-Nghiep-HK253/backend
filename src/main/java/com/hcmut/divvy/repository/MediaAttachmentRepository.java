package com.hcmut.divvy.repository;

import com.hcmut.divvy.entity.MediaAttachment;
import com.hcmut.divvy.entity.enums.MediaEntityType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MediaAttachmentRepository extends JpaRepository<MediaAttachment, Integer> {

    /** Finds all attachments associated with the given entity type and entity ID. */
    List<MediaAttachment> findByEntityTypeAndEntityId(MediaEntityType entityType, Integer entityId);

    /** Checks if any attachment exists for the given entity. */
    boolean existsByEntityTypeAndEntityId(MediaEntityType entityType, Integer entityId);

    /** Deletes all attachments associated with the given entity (e.g. when deleting an Expense). */
    void deleteAllByEntityTypeAndEntityId(MediaEntityType entityType, Integer entityId);
}
