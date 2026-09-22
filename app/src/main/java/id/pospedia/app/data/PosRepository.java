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
        List<Product> out=new ArrayList<>(); SQLiteDatabase db=helper.getReadableDatabase();
        String q="%"+(search==null?"":search)+"%";
        try(Cursor c=db.rawQuery("SELECT id,tenant_id,outlet_id,sku,name,category,price,stock FROM products WHERE tenant_id=? AND outlet_id=? AND active=1 AND name LIKE ? ORDER BY name",new String[]{tenant,outlet,q})){
            while(c.moveToNext()) out.add(new Product(c.getLong(0),c.getString(1),c.getString(2),c.getString(3),c.getString(4),c.getString(5),c.getLong(6),c.getInt(7)));
        } return out;
    }

    public String saveTransaction(String tenant,String outlet,List<CartItem> cart,long total,String method,long cash,long change){
        String id="TRX-"+System.currentTimeMillis(); SQLiteDatabase db=helper.getWritableDatabase(); db.beginTransaction();
        try{
            ContentValues h=new ContentValues(); h.put("id",id);h.put("tenant_id",tenant);h.put("outlet_id",outlet);h.put("created_at",System.currentTimeMillis());h.put("total",total);h.put("payment_method",method);h.put("cash",cash);h.put("change_amount",change);
            int count=0; for(CartItem i:cart) count+=i.qty; h.put("item_count",count); db.insertOrThrow("transactions",null,h);
            for(CartItem i:cart){ ContentValues v=new ContentValues();v.put("transaction_id",id);v.put("product_id",i.product.id);v.put("name",i.product.name);v.put("qty",i.qty);v.put("price",i.product.price);v.put("subtotal",i.subtotal());db.insertOrThrow("transaction_items",null,v); }
            db.setTransactionSuccessful(); return id;
        } finally { db.endTransaction(); }
    }

    public List<String[]> transactions(String tenant,String outlet){
        List<String[]> out=new ArrayList<>(); SQLiteDatabase db=helper.getReadableDatabase();
        try(Cursor c=db.rawQuery("SELECT id,created_at,total,payment_method,item_count,cash,change_amount FROM transactions WHERE tenant_id=? AND outlet_id=? ORDER BY created_at DESC",new String[]{tenant,outlet})){
            while(c.moveToNext()) out.add(new String[]{c.getString(0),String.valueOf(c.getLong(1)),String.valueOf(c.getLong(2)),c.getString(3),String.valueOf(c.getInt(4)),String.valueOf(c.getLong(5)),String.valueOf(c.getLong(6))});
        } return out;
    }
}
