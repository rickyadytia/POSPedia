package id.pospedia.app.data;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import id.pospedia.app.domain.*;
import java.util.*;

public final class PosRepository {
    private final AppDatabase helper;
    public PosRepository(Context c){ helper=new AppDatabase(c); }

    public List<Product> products(String tenant,String outlet,String search){
        List<Product> out=new ArrayList<>(); SQLiteDatabase db=helper.getReadableDatabase(); String q="%"+(search==null?"":search)+"%";
        try(Cursor c=db.rawQuery("SELECT id,tenant_id,outlet_id,sku,name,category,price,stock FROM products WHERE tenant_id=? AND outlet_id=? AND active=1 AND (name LIKE ? OR sku LIKE ? OR barcode LIKE ?) ORDER BY name",new String[]{tenant,outlet,q,q,q})){
            while(c.moveToNext()) out.add(new Product(c.getLong(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getLong(6),c.getInt(7)));
        } return out;
    }
    public long addProduct(String tenant,String outlet,String sku,String barcode,String name,String category,long price,long cost,int stock){
        ContentValues v=new ContentValues();v.put("tenant_id",tenant);v.put("outlet_id",outlet);v.put("sku",sku);v.put("barcode",barcode);v.put("name",name);v.put("category",category);v.put("price",price);v.put("cost",cost);v.put("stock",stock);return helper.getWritableDatabase().insertOrThrow("products",null,v);
    }
    public void updateProduct(String tenant,long id,String name,long price,int stock){ContentValues v=new ContentValues();v.put("name",name);v.put("price",price);v.put("stock",stock);helper.getWritableDatabase().update("products",v,"id=? AND tenant_id=?",new String[]{String.valueOf(id),tenant});}
    public void deactivateProduct(String tenant,long id){ContentValues v=new ContentValues();v.put("active",0);helper.getWritableDatabase().update("products",v,"id=? AND tenant_id=?",new String[]{String.valueOf(id),tenant});}
    public List<String> categories(String tenant){List<String> out=new ArrayList<>();try(Cursor c=helper.getReadableDatabase().rawQuery("SELECT name FROM categories WHERE tenant_id=? AND active=1 ORDER BY name",new String[]{tenant})){while(c.moveToNext())out.add(c.getString(0));}return out;}
    public void addCategory(String tenant,String name){ContentValues v=new ContentValues();v.put("tenant_id",tenant);v.put("name",name);helper.getWritableDatabase().insert("categories",null,v);}
    public List<String[]> customers(String tenant){List<String[]> out=new ArrayList<>();try(Cursor c=helper.getReadableDatabase().rawQuery("SELECT id,name,phone,email FROM customers WHERE tenant_id=? AND active=1 ORDER BY name",new String[]{tenant})){while(c.moveToNext())out.add(new String[]{c.getString(0),c.getString(1),c.getString(2),c.getString(3)});}return out;}
    public void addCustomer(String tenant,String name,String phone,String email){ContentValues v=new ContentValues();v.put("tenant_id",tenant);v.put("name",name);v.put("phone",phone);v.put("email",email);helper.getWritableDatabase().insert("customers",null,v);}
    public void adjustStock(String tenant,String outlet,long productId,int delta,String type,String note){
        SQLiteDatabase db=helper.getWritableDatabase();db.beginTransaction();try{db.execSQL("UPDATE products SET stock=MAX(0,stock+?) WHERE id=? AND tenant_id=? AND outlet_id=?",new Object[]{delta,productId,tenant,outlet});ContentValues v=new ContentValues();v.put("tenant_id",tenant);v.put("outlet_id",outlet);v.put("product_id",productId);v.put("type",type);v.put("qty",delta);v.put("note",note);v.put("created_at",System.currentTimeMillis());db.insert("inventory_history",null,v);db.setTransactionSuccessful();}finally{db.endTransaction();}
    }
    public int lowStockCount(String tenant,String outlet){try(Cursor c=helper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM products WHERE tenant_id=? AND outlet_id=? AND active=1 AND stock<=low_stock",new String[]{tenant,outlet})){return c.moveToFirst()?c.getInt(0):0;}}
    public int productCount(String tenant,String outlet){try(Cursor c=helper.getReadableDatabase().rawQuery("SELECT COUNT(*) FROM products WHERE tenant_id=? AND outlet_id=? AND active=1",new String[]{tenant,outlet})){return c.moveToFirst()?c.getInt(0):0;}}
    public String saveTransaction(String tenant,String outlet,List<CartItem> cart,long subtotal,long discount,long tax,long total,String method,long cash,long change){
        String id="TRX-"+System.currentTimeMillis(); SQLiteDatabase db=helper.getWritableDatabase(); db.beginTransaction();
        try{ContentValues h=new ContentValues();h.put("id",id);h.put("tenant_id",tenant);h.put("outlet_id",outlet);h.put("created_at",System.currentTimeMillis());h.put("subtotal",subtotal);h.put("discount",discount);h.put("tax",tax);h.put("total",total);h.put("payment_method",method);h.put("cash",cash);h.put("change_amount",change);int count=0;for(CartItem i:cart)count+=i.qty;h.put("item_count",count);db.insertOrThrow("transactions",null,h);
            for(CartItem i:cart){ContentValues v=new ContentValues();v.put("transaction_id",id);v.put("product_id",i.product.id);v.put("name",i.product.name);v.put("qty",i.qty);v.put("price",i.product.price);v.put("subtotal",i.subtotal());db.insertOrThrow("transaction_items",null,v);db.execSQL("UPDATE products SET stock=MAX(0,stock-?) WHERE id=? AND tenant_id=? AND outlet_id=?",new Object[]{i.qty,i.product.id,tenant,outlet});}
            db.setTransactionSuccessful();return id;
        }finally{db.endTransaction();}
    }
    public String saveTransaction(String tenant,String outlet,List<CartItem> cart,long total,String method,long cash,long change){return saveTransaction(tenant,outlet,cart,total,0,0,total,method,cash,change);}
    public List<String[]> transactions(String tenant,String outlet){List<String[]> out=new ArrayList<>();try(Cursor c=helper.getReadableDatabase().rawQuery("SELECT id,created_at,total,payment_method,item_count,cash,change_amount,status FROM transactions WHERE tenant_id=? AND outlet_id=? ORDER BY created_at DESC",new String[]{tenant,outlet})){while(c.moveToNext())out.add(new String[]{c.getString(0),String.valueOf(c.getLong(1)),String.valueOf(c.getLong(2)),c.getString(3),String.valueOf(c.getInt(4)),String.valueOf(c.getLong(5)),String.valueOf(c.getLong(6)),c.getString(7)});}return out;}
    public long[] todaySummary(String tenant,String outlet){long start=System.currentTimeMillis()-86400000L;try(Cursor c=helper.getReadableDatabase().rawQuery("SELECT COALESCE(SUM(total),0),COUNT(*),COALESCE(AVG(total),0) FROM transactions WHERE tenant_id=? AND outlet_id=? AND created_at>=? AND status='COMPLETED'",new String[]{tenant,outlet,String.valueOf(start)})){return c.moveToFirst()?new long[]{c.getLong(0),c.getLong(1),c.getLong(2)}:new long[]{0,0,0};}}
    public Map<String,Long> salesByPayment(String tenant,String outlet){Map<String,Long> m=new LinkedHashMap<>();try(Cursor c=helper.getReadableDatabase().rawQuery("SELECT payment_method,COALESCE(SUM(total),0) FROM transactions WHERE tenant_id=? AND outlet_id=? AND status='COMPLETED' GROUP BY payment_method ORDER BY 2 DESC",new String[]{tenant,outlet})){while(c.moveToNext())m.put(c.getString(0),c.getLong(1));}return m;}
}
