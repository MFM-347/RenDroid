package dev.mfm.renamer;

import com.google.android.material.color.DynamicColors;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;
import dev.mfm.renamer.databinding.ActivityMainBinding;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
  private ActivityMainBinding binding;
  private static final int REQUEST_CODE_PERMISSION = 100;
  private Uri selectedDirectoryUri;
  @Override
  protected void onCreate(Bundle savedInstanceState) {
    DynamicColors.applyToActivityIfAvailable(this);
    super.onCreate(savedInstanceState);
    binding = ActivityMainBinding.inflate(getLayoutInflater());
    setContentView(binding.getRoot());

    binding.browseButton.setOnClickListener(view -> requestPermissionsIfNecessary());
    binding.renameButton.setOnClickListener(view -> startRenaming());
  }
  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    getMenuInflater().inflate(R.menu.bottom_app_bar, menu);
    return true;
  }
  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();
    if (id == R.id.code) {
      openSourceCode();
      return true;
    } else if (id == R.id.mail) {
      sendFeedback();
      return true;
    } else if (id == R.id.darkMode) {
      toggleDarkMode(item);
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
  private void openSourceCode() {
    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/MFM-347/RenDroid"));
    startActivity(intent);
  }
  private void sendFeedback() {
    Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", "madnifm347@outlook.com", null));
    emailIntent.putExtra(Intent.EXTRA_SUBJECT, "App Feedback");
    startActivity(Intent.createChooser(emailIntent, "Send feedback"));
  }
  private void toggleDarkMode(MenuItem item) {
    boolean isChecked = AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES;
    AppCompatDelegate.setDefaultNightMode(isChecked ? AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES);
    item.setChecked(!isChecked);
  }
  private void requestPermissionsIfNecessary() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
      if (!Environment.isExternalStorageManager()) {
        Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION,
          Uri.parse("package:" + getPackageName()));
        storagePermissionLauncher.launch(intent);
      } else {
        openDirectoryPicker();
      }
    } else {
      if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
        PackageManager.PERMISSION_GRANTED) {
        ActivityCompat.requestPermissions(this,
          new String[] {
            Manifest.permission.WRITE_EXTERNAL_STORAGE
          }, REQUEST_CODE_PERMISSION);
      } else {
        openDirectoryPicker();
      }
    }
  }
  private final ActivityResultLauncher < Intent > storagePermissionLauncher =
    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
      if (Environment.isExternalStorageManager()) {
        openDirectoryPicker();
      } else {
        Toast.makeText(this, "Permission required to access storage", Toast.LENGTH_SHORT).show();
      }
    });
  private void openDirectoryPicker() {
    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
    directoryPickerLauncher.launch(intent);
  }
  private final ActivityResultLauncher < Intent > directoryPickerLauncher =
    registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
      if (result.getResultCode() == RESULT_OK && result.getData() != null) {
        selectedDirectoryUri = result.getData().getData();
        binding.directoryEditText.setText(selectedDirectoryUri.toString());
      } else {
        Toast.makeText(this, "Directory selection canceled or failed.", Toast.LENGTH_SHORT).show();
      }
    });

  private void startRenaming() {
    String baseName = binding.baseNameEditText.getText().toString().trim();
    if (selectedDirectoryUri == null || baseName.isEmpty()) {
      Toast.makeText(this, "Please provide both a directory and a base name.", Toast.LENGTH_SHORT).show();
      return;
    }
    DocumentFile directory = DocumentFile.fromTreeUri(this, selectedDirectoryUri);
    if (directory == null || !directory.isDirectory()) {
      Toast.makeText(this, "Invalid directory selected.", Toast.LENGTH_SHORT).show();
      return;
    }
    renameFiles(directory, baseName);
  }

  private void renameFiles(DocumentFile directory, String baseName) {
    DocumentFile[] files = directory.listFiles();
    int index = 1;
    if (files == null || files.length == 0) {
      Toast.makeText(this, "No files to rename.", Toast.LENGTH_SHORT).show();
      return;
    }
    Arrays.sort(files, (file1, file2) -> Long.compare(file2.lastModified(), file1.lastModified()));
    for (DocumentFile file: files) {
      if (file.isFile()) {
        String extension = "";
        String fileName = file.getName();
        if (fileName != null && fileName.lastIndexOf(".") != -1) {
          extension = fileName.substring(fileName.lastIndexOf("."));
        }
        String newFileName = baseName + "-" + index + extension;
        boolean renamed = file.renameTo(newFileName);
        if (!renamed) {
          Toast.makeText(this, "Error renaming file: " + file.getName(), Toast.LENGTH_SHORT).show();
        } else {
          index++;
        }
      }
    }
    Toast.makeText(this, "Files renamed successfully!", Toast.LENGTH_LONG).show();
  }

  @Override
  public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    if (requestCode == REQUEST_CODE_PERMISSION) {
      if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        openDirectoryPicker();
      } else {
        Toast.makeText(this, "Permission required to access storage", Toast.LENGTH_SHORT).show();
      }
    }
  }
}
