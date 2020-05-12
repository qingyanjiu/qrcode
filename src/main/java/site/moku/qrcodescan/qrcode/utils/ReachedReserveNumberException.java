package site.moku.qrcodescan.qrcode.utils;

public class ReachedReserveNumberException extends Exception {

    private String message;

    public ReachedReserveNumberException(String message) {
        this.message = message;
    }
}
