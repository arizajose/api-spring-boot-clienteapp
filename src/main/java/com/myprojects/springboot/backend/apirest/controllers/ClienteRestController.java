package com.myprojects.springboot.backend.apirest.controllers;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.myprojects.springboot.backend.apirest.models.entity.Cliente;
import com.myprojects.springboot.backend.apirest.models.services.IClienteService;

@CrossOrigin(origins = { "http://localhost:4200" })
@RestController
@RequestMapping(path = "/api")
public class ClienteRestController {

	@Autowired
	private IClienteService clienteService;

	@GetMapping(path = "/clientes")
	@ResponseStatus(code = HttpStatus.OK)
	public List<Cliente> index() {
		return clienteService.findAll();
	}

	@GetMapping(path = "/clientes/{id}")
	// @ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> show(@PathVariable Integer id) {

		Cliente cliente = null;
		Map<String, Object> response = new HashMap<>();

		try {
			cliente = clienteService.findById(id);
			if (cliente == null) {
				response.put("mensaje",
						"El cliente con id ".concat(id.toString() + " no esta registrado en el sistema."));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			} else {
				return new ResponseEntity<Cliente>(cliente, HttpStatus.OK);
			}
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al realizar la consulta a la base de datos.");
			response.put("error", e.getMessage().concat(": " + e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping(path = "/clientes")
	// @ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<?> create(@Valid @RequestBody Cliente cliente, BindingResult bindingResult) {
		// cliente.setCreateAt(new Date());
		Cliente clienteNew = null;
		Map<String, Object> response = new HashMap<>();
		
		if (bindingResult.hasErrors()) {
			//Option 1
			/*List<String> errorsList = new ArrayList<>();
			for (FieldError error : bindingResult.getFieldErrors()) {
				errorsList.add("El campo '"+error.getField()+"' "+error.getDefaultMessage());
			}*/
			//Option 2 : arrow functions
			List<String> errorsList = bindingResult.getFieldErrors()
					.stream()
					.map((error) -> { return "El campo '"+error.getField()+"' "+error.getDefaultMessage();})
					.collect(Collectors.toList());
			
			response.put("errors",errorsList);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}
		
		try {
			clienteNew = clienteService.save(cliente);
			response.put("mensaje", "El cliente se ha registrado con éxito.");
			response.put("registro", clienteNew);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registrar en la base de datos.");
			response.put("error", e.getMessage().concat(": " + e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PutMapping(path = "/clientes/{id}")
	// @ResponseStatus(code = HttpStatus.CREATED)
	public ResponseEntity<?> update( @Valid @RequestBody Cliente cliente, @PathVariable Integer id,
			BindingResult bindingResult) {
		Cliente clienteActual = clienteService.findById(id);
		Cliente clienteUpdated = null;
		Map<String, Object> response = new HashMap<>();
		
		if (bindingResult.hasErrors()) {
			List<String> errorsList = bindingResult.getFieldErrors()
					.stream()
					.map((error) -> { return "El campo '"+error.getField()+"' "+error.getDefaultMessage();})
					.collect(Collectors.toList());
			
			response.put("errors",errorsList);
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.BAD_REQUEST);
		}

		try {
			if (clienteActual == null) {
				response.put("mensaje",
						"Error: no se pudo editar, el cliente ID: ".concat(id.toString() + " no existe."));
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.NOT_FOUND);
			} else {
				clienteActual.setApellido(cliente.getApellido());
				clienteActual.setNombre(cliente.getNombre());
				clienteActual.setEmail(cliente.getEmail());
				clienteActual.setCreateAt(cliente.getCreateAt());

				clienteUpdated = clienteService.save(clienteActual);
				response.put("mensaje", "EL cliente se ha actualizado con éxito.");
				response.put("registro", clienteUpdated);
				return new ResponseEntity<Map<String, Object>>(response, HttpStatus.CREATED);
			}

		} catch (DataAccessException e) {
			response.put("mensaje", "Error al registrar en la base de datos.");
			response.put("error", e.getMessage().concat(": " + e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	@DeleteMapping(path = "/clientes/{id}")
	// @ResponseStatus(code = HttpStatus.NO_CONTENT)
	public ResponseEntity<?> delete(@PathVariable Integer id) {
		Map<String, Object> response = new HashMap<>();
		try {
			clienteService.delete(id);
			response.put("mensaje", "El cliente se ha eliminado con éxito.");
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.OK);
		} catch (DataAccessException e) {
			response.put("mensaje", "Error al eliminar el registro en la base de datos.");
			response.put("error", e.getMessage().concat(": " + e.getMostSpecificCause().getMessage()));
			return new ResponseEntity<Map<String, Object>>(response, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

}
