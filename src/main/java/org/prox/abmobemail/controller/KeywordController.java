package org.prox.abmobemail.controller;

import lombok.AllArgsConstructor;
import org.prox.abmobemail.entity.Keyword;
import org.prox.abmobemail.service.KeywordService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/keywords")
@AllArgsConstructor
@CrossOrigin("*")
public class KeywordController {

    private KeywordService keywordService;

    @GetMapping
    public ResponseEntity<Page<Keyword>> getAllKeywords(Pageable pageable) {
        Page<Keyword> keywords = keywordService.findAll(pageable);
        return new ResponseEntity<>(keywords, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Keyword> createKeyword(@RequestBody Keyword keyword) {
        Keyword createdKeyword = keywordService.create(keyword);
        return new ResponseEntity<>(createdKeyword, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Keyword> updateKeyword(@PathVariable Long id, @RequestBody Keyword keyword) {
        Keyword updatedKeyword = keywordService.update(id, keyword);
        return new ResponseEntity<>(updatedKeyword, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteKeyword(@PathVariable Long id) {
        keywordService.delete(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
