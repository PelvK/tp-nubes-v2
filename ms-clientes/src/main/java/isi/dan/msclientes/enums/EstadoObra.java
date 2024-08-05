package isi.dan.msclientes.enums;

public enum EstadoObra {
    HABILITADA("Obra habilitada"),
    PENDIENTE("Obra pendiente"),
    FINALIZADA("Obra finalizada");

    private String descripcion;

    EstadoObra(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
