package id.pospedia.app.data;

import android.content.*;
import android.database.sqlite.*;

public final class AppDatabase extends SQLiteOpenHelper {
    public AppDatabase(Context c){ super(c,"pospedia.db",null,2); }
    @Override public void onCreate(SQLiteDatabase db){
        db.execSQL("CREATE TABLE products(id INTEGER PRIMARY KEY AUTOINCREMENT,tenant_id TEXT NOT NULL,outlet_id TEXT NOT NULL,sku TEXT,barcode TEXT,name TEXT,category TEXT,price INTEGER,cost INTEGER,stock INTEGER,low_stock INTEGER DEFAULT 5,image_uri TEXT,active INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE categories(id INTEGER PRIMARY KEY AUTOINCREMENT,tenant_id TEXT NOT NULL,name TEXT NOT NULL,active INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE customers(id INTEGER PRIMARY KEY AUTOINCREMENT,tenant_id TEXT NOT NULL,name TEXT NOT NULL,phone TEXT,email TEXT,active INTEGER DEFAULT 1)");
        db.execSQL("CREATE TABLE inventory_history(id INTEGER PRIMARY KEY AUTOINCREMENT,tenant_id TEXT NOT NULL,outlet_id TEXT NOT NULL,product_id INTEGER,type TEXT,qty INTEGER,note TEXT,created_at INTEGER)");
        db.execSQL("CREATE TABLE transactions(id TEXT PRIMARY KEY,tenant_id TEXT NOT NULL,outlet_id TEXT NOT NULL,customer_id INTEGER,created_at INTEGER,subtotal INTEGER,discount INTEGER,tax INTEGER,total INTEGER,payment_method TEXT,cash INTEGER,change_amount INTEGER,item_count INTEGER,status TEXT DEFAULT 'COMPLETED')");
        db.execSQL("CREATE TABLE transaction_items(id INTEGER PRIMARY KEY AUTOINCREMENT,transaction_id TEXT,product_id INTEGER,name TEXT,qty INTEGER,price INTEGER,discount INTEGER DEFAULT 0,subtotal INTEGER)");
        db.execSQL("CREATE TABLE app_settings(tenant_id TEXT NOT NULL,key TEXT NOT NULL,value TEXT,PRIMARY KEY(tenant_id,key))");
        for(String c:new String[]{"Makanan","Minuman","Snack","Lainnya"}){ContentValues v=new ContentValues();v.put("tenant_id","DEMO");v.put("name",c);db.insert("categories",null,v);}
        seed(db,"KOPI-001","899000000001","Kopi Susu","Minuman",18000,9000,40); seed(db,"TEH-001","899000000002","Es Teh","Minuman",10000,3500,55); seed(db,"NASI-001","899000000003","Nasi Goreng","Makanan",28000,15000,25);
        seed(db,"MIE-001","899000000004","Mie Goreng","Makanan",25000,13000,30); seed(db,"AIR-001","899000000005","Air Mineral","Minuman",7000,3000,80); seed(db,"SNACK-001","899000000006","Kentang Goreng","Snack",22000,11000,4);
        ContentValues cu=new ContentValues();cu.put("tenant_id","DEMO");cu.put("name","Pelanggan Umum");cu.put("phone","");db.insert("customers",null,cu);
    }
    private void seed(SQLiteDatabase db,String sku,String barcode,String name,String cat,long price,long cost,int stock){
        ContentValues v=new ContentValues();v.put("tenant_id","DEMO");v.put("outlet_id","OUTLET-01");v.put("sku",sku);v.put("barcode",barcode);v.put("name",name);v.put("category",cat);v.put("price",price);v.put("cost",cost);v.put("stock",stock);db.insert("products",null,v);
    }
    @Override public void onUpgrade(SQLiteDatabase db,int oldV,int newV){
        if(oldV<2){db.execSQL("DROP TABLE IF EXISTS transaction_items");db.execSQL("DROP TABLE IF EXISTS transactions");db.execSQL("DROP TABLE IF EXISTS products");db.execSQL("DROP TABLE IF EXISTS categories");db.execSQL("DROP TABLE IF EXISTS customers");db.execSQL("DROP TABLE IF EXISTS inventory_history");db.execSQL("DROP TABLE IF EXISTS app_settings");onCreate(db);}
    }
}
