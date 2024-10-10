package org.prox.abmobemail.service;

import lombok.AllArgsConstructor;
import org.prox.abmobemail.entity.Keyword;
import org.prox.abmobemail.exception.ResourceNotFoundException;
import org.prox.abmobemail.repository.KeywordRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class KeywordService {
    private final KeywordRepository keywordRepository;

    public Page<Keyword> findAll(Pageable pageable) {
        return keywordRepository.findAll(pageable);
    }

    public Keyword create(Keyword keyword) {
        return keywordRepository.save(keyword);
    }

    public Keyword update(Long id, Keyword keyword) {
        Optional<Keyword> existingKeyword = keywordRepository.findById(id);
        if (existingKeyword.isPresent()) {
            Keyword updatedKeyword = existingKeyword.get();
            updatedKeyword.setTitle(keyword.getTitle());
            updatedKeyword.setEmail(keyword.getEmail());
            updatedKeyword.setKeyword(keyword.getKeyword());
            return keywordRepository.save(updatedKeyword);
        } else {
            throw new ResourceNotFoundException("Keyword not found with id " + id);
        }
    }

    public void delete(Long id) {
        keywordRepository.deleteById(id);
    }
}
