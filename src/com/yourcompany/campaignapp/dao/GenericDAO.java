package com.yourcompany.campaignapp.dao;

import java.sql.SQLException;
import java.util.List;

public interface GenericDAO<T> {

    T create(T entity) throws SQLException;

    T read(int id) throws SQLException;

    boolean update(T entity) throws SQLException;

    boolean delete(int id) throws SQLException;

    List<T> readAll() throws SQLException;
}