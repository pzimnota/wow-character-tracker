package com.wowtracker.web.dto;

import java.time.Instant;
import java.util.List;

public record ApiError(int status,
                       String path,
                       List<ValidationError> errors,
                       Instant timestamp) {
}
