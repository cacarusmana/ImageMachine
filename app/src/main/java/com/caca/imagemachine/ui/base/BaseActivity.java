package com.caca.imagemachine.ui.base;

import androidx.appcompat.app.AppCompatActivity;

/**
 * @author caca rusmana on 21/03/22
 */
public abstract class BaseActivity extends AppCompatActivity {


    protected abstract void initComponent();
    protected abstract void initListener();
    protected abstract void initObserver();


}
