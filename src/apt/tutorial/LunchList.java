package apt.tutorial;

import com.example.android.supportv4.R;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.AdapterView;

public class LunchList extends FragmentActivity {
	List<Restaurant> model =new ArrayList<Restaurant>();
	ArrayAdapter<Restaurant> adapter=null;
	EditText name=null;
	EditText address=null;
	RadioGroup types=null;
	TabHost mTabHost=null;
	TabManager mTabManager=null;
	
	//private TabHost mTabHost;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.FragmentActivity#onCreate(android.os.Bundle)
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_lunch_list);
		name=(EditText)findViewById(R.id.name);
		address=(EditText)findViewById(R.id.addr);
		types=(RadioGroup)findViewById(R.id.types);
		Button save=(Button)findViewById(R.id.save);
		save.setOnClickListener(onSave);
		ListView list=(ListView)findViewById(R.id.restaurants);
		adapter=new RestaurantAdapter();
        list.setAdapter(adapter);
        
        mTabHost=(TabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup();
        
        mTabManager = new TabManager(this, mTabHost,android.R.id.tabcontent);
        
        mTabManager.addTab(mTabHost.newTabSpec("tag1").setIndicator("List",
        		FragmentStackSupport.CountingFragment.class,
        		getResources().getDrawable(R.drawable.list)),null);
        mTabManager.addTab(mTabHost.newTabSpec("tag2").setIndicator("Details",
        		
        		getResources().getDrawable(R.drawable.restaurant)),null);
        
        if (savedInstanceState != null){
        	mTabHost.setCurrentTabByTag(savedInstanceState.getString("tab"));
        }

        /*
        mTabHost.setup();       
        TabSpec spec=mTabHost.newTabSpec("tag1");
        spec.setContent(R.id.restaurants);
        spec.setIndicator("List",getResources().getDrawable(R.drawable.list));       
        mTabHost.addTab(spec);    
        spec=mTabHost.newTabSpec("tag2");
        spec.setContent(R.id.details);
        spec.setIndicator("Details",getResources().getDrawable(R.drawable.restaurant));    
        mTabHost.addTab(spec);
        mTabHost.setCurrentTab(0);*/
        list.setOnItemClickListener(onListClick);   
    }
	
	@Override
	protected void onSaveInstanceState(Bundle outState){
		super.onSaveInstanceState(outState);
		outState.putString("tab",mTabHost.getCurrentTabTag());
	}
	
    private View.OnClickListener onSave=new View.OnClickListener()
    {
    	public void onClick(View v){
    		Restaurant r=new Restaurant();
		
    		r.setName(name.getText().toString());
    		r.setAddress(address.getText().toString());
    		
    		RadioGroup types=(RadioGroup)findViewById(R.id.types);
    		switch (types.getCheckedRadioButtonId()){
    		case R.id.sit_down:
    			r.setType("sit_down");
    		break;
    		case R.id.take_out:
    			r.setType("take_out");
    		break;
    		case R.id.delivery:
    			r.setType("delivery");
    			break;
    		}
    		adapter.add(r);
    	}
    };
    
    class RestaurantAdapter extends ArrayAdapter<Restaurant>{

    	RestaurantAdapter(){
    		super(LunchList.this,android.R.layout.simple_list_item_1,model);
    		
    	}
    	public View getView(int position,View convertView,ViewGroup parent)
    	{
    		View row=convertView;
    		RestaurantHolder holder=null;
    		
    		if (row==null){
    			LayoutInflater inflater=getLayoutInflater();
    			row=inflater.inflate(R.layout.row,parent, false);
    			holder=new RestaurantHolder(row);
    			row.setTag(holder);
    		}
    		else{
    			holder=(RestaurantHolder)row.getTag();
    		}
    		
    		holder.populateFrom(model.get(position));
    		
    		return(row);
    	}
    }
    
    static class RestaurantHolder{
    	private TextView name=null;
    	private TextView address=null;
    	private ImageView icon=null;
    	
    	RestaurantHolder(View row){
    		name=(TextView)row.findViewById(R.id.title);
    		address=(TextView)row.findViewById(R.id.address);
    		icon=(ImageView)row.findViewById(R.id.icon);
    	}
    	
    	void populateFrom(Restaurant r){
    		name.setText(r.getName());
    		address.setText(r.getAddress());
    		if (r.getType().equals("sit_down")){
    			icon.setImageResource(R.drawable.ball_red);
    		}
    		else if (r.getType().equals("take_out")){
    			icon.setImageResource(R.drawable.ball_yellow);
    		}
    		else {
    			icon.setImageResource(R.drawable.ball_green);
    		}
    	}
    }
    
	private AdapterView.OnItemClickListener onListClick=new
		AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent,View view, int position,long id) {		
				Restaurant r=model.get(position);
				name.setText(r.getName());
				address.setText(r.getAddress());
				if (r.getType().equals("sit_down")) {
				types.check(R.id.sit_down);
				}
				else if (r.getType().equals("take_out")) {
				types.check(R.id.take_out);
				}
				else {
				types.check(R.id.delivery);
				}
			}
	};	

	public static class TabManager implements TabHost.OnTabChangeListener {
	       
			private final FragmentActivity mActivity;
	        private final TabHost mTabHost;
	        private final int mContainerId;
	        private final HashMap<String, TabInfo> mTabs = new HashMap<String, TabInfo>();
	        TabInfo mLastTab;

	        static final class TabInfo {
	            private final String tag;
	            private final Class<?> clss;
	            private final Bundle args;
	            private Fragment fragment;

	            TabInfo(String _tag, Class<?> _class, Bundle _args) {
	                tag = _tag;
	                clss = _class;
	                args = _args;
	            }
	        }

	        static class DummyTabFactory implements TabHost.TabContentFactory {
	            private final Context mContext;

	            public DummyTabFactory(Context context) {
	                mContext = context;
	            }

	            public View createTabContent(String tag) {
	                View v = new View(mContext);
	                v.setMinimumWidth(0);
	                v.setMinimumHeight(0);
	                return v;
	            }
	        }

	        public TabManager(FragmentActivity activity, TabHost tabHost, int containerId) {
	            mActivity = activity;
	            mTabHost = tabHost;
	            mContainerId = containerId;
	            mTabHost.setOnTabChangedListener(this);
	        }

	        public void addTab(TabHost.TabSpec tabSpec, Class<?> clss, Bundle args) {
	            tabSpec.setContent(new DummyTabFactory(mActivity));
	            String tag = tabSpec.getTag();

	            TabInfo info = new TabInfo(tag,clss, args);
	           
	            // Check to see if we already have a fragment for this tab, probably
	            // from a previously saved state.  If so, deactivate it, because our
	            // initial state is that a tab isn't shown.
	            info.fragment = mActivity.getSupportFragmentManager().findFragmentByTag(tag);
	            if (info.fragment != null && !info.fragment.isDetached()) {
	                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
	                ft.detach(info.fragment);
	                ft.commit();
	            }

	            mTabs.put(tag, info);
	            mTabHost.addTab(tabSpec);
	        }

	        public void onTabChanged(String tabId) {
	            TabInfo newTab = mTabs.get(tabId);
	            if (mLastTab != newTab) {
	                FragmentTransaction ft = mActivity.getSupportFragmentManager().beginTransaction();
	                if (mLastTab != null) {
	                    if (mLastTab.fragment != null) {
	                        ft.detach(mLastTab.fragment);
	                    }
	                }
	                if (newTab != null) {
	                	if (newTab.fragment == null) {
	                        newTab.fragment = Fragment.instantiate(mActivity,
	                                newTab.clss.getName(), newTab.args);
	                        ft.add(mContainerId, newTab.fragment, newTab.tag);
	                    } else {
	                        ft.attach(newTab.fragment);
	                    }
	                }

	                mLastTab = newTab;
	                ft.commit();
	                mActivity.getSupportFragmentManager().executePendingTransactions();
	            }
	        }
	    }
	
}
