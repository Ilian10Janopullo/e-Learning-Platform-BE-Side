package com.example.eLearningPlatform.services.interfaces;

import com.example.eLearningPlatform.controllers.dto.FlagDTO;
import com.example.eLearningPlatform.models.entities.Flag;
import com.example.eLearningPlatform.models.enums.FlaggedType;

import java.util.List;
import java.util.Optional;

public interface FlagService {

    Flag raiseFlag(Long flaggedById, FlagDTO flagDto);
    Flag getFlagById(Long id);
    Optional<Flag> getFlagByContentTypeAndObjectId(FlaggedType contentType, Long objectId);
    Optional<Flag> getFlagByObjectIdAndContentTypeAndUserId(Long objectId, FlaggedType contentType, Long userId);
    List<Flag> getFlagByContentType(FlaggedType contentType);

    List<Flag> getAllFlags();

    void deleteFlaggedContent(FlaggedType contentType, Long objectId);

    void deleteFlag(Long id);
}
