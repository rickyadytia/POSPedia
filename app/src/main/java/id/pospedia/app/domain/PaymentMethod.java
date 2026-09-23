package id.pospedia.app.domain;
public enum PaymentMethod {
    CASH("Tunai"), QRIS("QRIS"), BANK_TRANSFER("Bank Transfer"), DEBIT_CARD("Debit Card"), CREDIT_CARD("Credit Card"), OTHER("Lainnya");
    public final String label; PaymentMethod(String label){this.label=label;}
}
