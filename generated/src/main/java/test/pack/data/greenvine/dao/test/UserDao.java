package test.pack.data.greenvine.dao.test;

import java.util.List;
import test.pack.data.greenvine.entity.test.User;
import java.lang.String;

import javax.annotation.Generated;

@Generated("net.sourceforge.greenvine.generator.impl.java.dao.DaoGenerator")
public interface UserDao {
	
    /**
	 * Returns the User with the specified identity
	 * or throws an unchecked {@link RuntimeException}  
	 * if an entity with matching identity not found
	 *	 
	 * @param username the value of the identity
	 * @return User with matching identity
	 */
	public abstract User loadUser(String username);
    
    /**
	 * Returns the User with the specified identity
	 * or null if an entity with matching identity not found
     *
	 * @param username the value of the identity
	 * @return User with matching identity (or null)
	 */
	public abstract User findUser(String username);
  
	/**
	 * Retrieve all User objects
	 * 
	 * @return List<User>of all User 
	 * objects in the database. 
	 */
	public abstract List<User> findAllUsers();
			
	/**
	 * Update a supplied User loaded in a separate transaction
	 * @param user the User to update
	 */
	public abstract void updateUser(User user);
	
	/**
	 * Create a supplied User object
	 * @param user the User to create
	 */
	public abstract void createUser(User user);
    
    /**
	 * Remove the User with the specified identity
	 * @param username the value of the identity
	 */
	public abstract void removeUser(String username);
	

}