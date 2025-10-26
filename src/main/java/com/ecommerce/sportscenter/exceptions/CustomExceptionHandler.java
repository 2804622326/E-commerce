package com.ecommerce.sportscenter.exceptions;

import com.ecommerce.sportscenter.model.CustomErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomExceptionHandler extends ResponseEntityExceptionHandler {
    
    @ExceptionHandler(ProductNotFoundException.class)
    public ResponseEntity<Object> handleProductNotFoundException(ProductNotFoundException ex, WebRequest request){
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatus.NOT_FOUND, 
                "Product Not Found", 
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BasketNotFoundException.class)
    public ResponseEntity<Object> handleBasketNotFoundException(BasketNotFoundException ex, WebRequest request){
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatus.NOT_FOUND, 
                "Basket Not Found", 
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BasketAlreadyExistsException.class)
    public ResponseEntity<Object> handleBasketAlreadyExistsException(BasketAlreadyExistsException ex, WebRequest request){
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatus.CONFLICT, 
                "Basket Already Exists", 
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.CONFLICT);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    public ResponseEntity<Object> handleOrderNotFoundException(OrderNotFoundException ex, WebRequest request){
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatus.NOT_FOUND, 
                "Order Not Found", 
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(BrandNotFoundException.class)
    public ResponseEntity<Object> handleBrandNotFoundException(BrandNotFoundException ex, WebRequest request){
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatus.NOT_FOUND, 
                "Brand Not Found", 
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(TypeNotFoundException.class)
    public ResponseEntity<Object> handleTypeNotFoundException(TypeNotFoundException ex, WebRequest request){
        CustomErrorResponse customErrorResponse = new CustomErrorResponse(
                HttpStatus.NOT_FOUND, 
                "Type Not Found", 
                ex.getMessage()
        );
        return new ResponseEntity<>(customErrorResponse, HttpStatus.NOT_FOUND);
    }
}
