package com.appWeb.cotizacion.service.client.Implement;

import com.appWeb.cotizacion.dto.client.ClientDTO;
import com.appWeb.cotizacion.model.client.Client;
import com.appWeb.cotizacion.repository.client.ClientRepository;
import com.appWeb.cotizacion.service.client.ClientService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ClientServiceImpl implements ClientService {
    @Autowired
    private ClientRepository clientRepository;

    private Client convertToEntity(ClientDTO dto) {
        Client client = new Client();
        client.setId(dto.getId());
        client.setFirstName(dto.getFirstName());
        client.setLastName(dto.getLastName());
        client.setTypeDocument(dto.getTypeDocument());
        client.setDocumentNumber(dto.getDocumentNumber());
        client.setBusinessName(dto.getBusinessName());
        client.setPhoneNumber(dto.getPhoneNumber());
        client.setEmail(dto.getEmail());
        client.setEnabled(dto.getEnabled());
        return client;
    }

    private ClientDTO convertToDTO(Client client) {
        ClientDTO dto = new ClientDTO();
        dto.setId(client.getId());
        dto.setFirstName(client.getFirstName());
        dto.setLastName(client.getLastName());
        dto.setTypeDocument(client.getTypeDocument());
        dto.setDocumentNumber(client.getDocumentNumber());
        dto.setBusinessName(client.getBusinessName());
        dto.setPhoneNumber(client.getPhoneNumber());
        dto.setEmail(client.getEmail());
        dto.setEnabled(client.getEnabled());
        return dto;
    }

    @Override
    public ResponseEntity<Map<String, Object>> saveClient(ClientDTO clienteDTO) {
        Map<String, Object> respuesta = new HashMap<>();
        if (clientRepository.findByDocumentNumber(clienteDTO.getDocumentNumber()) != null) {
            respuesta.put("mensaje", "Ya existe un cliente con ese número de documento");
            respuesta.put("status", HttpStatus.BAD_REQUEST);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }

        Client saved = clientRepository.save(convertToEntity(clienteDTO));
        respuesta.put("mensaje", "Cliente registrado correctamente");
        respuesta.put("data", convertToDTO(saved));
        respuesta.put("status", HttpStatus.CREATED);
        respuesta.put("fecha", new Date());
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @Override
    public ResponseEntity<Map<String, Object>> getAllClientsEnabled() {
        Map<String, Object> respuesta = new HashMap<>();
        List<ClientDTO> lista = clientRepository.findByEnabledTrue().stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());

        if (!lista.isEmpty()) {
            respuesta.put("mensaje", "Lista de clientes habilitados");
            respuesta.put("data", lista);
            respuesta.put("status", HttpStatus.OK);
            respuesta.put("fecha", new Date());
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "No hay clientes registrados");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getClientById(Long id) {
        Map<String, Object> respuesta = new HashMap<>();
        Optional<Client> opt = clientRepository.findById(id);

        if (opt.isPresent()) {
            respuesta.put("mensaje", "Cliente encontrado");
            respuesta.put("data", convertToDTO(opt.get()));
            respuesta.put("status", HttpStatus.OK);
            respuesta.put("fecha", new Date());
            return ResponseEntity.ok(respuesta);
        } else {
            respuesta.put("mensaje", "Cliente no encontrado con ID: " + id);
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> updateClient(ClientDTO clienteDTO, Long id) {
        Map<String, Object> respuesta = new HashMap<>();
        Optional<Client> opt = clientRepository.findById(id);

        if (opt.isEmpty()) {
            respuesta.put("mensaje", "Cliente no encontrado");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }

        Client duplicado = clientRepository.findByDocumentNumber(clienteDTO.getDocumentNumber());
        if (duplicado != null && !duplicado.getId().equals(id)) {
            respuesta.put("mensaje", "Ya existe otro cliente con ese número de documento");
            respuesta.put("status", HttpStatus.CONFLICT);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.CONFLICT).body(respuesta);
        }

        Client client = opt.get();
        client.setTypeDocument(clienteDTO.getTypeDocument());
        client.setDocumentNumber(clienteDTO.getDocumentNumber());
        client.setPhoneNumber(clienteDTO.getPhoneNumber());
        client.setEmail(clienteDTO.getEmail());

        if ("DNI".equalsIgnoreCase(clienteDTO.getTypeDocument())) {
            client.setFirstName(clienteDTO.getFirstName());
            client.setLastName(clienteDTO.getLastName());
            client.setBusinessName("-");
        } else {
            client.setBusinessName(clienteDTO.getBusinessName());
            client.setFirstName("-");
            client.setLastName("-");
        }

        clientRepository.save(client);

        respuesta.put("mensaje", "Cliente actualizado correctamente");
        respuesta.put("data", convertToDTO(client));
        respuesta.put("status", HttpStatus.OK);
        respuesta.put("fecha", new Date());
        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<Map<String, Object>> deleteClient(Long id) {
        Map<String, Object> respuesta = new HashMap<>();
        Optional<Client> opt = clientRepository.findById(id);

        if (opt.isPresent()) {
            Client client = opt.get();
            client.setEnabled(false);
            clientRepository.save(client);
            respuesta.put("mensaje", "Cliente deshabilitado correctamente");
            respuesta.put("status", HttpStatus.NO_CONTENT);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NO_CONTENT).body(respuesta);
        } else {
            respuesta.put("mensaje", "Cliente no encontrado");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    @Override
    public ResponseEntity<Map<String, Object>> getClientByDocumentNumber(String documentNumber) {
        Map<String, Object> respuesta = new HashMap<>();
        Client client = clientRepository.findByDocumentNumber(documentNumber);

        if (client == null || !Boolean.TRUE.equals(client.getEnabled())) {
            respuesta.put("mensaje", "Cliente no encontrado o está deshabilitado");
            respuesta.put("status", HttpStatus.NOT_FOUND);
            respuesta.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }

        ClientDTO dto = convertToDTO(client);

        respuesta.put("mensaje", "Cliente encontrado");
        respuesta.put("data", dto);
        respuesta.put("status", HttpStatus.OK);
        respuesta.put("fecha", new Date());
        return ResponseEntity.ok(respuesta);
    }

    @Override
    public ResponseEntity<Map<String, Object>> buscarPorDocumentoONombre(String termino) {
        Map<String, Object> res = new HashMap<>();
        List<ClientDTO> lista = clientRepository.buscarPorDocumentoONombre(termino)
                .stream()
                .map(this::convertToDTO)
                .toList();

        if (lista.isEmpty()) {
            res.put("mensaje", "No se encontraron coincidencias");
            res.put("status", HttpStatus.NOT_FOUND);
        } else {
            res.put("mensaje", "Resultados encontrados");
            res.put("data", lista);
            res.put("status", HttpStatus.OK);
        }

        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }


}
