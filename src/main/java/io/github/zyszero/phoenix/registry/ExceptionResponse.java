package io.github.zyszero.phoenix.registry;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * wrapper exception response.
 * @Author: zyszero
 * @Date: 2024/5/11 4:19
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExceptionResponse {
    private HttpStatus status;
    private String message;

}
