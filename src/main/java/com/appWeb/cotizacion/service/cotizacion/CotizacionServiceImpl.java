package com.appWeb.cotizacion.service.cotizacion;

import com.appWeb.cotizacion.dto.client.ClientDTO;
import com.appWeb.cotizacion.dto.cotizacion.CotizacionDTO;
import com.appWeb.cotizacion.dto.cotizacion.CotizacionResponseDTO;
import com.appWeb.cotizacion.dto.cotizacion.DetalleCotizacionDTO;
import com.appWeb.cotizacion.dto.products.ProductDTO;
import com.appWeb.cotizacion.dto.vehicle.VehicleDTO;
import com.appWeb.cotizacion.enums.EstadoCotizacion;
import com.appWeb.cotizacion.model.cotizacion.Cotizacion;
import com.appWeb.cotizacion.model.cotizacion.DetalleCotizacion;
import com.appWeb.cotizacion.model.productos.Products;
import com.appWeb.cotizacion.model.usuario.User;
import com.appWeb.cotizacion.repository.client.ClientRepository;
import com.appWeb.cotizacion.repository.cotizacion.CotizacionRepository;
import com.appWeb.cotizacion.repository.cotizacion.DetalleCotizacionRepository;
import com.appWeb.cotizacion.repository.productos.ProductsRepository;
import com.appWeb.cotizacion.repository.user.UserRepository;
import com.appWeb.cotizacion.repository.vehicle.VehicleRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class CotizacionServiceImpl implements CotizacionService {

    private final CotizacionRepository cotizacionRepository;
    private final DetalleCotizacionRepository detalleCotizacionRepository;
    private final ProductsRepository productsRepository;
    private final VehicleRepository vehicleRepository;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final DetalleCotizacionService detalleCotizacionService;


    @Override
    public CotizacionResponseDTO mapToResponseDTO(Cotizacion cotizacion) {
        CotizacionResponseDTO dto = new CotizacionResponseDTO();
        dto.setId(cotizacion.getId());
        dto.setNumeroCotizacion(cotizacion.getNumeroCotizacion());
        dto.setEstado(cotizacion.getEstado().name());
        dto.setFecha(cotizacion.getFecha());
        dto.setFechaCreacion(cotizacion.getFechaCreacion());
        dto.setFechaModificacion(cotizacion.getFechaModificacion());
        dto.setObservaciones(cotizacion.getObservaciones());
        dto.setSubtotal(cotizacion.getSubtotal());
        dto.setIgv(cotizacion.getIgv());
        dto.setTotal(cotizacion.getTotal());


        // Cliente
        ClientDTO clienteDTO = new ClientDTO();
        clienteDTO.setId(cotizacion.getCliente().getId());
        clienteDTO.setTypeDocument(cotizacion.getCliente().getTypeDocument().toUpperCase());
        clienteDTO.setDocumentNumber(cotizacion.getCliente().getDocumentNumber());
        clienteDTO.setFirstName(cotizacion.getCliente().getFirstName());
        clienteDTO.setLastName(cotizacion.getCliente().getLastName());
        clienteDTO.setBusinessName(cotizacion.getCliente().getBusinessName());
        clienteDTO.setEmail(cotizacion.getCliente().getEmail());
        clienteDTO.setPhoneNumber(cotizacion.getCliente().getPhoneNumber());
        dto.setCliente(clienteDTO);

        // Vehículo
        VehicleDTO vehiculoDTO = new VehicleDTO();
        vehiculoDTO.setId(cotizacion.getVehiculo().getId());
        vehiculoDTO.setPlaca(cotizacion.getVehiculo().getPlaca().toUpperCase());
        vehiculoDTO.setMarca(cotizacion.getVehiculo().getMarca());
        vehiculoDTO.setModelo(cotizacion.getVehiculo().getModelo());
        vehiculoDTO.setYear(cotizacion.getVehiculo().getYear());
        dto.setVehiculo(vehiculoDTO);

        // Detalles
        List<DetalleCotizacionDTO> detalles = cotizacion.getDetalles().stream().map(detalle -> {
            DetalleCotizacionDTO detalleDTO = new DetalleCotizacionDTO();
            ProductDTO productoDTO = new ProductDTO();
            productoDTO.setId(detalle.getProducto().getId());
            productoDTO.setName(detalle.getProducto().getName());
            productoDTO.setModel(detalle.getProducto().getModel());
            productoDTO.setBrand(detalle.getProducto().getBrand());
            productoDTO.setSalePrice(detalle.getProducto().getSalePrice());
            productoDTO.setCostPrice(detalle.getProducto().getCostPrice());
            productoDTO.setDealerPrice(detalle.getProducto().getCostDealer());
            detalleDTO.setNombreProducto(productoDTO.getName());
            detalleDTO.setProductoId(productoDTO.getId());
            detalleDTO.setCantidad(detalle.getCantidad());
            detalleDTO.setPrecioUnitario(detalle.getPrecioUnitario());
            detalleDTO.setSubtotal(detalle.getSubtotal());
            return detalleDTO;
        }).toList();

        dto.setDetalles(detalles);

        if (cotizacion.getUser() != null) {
            dto.setUserNombre(cotizacion.getUser().getNombre());
            dto.setUserApellido(cotizacion.getUser().getApellido());
        }

        if (cotizacion.getUserModificador() != null) {
            dto.setUsuarioModificadorNombre(cotizacion.getUserModificador().getNombre());
            dto.setUsuarioModificadorApellido(cotizacion.getUserModificador().getApellido());
        }


        return dto;
    }

    @Override
    public ResponseEntity<Map<String, Object>> listarCotizaciones() {
        Map<String, Object> res = new HashMap<>();
        List<Cotizacion> lista = cotizacionRepository.findByEstadoNot(
                EstadoCotizacion.ELIMINADA, Sort.by(Sort.Direction.DESC, "fecha")
        );
//        List<Cotizacion> lista = cotizacionRepository.findAll(Sort.by(Sort.Direction.DESC, "fecha"));
        List<CotizacionResponseDTO> dtos = lista.stream().map(this::mapToResponseDTO).toList();

        res.put("mensaje", dtos.isEmpty() ? "No hay cotizaciones registradas" : "Lista de cotizaciones");
        res.put("data", dtos);
        res.put("status", dtos.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> obtenerCotizacionPorId(Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Cotizacion> cot = cotizacionRepository.findByIdAndEstadoNot(id, EstadoCotizacion.ELIMINADA);
//        Optional<Cotizacion> cot = cotizacionRepository.findById(id);
        if (cot.isPresent()) {
            res.put("mensaje", "Cotización encontrada");
            res.put("data", mapToResponseDTO(cot.get()));
            res.put("status", HttpStatus.OK);
        } else {
            res.put("mensaje", "Cotización no encontrada con ID: " + id);
            res.put("status", HttpStatus.NOT_FOUND);
        }
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public Cotizacion obtenerPorId(Long id) {
        return cotizacionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cotización no encontrada con ID: " + id));
    }

    @Transactional
    @Override
    public ResponseEntity<Map<String, Object>> crearCotizacion(CotizacionDTO dto) {
        Map<String, Object> res = new HashMap<>();
        try {
            Cotizacion cot = new Cotizacion();
            cot.setNumeroCotizacion(generarNumeroCotizacion());
            cot.setFecha(LocalDate.now());
            cot.setFechaCreacion(LocalDateTime.now());
            cot.setCliente(clientRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new RuntimeException("Cliente no encontrado")));
            cot.setVehiculo(vehicleRepository.findById(dto.getVehiculoId())
                    .orElseThrow(() -> new RuntimeException("Vehículo no encontrado")));

            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

            cot.setUser(user);

            cot.setObservaciones(dto.getObservaciones());
            cot.setEstado(EstadoCotizacion.CREADA);

            for (DetalleCotizacionDTO det : dto.getDetalles()) {
                System.out.println("Producto ID recibido: " + det.getProductoId());
                Products prod = productsRepository.findById(det.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                DetalleCotizacion detalle = new DetalleCotizacion(cot, prod, det.getCantidad(), det.getPrecioUnitario());
                cot.agregarDetalle(detalle);
            }

            cot.calcularTotales();
            Cotizacion guardada = cotizacionRepository.save(cot);

            res.put("mensaje", "Cotización registrada");
            res.put("data", mapToResponseDTO(guardada));
            res.put("status", HttpStatus.CREATED);
        } catch (RuntimeException ex) {
            throw ex; // dejar que Spring maneje el rollback y el error
        } catch (Exception e) {
            res.put("mensaje", "Error al registrar cotización: " + e.getMessage());
            res.put("status", HttpStatus.BAD_REQUEST);
        }
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Transactional
    @Override
    public ResponseEntity<Map<String, Object>> actualizarCotizacion(CotizacionResponseDTO dto) {
        Map<String, Object> res = new HashMap<>();

        try {
            Cotizacion cot = cotizacionRepository.findByIdAndEstadoNot(dto.getId(), EstadoCotizacion.ELIMINADA)
                    .orElseThrow(() -> new EntityNotFoundException("No se puede actualizar: Cotización no encontrada o eliminada"));

//            Cotizacion cot = cotizacionRepository.findById(dto.getId())
//                    .orElseThrow(() -> new EntityNotFoundException("Cotización no encontrada"));

            // Limpiar detalles anteriores
            detalleCotizacionRepository.deleteAllByCotizacionId(cot.getId());

            // Usuario que modifica
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));
            cot.setUserModificador(user);

//            // Detalles nuevos
//            List<DetalleCotizacion> nuevos = new ArrayList<>();
//            for (DetalleCotizacionDTO d : dto.getDetalles()) {
//                Products prod = productsRepository.findById(d.getProductoId())
//                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
//                DetalleCotizacion det = new DetalleCotizacion(cot, prod, d.getCantidad(), d.getPrecioUnitario());
//                nuevos.add(det);
//            }

            // Agregar nuevos detalles con lógica de negocio
            for (DetalleCotizacionDTO d : dto.getDetalles()) {
                Products prod = productsRepository.findById(d.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));
                DetalleCotizacion det = new DetalleCotizacion(cot, prod, d.getCantidad(), d.getPrecioUnitario());
                cot.agregarDetalle(det); // clave: lógica de agregación interna
            }

            cot.calcularTotales();
            cot.setFechaModificacion(LocalDateTime.now());
            cot.setEstado(EstadoCotizacion.MODIFICADA);
            cot.setObservaciones(dto.getObservaciones());

            cotizacionRepository.save(cot);

            res.put("mensaje", "Cotización actualizada");
            res.put("data", mapToResponseDTO(cot));
            res.put("status", HttpStatus.OK);

        } catch (Exception e) {
            res.put("mensaje", "Error al actualizar: " + e.getMessage());
            res.put("status", HttpStatus.BAD_REQUEST);
        }
        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    @Override
    public ResponseEntity<Map<String, Object>> eliminarCotizacion(Long id) {
        Map<String, Object> res = new HashMap<>();
        Optional<Cotizacion> optional = cotizacionRepository.findById(id);

        if (optional.isPresent()) {
            Cotizacion cotizacion = optional.get();

            // Obtener usuario autenticado
            String email = SecurityContextHolder.getContext().getAuthentication().getName();
            User user = userRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

            // Cambiar estado a ELIMINADA
            cotizacion.setEstado(EstadoCotizacion.ELIMINADA);
            cotizacion.setFechaModificacion(LocalDateTime.now());
            cotizacion.setUserModificador(user);

            cotizacionRepository.save(cotizacion);

            res.put("mensaje", "Cotización marcada como eliminada");
            res.put("status", HttpStatus.OK);
        } else {
            res.put("mensaje", "Cotización no encontrada con ID: " + id);
            res.put("status", HttpStatus.NOT_FOUND);
        }

        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }



    private String generarNumeroCotizacion() {
        Long count = cotizacionRepository.count();
        return "COT-" + String.format("%03d", count + 1);
    }


}

