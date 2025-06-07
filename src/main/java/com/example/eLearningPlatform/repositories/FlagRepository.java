package com.example.eLearningPlatform.repositories;

import com.example.eLearningPlatform.models.entities.Flag;
import com.example.eLearningPlatform.models.enums.FlaggedType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface FlagRepository extends JpaRepository<Flag, Long> {

    Optional<Flag> findFlagByContentTypeAndObjectId(FlaggedType contentType, Long objectId);

    List<Flag> findByContentType(FlaggedType contentType);

    Optional<Flag> findByObjectIdAndContentTypeAndUserId(Long objectId, FlaggedType contentType, Long userId);

    // Delete flags for a given content type and object ID.
    // Note: This only deletes the flag records.
    @Transactional
    @Modifying
    @Query("delete from Flag f where f.contentType = ?1 and f.objectId = ?2")
    void deleteByContentTypeAndObjectId(FlaggedType contentType, Long objectId);

}
