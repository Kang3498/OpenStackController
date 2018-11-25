package openstack.contributhon.com.openstackcontroller;

public class ErrorMessage {

    Request badRequest, conflictingRequest, NeutronError;

    public String getMesage(int id) {
        switch (id) {
            case 0:
                return badRequest.message;
            case 1:
                return conflictingRequest.message;
            case 2:
                return NeutronError.message;
        }
        return "";
    }

    public static class Request {
        String message;
    }
}