package com.example.eLearningPlatform.services.classes;

import com.example.eLearningPlatform.controllers.dto.FlagDTO;
import com.example.eLearningPlatform.models.entities.Flag;
import com.example.eLearningPlatform.models.entities.User;
import com.example.eLearningPlatform.models.enums.FlaggedType;
import com.example.eLearningPlatform.repositories.FlagRepository;
import com.example.eLearningPlatform.repositories.CommentRepository;
import com.example.eLearningPlatform.repositories.ReviewRepository;
import com.sun.jdi.LongValue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.eLearningPlatform.repositories.UserRepository;
import com.example.eLearningPlatform.services.interfaces.FlagService;
import com.example.eLearningPlatform.services.interfaces.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class FlagServiceImpl implements FlagService {

    private final FlagRepository flagRepository;
    private final CommentRepository commentRepository;
    private final ReviewRepository reviewRepository;

    @Autowired
    public FlagServiceImpl(FlagRepository flagRepository,
                           CommentRepository commentRepository,
                           ReviewRepository reviewRepository) {
        this.flagRepository = flagRepository;
        this.commentRepository = commentRepository;
        this.reviewRepository = reviewRepository;
    }

    @Override
    public Optional<Flag> getFlagByContentTypeAndObjectId(FlaggedType contentType, Long objectId) {
        return flagRepository.findFlagByContentTypeAndObjectId(contentType, objectId);
    }

    @Override
    public Optional<Flag> getFlagByObjectIdAndContentTypeAndUserId(Long objectId, FlaggedType contentType, Long userId) {
        return flagRepository.findByObjectIdAndContentTypeAndUserId(objectId, contentType, userId);
    }

    @Override
    public List<Flag> getFlagByContentType(FlaggedType contentType) {
        return flagRepository.findByContentType(contentType);
    }

    @Override
    public List<Flag> getAllFlags() {
        return flagRepository.findAll();
    }

    @Override
    @Transactional
    public void deleteFlaggedContent(FlaggedType contentType, Long objectId) {
        if (FlaggedType.COMMENT.equals(contentType)) {
            commentRepository.deleteById(objectId);
        } else if (FlaggedType.REVIEW.equals(contentType)) {
            reviewRepository.deleteById(objectId);
        }

        flagRepository.deleteByContentTypeAndObjectId(contentType, objectId);
    }

    @Override
    @Transactional
    public void deleteFlag(Long id) {
        flagRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Flag raiseFlag(Long flaggedById, FlagDTO flagDto) {

        Flag flag = new Flag();
        flag.setUserId(flaggedById);
        flag.setReason(flagDto.getReason());
        flag.setContentType(flagDto.getContentType());
        flag.setObjectId(flagDto.getObjectId());
        flag.setCreatedAt(LocalDateTime.now());

        return flagRepository.save(flag);
    }

    @Override
    public Flag getFlagById(Long id) {
        return flagRepository.findById(id).orElseThrow(() -> new RuntimeException("Flag not found!"));
    }
}
