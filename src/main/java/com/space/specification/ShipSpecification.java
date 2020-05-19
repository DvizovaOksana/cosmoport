package com.space.specification;

import com.space.model.Ship;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.Date;

public class ShipSpecification implements Specification<Ship> {

    private String key;
    private SearchOperation operation;
    private Object value;

    public ShipSpecification(String key, SearchOperation operation, Object value) {
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    @Override
    public Predicate toPredicate(
            Root<Ship> root, CriteriaQuery<?> query, CriteriaBuilder builder) {

        switch (operation) {
            case EQUAL:
                return builder.equal(root.get(key), value);
            case GE:
                return builder.ge(root.get(key), (Number) value);
            case LE:
                return builder.le(root.get(key), (Number) value);
            case GE_DATE:
                return builder.greaterThanOrEqualTo(root.get(key), (Date)value);
            case LE_DATE:
                return builder.lessThanOrEqualTo(root.get(key), (Date)value);
            case LIKE:
                return builder.like(root.get(key), value.toString());
            case CONTAINS:
                return builder.like(root.get(key), "%" + value + "%");
            default:
                return null;
        }
    }
}
