package isi.dan.ms.pedidos.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestControllerException {

	private static final Logger logger = LoggerFactory.getLogger(RestControllerException.class);

	@ExceptionHandler(PedidoNotFoundException.class)
	public ResponseEntity<ErrorInfo> handlePedidoNotFoundException(PedidoNotFoundException ex) {
		logger.error("ERROR Buscando Pedido ", ex);
		String detalle = ex.getCause() == null ? "Pedido no encontrado" : ex.getCause().getMessage();

		return new ResponseEntity<ErrorInfo>(
				new ErrorInfo(Instant.now(), ex.getMessage(), detalle, HttpStatus.NOT_FOUND.value()),
				HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(ClienteNotFoundException.class)
	public ResponseEntity<ErrorInfo> handleClienteNotFoundException(ClienteNotFoundException ex) {
		logger.error("ERROR Buscando Cliente ", ex);
		String detalle = ex.getCause() == null ? "Cliente no encontrado" : ex.getCause().getMessage();

		return new ResponseEntity<ErrorInfo>(
				new ErrorInfo(Instant.now(), ex.getMessage(), detalle, HttpStatus.NOT_FOUND.value()),
				HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(ObraNotFoundException.class)
	public ResponseEntity<ErrorInfo> handleObraNotFoundException(ObraNotFoundException e) {
		logger.error("ERROR Buscando Obra ", e);
		String detalle = e.getCause() == null ? "Obra no encontrada" : e.getCause().getMessage();

		return new ResponseEntity<ErrorInfo>(
				new ErrorInfo(Instant.now(), e.getMessage(), detalle, HttpStatus.NOT_FOUND.value()),
				HttpStatus.NOT_FOUND);
	}
	@ExceptionHandler(IlegalStateException.class)
	public ResponseEntity<ErrorInfo> handleIlegalStateException(IlegalStateException ex) {
		logger.error("ERROR Estado ilegal ", ex);
		String detalle = ex.getCause() == null ? "El estado indicado no es correcto" : ex.getCause().getMessage();

		return new ResponseEntity<ErrorInfo>(
				new ErrorInfo(Instant.now(), ex.getMessage(), detalle, HttpStatus.NOT_FOUND.value()),
				HttpStatus.NOT_FOUND);
	}


	private Map<String, List<String>> getErrorsMap(List<String> errors) {
		Map<String, List<String>> errorResponse = new HashMap<>();
		errorResponse.put("errors", errors);
		return errorResponse;
	}
	

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, List<String>>> handleValidacionEntradaException(
			MethodArgumentNotValidException ex) {
		logger.error("ERROR Intentando validar la entrada ", ex);
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map(FieldError::getDefaultMessage)
				.collect(Collectors.toList());
		return new ResponseEntity<>(getErrorsMap(errors), new HttpHeaders(), HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorInfo> handleOtherExceptions(Exception ex) {
		logger.error("ERROR MS PEDIDOS", ex);
		String detalle = ex.getCause() == null ? "error en el servidor" : ex.getCause().getMessage();
		return new ResponseEntity<ErrorInfo>(
				new ErrorInfo(Instant.now(), ex.getMessage(), detalle, HttpStatus.INTERNAL_SERVER_ERROR.value()),
				HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
}
