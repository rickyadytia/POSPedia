package id.pospedia.app.data;

import android.content.Context;
import android.content.SharedPreferences;

public final class SessionStore {
    private final SharedPreferences p;
    public SessionStore(Context c){ p=c.getSharedPreferences("pospedia_session",Context.MODE_PRIVATE); }
    public boolean loggedIn(){ return p.getBoolean("logged_in",false); }
    public void login(String tenant,String email,String outlet){ p.edit().putBoolean("logged_in",true).putString("tenant",tenant).putString("email",email).putString("outlet",outlet).apply(); }
    public void logout(){ p.edit().clear().apply(); }
    public String tenant(){ return p.getString("tenant","DEMO"); }
    public String email(){ return p.getString("email","cashier@demo.id"); }
    public String outlet(){ return p.getString("outlet","OUTLET-01"); }
}
