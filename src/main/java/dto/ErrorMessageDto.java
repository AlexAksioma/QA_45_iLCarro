package dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
@Getter
@Setter
@Builder

public class ErrorMessageDto {
    private String timestamp;
    private int status;
    private String error;
    private Object message;
    //private String message;  wrong
    private String path;
}
