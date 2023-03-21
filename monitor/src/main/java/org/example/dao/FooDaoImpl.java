package org.example.dao;

import org.example.dao.connection.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class FooDaoImpl implements FooDao {
    public List<String[]> findAllNums() {
        List<String[]> nums = new ArrayList<>();

        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT property_code, GROUP_CONCAT(num ORDER BY created DESC) as nums FROM foo GROUP BY property_code");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                nums.add(new String[]{resultSet.getString("property_code"), resultSet.getString("nums")});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nums;
    }

}
