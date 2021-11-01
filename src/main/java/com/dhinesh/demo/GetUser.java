package com.dhinesh.demo;

import java.util.*;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.SecurityContext;
import java.security.Principal;

@Path("/users")
public class GetUser {
	
	UserRepository repo = new UserRepository();
	
	@GET
	@Secured
	@Produces("application/json")
	public List<User> getUser(@Context SecurityContext securityContext) {	
		Principal principal = securityContext.getUserPrincipal();
		String username = principal.getName();

		System.out.println(username);
		
		return repo.getAllUsers();
		
	}

	@GET
	@Path("/user/{id}")
	@Produces("application/json")
	public User getUserById(@PathParam("id") String id) {

		User user = repo.getUserById(id);

		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			return user;
		}

	}
	

	@POST
	@Path("/create")
	@Produces("application/json")
	@Consumes("application/json")
	public User addUser(User user) {
		
		return repo.addUser(user);
		
	}

	@PUT
	@Path("/update")
	@Produces("application/json")
	@Consumes("application/json")
	public User updateUser(User user) {

		// if the user does not exist, either throw 404 or create user.
		if(repo.getUserById(user.getId()) == null) {
			// throw new WebApplicationException(Response.Status.NOT_FOUND);

			System.out.println("User not found. Created user.");
			return repo.addUser(user);
		}
		
		return repo.update(user);
		
	}

	@DELETE
	@Path("/delete/{id}")
	@Produces("application/json")
	@Consumes("application/json")
	public User deleteUser(@PathParam("id") String id) {

		User user = repo.getUserById(id);

		if(user == null) {
			throw new WebApplicationException(Response.Status.NOT_FOUND);
		} else {
			repo.delete(id);
		}

		return user;
		
	}
	
}
