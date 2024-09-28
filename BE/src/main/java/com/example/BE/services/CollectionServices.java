package com.example.BE.services;

import com.example.BE.models.Collection;
import com.example.BE.repository.CollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CollectionServices {

    @Autowired
    private CollectionRepository collectionRepository;

    public List<Collection> findAll() {
        return collectionRepository.findAll();
    }

    public Optional<Collection> findById(int id) {
        return collectionRepository.findById(id);
    }

    public Collection save(Collection collection) {
        return collectionRepository.save(collection);
    }

    public void deleteById(int id) {
        collectionRepository.deleteById(id);
    }
}
