package com.zoco.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.zoco.common.ReqTask;
import com.zoco.common.ZocoConstants;
import com.zoco.common.ZocoHandler;
import com.zoco.common.ZocoNetwork;
import com.zoco.obj.Book;
import com.zoco.obj.BookInfos;
import com.zoco.obj.User;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;


public class MainActivity extends ActionBarActivity implements
        NavigationDrawerFragment.NavigationDrawerCallbacks, SearchView.OnQueryTextListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the
     * navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in
     * {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    private SearchView mSearchView;

    public static final String BOOK_INFO = "BOOK_INFO";

    BookInfos infos = new BookInfos();
    BookListAdapter bookListAdapter;
    ListView bookListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment) getSupportFragmentManager()
                .findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        //제목 저자 가격

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        //login
        //login();

        //make dir for image;
        mkImageDir();

        //set for listview
        bookListAdapter = new BookListAdapter(getBaseContext(), infos);
        bookListView = (ListView) findViewById(R.id.book_list);
        bookListView.setAdapter(bookListAdapter);

    }

 

    private void mkImageDir() {
        File file = new File(ZocoConstants.ZOCO_IMAGE_DIR);
        if (!file.exists()) {
            file.mkdir();
        }
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager
                .beginTransaction()
                .replace(R.id.container,
                        PlaceholderFragment.newInstance(position + 1)).commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    /*
     * 네비게이션 바가 열리거나 닫혔을때마다 콜백된다.
     * 물론 oncreate가 불린후에도 콜백된다.
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        //navibar가 열리지 않았을때 해당 모드로 초기화 시킨다.
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            MenuInflater inflater = getMenuInflater();
            inflater.inflate(R.menu.main, menu);
            MenuItem searchItem = menu.findItem(R.id.action_search);
            mSearchView = (SearchView) searchItem.getActionView();
            setupSearchView(searchItem);
            //restoreActionBar();
            return super.onCreateOptionsMenu(menu);
        }

        return true;


    }

    private void setupSearchView(MenuItem searchItem) {

        if (isAlwaysExpanded()) {
            mSearchView.setIconifiedByDefault(false);
        } else {
            searchItem.setShowAsActionFlags(MenuItem.SHOW_AS_ACTION_IF_ROOM
                    | MenuItem.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW);
        }

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if (searchManager != null) {
            List<SearchableInfo> searchables = searchManager.getSearchablesInGlobalSearch();

            SearchableInfo info = searchManager.getSearchableInfo(getComponentName());
            for (SearchableInfo inf : searchables) {
                if (inf.getSuggestAuthority() != null
                        && inf.getSuggestAuthority().startsWith("applications")) {
                    info = inf;
                }
            }
            //mSearchView.setSearchableInfo(info);
        }

        mSearchView.setOnQueryTextListener(this);
    }

    public boolean onQueryTextChange(String newText) {
        //mStatusView.setText("Query = " + newText);
        //Toast.makeText(getBaseContext(),newText,Toast.LENGTH_LONG).show();
        return false;
    }

    //query짜서 보낸 데이터넣는 과정
    public boolean onQueryTextSubmit(String query) {
        //url바꿔야함
        String url = null;
        try {
            url = ZocoNetwork.URL_4_QUERY_BOOK + URLEncoder.encode(query, "UTF-8") + "&offset=" + URLEncoder.encode("0", "UTF-8");
            String encodedurl = null;

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        Handler handler = new ZocoHandler() {
            @Override
            public void onReceive(String result) {
                //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
                BookInfos searchedInfos=new Gson().fromJson(result, BookInfos.class);
                infos.clear();
                infos.addAll(searchedInfos);
                bookListAdapter.notifyDataSetChanged();
            }
        };

        new ReqTask(getBaseContext(), ZocoNetwork.Method.GET).setHandler(handler).execute(url);
        return false;
    }

    public boolean onClose() {
        //mStatusView.setText("Closed!");
        return false;
    }

    protected boolean isAlwaysExpanded() {
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("logout", "setLogOut");
            startActivity(intent);
            finish();
            return true;
        } else if (id == R.id.register) {
            Toast.makeText(getBaseContext(), "register", Toast.LENGTH_LONG).show();
            startActivityForResult(new Intent("com.google.zxing.client.android.SCAN"), 0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int requestResult, Intent intent) {
        // TODO Auto-generated method stub
        if (requestCode == 0) {
            if (requestResult == RESULT_OK) {
                String contents = intent.getStringExtra("SCAN_RESULT");
//				Toast.makeText(getBaseContext(), "result : " + contents, Toast.LENGTH_LONG).show();
                BookScanTask worker = new BookScanTask();
                worker.execute(contents);

            }
        }
    }

    //처음 url, progress 값,
    class BookScanTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            // TODO Auto-generated method stub
            String isbn = params[0];
            String result = null;
            String requestURL = ZocoConstants.NAVER_SEARCH_URL + isbn;
            Log.e("KDH url", requestURL);
            try {
                result = new ZocoNetwork().setGetOption(requestURL).execute();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                throw new IllegalAccessError(e.getMessage());
            }
            //Toast.makeText(getBaseContext(), result, Toast.LENGTH_LONG).show();
            return result;
        }

        @Override
        protected void onPostExecute(String result) {
            // TODO Auto-generated method stub
            Serializer serializer = new Persister();
            try {
                Book book = serializer.read(Book.class, result);
                Intent intent = new Intent(getBaseContext(), BookRegisterActivity.class);
                intent.putExtra(BOOK_INFO, book.getBookContent());
                startActivity(intent);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "cannot get book info", Toast.LENGTH_LONG).show();
            }
            super.onPostExecute(result);
        }

    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container,
                    false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(getArguments().getInt(
                    ARG_SECTION_NUMBER));
        }
    }

}
