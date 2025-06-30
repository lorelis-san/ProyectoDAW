package com.appWeb.cotizacion.controller;

import com.appWeb.cotizacion.dto.client.ClientDTO;
import com.appWeb.cotizacion.service.client.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/client")
@CrossOrigin(origins = "*")
public class ClientController {

    @Autowired
    private ClientService clientService;


    @GetMapping
    public ResponseEntity<Map<String, Object>> listarClientes() {
        return clientService.getAllClientsEnabled();
    }
    @GetMapping("/buscar")
    public ResponseEntity<Map<String, Object>> buscar(@RequestParam String termino) {
        return clientService.buscarPorDocumentoONombre(termino);
    }


    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerCliente(@PathVariable Long id) {
        return clientService.getClientById(id);
    }

    @GetMapping("/buscar/{documentNumber}")
    public ResponseEntity<Map<String, Object>> buscarClientePorDocumento(@PathVariable String documentNumber) {
        return clientService.getClientByDocumentNumber(documentNumber);
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> crearCliente(@RequestBody ClientDTO cliente) {
        return clientService.saveClient(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCliente(@PathVariable Long id, @RequestBody ClientDTO cliente) {
        cliente.setId(id);
        return clientService.updateClient(cliente, id);
    }

    // Eliminar (inhabilitar) cliente
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCliente(@PathVariable Long id) {
        return clientService.deleteClient(id);
    }
}
