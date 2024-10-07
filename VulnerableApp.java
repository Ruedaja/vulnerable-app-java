package com.example;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.beans.factory.annotation.Value;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

@RestController
public class VulnerableApp {

    @Value("${DB_PASSWORD}")
    private String dbPassword; // Exposición de secreto

    @GetMapping("/greet")
    public String greet(@RequestParam(value = "name", defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    @GetMapping("/getUser")
    public String getUser(@RequestParam(value = "username") String username) {
        String userInfo = "";
        try {
            // Inyección de SQL
            String query = "SELECT * FROM users WHERE username = '" + username + "'";
            Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/mydb", "user", dbPassword);
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                userInfo += "User: " + rs.getString("username") + ", Email: " + rs.getString("email") + "\n";
            }
            rs.close();
            stmt.close();
            connection.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return userInfo.isEmpty() ? "User not found" : userInfo;
    }
}
