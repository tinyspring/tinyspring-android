package com.tinyspring.android.plugin;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.ViewGroup;

public abstract interface IFragmentPlugin extends IPlugin {
	public abstract void onFragmentCreate(Fragment paramFragment, LayoutInflater paramLayoutInflater, ViewGroup paramViewGroup, Boolean paramBoolean);
}