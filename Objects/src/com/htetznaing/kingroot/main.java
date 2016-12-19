package com.htetznaing.kingroot;


import anywheresoftware.b4a.B4AMenuItem;
import android.app.Activity;
import android.os.Bundle;
import anywheresoftware.b4a.BA;
import anywheresoftware.b4a.BALayout;
import anywheresoftware.b4a.B4AActivity;
import anywheresoftware.b4a.ObjectWrapper;
import anywheresoftware.b4a.objects.ActivityWrapper;
import java.lang.reflect.InvocationTargetException;
import anywheresoftware.b4a.B4AUncaughtException;
import anywheresoftware.b4a.debug.*;
import java.lang.ref.WeakReference;

public class main extends Activity implements B4AActivity{
	public static main mostCurrent;
	static boolean afterFirstLayout;
	static boolean isFirst = true;
    private static boolean processGlobalsRun = false;
	BALayout layout;
	public static BA processBA;
	BA activityBA;
    ActivityWrapper _activity;
    java.util.ArrayList<B4AMenuItem> menuItems;
	public static final boolean fullScreen = false;
	public static final boolean includeTitle = true;
    public static WeakReference<Activity> previousOne;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (isFirst) {
			processBA = new BA(this.getApplicationContext(), null, null, "com.htetznaing.kingroot", "com.htetznaing.kingroot.main");
			processBA.loadHtSubs(this.getClass());
	        float deviceScale = getApplicationContext().getResources().getDisplayMetrics().density;
	        BALayout.setDeviceScale(deviceScale);
            
		}
		else if (previousOne != null) {
			Activity p = previousOne.get();
			if (p != null && p != this) {
                BA.LogInfo("Killing previous instance (main).");
				p.finish();
			}
		}
        processBA.runHook("oncreate", this, null);
		if (!includeTitle) {
        	this.getWindow().requestFeature(android.view.Window.FEATURE_NO_TITLE);
        }
        if (fullScreen) {
        	getWindow().setFlags(android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN,   
        			android.view.WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }
		mostCurrent = this;
        processBA.sharedProcessBA.activityBA = null;
		layout = new BALayout(this);
		setContentView(layout);
		afterFirstLayout = false;
        WaitForLayout wl = new WaitForLayout();
        if (anywheresoftware.b4a.objects.ServiceHelper.StarterHelper.startFromActivity(processBA, wl, false))
		    BA.handler.postDelayed(wl, 5);

	}
	static class WaitForLayout implements Runnable {
		public void run() {
			if (afterFirstLayout)
				return;
			if (mostCurrent == null)
				return;
            
			if (mostCurrent.layout.getWidth() == 0) {
				BA.handler.postDelayed(this, 5);
				return;
			}
			mostCurrent.layout.getLayoutParams().height = mostCurrent.layout.getHeight();
			mostCurrent.layout.getLayoutParams().width = mostCurrent.layout.getWidth();
			afterFirstLayout = true;
			mostCurrent.afterFirstLayout();
		}
	}
	private void afterFirstLayout() {
        if (this != mostCurrent)
			return;
		activityBA = new BA(this, layout, processBA, "com.htetznaing.kingroot", "com.htetznaing.kingroot.main");
        
        processBA.sharedProcessBA.activityBA = new java.lang.ref.WeakReference<BA>(activityBA);
        anywheresoftware.b4a.objects.ViewWrapper.lastId = 0;
        _activity = new ActivityWrapper(activityBA, "activity");
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (BA.isShellModeRuntimeCheck(processBA)) {
			if (isFirst)
				processBA.raiseEvent2(null, true, "SHELL", false);
			processBA.raiseEvent2(null, true, "CREATE", true, "com.htetznaing.kingroot.main", processBA, activityBA, _activity, anywheresoftware.b4a.keywords.Common.Density, mostCurrent);
			_activity.reinitializeForShell(activityBA, "activity");
		}
        initializeProcessGlobals();		
        initializeGlobals();
        
        BA.LogInfo("** Activity (main) Create, isFirst = " + isFirst + " **");
        processBA.raiseEvent2(null, true, "activity_create", false, isFirst);
		isFirst = false;
		if (this != mostCurrent)
			return;
        processBA.setActivityPaused(false);
        BA.LogInfo("** Activity (main) Resume **");
        processBA.raiseEvent(null, "activity_resume");
        if (android.os.Build.VERSION.SDK_INT >= 11) {
			try {
				android.app.Activity.class.getMethod("invalidateOptionsMenu").invoke(this,(Object[]) null);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}
	public void addMenuItem(B4AMenuItem item) {
		if (menuItems == null)
			menuItems = new java.util.ArrayList<B4AMenuItem>();
		menuItems.add(item);
	}
	@Override
	public boolean onCreateOptionsMenu(android.view.Menu menu) {
		super.onCreateOptionsMenu(menu);
        try {
            if (processBA.subExists("activity_actionbarhomeclick")) {
                Class.forName("android.app.ActionBar").getMethod("setHomeButtonEnabled", boolean.class).invoke(
                    getClass().getMethod("getActionBar").invoke(this), true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (processBA.runHook("oncreateoptionsmenu", this, new Object[] {menu}))
            return true;
		if (menuItems == null)
			return false;
		for (B4AMenuItem bmi : menuItems) {
			android.view.MenuItem mi = menu.add(bmi.title);
			if (bmi.drawable != null)
				mi.setIcon(bmi.drawable);
            if (android.os.Build.VERSION.SDK_INT >= 11) {
				try {
                    if (bmi.addToBar) {
				        android.view.MenuItem.class.getMethod("setShowAsAction", int.class).invoke(mi, 1);
                    }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			mi.setOnMenuItemClickListener(new B4AMenuItemsClickListener(bmi.eventName.toLowerCase(BA.cul)));
		}
        
		return true;
	}   
 @Override
 public boolean onOptionsItemSelected(android.view.MenuItem item) {
    if (item.getItemId() == 16908332) {
        processBA.raiseEvent(null, "activity_actionbarhomeclick");
        return true;
    }
    else
        return super.onOptionsItemSelected(item); 
}
@Override
 public boolean onPrepareOptionsMenu(android.view.Menu menu) {
    super.onPrepareOptionsMenu(menu);
    processBA.runHook("onprepareoptionsmenu", this, new Object[] {menu});
    return true;
    
 }
 protected void onStart() {
    super.onStart();
    processBA.runHook("onstart", this, null);
}
 protected void onStop() {
    super.onStop();
    processBA.runHook("onstop", this, null);
}
    public void onWindowFocusChanged(boolean hasFocus) {
       super.onWindowFocusChanged(hasFocus);
       if (processBA.subExists("activity_windowfocuschanged"))
           processBA.raiseEvent2(null, true, "activity_windowfocuschanged", false, hasFocus);
    }
	private class B4AMenuItemsClickListener implements android.view.MenuItem.OnMenuItemClickListener {
		private final String eventName;
		public B4AMenuItemsClickListener(String eventName) {
			this.eventName = eventName;
		}
		public boolean onMenuItemClick(android.view.MenuItem item) {
			processBA.raiseEvent(item.getTitle(), eventName + "_click");
			return true;
		}
	}
    public static Class<?> getObject() {
		return main.class;
	}
    private Boolean onKeySubExist = null;
    private Boolean onKeyUpSubExist = null;
	@Override
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeydown", this, new Object[] {keyCode, event}))
            return true;
		if (onKeySubExist == null)
			onKeySubExist = processBA.subExists("activity_keypress");
		if (onKeySubExist) {
			if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK &&
					android.os.Build.VERSION.SDK_INT >= 18) {
				HandleKeyDelayed hk = new HandleKeyDelayed();
				hk.kc = keyCode;
				BA.handler.post(hk);
				return true;
			}
			else {
				boolean res = new HandleKeyDelayed().runDirectly(keyCode);
				if (res)
					return true;
			}
		}
		return super.onKeyDown(keyCode, event);
	}
	private class HandleKeyDelayed implements Runnable {
		int kc;
		public void run() {
			runDirectly(kc);
		}
		public boolean runDirectly(int keyCode) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keypress", false, keyCode);
			if (res == null || res == true) {
                return true;
            }
            else if (keyCode == anywheresoftware.b4a.keywords.constants.KeyCodes.KEYCODE_BACK) {
				finish();
				return true;
			}
            return false;
		}
		
	}
    @Override
	public boolean onKeyUp(int keyCode, android.view.KeyEvent event) {
        if (processBA.runHook("onkeyup", this, new Object[] {keyCode, event}))
            return true;
		if (onKeyUpSubExist == null)
			onKeyUpSubExist = processBA.subExists("activity_keyup");
		if (onKeyUpSubExist) {
			Boolean res =  (Boolean)processBA.raiseEvent2(_activity, false, "activity_keyup", false, keyCode);
			if (res == null || res == true)
				return true;
		}
		return super.onKeyUp(keyCode, event);
	}
	@Override
	public void onNewIntent(android.content.Intent intent) {
        super.onNewIntent(intent);
		this.setIntent(intent);
        processBA.runHook("onnewintent", this, new Object[] {intent});
	}
    @Override 
	public void onPause() {
		super.onPause();
        if (_activity == null) //workaround for emulator bug (Issue 2423)
            return;
		anywheresoftware.b4a.Msgbox.dismiss(true);
        BA.LogInfo("** Activity (main) Pause, UserClosed = " + activityBA.activity.isFinishing() + " **");
        processBA.raiseEvent2(_activity, true, "activity_pause", false, activityBA.activity.isFinishing());		
        processBA.setActivityPaused(true);
        mostCurrent = null;
        if (!activityBA.activity.isFinishing())
			previousOne = new WeakReference<Activity>(this);
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        processBA.runHook("onpause", this, null);
	}

	@Override
	public void onDestroy() {
        super.onDestroy();
		previousOne = null;
        processBA.runHook("ondestroy", this, null);
	}
    @Override 
	public void onResume() {
		super.onResume();
        mostCurrent = this;
        anywheresoftware.b4a.Msgbox.isDismissing = false;
        if (activityBA != null) { //will be null during activity create (which waits for AfterLayout).
        	ResumeMessage rm = new ResumeMessage(mostCurrent);
        	BA.handler.post(rm);
        }
        processBA.runHook("onresume", this, null);
	}
    private static class ResumeMessage implements Runnable {
    	private final WeakReference<Activity> activity;
    	public ResumeMessage(Activity activity) {
    		this.activity = new WeakReference<Activity>(activity);
    	}
		public void run() {
			if (mostCurrent == null || mostCurrent != activity.get())
				return;
			processBA.setActivityPaused(false);
            BA.LogInfo("** Activity (main) Resume **");
		    processBA.raiseEvent(mostCurrent._activity, "activity_resume", (Object[])null);
		}
    }
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
	      android.content.Intent data) {
		processBA.onActivityResult(requestCode, resultCode, data);
        processBA.runHook("onactivityresult", this, new Object[] {requestCode, resultCode});
	}
	private static void initializeGlobals() {
		processBA.raiseEvent2(null, true, "globals", false, (Object[])null);
	}
    public void onRequestPermissionsResult(int requestCode,
        String permissions[], int[] grantResults) {
        Object[] o;
        if (permissions.length > 0)
            o = new Object[] {permissions[0], grantResults[0] == 0};
        else
            o = new Object[] {"", false};
        processBA.raiseEventFromDifferentThread(null,null, 0, "activity_permissionresult", true, o);
            
    }

public anywheresoftware.b4a.keywords.Common __c = null;
public static anywheresoftware.b4a.objects.Timer _ad1 = null;
public static anywheresoftware.b4a.objects.Timer _ad2 = null;
public anywheresoftware.b4a.objects.ImageViewWrapper _imv = null;
public anywheresoftware.b4a.phone.Phone _ph = null;
public anywheresoftware.b4a.objects.ButtonWrapper _b1 = null;
public anywheresoftware.b4a.objects.LabelWrapper _lfoot = null;
public anywheresoftware.b4a.admobwrapper.AdViewWrapper _banner = null;
public mobi.mindware.admob.interstitial.AdmobInterstitialsAds _interstitial = null;
public anywheresoftware.b4a.objects.LabelWrapper _ab = null;
public static String _os = "";
public com.htetznaing.kingroot.starter _starter = null;

public static boolean isAnyActivityVisible() {
    boolean vis = false;
vis = vis | (main.mostCurrent != null);
return vis;}
public static String  _activity_create(boolean _firsttime) throws Exception{
anywheresoftware.b4a.objects.drawable.ColorDrawable _b1bg = null;
anywheresoftware.b4a.objects.LabelWrapper _l = null;
 //BA.debugLineNum = 31;BA.debugLine="Sub Activity_Create(FirstTime As Boolean)";
 //BA.debugLineNum = 32;BA.debugLine="If ph.SdkVersion > 19 Then";
if (mostCurrent._ph.getSdkVersion()>19) { 
 //BA.debugLineNum = 33;BA.debugLine="Banner.Initialize(\"Banner\",\"ca-app-pub-417334857";
mostCurrent._banner.Initialize(mostCurrent.activityBA,"Banner","ca-app-pub-4173348573252986/4536076553");
 //BA.debugLineNum = 34;BA.debugLine="Banner.LoadAd";
mostCurrent._banner.LoadAd();
 //BA.debugLineNum = 35;BA.debugLine="Activity.AddView(Banner,0%x,80%y,100%x,50dip)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._banner.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (80),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 37;BA.debugLine="Interstitial.Initialize(\"Interstitial\",\"ca-app-p";
mostCurrent._interstitial.Initialize(mostCurrent.activityBA,"Interstitial","ca-app-pub-4173348573252986/4396475759");
 //BA.debugLineNum = 38;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 //BA.debugLineNum = 40;BA.debugLine="ad1.Initialize(\"ad1\",100)";
_ad1.Initialize(processBA,"ad1",(long) (100));
 //BA.debugLineNum = 41;BA.debugLine="ad1.Enabled = False";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 42;BA.debugLine="ad2.Initialize(\"ad2\",30000)";
_ad2.Initialize(processBA,"ad2",(long) (30000));
 //BA.debugLineNum = 43;BA.debugLine="ad2.Enabled = True";
_ad2.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 46;BA.debugLine="Select ph.SdkVersion";
switch (BA.switchObjectToInt(mostCurrent._ph.getSdkVersion(),(int) (2),(int) (3),(int) (4),(int) (5),(int) (6),(int) (7),(int) (8),(int) (9),(int) (10),(int) (11),(int) (12),(int) (13),(int) (14),(int) (15),(int) (16),(int) (17),(int) (18),(int) (19),(int) (21),(int) (22),(int) (23),(int) (24),(int) (25))) {
case 0: {
 //BA.debugLineNum = 47;BA.debugLine="Case 2 : OS = \"1.1\"";
mostCurrent._os = "1.1";
 break; }
case 1: {
 //BA.debugLineNum = 48;BA.debugLine="Case 3 : OS = \"1.5\"";
mostCurrent._os = "1.5";
 break; }
case 2: {
 //BA.debugLineNum = 49;BA.debugLine="Case 4 : OS = \"1.6\"";
mostCurrent._os = "1.6";
 break; }
case 3: {
 //BA.debugLineNum = 50;BA.debugLine="Case 5 : OS = \"2.0\"";
mostCurrent._os = "2.0";
 break; }
case 4: {
 //BA.debugLineNum = 51;BA.debugLine="Case 6 : OS = \"2.0.1\"";
mostCurrent._os = "2.0.1";
 break; }
case 5: {
 //BA.debugLineNum = 52;BA.debugLine="Case 7 : OS = \"2.1\"";
mostCurrent._os = "2.1";
 break; }
case 6: {
 //BA.debugLineNum = 53;BA.debugLine="Case 8 : OS = \"2.2\"";
mostCurrent._os = "2.2";
 break; }
case 7: {
 //BA.debugLineNum = 54;BA.debugLine="Case 9 : OS = \"2.3 - 2.3.2\"";
mostCurrent._os = "2.3 - 2.3.2";
 break; }
case 8: {
 //BA.debugLineNum = 55;BA.debugLine="Case 10 : OS = \"	2.3.3 - 2.3.7\" ' 2.3.3 or 2.3.";
mostCurrent._os = "	2.3.3 - 2.3.7";
 break; }
case 9: {
 //BA.debugLineNum = 56;BA.debugLine="Case 11 : OS = \"3.0\"";
mostCurrent._os = "3.0";
 break; }
case 10: {
 //BA.debugLineNum = 57;BA.debugLine="Case 12 : OS = \"3.1\"";
mostCurrent._os = "3.1";
 break; }
case 11: {
 //BA.debugLineNum = 58;BA.debugLine="Case 13 : OS = \"3.2\"";
mostCurrent._os = "3.2";
 break; }
case 12: {
 //BA.debugLineNum = 59;BA.debugLine="Case 14 : OS = \"	4.0.1 - 4.0.2\"";
mostCurrent._os = "	4.0.1 - 4.0.2";
 break; }
case 13: {
 //BA.debugLineNum = 60;BA.debugLine="Case 15 : OS = \"4.0.3 - 4.0.4\"";
mostCurrent._os = "4.0.3 - 4.0.4";
 break; }
case 14: {
 //BA.debugLineNum = 61;BA.debugLine="Case 16 : OS = \"	4.1.x\"";
mostCurrent._os = "	4.1.x";
 break; }
case 15: {
 //BA.debugLineNum = 62;BA.debugLine="Case 17 : OS = \"	4.2.x\"";
mostCurrent._os = "	4.2.x";
 break; }
case 16: {
 //BA.debugLineNum = 63;BA.debugLine="Case 18 : OS = 	\"4.3.x\"";
mostCurrent._os = "4.3.x";
 break; }
case 17: {
 //BA.debugLineNum = 64;BA.debugLine="Case 19 : OS = \"	4.4 - 4.4.4\"";
mostCurrent._os = "	4.4 - 4.4.4";
 break; }
case 18: {
 //BA.debugLineNum = 65;BA.debugLine="Case 21: OS = \"5.0\"";
mostCurrent._os = "5.0";
 break; }
case 19: {
 //BA.debugLineNum = 66;BA.debugLine="Case 22: OS = \"5.1\"";
mostCurrent._os = "5.1";
 break; }
case 20: {
 //BA.debugLineNum = 67;BA.debugLine="Case 23: OS = \"6.0\"";
mostCurrent._os = "6.0";
 break; }
case 21: {
 //BA.debugLineNum = 68;BA.debugLine="Case 24 : OS = \"	7.0\"";
mostCurrent._os = "	7.0";
 break; }
case 22: {
 //BA.debugLineNum = 69;BA.debugLine="Case 25 : OS = \"	7.1\"";
mostCurrent._os = "	7.1";
 break; }
default: {
 //BA.debugLineNum = 70;BA.debugLine="Case Else : OS = \"?\"";
mostCurrent._os = "?";
 break; }
}
;
 //BA.debugLineNum = 73;BA.debugLine="Activity.Color = Colors.White";
mostCurrent._activity.setColor(anywheresoftware.b4a.keywords.Common.Colors.White);
 //BA.debugLineNum = 74;BA.debugLine="ph.SetScreenOrientation(1)";
mostCurrent._ph.SetScreenOrientation(processBA,(int) (1));
 //BA.debugLineNum = 76;BA.debugLine="imv.Initialize(\"imv\")";
mostCurrent._imv.Initialize(mostCurrent.activityBA,"imv");
 //BA.debugLineNum = 77;BA.debugLine="imv.Bitmap = LoadBitmap(File.DirAssets,\"icon.png\")";
mostCurrent._imv.setBitmap((android.graphics.Bitmap)(anywheresoftware.b4a.keywords.Common.LoadBitmap(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"icon.png").getObject()));
 //BA.debugLineNum = 78;BA.debugLine="imv.Gravity = Gravity.FILL";
mostCurrent._imv.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.FILL);
 //BA.debugLineNum = 79;BA.debugLine="Activity.AddView(imv,50%x - 50dip  , 10dip ,  110d";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._imv.getObject()),(int) (anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (50),mostCurrent.activityBA)-anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50))),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (10)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (110)),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (110)));
 //BA.debugLineNum = 81;BA.debugLine="b1.Initialize(\"b1\")";
mostCurrent._b1.Initialize(mostCurrent.activityBA,"b1");
 //BA.debugLineNum = 82;BA.debugLine="Dim b1bg As ColorDrawable";
_b1bg = new anywheresoftware.b4a.objects.drawable.ColorDrawable();
 //BA.debugLineNum = 83;BA.debugLine="b1bg.Initialize(Colors.Black,10)";
_b1bg.Initialize(anywheresoftware.b4a.keywords.Common.Colors.Black,(int) (10));
 //BA.debugLineNum = 84;BA.debugLine="b1.Text = \"Install\"";
mostCurrent._b1.setText((Object)("Install"));
 //BA.debugLineNum = 85;BA.debugLine="b1.Background = b1bg";
mostCurrent._b1.setBackground((android.graphics.drawable.Drawable)(_b1bg.getObject()));
 //BA.debugLineNum = 86;BA.debugLine="Activity.AddView(b1,20%x,(imv.Top+imv.Height)+3%y,";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._b1.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (20),mostCurrent.activityBA),(int) ((mostCurrent._imv.getTop()+mostCurrent._imv.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (3),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (60),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.DipToCurrent((int) (50)));
 //BA.debugLineNum = 88;BA.debugLine="Dim l As Label";
_l = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 89;BA.debugLine="l.Initialize(\"l\")";
_l.Initialize(mostCurrent.activityBA,"l");
 //BA.debugLineNum = 90;BA.debugLine="l.Text = \"Credit to < KingRoot Developer Team />\"";
_l.setText((Object)("Credit to < KingRoot Developer Team />"));
 //BA.debugLineNum = 91;BA.debugLine="l.TextColor = Colors.Red";
_l.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Red);
 //BA.debugLineNum = 92;BA.debugLine="Activity.AddView(l,0%x,(b1.Height+b1.Top)+5%y,100%";
mostCurrent._activity.AddView((android.view.View)(_l.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) ((mostCurrent._b1.getHeight()+mostCurrent._b1.getTop())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 93;BA.debugLine="l.Gravity = Gravity.CENTER";
_l.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 95;BA.debugLine="ab.Initialize(\"\")";
mostCurrent._ab.Initialize(mostCurrent.activityBA,"");
 //BA.debugLineNum = 96;BA.debugLine="ab.Text =\"Brand Name : \" & ph.Manufacturer & CRLF";
mostCurrent._ab.setText((Object)("Brand Name : "+mostCurrent._ph.getManufacturer()+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF+"Device Name : "+mostCurrent._ph.getModel()+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF+"Android Version : "+mostCurrent._os+anywheresoftware.b4a.keywords.Common.CRLF+anywheresoftware.b4a.keywords.Common.CRLF+"Produck Name : "+mostCurrent._ph.getProduct()));
 //BA.debugLineNum = 97;BA.debugLine="ab.TextColor = Colors.RGB(0,121,107)";
mostCurrent._ab.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.RGB((int) (0),(int) (121),(int) (107)));
 //BA.debugLineNum = 98;BA.debugLine="ab.Gravity = Gravity.CENTER";
mostCurrent._ab.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 99;BA.debugLine="Activity.AddView(ab,0%x,(l.Top+l.Height)+1%y,100%x";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._ab.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),(int) ((_l.getTop()+_l.getHeight())+anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (1),mostCurrent.activityBA)),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (30),mostCurrent.activityBA));
 //BA.debugLineNum = 101;BA.debugLine="lfoot.Initialize(\"ft\")";
mostCurrent._lfoot.Initialize(mostCurrent.activityBA,"ft");
 //BA.debugLineNum = 102;BA.debugLine="lfoot.Text = \"Developed By Khun Htetz Naing\"";
mostCurrent._lfoot.setText((Object)("Developed By Khun Htetz Naing"));
 //BA.debugLineNum = 103;BA.debugLine="lfoot.TextColor = Colors.Magenta";
mostCurrent._lfoot.setTextColor(anywheresoftware.b4a.keywords.Common.Colors.Magenta);
 //BA.debugLineNum = 104;BA.debugLine="lfoot.Gravity = Gravity.CENTER";
mostCurrent._lfoot.setGravity(anywheresoftware.b4a.keywords.Common.Gravity.CENTER);
 //BA.debugLineNum = 105;BA.debugLine="Activity.AddView(lfoot,0%x,95%y,100%x,5%y)";
mostCurrent._activity.AddView((android.view.View)(mostCurrent._lfoot.getObject()),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (0),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (95),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerXToCurrent((float) (100),mostCurrent.activityBA),anywheresoftware.b4a.keywords.Common.PerYToCurrent((float) (5),mostCurrent.activityBA));
 //BA.debugLineNum = 106;BA.debugLine="End Sub";
return "";
}
public static String  _activity_pause(boolean _userclosed) throws Exception{
 //BA.debugLineNum = 167;BA.debugLine="Sub Activity_Pause (UserClosed As Boolean)";
 //BA.debugLineNum = 169;BA.debugLine="End Sub";
return "";
}
public static String  _activity_resume() throws Exception{
 //BA.debugLineNum = 136;BA.debugLine="Sub Activity_Resume";
 //BA.debugLineNum = 137;BA.debugLine="If File.Exists(File.DirRootExternal,\"KingRoot.apk\"";
if (anywheresoftware.b4a.keywords.Common.File.Exists(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"KingRoot.apk")==anywheresoftware.b4a.keywords.Common.True) { 
 //BA.debugLineNum = 138;BA.debugLine="File.Delete(File.DirRootExternal,\"KingRoot.apk\")";
anywheresoftware.b4a.keywords.Common.File.Delete(anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"KingRoot.apk");
 };
 //BA.debugLineNum = 140;BA.debugLine="End Sub";
return "";
}
public static String  _ad1_tick() throws Exception{
 //BA.debugLineNum = 142;BA.debugLine="Sub ad1_Tick";
 //BA.debugLineNum = 143;BA.debugLine="If ph.SdkVersion > 19 Then";
if (mostCurrent._ph.getSdkVersion()>19) { 
 //BA.debugLineNum = 144;BA.debugLine="If Interstitial.Status = Interstitial.Status_AdRe";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_AdReadyToShow) { 
 //BA.debugLineNum = 145;BA.debugLine="Interstitial.Show";
mostCurrent._interstitial.Show(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 148;BA.debugLine="If Interstitial.Status = Interstitial.Status_Dism";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_Dismissed) { 
 //BA.debugLineNum = 149;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 };
 };
 //BA.debugLineNum = 152;BA.debugLine="ad1.Enabled = False";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.False);
 //BA.debugLineNum = 153;BA.debugLine="End Sub";
return "";
}
public static String  _ad2_tick() throws Exception{
 //BA.debugLineNum = 155;BA.debugLine="Sub ad2_Tick";
 //BA.debugLineNum = 156;BA.debugLine="If ph.SdkVersion > 19 Then";
if (mostCurrent._ph.getSdkVersion()>19) { 
 //BA.debugLineNum = 157;BA.debugLine="If Interstitial.Status = Interstitial.Status_AdRe";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_AdReadyToShow) { 
 //BA.debugLineNum = 158;BA.debugLine="Interstitial.Show";
mostCurrent._interstitial.Show(mostCurrent.activityBA);
 };
 //BA.debugLineNum = 161;BA.debugLine="If Interstitial.Status = Interstitial.Status_Dism";
if (mostCurrent._interstitial.Status==mostCurrent._interstitial.Status_Dismissed) { 
 //BA.debugLineNum = 162;BA.debugLine="Interstitial.LoadAd";
mostCurrent._interstitial.LoadAd(mostCurrent.activityBA);
 };
 };
 //BA.debugLineNum = 165;BA.debugLine="End Sub";
return "";
}
public static String  _b1_click() throws Exception{
anywheresoftware.b4a.objects.IntentWrapper _i = null;
 //BA.debugLineNum = 125;BA.debugLine="Sub b1_Click";
 //BA.debugLineNum = 126;BA.debugLine="If ph.SdkVersion > 19 Then";
if (mostCurrent._ph.getSdkVersion()>19) { 
 //BA.debugLineNum = 127;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 129;BA.debugLine="File.Copy(File.DirAssets,\"KingRoot.apk\", File.Dir";
anywheresoftware.b4a.keywords.Common.File.Copy(anywheresoftware.b4a.keywords.Common.File.getDirAssets(),"KingRoot.apk",anywheresoftware.b4a.keywords.Common.File.getDirRootExternal(),"KingRoot.apk");
 //BA.debugLineNum = 130;BA.debugLine="Dim i As Intent";
_i = new anywheresoftware.b4a.objects.IntentWrapper();
 //BA.debugLineNum = 131;BA.debugLine="i.Initialize(i.ACTION_VIEW,\"file:///\"&File.Dir";
_i.Initialize(_i.ACTION_VIEW,"file:///"+anywheresoftware.b4a.keywords.Common.File.getDirRootExternal()+"/KingRoot.apk");
 //BA.debugLineNum = 132;BA.debugLine="i.SetType(\"application/vnd.android.package-archiv";
_i.SetType("application/vnd.android.package-archive");
 //BA.debugLineNum = 133;BA.debugLine="StartActivity(i)";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.getObject()));
 //BA.debugLineNum = 134;BA.debugLine="End Sub";
return "";
}
public static String  _ft_click() throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneIntents _aa = null;
 //BA.debugLineNum = 112;BA.debugLine="Sub ft_Click";
 //BA.debugLineNum = 113;BA.debugLine="Dim aa As PhoneIntents";
_aa = new anywheresoftware.b4a.phone.Phone.PhoneIntents();
 //BA.debugLineNum = 114;BA.debugLine="StartActivity(aa.OpenBrowser(\"https://www.faceboo";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_aa.OpenBrowser("https://www.facebook.com/Khun.Htetz.Naing")));
 //BA.debugLineNum = 115;BA.debugLine="End Sub";
return "";
}
public static String  _globals() throws Exception{
 //BA.debugLineNum = 20;BA.debugLine="Sub Globals";
 //BA.debugLineNum = 21;BA.debugLine="Dim imv As ImageView";
mostCurrent._imv = new anywheresoftware.b4a.objects.ImageViewWrapper();
 //BA.debugLineNum = 22;BA.debugLine="Dim ph As Phone";
mostCurrent._ph = new anywheresoftware.b4a.phone.Phone();
 //BA.debugLineNum = 23;BA.debugLine="Dim b1 As Button";
mostCurrent._b1 = new anywheresoftware.b4a.objects.ButtonWrapper();
 //BA.debugLineNum = 24;BA.debugLine="Dim lfoot As Label";
mostCurrent._lfoot = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 25;BA.debugLine="Dim Banner As AdView";
mostCurrent._banner = new anywheresoftware.b4a.admobwrapper.AdViewWrapper();
 //BA.debugLineNum = 26;BA.debugLine="Dim Interstitial As mwAdmobInterstitial";
mostCurrent._interstitial = new mobi.mindware.admob.interstitial.AdmobInterstitialsAds();
 //BA.debugLineNum = 27;BA.debugLine="Dim ab As Label";
mostCurrent._ab = new anywheresoftware.b4a.objects.LabelWrapper();
 //BA.debugLineNum = 28;BA.debugLine="Dim OS As String";
mostCurrent._os = "";
 //BA.debugLineNum = 29;BA.debugLine="End Sub";
return "";
}
public static String  _imv_click() throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneIntents _i = null;
 //BA.debugLineNum = 117;BA.debugLine="Sub imv_Click";
 //BA.debugLineNum = 118;BA.debugLine="If ph.SdkVersion > 19 Then";
if (mostCurrent._ph.getSdkVersion()>19) { 
 //BA.debugLineNum = 119;BA.debugLine="ad1.Enabled = True";
_ad1.setEnabled(anywheresoftware.b4a.keywords.Common.True);
 };
 //BA.debugLineNum = 121;BA.debugLine="Dim i As PhoneIntents";
_i = new anywheresoftware.b4a.phone.Phone.PhoneIntents();
 //BA.debugLineNum = 122;BA.debugLine="StartActivity(i.OpenBrowser(\"https://play.google.";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_i.OpenBrowser("https://play.google.com/store/apps/details?id=com.htetznaing.kingroot")));
 //BA.debugLineNum = 123;BA.debugLine="End Sub";
return "";
}
public static String  _l_click() throws Exception{
anywheresoftware.b4a.phone.Phone.PhoneIntents _ngoma = null;
 //BA.debugLineNum = 108;BA.debugLine="Sub l_Click";
 //BA.debugLineNum = 109;BA.debugLine="Dim ngoma As PhoneIntents";
_ngoma = new anywheresoftware.b4a.phone.Phone.PhoneIntents();
 //BA.debugLineNum = 110;BA.debugLine="StartActivity(ngoma.OpenBrowser(\"http://kingroot.";
anywheresoftware.b4a.keywords.Common.StartActivity(mostCurrent.activityBA,(Object)(_ngoma.OpenBrowser("http://kingroot.net/")));
 //BA.debugLineNum = 111;BA.debugLine="End Sub";
return "";
}

public static void initializeProcessGlobals() {
    
    if (main.processGlobalsRun == false) {
	    main.processGlobalsRun = true;
		try {
		        main._process_globals();
starter._process_globals();
		
        } catch (Exception e) {
			throw new RuntimeException(e);
		}
    }
}public static String  _process_globals() throws Exception{
 //BA.debugLineNum = 16;BA.debugLine="Sub Process_Globals";
 //BA.debugLineNum = 17;BA.debugLine="Dim ad1,ad2 As Timer";
_ad1 = new anywheresoftware.b4a.objects.Timer();
_ad2 = new anywheresoftware.b4a.objects.Timer();
 //BA.debugLineNum = 18;BA.debugLine="End Sub";
return "";
}
}
