package isi.dan.ms_productos.dto;

import lombok.Data;

@Data
public class StockUpdateDTO {
    @NotNull(message = "el id no debe ser nulo")
    private Long idProducto;
    @Min(value = 0, message = "Cantidad debe ser mayor o igual a 0")
    private Integer cantidad;
    @Min(value = 0, message = "Precio debe ser mayor o igual a 0")
    private Double precio;
}
