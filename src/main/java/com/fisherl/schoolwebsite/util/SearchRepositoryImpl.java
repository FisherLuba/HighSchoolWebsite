package com.fisherl.schoolwebsite.util;


import com.fisherl.schoolwebsite.post.Post;
import org.hibernate.search.engine.search.query.SearchResult;
import org.hibernate.search.mapper.orm.Search;
import org.hibernate.search.mapper.orm.session.SearchSession;
import org.springframework.data.jpa.repository.support.JpaEntityInformation;
import org.springframework.data.jpa.repository.support.SimpleJpaRepository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.io.Serializable;
import java.util.List;

@Transactional
public class SearchRepositoryImpl<T, ID extends Serializable> extends SimpleJpaRepository<T, ID>
        implements SearchRepository<T, ID> {

    private final EntityManager entityManager;

    public SearchRepositoryImpl(Class<T> domainClass, EntityManager entityManager) {
        super(domainClass, entityManager);
        this.entityManager = entityManager;
    }

    public SearchRepositoryImpl(
            JpaEntityInformation<T, ID> entityInformation, EntityManager entityManager) {
        super(entityInformation, entityManager);
        this.entityManager = entityManager;
    }

    @Override
    public List<T> searchBy(String category, List<String> subtopics, String text, int offset, int limit, String... fields) {

        SearchResult<T> result = getSearchResult(category, subtopics, text, offset, limit, fields);

        return result.hits();
    }

    @Override
    public List<T> searchByApproved(String category, List<String> subtopics, String text, int offset, int limit, String... fields) {
        SearchSession searchSession = Search.session(entityManager);

        SearchResult<T> result = searchSession
                .search(getDomainClass())
                .where(f -> {
                            var search = f.bool().must(f.match().fields(fields).matching(text).fuzzy(2));
                            if (!category.equals(Post.RECENT_CATEGORY)) {
                                search = search.must(f.match().fields("category").matching(category));
                            }
                            return search.mustNot(f.match().fields("approved_by_id").matching(null));
                        }
                )
                .fetch(offset * limit, limit);

        return result.hits();
    }

    private SearchResult<T> getSearchResult(String category, List<String> subtopics, String text, int offset, int limit, String[] fields) {
        SearchSession searchSession = Search.session(entityManager);

        return searchSession
                .search(getDomainClass())
                .where(f -> {
                            var search = f.bool().
                                    must(f.match().fields(fields).matching(text).fuzzy(2));
                            if (!category.equals(Post.RECENT_CATEGORY)) {
                                search = search.must(f.match().fields("category").matching(category));
                            }
                            if (subtopics.isEmpty()) return search;
                            return search.must(f.terms().fields("subtopics").matchingAny(subtopics));
                        }
                )
                .fetch(offset * limit, limit);
    }
}