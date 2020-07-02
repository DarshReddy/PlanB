package com.app.kagada.planb.ui.share;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.kagada.planb.R;

public class ShareFragment extends Fragment {

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_share, container, false);
    try {
      Intent shareIntent = new Intent(Intent.ACTION_SEND);
      shareIntent.setType("text/plain");
      shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Foople");
      String shareMessage= "\nLet me recommend you this application which I like\n\n";
      shareMessage = shareMessage + "https://drive.google.com/open?id=1HatuyH06lMirLOOpUIgpg1cyMci5T97V"+"\n\n";
      shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
      startActivity(Intent.createChooser(shareIntent, "choose one"));
    } catch(Exception e) {
      //e.toString();
    }

    return root;
  }
}