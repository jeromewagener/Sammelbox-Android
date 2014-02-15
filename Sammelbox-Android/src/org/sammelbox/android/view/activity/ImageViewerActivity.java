package org.sammelbox.android.view.activity;

import org.sammelbox.R;

import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebView;

public class ImageViewerActivity extends Activity { 
    private static String pathToImage;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
 
        WebView imageViewer = (WebView)findViewById(R.id.imageViewer);
        imageViewer.getSettings().setBuiltInZoomControls(true);
        
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(pathToImage, options);
        int width = options.outWidth;
        int height = options.outHeight;

        String imagePath = "file://" + pathToImage;
        
        String resizing;
        if (width > height) {
        	resizing = "width=\"80%\"";
        } else {
        	resizing = "height=\"80%\"";
        }
        
        String html = "<html><head></head><body><img " + resizing + " src=\""+ imagePath + "\"></body></html>";
        imageViewer.loadDataWithBaseURL("", html, "text/html","utf-8", ""); 
    }

	public static String getPathToImage() {
		return pathToImage;
	}

	public static void setPathToImage(String pathToImage) {
		ImageViewerActivity.pathToImage = pathToImage;
	}
}
