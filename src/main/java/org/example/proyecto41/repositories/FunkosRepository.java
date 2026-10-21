package org.example.proyecto41.repositories;

import org.example.proyecto41.models.Funko;

import java.math.BigDecimal;
import java.util.*;

public interface FunkosRepository {
    List<Funko>       buscar(Optional<String> categoria, Optional<String> nombre,
                             Optional<BigDecimal> precioMax);
    Optional<Funko>   findById(Long id);
    boolean           existsByNombre(String nombre);
    Funko             save(Funko funko);
    void              update(Funko funko);
    boolean           deleteById(Long id);
    Map<String, Long> contarPorCategoria();
}