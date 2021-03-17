package com.example.myapplication;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.documentfile.provider.DocumentFile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.UriPermission;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.view.View;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

  static final int REQUEST_CODE = 1;

  Uri uri;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    findViewById(R.id.floatingActionButton).setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        openTree();
      }
    });

    String u =PreferenceManager.getDefaultSharedPreferences(this).getString("uri", "");
    if (!u.isEmpty()) {
      uri = Uri.parse(u);
      updateListing();
    }
  }


  void openTree() {
    Intent intent= new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    intent.addFlags(
            Intent.FLAG_GRANT_READ_URI_PERMISSION
            | Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            | Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION
            | Intent.FLAG_GRANT_PREFIX_URI_PERMISSION);
    startActivityForResult(intent, REQUEST_CODE);
  }


  @Override
  protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    if (data!=null && requestCode==REQUEST_CODE) {
      uri = data.getData();
      if (uri!=null) {
        int takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION;
        getContentResolver().takePersistableUriPermission(uri, takeFlags);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(this).edit();
        editor.putString("uri", uri.toString());
        editor.commit();
      }
      updateListing();
      return;
    }
    super.onActivityResult(requestCode, resultCode, data);
  }

  void updateListing() {
    DocumentFile file = DocumentFile.fromTreeUri(this, uri);
    DocumentFile[] files = file.listFiles();
    TextView view = findViewById(R.id.text);
    view.setText("files "+ Arrays.asList(files)+
                  "\n\n"+permissions() );

  }

  private List<UriPermission> permissions() {
    return getContentResolver().getPersistedUriPermissions();
  }

  private List<ResolveInfo> documentsProviders() {
    final Intent intent = new Intent(DocumentsContract.PROVIDER_INTERFACE);
    return getPackageManager().queryIntentContentProviders(intent, 0);
  }


}