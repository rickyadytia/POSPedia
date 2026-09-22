package id.pospedia.app;

import android.app.*;
import android.os.*;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.text.InputType;
import android.view.*;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import id.pospedia.app.data.*;
import id.pospedia.app.domain.*;
import id.pospedia.app.util.Money;
import java.text.SimpleDateFormat;
import java.util.*;

public class MainActivity extends AppCompatActivity {
    private final int GREEN=Color.rgb(5,150,105), DARK=Color.rgb(15,23,42), MUTED=Color.rgb(100,116,139), BG=Color.rgb(248,250,252);
    private SessionStore session; private PosRepository repo; private LinearLayout root,content,nav; private final List<CartItem> cart=new ArrayList<>();
    private String lastTransactionId=""; private long lastTotal=0,lastCash=0,lastChange=0;

    @Override protected void onCreate(Bundle b){super.onCreate(b);session=new SessionStore(this);repo=new PosRepository(this);showSplash();}
    private void showSplash(){
        root=vertical(); root.setGravity(Gravity.CENTER); GradientDrawable g=new GradientDrawable(GradientDrawable.Orientation.TL_BR,new int[]{Color.rgb(6,78,59),GREEN});root.setBackground(g);
        TextView logo=text("POSPedia",38,Color.WHITE,true);root.addView(logo);TextView tag=text("Smart POS for Growing Business",16,0xffd1fae5,false);tag.setPadding(0,14,0,0);root.addView(tag);setContentView(root);
        new Handler(Looper.getMainLooper()).postDelayed(()->{if(session.loggedIn()) showShell("home"); else showLogin();},900);
    }
    private void showLogin(){
        ScrollView sv=new ScrollView(this); root=vertical();root.setPadding(dp(28),dp(54),dp(28),dp(28));root.setBackgroundColor(BG);sv.addView(root);
        root.addView(text("POSPedia",32,GREEN,true));root.addView(spacer(26));root.addView(text("Selamat Datang 👋",28,DARK,true));root.addView(text("Masuk untuk mulai melayani pelanggan.",15,MUTED,false));root.addView(spacer(28));
        EditText tenant=input("Tenant Code");tenant.setText("DEMO");EditText email=input("Email");email.setText("cashier@demo.id");EditText pass=input("Password");pass.setText("demo");pass.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_PASSWORD);
        root.addView(label("TENANT"));root.addView(tenant);root.addView(spacer(14));root.addView(label("EMAIL"));root.addView(email);root.addView(spacer(14));root.addView(label("PASSWORD"));root.addView(pass);root.addView(spacer(20));
        Button login=button("Masuk ke POSPedia",GREEN);root.addView(login);root.addView(spacer(12));Button google=button("Login dengan Google — segera hadir",0xffe2e8f0);google.setTextColor(MUTED);google.setEnabled(false);root.addView(google);
        login.setOnClickListener(v->{String t=tenant.getText().toString().trim(),e=email.getText().toString().trim(),p=pass.getText().toString();if(t.isEmpty()||e.isEmpty()||p.isEmpty()){toast("Lengkapi data login");return;}if(!t.equalsIgnoreCase("DEMO")||!e.equalsIgnoreCase("cashier@demo.id")||!p.equals("demo")){toast("Gunakan akun demo yang tersedia");return;}chooseOutlet(t,e);});setContentView(sv);
    }
    private void chooseOutlet(String tenant,String email){new AlertDialog.Builder(this).setTitle("Pilih Outlet").setItems(new String[]{"Outlet Utama"},(d,w)->{session.login(tenant.toUpperCase(),email,"OUTLET-01");showShell("home");}).setNegativeButton("Batal",null).show();}
    private void showShell(String screen){
        root=vertical();root.setBackgroundColor(BG);LinearLayout header=horizontal();header.setGravity(Gravity.CENTER_VERTICAL);header.setPadding(dp(20),dp(14),dp(20),dp(12));header.setBackgroundColor(Color.WHITE);
        LinearLayout brand=vertical();brand.addView(text("POSPedia",22,GREEN,true));brand.addView(text("Demo Store • Outlet Utama",11,MUTED,false));header.addView(brand,new LinearLayout.LayoutParams(0,dp(56),1));header.addView(text("●",26,GREEN,false));root.addView(header);
        ScrollView scroll=new ScrollView(this);content=vertical();content.setPadding(dp(18),dp(18),dp(18),dp(28));scroll.addView(content);root.addView(scroll,new LinearLayout.LayoutParams(-1,0,1));
        nav=horizontal();nav.setGravity(Gravity.CENTER);nav.setPadding(dp(4),dp(5),dp(4),dp(5));nav.setBackgroundColor(Color.WHITE);
        addNav("⌂\nBeranda","home",screen);addNav("▣\nPOS","pos",screen);addNav("□\nProduk","products",screen);addNav("↻\nTransaksi","transactions",screen);addNav("•••\nLainnya","more",screen);root.addView(nav,new LinearLayout.LayoutParams(-1,dp(68)));setContentView(root);
        switch(screen){case"pos":renderPos("");break;case"products":renderProducts();break;case"transactions":renderTransactions();break;case"more":renderMore();break;default:renderHome();}
    }
    private void addNav(String title,String target,String active){Button b=new Button(this);b.setText(title);b.setTextSize(11);b.setAllCaps(false);b.setTextColor(target.equals(active)?GREEN:MUTED);b.setBackgroundColor(Color.TRANSPARENT);b.setOnClickListener(v->showShell(target));nav.addView(b,new LinearLayout.LayoutParams(0,-1,1));}
    private void renderHome(){
        content.addView(text("Halo, Cashier 👋",26,DARK,true));content.addView(text("Semoga penjualan hari ini lancar.",14,MUTED,false));content.addView(spacer(18));
        LinearLayout sales=card(GREEN);sales.addView(text("PENJUALAN HARI INI",12,0xffd1fae5,true));long total=0;int trx=0,items=0;for(String[] t:repo.transactions(session.tenant(),session.outlet())){total+=Long.parseLong(t[2]);trx++;items+=Integer.parseInt(t[4]);}
        sales.addView(text(Money.idr(total),30,Color.WHITE,true));sales.addView(text(trx+" transaksi  •  "+items+" item",13,0xffd1fae5,false));content.addView(sales);content.addView(spacer(22));
        content.addView(text("Menu Cepat",18,DARK,true));LinearLayout r1=horizontal();r1.addView(quick("▣","Penjualan",()->showShell("pos")));r1.addView(quick("□","Produk",()->showShell("products")));content.addView(r1);LinearLayout r2=horizontal();r2.addView(quick("↻","Riwayat",()->showShell("transactions")));r2.addView(quick("⌂","Outlet",()->showShell("more")));content.addView(r2);content.addView(spacer(20));content.addView(text("Transaksi Terbaru",18,DARK,true));renderRecent(3);
    }
    private void renderPos(String search){
        content.removeAllViews();content.addView(text("Penjualan",25,DARK,true));EditText s=input("Cari produk");s.setText(search);content.addView(s);Button find=button("Cari Produk",0xffe2e8f0);find.setTextColor(DARK);find.setOnClickListener(v->renderPos(s.getText().toString()));content.addView(find);content.addView(spacer(10));
        LinearLayout chips=horizontal();for(String c:new String[]{"Semua","Makanan","Minuman","Snack"}){TextView t=text(c,12,c.equals("Semua")?Color.WHITE:DARK,true);t.setGravity(Gravity.CENTER);t.setBackground(round(c.equals("Semua")?GREEN:0xffe2e8f0,18));t.setPadding(dp(10),dp(7),dp(10),dp(7));chips.addView(t,new LinearLayout.LayoutParams(0,-2,1));}content.addView(chips);content.addView(spacer(12));
        for(Product p:repo.products(session.tenant(),session.outlet(),search)){LinearLayout c=card(Color.WHITE);LinearLayout row=horizontal();LinearLayout info=vertical();info.addView(text(p.name,17,DARK,true));info.addView(text(p.category+" • Stok "+p.stock,12,MUTED,false));info.addView(text(Money.idr(p.price),16,GREEN,true));row.addView(info,new LinearLayout.LayoutParams(0,-2,1));Button add=button("+",GREEN);add.setOnClickListener(v->{addCart(p);renderPos(s.getText().toString());});row.addView(add,new LinearLayout.LayoutParams(dp(52),dp(48)));c.addView(row);content.addView(c);content.addView(spacer(9));}
        if(!cart.isEmpty()){Button cartBtn=button(cartQty()+" Item  •  "+Money.idr(cartTotal())+"   Lihat Keranjang",GREEN);cartBtn.setOnClickListener(v->renderCart());content.addView(cartBtn);}
    }
    private void addCart(Product p){for(CartItem i:cart)if(i.product.id==p.id){if(i.qty<p.stock)i.qty++;return;}cart.add(new CartItem(p));}
    private void renderCart(){
        content.removeAllViews();content.addView(text("Keranjang",25,DARK,true));for(CartItem i:new ArrayList<>(cart)){LinearLayout c=card(Color.WHITE);c.addView(text(i.product.name,17,DARK,true));c.addView(text(Money.idr(i.product.price),14,GREEN,true));LinearLayout q=horizontal();Button minus=button("−",0xffe2e8f0);minus.setTextColor(DARK);TextView qty=text(String.valueOf(i.qty),17,DARK,true);qty.setGravity(Gravity.CENTER);Button plus=button("+",GREEN);minus.setOnClickListener(v->{i.qty--;if(i.qty<=0)cart.remove(i);renderCart();});plus.setOnClickListener(v->{if(i.qty<i.product.stock)i.qty++;renderCart();});q.addView(minus,new LinearLayout.LayoutParams(0,dp(48),1));q.addView(qty,new LinearLayout.LayoutParams(0,dp(48),1));q.addView(plus,new LinearLayout.LayoutParams(0,dp(48),1));c.addView(q);content.addView(c);content.addView(spacer(8));}
        if(cart.isEmpty()){content.addView(text("Keranjang masih kosong.",15,MUTED,false));return;}summary(content,cartTotal());Button pay=button("Lanjut ke Pembayaran",GREEN);pay.setOnClickListener(v->renderPayment());content.addView(pay);
    }
    private void renderPayment(){
        content.removeAllViews();long total=cartTotal();content.addView(text("Pembayaran",25,DARK,true));content.addView(text("Total Pembayaran",13,MUTED,false));content.addView(text(Money.idr(total),32,DARK,true));content.addView(spacer(16));content.addView(text("Metode Pembayaran",16,DARK,true));
        RadioGroup methods=new RadioGroup(this);methods.setOrientation(RadioGroup.HORIZONTAL);String[] ms={"Tunai","QRIS","Kartu","Lainnya"};for(int x=0;x<ms.length;x++){RadioButton rb=new RadioButton(this);rb.setText(ms[x]);rb.setId(100+x);methods.addView(rb);if(x==0)rb.setChecked(true);}content.addView(methods);
        EditText cash=input("Uang diterima");cash.setInputType(InputType.TYPE_CLASS_NUMBER);content.addView(cash);LinearLayout quick=horizontal();for(long a:new long[]{total,50000,100000,200000}){Button b=button(a==total?"Uang Pas":Money.idr(a),0xffe2e8f0);b.setTextColor(DARK);b.setTextSize(10);b.setOnClickListener(v->cash.setText(String.valueOf(a)));quick.addView(b,new LinearLayout.LayoutParams(0,dp(48),1));}content.addView(quick);
        Button finish=button("Selesaikan Transaksi",GREEN);finish.setOnClickListener(v->{String method=ms[Math.max(0,methods.getCheckedRadioButtonId()-100)];long paid=total;if(method.equals("Tunai")){try{paid=Long.parseLong(cash.getText().toString());}catch(Exception e){toast("Masukkan uang diterima");return;}if(paid<total){toast("Uang diterima kurang dari total");return;}}complete(method,paid);});content.addView(spacer(16));content.addView(finish);
    }
    private void complete(String method,long cash){
        lastTotal=cartTotal();lastCash=cash;lastChange=Math.max(0,cash-lastTotal);lastTransactionId=repo.saveTransaction(session.tenant(),session.outlet(),cart,lastTotal,method,cash,lastChange);cart.clear();renderSuccess();
    }
    private void renderSuccess(){
        content.removeAllViews();content.setGravity(Gravity.CENTER_HORIZONTAL);content.addView(text("✓",68,GREEN,true));content.addView(text("Transaksi Berhasil!",27,DARK,true));content.addView(text(lastTransactionId,13,MUTED,false));content.addView(spacer(18));summary(content,lastTotal);content.addView(text("Bayar: "+Money.idr(lastCash)+"\nKembalian: "+Money.idr(lastChange),15,DARK,false));Button receipt=button("Lihat Struk",GREEN);receipt.setOnClickListener(v->renderReceipt());content.addView(receipt);Button again=button("Transaksi Baru",0xffe2e8f0);again.setTextColor(DARK);again.setOnClickListener(v->showShell("pos"));content.addView(again);
    }
    private void renderReceipt(){
        content.removeAllViews();content.addView(text("POSPedia",28,DARK,true));content.addView(text("Demo Store\nOutlet Utama\n"+lastTransactionId+"\n"+new SimpleDateFormat("dd MMM yyyy HH:mm",Locale.getDefault()).format(new Date())+"\nCashier: "+session.email(),14,MUTED,false));content.addView(spacer(12));content.addView(text("TOTAL  "+Money.idr(lastTotal)+"\nBAYAR  "+Money.idr(lastCash)+"\nKEMBALIAN  "+Money.idr(lastChange),16,DARK,true));content.addView(spacer(18));content.addView(text("Terima kasih\nFormat disiapkan untuk thermal receipt 58mm",13,MUTED,false));
    }
    private void renderTransactions(){content.addView(text("Riwayat Transaksi",25,DARK,true));content.addView(text("Tersimpan offline dan terisolasi per tenant/outlet.",13,MUTED,false));content.addView(spacer(12));renderRecent(100);}
    private void renderRecent(int limit){int n=0;for(String[] t:repo.transactions(session.tenant(),session.outlet())){if(n++>=limit)break;LinearLayout c=card(Color.WHITE);c.addView(text(t[0],15,DARK,true));c.addView(text(t[3]+" • "+t[4]+" item",12,MUTED,false));c.addView(text(Money.idr(Long.parseLong(t[2])),17,GREEN,true));content.addView(c);content.addView(spacer(8));}if(n==0)content.addView(text("Belum ada transaksi.",14,MUTED,false));}
    private void renderProducts(){content.addView(text("Produk",25,DARK,true));content.addView(text("Katalog tenant "+session.tenant(),13,MUTED,false));content.addView(spacer(12));for(Product p:repo.products(session.tenant(),session.outlet(),"")){LinearLayout c=card(Color.WHITE);c.addView(text(p.name,16,DARK,true));c.addView(text(p.sku+" • "+p.category+" • Stok "+p.stock,12,MUTED,false));c.addView(text(Money.idr(p.price),15,GREEN,true));content.addView(c);content.addView(spacer(8));}TextView note=text("Tambah/edit/deactivate produk akan diaktifkan pada fase API/admin.",12,MUTED,false);content.addView(note);}
    private void renderMore(){content.addView(text("Lainnya",25,DARK,true));LinearLayout profile=card(Color.WHITE);profile.addView(text("Cashier Demo",18,DARK,true));profile.addView(text(session.email()+"\nTenant: "+session.tenant()+"\nOutlet: "+session.outlet()+"\nRole: CASHIER",13,MUTED,false));content.addView(profile);for(String m:new String[]{"Tenant & Outlet","Users & Permission","Printer 58mm","Settings","Tentang POSPedia • v0.1.0"}){TextView x=text(m+"  ›",16,DARK,false);x.setPadding(dp(8),dp(15),dp(8),dp(15));content.addView(x);}Button logout=button("Logout",0xfffee2e2);logout.setTextColor(0xffb91c1c);logout.setOnClickListener(v->{session.logout();cart.clear();showLogin();});content.addView(logout);}
    private void summary(LinearLayout parent,long total){LinearLayout c=card(Color.WHITE);c.addView(text("Subtotal   "+Money.idr(total)+"\nDiskon      "+Money.idr(0)+"\nPajak        "+Money.idr(0),14,MUTED,false));c.addView(text("Total         "+Money.idr(total),19,DARK,true));parent.addView(c);}
    private int cartQty(){int n=0;for(CartItem i:cart)n+=i.qty;return n;} private long cartTotal(){long n=0;for(CartItem i:cart)n+=i.subtotal();return n;}
    private LinearLayout quick(String icon,String name,Runnable r){LinearLayout c=card(Color.WHITE);c.setGravity(Gravity.CENTER);c.addView(text(icon,26,GREEN,true));c.addView(text(name,13,DARK,true));c.setOnClickListener(v->r.run());LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(0,dp(92),1);p.setMargins(dp(4),dp(4),dp(4),dp(4));c.setLayoutParams(p);return c;}
    private LinearLayout card(int color){LinearLayout l=vertical();l.setPadding(dp(18),dp(16),dp(18),dp(16));l.setBackground(round(color,18));return l;}
    private GradientDrawable round(int color,int radius){GradientDrawable d=new GradientDrawable();d.setColor(color);d.setCornerRadius(dp(radius));if(color==Color.WHITE)d.setStroke(dp(1),0xffe2e8f0);return d;}
    private EditText input(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextColor(DARK);e.setHintTextColor(0xff94a3b8);e.setTextSize(15);e.setSingleLine(true);e.setPadding(dp(14),0,dp(14),0);e.setBackground(round(Color.WHITE,14));e.setLayoutParams(new LinearLayout.LayoutParams(-1,dp(54)));return e;}
    private Button button(String s,int color){Button b=new Button(this);b.setText(s);b.setAllCaps(false);b.setTextColor(Color.WHITE);b.setTextSize(14);b.setBackground(round(color,14));LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(52));p.setMargins(0,dp(5),0,dp(5));b.setLayoutParams(p);return b;}
    private TextView label(String s){TextView t=text(s,11,MUTED,true);t.setPadding(2,0,0,6);return t;}
    private TextView text(String s,float size,int color,boolean bold){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(color);if(bold)t.setTypeface(null,1);return t;}
    private Space spacer(int h){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));return s;}
    private LinearLayout vertical(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.VERTICAL);return l;}private LinearLayout horizontal(){LinearLayout l=new LinearLayout(this);l.setOrientation(LinearLayout.HORIZONTAL);return l;}
    private int dp(int v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}private void toast(String s){Toast.makeText(this,s,Toast.LENGTH_SHORT).show();}
}
