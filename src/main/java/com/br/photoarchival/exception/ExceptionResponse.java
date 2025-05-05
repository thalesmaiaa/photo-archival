package com.br.photoarchival.exception;

import java.util.List;

public record ExceptionResponse(String message, List<String> details) {
}
