package org.example.proyecto41.services;

import org.example.proyecto41.dtos.EstadisticasResponse;
import org.example.proyecto41.dtos.FunkoCreateRequest;
import org.example.proyecto41.dtos.FunkoResponse;
import org.example.proyecto41.dtos.FunkoUpdateRequest;
import org.example.proyecto41.exceptions.FunkoConflictException;
import org.example.proyecto41.exceptions.FunkoNotFoundException;
import org.example.proyecto41.mappers.FunkoMapper;
import org.example.proyecto41.models.Funko;
import org.example.proyecto41.repositories.FunkosRepository;
import org.slf4j.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;

@Service
public class FunkosServiceImpl implements FunkosService {

    private static final Logger log = LoggerFactory.getLogger(FunkosServiceImpl.class);

    private final FunkosRepository repositorio;
    private final FunkoMapper mapper;

    public FunkosServiceImpl(FunkosRepository repositorio, FunkoMapper mapper) {
        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<FunkoResponse> buscar(Optional<String> categoria, Optional<String> nombre,
                                      Optional<BigDecimal> precioMax) {
        log.debug("buscar categoria={} nombre={} precioMax={}", categoria, nombre, precioMax);
        return mapper.toResponse(repositorio.buscar(categoria, nombre, precioMax));
    }

    @Override
    @Transactional(readOnly = true)
    public FunkoResponse findById(Long id) {
        return repositorio.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new FunkoNotFoundException(id));
    }

    @Override
    @Transactional
    public FunkoResponse save(FunkoCreateRequest request) {
        if (repositorio.existsByNombre(request.nombre()))
            throw new FunkoConflictException(
                    "Ya existe un funko con el nombre: " + request.nombre());

        return mapper.toResponse(repositorio.save(mapper.toModel(request)));
    }

    @Override
    @Transactional
    public FunkoResponse replace(Long id, FunkoCreateRequest request) {
        var original = repositorio.findById(id)
                .orElseThrow(() -> new FunkoNotFoundException(id));

        var nuevo = mapper.reemplazar(original, request);
        repositorio.update(nuevo);
        return mapper.toResponse(nuevo);
    }

    @Override
    @Transactional
    public FunkoResponse update(Long id, FunkoUpdateRequest request) {
        var original = repositorio.findById(id)
                .orElseThrow(() -> new FunkoNotFoundException(id));

        var parcheado = mapper.parchear(original, request);
        repositorio.update(parcheado);
        return mapper.toResponse(parcheado);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!repositorio.deleteById(id))
            throw new FunkoNotFoundException(id);
    }

    @Override
    @Transactional(readOnly = true)
    public EstadisticasResponse estadisticas() {
        var todos = repositorio.buscar(Optional.empty(), Optional.empty(), Optional.empty());

        var valor = BigDecimal.ZERO;
        for (var f : todos)
            valor = valor.add(f.precio().multiply(BigDecimal.valueOf(f.cantidad())));

        var masCaro = todos.stream()
                .max(Comparator.comparing(Funko::precio))
                .map(Funko::nombre)
                .orElse("—");

        var sinStock = todos.stream().filter(f -> f.cantidad() == 0).count();

        return new EstadisticasResponse(todos.size(), repositorio.contarPorCategoria(),
                valor, sinStock, masCaro);
    }
}
