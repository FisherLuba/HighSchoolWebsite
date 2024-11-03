package com.fisherl.schoolwebsite.util;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.List;

@NoRepositoryBean
public interface SearchRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {

    List<T> searchBy(String category, List<String> subtopics, String text, int offset, int limit, String... fields);
    List<T> searchByApproved(String category, List<String> subtopics, String text, int offset, int limit, String... fields);

}