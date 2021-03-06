package org.egov.eis.persistance.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.exception.SQLGrammarException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SequenceNumberGenerator {

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Transactional(propagation = Propagation.REQUIRES_NEW, noRollbackFor = SQLGrammarException.class)
    public Serializable getNextSequence(final String sequenceName) throws SQLGrammarException {
        final List<Object> preparedStatementValues = new ArrayList<>();
        String query = "SELECT nextval (?) as nextval";
        preparedStatementValues.add(sequenceName);
        return (Serializable) jdbcTemplate.queryForObject(query, preparedStatementValues.toArray(), Integer.class);
    }
}
