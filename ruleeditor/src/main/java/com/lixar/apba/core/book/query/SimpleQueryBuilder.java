package com.lixar.apba.core.book.query;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.LinkedHashMap;

public class SimpleQueryBuilder<T> {

    private EntityManager entityManager;
    private Class<T> clazz;
    private StringBuilder sb = new StringBuilder();
    private String select;
    private LinkedHashMap<String, Object> parameterMap = new LinkedHashMap<>();

    public SimpleQueryBuilder(EntityManager entityManager, Class<T> clazz) {
        this.entityManager = entityManager;
        this.clazz = clazz;
        select = "SELECT d FROM " + clazz.getName() + " d WHERE";
    }

    public void addEqualUnlessNull(String name, Integer value) {
        _addTermUnlessNull(name, value);
    }

    public void addEqualUnlessNull(String name, String value) {
        _addTermUnlessNull(name, value);
    }

    public void addLessThenUnlessNull(String name, Integer value) {
        _addTermUnlessNull(name, "<", value);
    }

    public TypedQuery<T> buildQuery() {
        TypedQuery<T> query = entityManager.createQuery(select + sb.toString(), clazz);
        for (String key : parameterMap.keySet()) {
            query.setParameter(key, parameterMap.get(key));
        }

        return query;
    }

    private void _addTermUnlessNull(String name, Object value) {
        _addTermUnlessNull(name, "=", value);
    }

    private void _addTermUnlessNull(String name, String operator, Object value) {
        if (value == null) {
            return;
        }

        appendAndIfRequired();

        sb.append(" d.").append(name).append(" ").append(operator).append(" :").append(name);

        parameterMap.put(name, value);
    }

    private void appendAndIfRequired() {
        if (sb.length() > 0) {
            sb.append(" AND");
        }
    }
}
