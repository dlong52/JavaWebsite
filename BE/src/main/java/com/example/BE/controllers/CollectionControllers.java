package com.example.BE.controllers;

import com.example.BE.models.Collection;
import com.example.BE.services.CollectionServices;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/collections")
public class CollectionControllers {
    @Autowired
    private CollectionServices collectionService;

    @GetMapping
    public List<Collection> getAllCollections() {
        return collectionService.findAll();
    }
    @GetMapping("/{id}")
    public ResponseEntity<Collection> getCollectionById(@PathVariable int id) {
        return collectionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @PostMapping
    public Collection createCollection(@RequestBody Collection collection) {
        return collectionService.save(collection);
    }
    @PutMapping("/{id}")
    public ResponseEntity<Collection> updateCollection(@PathVariable int id, @RequestBody Collection collection) {
        return collectionService.findById(id)
                .map(existingCollection -> {
                    existingCollection.setName(collection.getName());
                    existingCollection.setDescription(collection.getDescription());
                    existingCollection.setActive(collection.isActive());
                    existingCollection.setCreatedAt(collection.getCreatedAt());
                    existingCollection.setUpdatedAt(collection.getUpdatedAt());
                    Collection updatedCollection = collectionService.save(existingCollection);
                    return ResponseEntity.ok(updatedCollection);
                })
                .orElse(ResponseEntity.notFound().build());
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCollection(@PathVariable int id) {
        if (collectionService.findById(id).isPresent()) {
            collectionService.deleteById(id);
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
