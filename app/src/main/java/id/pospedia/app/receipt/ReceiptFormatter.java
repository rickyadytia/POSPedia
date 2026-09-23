package id.pospedia.app.receipt;
import id.pospedia.app.util.Money;
public final class ReceiptFormatter {
    public enum Paper { MM58, MM80 }
    public static String header(String business,String outlet,String trx,String cashier){return business+"\n"+outlet+"\n"+trx+"\nKasir: "+cashier;}
    public static String totals(long subtotal,long discount,long tax,long total,String payment,long cash,long change){return "Subtotal "+Money.idr(subtotal)+"\nDiskon "+Money.idr(discount)+"\nPajak "+Money.idr(tax)+"\nTOTAL "+Money.idr(total)+"\n"+payment+" "+Money.idr(cash)+"\nKembalian "+Money.idr(change);}
    private ReceiptFormatter(){}
}
