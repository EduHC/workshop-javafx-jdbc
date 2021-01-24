package model.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DB;
import db.DbException;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

public class SellerDaoJDBC implements SellerDao{

	private Connection connection;
	
	public SellerDaoJDBC(Connection connection) {
		this.connection = connection;
	}
	
	@Override
	public void insert(Seller newSeller) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(
					"INSERT INTO seller "
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId) "
					+ "VALUES "
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			statement.setString(1, newSeller.getName());
			statement.setString(2, newSeller.getEmail());
			statement.setDate(3, new java.sql.Date(newSeller.getBirthDate().getTime()));
			statement.setDouble(4, newSeller.getBaseSalary());
			statement.setInt(5, newSeller.getDepartment().getId());
			
			int rowsAffected = statement.executeUpdate();
			
			if (rowsAffected > 0) {
				ResultSet result = statement.getGeneratedKeys();
				
				if(result.next()) {
					int sellerId = result.getInt(1);
					newSeller.setId(sellerId);
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
	public void update(Seller Seller) {
		PreparedStatement statement = null;
		
		try {
			statement = connection.prepareStatement(
					"UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			statement.setString(1, Seller.getName());
			statement.setString(2, Seller.getEmail());
			statement.setDate(3, new java.sql.Date(Seller.getBirthDate().getTime()));
			statement.setDouble(4, Seller.getBaseSalary());
			statement.setInt(5, Seller.getDepartment().getId());
			statement.setInt(6, Seller.getId());
			
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
			statement = connection.prepareStatement("DELETE FROM seller WHERE Id = ?");
			
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
	public Seller findById(Integer id) {
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.prepareStatement(
					"SELECT SELLER.*, DEPARTMENT.NAME AS Department "
					+ "FROM SELLER "
					+ "INNER JOIN DEPARTMENT "
					+ "ON SELLER.DepartmentId = DEPARTMENT.Id "
					+ "WHERE SELLER.Id = ?");
			
			statement.setInt(1, id);
			result = statement.executeQuery();
			
			if(result.next()) {
				Department department = instantiateDepartment(result);
				
				Seller seller = instantiateSeller(result, department);
				
				return seller;
			}	
			return null;
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeResultSet(result);
			DB.closeStatement(statement);
		}
	}

	private Seller instantiateSeller(ResultSet result, Department department) throws SQLException {
		Seller seller = new Seller();
		seller.setId(result.getInt("Id"));
		seller.setName(result.getString("Name"));
		seller.setEmail(result.getString("Email"));
		seller.setBirthDate(result.getDate("BirthDate"));
		seller.setBaseSalary(result.getDouble("BaseSalary"));
		seller.setDepartment(department);
		
		return seller;
	}

	private Department instantiateDepartment(ResultSet result) throws SQLException {
		Department department = new Department();
		department.setId(result.getInt("DepartmentId"));
		department.setName(result.getString("Department"));
		
		return department;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.prepareStatement(
					"SELECT SELLER.*, DEPARTMENT.NAME AS Department "
					+ "FROM SELLER "
					+ "INNER JOIN DEPARTMENT "
					+ "ON SELLER.DepartmentId = DEPARTMENT.Id "
					+ "ORDER BY Name");
			
			result = statement.executeQuery();
			
			List<Seller> sellerList = new ArrayList<>();
			Map<Integer, Department> departmentMap = new HashMap<>();
			
			while(result.next()) {
				
				Department departmentTmp = departmentMap.get(result.getInt("DepartmentId"));
				
				if(departmentTmp == null) {
					 departmentTmp = instantiateDepartment(result);
					 departmentMap.put(result.getInt("DepartmentId"), departmentTmp);
				}
				/*usado esse método de instanciação de Map para que o relacionamento dos objetos fique correto
				 evitando que seja instanciado mais de um objeto do mesmo departamento */
				
				Seller seller = instantiateSeller(result, departmentTmp);
				
				sellerList.add(seller);
			}	
			return sellerList;
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeResultSet(result);
			DB.closeStatement(statement);
		}
	}

	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement statement = null;
		ResultSet result = null;
		
		try {
			statement = connection.prepareStatement(
					"SELECT SELLER.*, DEPARTMENT.NAME AS Department "
					+ "FROM SELLER "
					+ "INNER JOIN DEPARTMENT "
					+ "ON SELLER.DepartmentId = DEPARTMENT.Id "
					+ "WHERE DepartmentId = ? "
					+ "ORDER BY Name");
			
			statement.setInt(1, department.getId());
			result = statement.executeQuery();
			
			List<Seller> sellerList = new ArrayList<>();
			Map<Integer, Department> departmentMap = new HashMap<>();
			
			while(result.next()) {
				
				Department departmentTmp = departmentMap.get(result.getInt("DepartmentId"));
				
				if(departmentTmp == null) {
					 departmentTmp = instantiateDepartment(result);
					 departmentMap.put(result.getInt("DepartmentId"), departmentTmp);
				}
				/*usado esse método de instanciação de Map para que o relacionamento dos objetos fique correto
				 evitando que seja instanciado mais de um objeto do mesmo departamento */
				
				Seller seller = instantiateSeller(result, departmentTmp);
				
				sellerList.add(seller);
			}	
			return sellerList;
		} 
		catch (SQLException e) {
			throw new DbException(e.getMessage());
		} 
		finally {
			DB.closeResultSet(result);
			DB.closeStatement(statement);
		}
	}
	
}
