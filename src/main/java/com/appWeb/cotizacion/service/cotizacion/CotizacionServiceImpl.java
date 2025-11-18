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
import lombok.RequiredArgsConstructor;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

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

    // =====================================================
    // MAPPER DTO
    // =====================================================
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
        List<DetalleCotizacionDTO> detalles = cotizacion.getDetalles()
                .stream()
                .map(detalle -> {
                    DetalleCotizacionDTO detDTO = new DetalleCotizacionDTO();
                    ProductDTO productoDTO = new ProductDTO();

                    productoDTO.setId(detalle.getProducto().getId());
                    productoDTO.setName(detalle.getProducto().getName());
                    productoDTO.setModel(detalle.getProducto().getModel());
                    productoDTO.setBrand(detalle.getProducto().getBrand());
                    productoDTO.setSalePrice(detalle.getProducto().getSalePrice());
                    productoDTO.setCostPrice(detalle.getProducto().getCostPrice());
                    productoDTO.setDealerPrice(detalle.getProducto().getCostDealer());

                    detDTO.setProductoId(productoDTO.getId());
                    detDTO.setNombreProducto(productoDTO.getName());
                    detDTO.setCantidad(detalle.getCantidad());
                    detDTO.setPrecioUnitario(detalle.getPrecioUnitario());
                    detDTO.setSubtotal(detalle.getSubtotal());

                    return detDTO;
                })
                .toList();

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

    // ===========================================================
    // LISTAR
    // ===========================================================
    @Override
    public ResponseEntity<Map<String, Object>> listarCotizaciones() {
        Map<String, Object> res = new HashMap<>();

        List<Cotizacion> lista = cotizacionRepository.findByEstadoNot(
                EstadoCotizacion.ELIMINADA,
                Sort.by(Sort.Direction.DESC, "fecha")
        );

        List<CotizacionResponseDTO> dtos = lista.stream()
                .map(this::mapToResponseDTO)
                .toList();

        res.put("mensaje", dtos.isEmpty() ? "No hay cotizaciones registradas" : "Lista de cotizaciones");
        res.put("data", dtos);
        res.put("status", dtos.isEmpty() ? HttpStatus.NOT_FOUND : HttpStatus.OK);
        res.put("fecha", new Date());

        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    // ===========================================================
    // OBTENER POR ID
    // ===========================================================
    @Override
    public ResponseEntity<Map<String, Object>> obtenerCotizacionPorId(Long id) {
        Map<String, Object> res = new HashMap<>();

        Optional<Cotizacion> cot = cotizacionRepository.findByIdAndEstadoNot(id, EstadoCotizacion.ELIMINADA);

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

    // ===========================================================
    // CREAR COTIZACIÓN
    // ===========================================================
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
            cot.setEstado(EstadoCotizacion.PENDIENTE);

            for (DetalleCotizacionDTO det : dto.getDetalles()) {
                Products prod = productsRepository.findById(det.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                DetalleCotizacion detalle = new DetalleCotizacion(
                        cot,
                        prod,
                        det.getCantidad(),
                        det.getPrecioUnitario()
                );

                cot.agregarDetalle(detalle);
            }

            cot.calcularTotales();
            Cotizacion guardada = cotizacionRepository.save(cot);

            res.put("mensaje", "Cotización registrada");
            res.put("data", mapToResponseDTO(guardada));
            res.put("status", HttpStatus.CREATED);

        } catch (Exception e) {
            res.put("mensaje", "Error al registrar cotización: " + e.getMessage());
            res.put("status", HttpStatus.BAD_REQUEST);
        }

        res.put("fecha", new Date());
        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    // ===========================================================
    // ACTUALIZAR COTIZACIÓN
    // ===========================================================
    @Transactional
    @Override
    public ResponseEntity<Map<String, Object>> actualizarCotizacion(CotizacionResponseDTO dto) {

        Map<String, Object> res = new HashMap<>();

        try {

            if (dto.getDetalles() == null || dto.getDetalles().isEmpty()) {
                throw new IllegalArgumentException("La cotización debe contener al menos un producto.");
            }

            Cotizacion cot = cotizacionRepository.findByIdAndEstadoNot(
                    dto.getId(),
                    EstadoCotizacion.ELIMINADA
            ).orElseThrow(() -> new EntityNotFoundException("No se puede actualizar: Cotización no encontrada o eliminada."));

            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado."));

            cot.setUserModificador(user);

            cot.getDetalles().clear();

            for (DetalleCotizacionDTO d : dto.getDetalles()) {
                Products producto = productsRepository.findById(d.getProductoId())
                        .orElseThrow(() -> new RuntimeException("Producto no encontrado"));

                DetalleCotizacion detalle = new DetalleCotizacion(
                        cot,
                        producto,
                        d.getCantidad(),
                        d.getPrecioUnitario()
                );

                cot.agregarDetalle(detalle);
            }

            cot.setEstado(EstadoCotizacion.MODIFICADA);
            cot.setObservaciones(dto.getObservaciones());
            cot.setFechaModificacion(LocalDateTime.now());
            cot.calcularTotales();

            Cotizacion cotActualizada = cotizacionRepository.save(cot);

            res.put("mensaje", "Cotización actualizada correctamente.");
            res.put("data", mapToResponseDTO(cotActualizada));
            res.put("status", HttpStatus.OK);

        } catch (Exception e) {
            res.put("mensaje", "Error al actualizar cotización: " + e.getMessage());
            res.put("status", HttpStatus.BAD_REQUEST);
        }

        res.put("fecha", new Date());

        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }

    // ===========================================================
    // ELIMINAR (CAMBIAR ESTADO)
    // ===========================================================
    @Override
    public ResponseEntity<Map<String, Object>> eliminarCotizacion(Long id) {
        Map<String, Object> res = new HashMap<>();

        Optional<Cotizacion> optional = cotizacionRepository.findById(id);

        if (optional.isPresent()) {

            Cotizacion cotizacion = optional.get();

            String email = SecurityContextHolder.getContext().getAuthentication().getName();

            User user = userRepository.findOneByEmail(email)
                    .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

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


    // ===========================================================
    // CAMBIAR ESTADO
    // ===========================================================
    @Override
    public ResponseEntity<Map<String, Object>> actualizarEstadoCotizacion(Long id, String estado) {

        Map<String, Object> res = new HashMap<>();

        Optional<Cotizacion> optional = cotizacionRepository.findById(id);

        if (optional.isEmpty()) {
            res.put("mensaje", "Cotización no encontrada con ID: " + id);
            res.put("fecha", new Date());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
        }

        Cotizacion cotizacion = optional.get();

        EstadoCotizacion nuevoEstado;

        try {
            nuevoEstado = EstadoCotizacion.valueOf(estado.toUpperCase());
        } catch (Exception e) {
            res.put("mensaje", "Estado inválido: " + estado);
            res.put("fecha", new Date());
            return ResponseEntity.badRequest().body(res);
        }

        String email = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findOneByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuario no autenticado"));

        cotizacion.setEstado(nuevoEstado);
        cotizacion.setFechaModificacion(LocalDateTime.now());
        cotizacion.setUserModificador(user);

        cotizacionRepository.save(cotizacion);

        res.put("mensaje", "Cotización actualizada a " + nuevoEstado.getDescripcion());
        res.put("data", mapToResponseDTO(cotizacion));
        res.put("fecha", new Date());

        return ResponseEntity.ok(res);
    }


    // ===========================================================
    // BÚSQUEDA
    // ===========================================================
    @Override
    public ResponseEntity<Map<String, Object>> buscarPorTermino(String termino) {
        Map<String, Object> res = new HashMap<>();

        List<Cotizacion> lista = cotizacionRepository.buscarPorTermino(termino);

        if (lista.isEmpty()) {
            res.put("mensaje", "No se encontraron cotizaciones");
            res.put("status", HttpStatus.NOT_FOUND);
        } else {
            res.put("mensaje", "Cotizaciones encontradas");
            res.put("data", lista.stream().map(this::mapToResponseDTO).toList());
            res.put("status", HttpStatus.OK);
        }

        res.put("fecha", new Date());

        return ResponseEntity.status((HttpStatus) res.get("status")).body(res);
    }


    // ===========================================================
    // MÉTODOS USANDO STORED PROCEDURES MODIFICADOS
    // ===========================================================

    // 1️⃣ Cotizaciones por estado (con cantidad + monto)
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> cotizacionesPorEstado() {

        List<Object[]> lista = cotizacionRepository.cotizacionesPorEstado();
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("estado", row[0]);
            item.put("cantidad", row[1]);
            item.put("montoTotal", row[2]);
            data.add(item);
        }

        response.put("mensaje", "Cotizaciones agrupadas por estado");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }


    // 2️⃣ Ingresos por mes
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> ingresosPorMes() {

        List<Object[]> lista = cotizacionRepository.ingresosPorMes();
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("mes", row[0]);
            item.put("total", row[1]);
            item.put("cotizacionesPorMes", row[2]);
            data.add(item);
        }

        response.put("mensaje", "Ingresos agrupados por mes");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }


    // 3️⃣ Ventas por usuario
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> ventasPorUsuario() {

        List<Object[]> lista = cotizacionRepository.ventasPorUsuario();
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("usuario", row[0]);
            item.put("cotizaciones", row[1]);
            item.put("total", row[2]);
            data.add(item);
        }

        response.put("mensaje", "Ventas totales por usuario");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }


    // 4️⃣ Clientes TOP
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> clientesTop() {

        List<Object[]> lista = cotizacionRepository.clientesTop();
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("cliente", row[0]);
            item.put("totalCotizaciones", row[1]);
            item.put("montoTotal", row[2]);
            data.add(item);
        }

        response.put("mensaje", "Clientes con más cotizaciones");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }


    // 5️⃣ Cotizaciones pendientes
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> cotizacionesPendientes() {

        List<Object[]> lista = cotizacionRepository.cotizacionesPendientes();
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("numeroCotizacion", row[0]);
            item.put("fecha", row[1]);
            item.put("cliente", row[2]);
            item.put("total", row[3]);
            item.put("estado", row[4]);
            data.add(item);
        }

        response.put("mensaje", "Cotizaciones pendientes o sin aprobar");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }


    // Monto Aprobadas Mes
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> montoAprobadasMes() {

        List<Object[]> lista = cotizacionRepository.montoAprobadasMes();
        Map<String, Object> response = new HashMap<>();

        Map<String, Object> data = new HashMap<>();

        if (!lista.isEmpty()) {
            Object[] row = lista.get(0);

            data.put("montoAprobadoMes", row[0]);
            data.put("cantidadAprobadas", row[1]);
        }

        response.put("mensaje", "Monto total de cotizaciones aprobadas del mes");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }


    // Gráfico cotizaciones por estado
    @Override
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> graficoCotizacionesPorEstado() {

        List<Object[]> lista = cotizacionRepository.graficoCotizacionesPorEstado();
        Map<String, Object> response = new HashMap<>();

        List<Map<String, Object>> data = new ArrayList<>();

        for (Object[] row : lista) {
            Map<String, Object> item = new HashMap<>();
            item.put("estado", row[0]);
            item.put("cantidad", row[1]);
            item.put("montoTotal", row[2]);
            data.add(item);
        }

        response.put("mensaje", "Cotizaciones por estado para gráfico");
        response.put("data", data);
        response.put("status", HttpStatus.OK);
        response.put("fecha", new Date());

        return ResponseEntity.ok(response);
    }


    // ===========================================================
    // GENERAR CÓDIGO
    // ===========================================================
    private String generarNumeroCotizacion() {
        Long count = cotizacionRepository.count();
        return "COT-" + String.format("%03d", count + 1);
    }

}
