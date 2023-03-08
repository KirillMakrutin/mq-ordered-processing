package org.example.dao;

import org.example.dao.connection.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FooDaoImpl implements FooDao {
    public List<Integer> findAllNums(int limit) {
        List<Integer> nums = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT num FROM foo ORDER BY created DESC LIMIT ?")) {

            statement.setInt(1, limit);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    int num = resultSet.getInt("num");
                    nums.add(num);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nums;
    }
}
