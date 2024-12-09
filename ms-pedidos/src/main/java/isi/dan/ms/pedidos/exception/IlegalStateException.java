package isi.dan.ms.pedidos.exception;

@SuppressWarnings("serial")
public class IlegalStateException extends Exception{
    public IlegalStateException(String msg){
        super(msg);
    }
}