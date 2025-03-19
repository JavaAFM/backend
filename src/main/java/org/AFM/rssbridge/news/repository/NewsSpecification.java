package org.AFM.rssbridge.news.repository;

import org.AFM.rssbridge.dto.request.FilterRequest;
import org.AFM.rssbridge.news.model.News;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Join;

public class NewsSpecification {
    public static Specification<News> filterByCriteria(FilterRequest filterRequest) {
        return (root, query, criteriaBuilder) -> {
            var predicates = criteriaBuilder.conjunction();

            if (filterRequest.getSource_name() != null
                    && !filterRequest.getSource_name().isEmpty()
                    && !"allSources".equals(filterRequest.getSource_name())) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.equal(root.get("source").get("name"), filterRequest.getSource_name()));
            }
            if (filterRequest.getFrom() != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.greaterThanOrEqualTo(root.get("publicationDate"), filterRequest.getFrom().atStartOfDay()));
            }
            if (filterRequest.getTo() != null) {
                predicates = criteriaBuilder.and(predicates,
                        criteriaBuilder.lessThanOrEqualTo(root.get("publicationDate"), filterRequest.getTo().atTime(23, 59, 59)));
            }
            query.orderBy(criteriaBuilder.desc(root.get("publicationDate")));

            return predicates;
        };
    }
}
