package org.example.proyecto41.services;

import org.example.proyecto41.dtos.EstadisticasResponse;
import org.example.proyecto41.dtos.FunkoCreateRequest;
import org.example.proyecto41.dtos.FunkoResponse;
import org.example.proyecto41.dtos.FunkoUpdateRequest;

import java.math.BigDecimal;
import java.util.*;

public interface FunkosService {
    List<FunkoResponse> buscar(Optional<String> categoria, Optional<String> nombre,
                               Optional<BigDecimal> precioMax);
    FunkoResponse findById(Long id);
    FunkoResponse save(FunkoCreateRequest funko);
    FunkoResponse replace(Long id, FunkoCreateRequest funko);
    FunkoResponse update(Long id, FunkoUpdateRequest funko);
    void deleteById(Long id);
    EstadisticasResponse estadisticas();
}