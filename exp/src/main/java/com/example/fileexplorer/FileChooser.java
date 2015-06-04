package com.example.fileexplorer;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.sql.Date;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;




public class FileChooser extends ListActivity {

    private File currentDir;
    private FileArrayAdapter adapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        currentDir = new File(Environment.getExternalStorageDirectory(), "");
        fill(currentDir);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; Add items to menu bar.
        getMenuInflater().inflate(R.menu.activity_fileexplorer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //action bar button to go home
        if (id == R.id.menu_settings) {
            //Want to return to the main activity and finish this activity
            //Intent home = new Intent(this, P2pMain.class);
            //P2pMain p = getIntent().getSerializableExtra("P2pMain").cancel_connection();
            //startActivity(home);
            //this.finish();
            super.onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void fill(File f)
    {
        File[]dirs = f.listFiles();
         this.setTitle("Currently In: "+f.getName());
         List<Item>dir = new ArrayList<Item>();
         List<Item>fls = new ArrayList<Item>();
         try{
             for(File ff: dirs)
             {
                Date lastModDate = new Date(ff.lastModified());
                DateFormat formater = DateFormat.getDateTimeInstance();
                String date_modify = formater.format(lastModDate);
                if(ff.isDirectory()){


                    File[] fbuf = ff.listFiles();
                    int buf = 0;
                    if(fbuf != null){
                        buf = fbuf.length;
                    }
                    else buf = 0;
                    String num_item = String.valueOf(buf);
                    if(buf == 0) num_item = num_item + " item";
                    else num_item = num_item + " items";

                    //String formated = lastModDate.toString();
                    dir.add(new Item(ff.getName(),num_item,date_modify,ff.getAbsolutePath(),"directory_icon"));
                }
                else
                {

                    fls.add(new Item(ff.getName(),ff.length() + " Byte", date_modify, ff.getAbsolutePath(),"file_icon"));
                }
             }
         }catch(Exception e)
         {

         }
         Collections.sort(dir);
         Collections.sort(fls);
         dir.addAll(fls);
         if(!f.getName().equalsIgnoreCase("sdcard"))
             dir.add(0,new Item("Return to Previous Directory","Full Path: "+currentDir,"",f.getParent(),"directory_up"));
         adapter = new FileArrayAdapter(FileChooser.this,R.layout.file_view,dir);
         this.setListAdapter(adapter);
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        // TODO Auto-generated method stub
        super.onListItemClick(l, v, position, id);
        Item o = adapter.getItem(position);
        if(o.getImage().equalsIgnoreCase("directory_icon")||o.getImage().equalsIgnoreCase("directory_up")){
                currentDir = new File(o.getPath());
                fill(currentDir);
        }
        else
        {
            onFileClick(o);
        }
    }

    public String get_Ip() {
        Intent intent = getIntent();
        String ip = intent.getExtras().getString("serverIP");
        return ip;
    }

        private void onFileClick(Item o)
    {
        //Toast.makeText(this, "Folder Clicked: "+ currentDir, Toast.LENGTH_SHORT).show();
        Client client = new Client(this.getApplicationContext());

        String host = get_Ip();
        String path = currentDir + "/" + o.getName();
        Toast.makeText(this, path, Toast.LENGTH_LONG).show();

        Log.d("file path", path );
        Log.d("host", host);

        client.send(host, path);

        //Intent intent = new Intent();
        //intent.putExtra("GetPath",currentDir.toString());
        //intent.putExtra("GetFileName",o.getName());
        //setResult(RESULT_OK, intent);
        //finish();
    }
}
