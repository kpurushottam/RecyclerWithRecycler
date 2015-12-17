package com.krp.android.recyclerwithrecycler;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.krp.android.recyclerwithrecycler.utils.ActionIntentUtil;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private RecyclerView mRecyclerView;
    private AlbumRecyclerAdapter mAlbumAdapter;

    private EditText etCount;
    private Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupToolbar();
        setupRecyclerView();

        etCount = (EditText) findViewById(R.id.et_count);
        btnSubmit = (Button) findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(this);
    }

    private void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
    }

    private void setupRecyclerView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAlbumAdapter = new AlbumRecyclerAdapter();
        mRecyclerView.setAdapter(mAlbumAdapter);
    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_submit) {
            int count = AlbumRecyclerAdapter.DEFAULT_SIZE;
            try {
                count = Integer.parseInt(etCount.getText().toString());
            } catch (NumberFormatException e) {
                count = count * 2;
            }
            mAlbumAdapter.notifyDataSetChanged(count);
            ActionIntentUtil.closeWindowSoftKeyboard(view);
        }
    }
}
