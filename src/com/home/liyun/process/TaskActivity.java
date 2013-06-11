package com.home.liyun.process;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;

public class TaskActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_task);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_task, menu);
		return true;
	}

}

package com.home.liyun;


/**
 * ActivityManager.RunningAppProcessInfo {
 *     public int importance                // 进程在系统中的重要级别
 *     public int importanceReasonCode        // 进程的重要原因代码
 *     public ComponentName importanceReasonComponent    // 进程中组件的描述信息
 *     public int importanceReasonPid        // 当前进程的子进程Id
 *     public int lru                        // 在同一个重要级别内的附加排序值
 *     public int pid                        // 当前进程Id
 *     public String[] pkgList                // 被载入当前进程的所有包名
 *     public String processName            // 当前进程的名称
 *     public int uid                        // 当前进程的用户Id
 * }
 */

//import crazypebble.sysassist.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.MemoryInfo;
import android.app.ListActivity;
import android.os.Bundle;
import android.os.Debug;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.os.Process;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class EnumProcessActivity extends ListActivity {
    
    private static List<RunningAppProcessInfo> procList = null;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.proc_list);
        
        procList = new ArrayList<RunningAppProcessInfo>();
        getProcessInfo();
        
        showProcessInfo();
    }
    
    public void showProcessInfo() {
        HashMap<String, String> memmap = new HashMap<String, String>();
        // 更新进程列表
        final List<HashMap<String,String>> infoList = new ArrayList<HashMap<String,String>>();
        memmap.put("proc_name","avail mem");
        MemoryInfo memInfo = new MemoryInfo();
        ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getMemoryInfo(memInfo);
        memmap.put("proc_id",(double)memInfo.availMem/1024/1024+"MB") ;
        infoList.add(memmap);
        for (Iterator<RunningAppProcessInfo> iterator = procList.iterator(); iterator.hasNext();) {
            RunningAppProcessInfo procInfo = iterator.next();
            HashMap<String, String> map = new HashMap<String, String>();
            map.put("proc_name", procInfo.processName);
            
            
            int[] memPid = new int[]{ procInfo.pid };
            //此MemoryInfo位于android.os.Debug.MemoryInfo包中，用来统计进程的内存信息
            Debug.MemoryInfo[] memoryInfo = ((ActivityManager) getSystemService(ACTIVITY_SERVICE)).getProcessMemoryInfo(memPid);
            //获取进程占内存用信息kb单位
            int memSize = memoryInfo[0].dalvikPrivateDirty;
            map.put("proc_id", "pid:"+procInfo.pid+"\tuid:"+procInfo.uid+"\tmem:"+memSize);
            

            
            infoList.add(map);
        }
        
        
        final SimpleAdapter simpleAdapter = new SimpleAdapter(
                this, 
                infoList, 
                R.layout.proc_list_item, 
                new String[]{"proc_name", "proc_id"},
                new int[]{R.id.proc_name, R.id.proc_id} );
        getListView().setOnItemClickListener(new OnItemClickListener() {
			
			
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				 //Toast.makeText(EnumProcessActivity.this, "你点击的是第" + arg3 + "项",  Toast.LENGTH_SHORT).show();  
				 //arg0.getAdapter().getItem(2).
				//int pid = Integer.parseInt(((TextView)arg1.findViewById(R.id.proc_id)).getText().toString());
				String packname = ((TextView)arg1.findViewById(R.id.proc_name)).getText().toString();
				Toast.makeText(EnumProcessActivity.this, "Killing"+ ((TextView)arg1.findViewById(R.id.proc_id)).getText(), Toast.LENGTH_LONG).show(); 
				//android.os.Process.killProcess(pid);
				ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
				activityManager.killBackgroundProcesses(packname);
				
				int size = infoList.size();
				if( size > 0 )
				{
					infoList.remove(infoList.size() - 1);
					simpleAdapter.notifyDataSetChanged();
				}
				getProcessInfo();
				showProcessInfo();


			}
		});
        setListAdapter(simpleAdapter);
        
    }
    
    public int getProcessInfo() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        procList = activityManager.getRunningAppProcesses();
        return procList.size();
    }
}