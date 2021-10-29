package com.dhinesh.demo;

// https://gist.github.com/bdemers/1e2713df2857bc0f35b81dc2ccd0ae9e

import java.util.ArrayList;
import java.util.List;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import jakarta.xml.bind.DatatypeConverter;

import java.sql.*;

public class UserRepository {

	Connection conn = null;

	public UserRepository() {
		String url = "jdbc:mysql://localhost:3306/demo";
		String username = "root";
		String password = "root";

		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			conn = DriverManager.getConnection(url, username, password);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	public List<User> getAllUsers() {
		List<User> users = new ArrayList<User>();
		String query = "select * from Users";

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			while (rs.next()) {
				User user = new User();

				user.setId(rs.getInt("id"));
				user.setAge(rs.getInt("age"));
				user.setName(rs.getString("name"));

				users.add(user);
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return users;
	}

	public User getUserById(int id) {
		String query = "select * from Users where id=" + id;
		User user = null;

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				user = new User();

				user.setId(rs.getInt("id"));
				user.setAge(rs.getInt("age"));
				user.setName(rs.getString("name"));

			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return user;
	}

	public User addUser(User user) {
		String sql = "insert into Users values (?,?,?)";

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setInt(1, user.getId());
			st.setInt(2, user.getAge());
			st.setString(3, user.getName());

			st.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return getUserById(user.getId());
	}

	public User update(User user) {
		String sql = "update Users set age=?, name=? where id=?";

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setInt(1, user.getAge());
			st.setString(2, user.getName());
			st.setInt(3, user.getId());

			st.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}

		return getUserById(user.getId());

	}

	public void delete(int id) {
		String sql = "delete from Users where id=?";

		try {
			PreparedStatement st = conn.prepareStatement(sql);

			st.setInt(1, id);

			st.executeUpdate();
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}

	public Credentials getUserByName(String name) {
		String query = "select id,password from Users where name=" + "'" + name + "'";
		Credentials credentials = null;

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				credentials = new Credentials();

				credentials.setUserId(rs.getInt("id"));
				credentials.setUsername(name);
				credentials.setPassword(rs.getString("password"));
			}

		} catch (Exception e) {
			// e.printStackTrace();
			System.out.println(e.getMessage());
		} 

		return credentials;
	}

	public void storeToken(int id, String token) {
		
		if(existsId(id)) {
			
			String sql = "update Tokens set token=? where user_id=?";

			try {
				PreparedStatement st = conn.prepareStatement(sql);
	
				st.setString(1, token);
				st.setInt(2, id);
	
				st.executeUpdate();
	
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} 

			System.out.println("Token updated!");
		
		} else {
			String sql = "insert into Tokens (user_id,token) values (?,?)";

			try {
				PreparedStatement st = conn.prepareStatement(sql);
	
				st.setInt(1, id);
				st.setString(2, token);
	
				st.executeUpdate();
	
			} catch (SQLException e) {
				e.printStackTrace();
				System.out.println(e.getMessage());
			} 

			System.out.println("Token added!");

		}
	}

	public int isValidToken(String token) {
		String query = "select user_id from Tokens where token=" +  "'" + token + "'";
		int id = -1;

		System.out.println(query);

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(query);

			if (rs.next()) {
				id = rs.getInt("user_id");
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		} 

		return id;
	}

	private boolean existsId(int id) {
		boolean exists = false;

		String sql = "select * from Tokens where user_id=" + "'" + id + "'";

		try {
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			if (rs.next()) {
				exists = true;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

		return exists;
	}

	public String getUserFromToken(String token) {

        String key = "tUCJS5Mn-5vx1hw75Tx5vw";
 
        //This line will throw an exception if it is not a signed JWS (as expected)
        Claims claims = Jwts.parser()         
           .setSigningKey(DatatypeConverter.parseBase64Binary(key))
           .parseClaimsJws(token).getBody();

        System.out.println("ID: " + claims.getId());
        System.out.println("Subject: " + claims.getSubject());
        System.out.println("Issuer: " + claims.getIssuer());
        System.out.println("Expiration: " + claims.getExpiration());

		return claims.getSubject();
    }

}
