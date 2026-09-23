package id.pospedia.app.domain;

public final class CartItem {
    public final Product product;
    public int qty;
    public String note="";
    public CartItem(Product product){ this.product=product; this.qty=1; }
    public long subtotal(){ return product.price * qty; }
}
