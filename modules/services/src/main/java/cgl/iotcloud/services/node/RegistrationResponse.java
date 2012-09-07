package cgl.iotcloud.services.node;

public class RegistrationResponse {
    private String status;

    private String reason;

    public RegistrationResponse() {
    }

    public RegistrationResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    public String getReason() {
        return reason;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
