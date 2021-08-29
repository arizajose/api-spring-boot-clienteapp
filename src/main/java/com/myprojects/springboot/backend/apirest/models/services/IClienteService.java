package com.myprojects.springboot.backend.apirest.models.services;

import java.util.List;

import com.myprojects.springboot.backend.apirest.models.entity.Cliente;

public interface IClienteService {
	
	public List<Cliente> findAll();
	
	public Cliente save( Cliente cliente);
	
	public void delete(Integer id);
	
	public Cliente findById(Integer id);

}
