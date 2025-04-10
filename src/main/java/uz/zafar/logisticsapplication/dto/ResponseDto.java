package uz.zafar.logisticsapplication.dto;



public class ResponseDto<T> {
    private boolean success ;
    private String message ;
    private T data ;
    public ResponseDto(Boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public ResponseDto(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public ResponseDto() {
    }

    public ResponseDto(boolean success, String message, T data) {
        this.data = data;
        this.message = message;
        this.success = success;
    }
}
