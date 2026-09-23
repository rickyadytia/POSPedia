package id.pospedia.app.payment;
public interface PaymentProvider {
    String providerCode();
    boolean supports(String method);
    Result charge(long amount,String reference);
    final class Result { public final boolean success; public final String reference; public final String message; public Result(boolean success,String reference,String message){this.success=success;this.reference=reference;this.message=message;} }
}
