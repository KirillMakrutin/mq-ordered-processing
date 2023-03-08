package org.example.dao;

import java.util.List;

public interface FooDao {
    List<Integer> findAllNums(int limit);
}
