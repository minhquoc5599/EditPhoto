package com.example.editphoto;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.editphoto.Adapter.ViewPagerAdapter;
import com.example.editphoto.Interface.BrushFragmentListener;
import com.example.editphoto.Interface.EditImageFragmentListener;
import com.example.editphoto.Interface.FiltersListFragmentListener;
import com.example.editphoto.Utils.BitmapUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zomato.photofilters.imageprocessors.Filter;
import com.zomato.photofilters.imageprocessors.subfilters.BrightnessSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.ContrastSubFilter;
import com.zomato.photofilters.imageprocessors.subfilters.SaturationSubfilter;

import java.io.IOException;
import java.util.List;
import java.util.MissingFormatArgumentException;

import ja.burhanrashid52.photoeditor.OnSaveBitmap;
import ja.burhanrashid52.photoeditor.PhotoEditor;
import ja.burhanrashid52.photoeditor.PhotoEditorView;

public class MainActivity extends AppCompatActivity implements FiltersListFragmentListener, EditImageFragmentListener, BrushFragmentListener {

    public static String pictureName ="flash.jpg";
    public static final int PERMISSION_PICK_IMAGE = 1000;

    PhotoEditorView photoEditorView;
    PhotoEditor photoEditor;
    CoordinatorLayout coordinatorLayout;

    Bitmap originalBitmap, filteredBitmap, finalBitmap;

    FiltersListFragment filtersListFragment;
    EditImageFragment editImageFragment;

    CardView btn_filters_list, btn_edit, btn_brush;

    int brightnessFinal =0;
    float saturationFinal = 1.0f;
    float constrantFinal =  1.0f;

    static {
        System.loadLibrary("NativeImageProcessor");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Edit photo");

        photoEditorView = findViewById(R.id.image_preview);
        photoEditor = new PhotoEditor.Builder(this, photoEditorView)
                .setPinchTextScalable(true)
                .build();
        coordinatorLayout = findViewById(R.id.coordinator);

        btn_edit = findViewById(R.id.btn_edit);
        btn_filters_list = findViewById(R.id.btn_filter_list);
        btn_brush = findViewById(R.id.btn_brush);

        btn_filters_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FiltersListFragment filtersListFragment = FiltersListFragment.getInstance();
                filtersListFragment.setListener(MainActivity.this);
                filtersListFragment.show(getSupportFragmentManager(), filtersListFragment.getTag());
            }
        });

        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditImageFragment editImageFragment = EditImageFragment.getInstance();
                editImageFragment.setListener(MainActivity.this);
                editImageFragment.show(getSupportFragmentManager(), editImageFragment.getTag());
            }
        });
        btn_brush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                photoEditor.setBrushDrawingMode(true);
                BrushFragment brushFragment = BrushFragment.getInstance();
                brushFragment.setListener(MainActivity.this);
                brushFragment.show(getSupportFragmentManager(), brushFragment.getTag());

            }
        });

        loadImage();

    }

    public void loadImage() {
        /*originalBitmap = BitmapUtils.getBitmapFromAssets(this, pictureName, 200, 200);
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(originalBitmap);*/
        Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/text-recognition-8b752.appspot.com/o/image1576516300429.png?alt=media&token=a1abee36-35fc-4d46-8755-454a99d9a410")
                .into(photoEditorView.getSource());
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager(), 0);

        filtersListFragment = new FiltersListFragment();
        filtersListFragment.setListener(this);

        editImageFragment = new EditImageFragment();
        editImageFragment.setListener(this);

        adapter.addFragment(filtersListFragment, "FILTERS");
        adapter.addFragment(editImageFragment, "EDIT");

        viewPager.setAdapter(adapter);
    }

    @Override
    public void onBrightnessChanged(int brightness) {
        brightnessFinal = brightness;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightness));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(
                finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));

    }

    @Override
    public void onSaturationChanged(float saturation) {
        saturationFinal = saturation;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new SaturationSubfilter(saturation));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(
                finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onConstrantChanged(float constrant) {
        constrantFinal = constrant;
        Filter myFilter = new Filter();
        myFilter.addSubFilter(new ContrastSubFilter(constrant));
        photoEditorView.getSource().setImageBitmap(myFilter.processFilter(
                finalBitmap.copy(Bitmap.Config.ARGB_8888, true)));
    }

    @Override
    public void onEditStarted() {

    }

    @Override
    public void onEditCompleted() {
        Bitmap bitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);

        Filter myFilter = new Filter();
        myFilter.addSubFilter(new BrightnessSubFilter(brightnessFinal));
        myFilter.addSubFilter(new SaturationSubfilter(saturationFinal));
        myFilter.addSubFilter(new ContrastSubFilter(constrantFinal));

        finalBitmap = myFilter.processFilter(bitmap);
    }

    @Override
    public void onFilterSelected(Filter filter) {
        resetControl();
        filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
        photoEditorView.getSource().setImageBitmap(filter.processFilter(filteredBitmap));
        finalBitmap = filteredBitmap.copy(Bitmap.Config.ARGB_8888, true);
    }

    private void resetControl() {
        if(editImageFragment != null)
        {
            editImageFragment.resetControl();
        }
        brightnessFinal = 0;
        saturationFinal = 1.0f;
        constrantFinal = 1.0f;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id  = item.getItemId();
        if(id ==R.id.action_open)
        {
            openImageFromGallery();
            return true;
        }
        if(id ==R.id.action_save)
        {
            saveImageFromGallery();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted())
                        {
                            photoEditor.saveAsBitmap(new OnSaveBitmap() {
                                @Override
                                public void onBitmapReady(Bitmap saveBitmap) {
                                    try {
                                        photoEditorView.getSource().setImageBitmap(saveBitmap);
                                        final String path = BitmapUtils.insertImage(getContentResolver(),
                                                saveBitmap,
                                                System.currentTimeMillis()+"profie.jpg",
                                                null);

                                        if(!TextUtils.isEmpty(path))
                                        {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Image saved to gallery"
                                                    , Snackbar.LENGTH_LONG).setAction("OPEN", new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    openImage(path);
                                                }
                                            });
                                            snackbar.show();
                                        }
                                        else
                                        {
                                            Snackbar snackbar = Snackbar.make(coordinatorLayout,
                                                    "Unable to save image"
                                                    , Snackbar.LENGTH_LONG);
                                            snackbar.show();
                                        }
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }

                                @Override
                                public void onFailure(Exception e) {

                                }
                            });
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

    }

    private void openImage(String path) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.parse(path), "image/*");
        startActivity(intent);

    }

    private void openImageFromGallery() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {
                        if(report.areAllPermissionsGranted())
                        {
                            Intent intent = new Intent(Intent.ACTION_PICK);
                            intent.setType("image/*");
                            startActivityForResult(intent, PERMISSION_PICK_IMAGE);
                        }
                        else
                        {
                            Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                    }
                }).check();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(resultCode==RESULT_OK && requestCode==PERMISSION_PICK_IMAGE)
        {
            assert data != null;
            Bitmap bitmap = BitmapUtils.getBitmapFromGallery(this, data.getData(), 800,800);

            originalBitmap.recycle();
            finalBitmap.recycle();
            filteredBitmap.recycle();

            originalBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            finalBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            filteredBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true);
            photoEditorView.getSource().setImageBitmap(originalBitmap);
            bitmap.recycle();

            filtersListFragment.displayThumbnail(originalBitmap);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onBrushSizeChangedListener(float size) {
        photoEditor.setBrushSize(size);
    }

    @Override
    public void onBrushOpacityChangedListener(int opacity) {
        photoEditor.setOpacity(opacity);
    }

    @Override
    public void onBrushColorChangedListener(int color) {
        photoEditor.setBrushColor(color);
    }

    @Override
    public void onBrushStateChangedListener(boolean isEraser) {
        if(isEraser)
        {
            photoEditor.brushEraser();
        }
        else
        {
            photoEditor.setBrushDrawingMode(true);
        }
    }
}
