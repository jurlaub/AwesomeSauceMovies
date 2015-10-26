package com.example.android.awesomesaucemovies;

import android.support.v4.app.Fragment;


public class MainActivity extends SingleFragmentActivity {

    private final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected Fragment createFragment(){
        return new MovieFragment();
    }



    @Override
    protected int getLayoutResID(){
//        return R.layout.activity_twopane;
        return R.layout.activity_masterdetail;
    }

//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        //setContentView(R.layout.activity_main);
//
//        FragmentManager fm = getFragmentManager();
//        Fragment fragment = fm.findFragmentById(R.id.fragmentContainer);
//
//        if (fragment == null){
//            fragment = new MovieFragment();
//            fm.beginTransaction()
//                    .add(R.id.fragmentContainer, fragment).commit();
//        }
//    }

//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            Log.i(LOG_TAG, "Settings");
//
//            startActivity(new Intent(this, SettingsActivity.class));
//
//            return true;
//        } else if (id == R.id.action_about_toast) {
//            Log.i(LOG_TAG, "About");
//
//            int duration = Toast.LENGTH_LONG;
//            Toast.makeText(getApplicationContext(), R.string.TMDb_notice, duration).show();
//
//        }
//
//        return super.onOptionsItemSelected(item);
//    }






}
