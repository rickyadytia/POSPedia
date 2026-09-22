package id.pospedia.app.data;

import android.content.*;
import android.database.sqlite.*;
import id.pospedia.app.domain.Product;
import java.util.*;

public final class AppDatabase extends SQLiteOpenHelper {
    public AppDatabase(Context c){ super(c,"pospedia.db",null,1); }
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE products(id INTEGER PRIMARY KEY AUTOINCREMENT,tenant_id TEXT NOT NULL,outlet_id TEXT NOT NULL,sku TEXT,name TEXT,category TEXT,price INTEGER,stock INTEGER,active INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE transactions(id TEXT PRIMARY KEY,tenant_id TEXT NOT NULL,outlet_id TEXT NOT NULL,created_at INTEGER,total INTEGER,payment_method TEXT,cash INTEGER,change_amount INTEGER,item_count INTEGER)");
        db.execSQL("CREATE TABLE transaction_items(id INTEGER PRIMARY KEY AUTOINCREMENT,transaction_id TEXT,product_id INTEGER,name TEXT,qty INTEGER,price INTEGER,subtotal INTEGER)");
        seed(db,"Kopi Susu","Makanan",18000,40); seed(db,"Es Teh","Minuman",10000,55); seed(db,"Nasi Goreng","Makanan",28000,25);
        seed(db,"Mie Goreng","Makanan",25000,30); seed(db,"Air Mineral","Minuman",7000,80); seed(db,"Kentang Goreng","Snack",22000,20);
    }
    private void seed(SQLiteDatabase db,String name,String cat,long price,int stock){
        ContentValues v=new ContentValues(); v.put("tenant_id","DEMO");v.put("outlet_id","OUTLET-01");v.put("sku","SKU-"+name.hashCode());v.put("name",name);v.put("category",cat);v.put("price",price);v.put("stock",stock);db.insert("products",null,v);
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV){}
}
