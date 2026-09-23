package id.pospedia.app.domain;

public final class Product {
    public final long id;
    public final String tenantId, outletId, sku, name, category;
    public final long price;
    public final int stock;
    public Product(long id,String tenantId,String outletId,String sku,String name,String category,long price,int stock){
        this.id=id; this.tenantId=tenantId; this.outletId=outletId; this.sku=sku; this.name=name; this.category=category; this.price=price; this.stock=stock;
    }
}
