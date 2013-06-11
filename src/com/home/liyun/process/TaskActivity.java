package com.home.liyun.process;


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

public class TaskActivity extends ListActivity {
    
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
				Toast.makeText(TaskActivity.this, "Killing"+ ((TextView)arg1.findViewById(R.id.proc_id)).getText(), Toast.LENGTH_LONG).show(); 
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