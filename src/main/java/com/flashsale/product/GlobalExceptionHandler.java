package com.flashsale.product;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import com.flashsale.order.InsufficientStockException;
import org.springframework.http.ResponseEntity;
import com.flashsale.order.OrderNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProductNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleProductNotFound(ProductNotFoundException ex) {
        return new ErrorResponse(
                404,
                ex.getMessage());
    }

    @ExceptionHandler(InsufficientStockException.class)
    public ResponseEntity<ErrorResponse> handleInsufficientStock(
            InsufficientStockException ex) {

        ErrorResponse error = new ErrorResponse(
                400,
                ex.getMessage());

        return ResponseEntity.badRequest().body(error);
    }

    @ExceptionHandler(OrderNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleOrderNotFound(OrderNotFoundException ex) {
        return new ErrorResponse(404, ex.getMessage());
    }

}