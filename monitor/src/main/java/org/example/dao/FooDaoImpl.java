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
             PreparedStatement statement = connection.prepareStatement("SELECT group_id, GROUP_CONCAT(num ORDER BY created DESC) as nums FROM foo GROUP BY group_id");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                nums.add(new String[]{resultSet.getString("group_id"), resultSet.getString("nums")});
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return nums;
    }

    @Override
    public int groupSize() {
        try (Connection connection = ConnectionFactory.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT COUNT(DISTINCT group_id) as size FROM foo");
             ResultSet resultSet = statement.executeQuery()) {

            if (resultSet.next()) {
                return resultSet.getInt("size");
            } else {
                return 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0;
    }
}
