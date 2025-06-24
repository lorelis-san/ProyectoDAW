package com.appWeb.cotizacion.service.client;

import com.appWeb.cotizacion.dto.client.ClientDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;


public interface ClientService {
    ResponseEntity<Map<String, Object>> saveClient(ClientDTO clienteDTO);
    ResponseEntity<Map<String, Object>> getAllClientsEnabled();
    ResponseEntity<Map<String, Object>> getClientById(Long id);
    ResponseEntity<Map<String, Object>> updateClient(ClientDTO clienteDTO, Long id);
    ResponseEntity<Map<String, Object>> deleteClient(Long id);
    ResponseEntity<Map<String, Object>> getClientByDocumentNumber(String documentNumber);

    ResponseEntity<Map<String, Object>> buscarPorDocumentoONombre(String termino);
}

