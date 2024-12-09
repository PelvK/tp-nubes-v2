package isi.dan.ms.pedidos.exception;

@SuppressWarnings("serial")
public class PedidoNotFoundException extends Exception{
    public PedidoNotFoundException(String msg){
        super(msg);
    }
}
