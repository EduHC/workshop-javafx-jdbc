package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import db.DB;
import db.DbException;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentDaoJDBC implements DepartmentDao{
	Connection connection;
	
	public DepartmentDaoJDBC(Connection connection) {
		this.connection = connection;
	}

	@Override
	public void insert(Department newDepartment) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(
					"INSERT INTO department "
					+ "(Name) "
					+ "VALUES "
					+ "(?)",
					Statement.RETURN_GENERATED_KEYS);
			
			statement.setString(1, newDepartment.getName());
			
			int rowsAffected = statement.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet result = statement.getGeneratedKeys();
				
				if(result.next()) {
					int departmentId = result.getInt(1);
					newDepartment.setId(departmentId);
				}
				
				DB.closeResultSet(result);
			} else {
				throw new DbException("Unexpected error! No rows affected!");
			}
		} 
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(statement);
		}
	}

	@Override
	public void update(Department department) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(
					"UPDATE department "
					+ "SET Name = ? "
					+ "WHERE Id = ?");
			
			statement.setString(1, department.getName());
			statement.setInt(2, department.getId());
			
			statement.executeUpdate();
					
		} 
		catch(SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(statement);
		}
	}

	@Override
	public void deleteById(Integer id) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement("DELETE FROM department WHERE Id = ?");
			
			statement.setInt(1, id);
			statement.executeUpdate();
		}
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(statement);
		}
		
	}

	@Override
	public Department findById(Integer id) {
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.prepareStatement(
					"SELECT * "
					+ "FROM DEPARTMENT "
					+ "WHERE Id = ?");
			
			statement.setInt(1, id);
			result = statement.executeQuery();
			
			if(result.next()) {
				return instantiateDepartment(result);
			}
			return null;
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	@Override
	public List<Department> findAll() {
		PreparedStatement statement = null;
		ResultSet result = null;
				
		try {
			statement = connection.prepareStatement("SELECT * FROM DEPARTMENT");
			result = statement.executeQuery();
		
			List<Department> departmentList = new ArrayList<>();
			
			while (result.next()) {
				Department department = instantiateDepartment(result);
				
				departmentList.add(department);
			}
			return departmentList;
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		}
		finally {
			DB.closeStatement(statement);
			DB.closeResultSet(result);
		}
	}

	private Department instantiateDepartment(ResultSet result) throws SQLException {
		Department department = new Department();
		department.setId(result.getInt("Id"));
		department.setName(result.getString("Name"));
		
		return department;
	}
}
