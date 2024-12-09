package isi.dan.ms.pedidos.modelo;

import java.time.Instant;

import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "historialesEstado")
public class HistorialEstado {

    EstadoPedido estado;
    Instant fechaEstado;
    String userEstado;
    String detalle;

    public HistorialEstado(EstadoPedido estado, String userEstado, String detalle) {
        this.estado = estado;
        this.fechaEstado= Instant.now();
        this.userEstado=userEstado;
        this.detalle=detalle;
    }
}

