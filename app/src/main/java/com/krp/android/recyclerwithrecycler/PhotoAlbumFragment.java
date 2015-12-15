package com.krp.android.recyclerwithrecycler;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by purushottam.kumar on 12/15/2015.
 */
public class PhotoAlbumFragment extends Fragment implements View.OnClickListener,
        PhotoInspectionGridAdapter.OnPhotoInspectionGridItemClickListener {

    private static final String TAG = PhotoAlbumFragment.class.getSimpleName();
    private static final String ARG_POSITION = "position";
    private static final int REQUEST_CAPTURE_PICTURE = 0XFF;
    private static final int DIALOG_FRAGMENT = 1;
    private PhotoCollection photoCollection;
    private String regno;
    private View view;

    private RecyclerView recyclerView;
    private PhotoAlbumListAdapter albumListAdapter;
    private OnListScrollHideListener scrollHideListener;

    private static PhotoAlbumFragment activeInstance;
    public static PhotoAlbumFragment newInstance(
            int position, Context context, int carid, int dealerid
    ) {
        activeInstance = new FragmentPhotos();
        activeInstance.photoCollection = PhotoController.getInstance()
                .getPhotoCollection(carid, dealerid, true);
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        activeInstance.setArguments(b);
        return activeInstance;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            int carId = savedInstanceState.getInt(Constants.KEY_CAR_ID);
            int dealerId = savedInstanceState.getInt(Constants.KEY_CAR_ID);
            if (carId != 0 && dealerId != 0) {
                photoCollection = PhotoController.getInstance()
                        .getPhotoCollection(carId, dealerId, true);
            }
        }
        regno = CarDao.getInstance().getRegistrationNumber(photoCollection.getCarId());
        regno = FileUtils.createProperRegNo(regno);
        view = inflater.inflate(R.layout.recycler, null);

        setupRecyclerView();
        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(!(activity instanceof ActivitySlidingTab)) {
            return;
        }
        if(activity instanceof OnListScrollHideListener) {
            scrollHideListener = (OnListScrollHideListener) activity;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshImageAdapters();
    }

    private void setupRecyclerView() {
        recyclerView = (RecyclerView) view.findViewById(R.id.recycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        albumListAdapter = new PhotoAlbumListAdapter(getActivity(), photoCollection, this, this);
        recyclerView.setAdapter(albumListAdapter);

        recyclerView.addOnScrollListener(new OnScrollHideListener() {
            @Override
            public void onHide() {
                if(scrollHideListener != null) {
                    scrollHideListener.onScrollHideViews();
                }
            }

            @Override
            public void onShow() {
                if(scrollHideListener != null) {
                    scrollHideListener.onScrollShowViews();
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.camera:
                chooseImage();
                break;

            case R.id.camera_chasis:
                chooseChasisImage();
                break;
        }
    }

    @Override
    public void onPhotoInspectionGridItemClick(RecyclerView.ViewHolder viewHolder, List<Photo> photos, int position) {
        showLargePic(photos.get(position));
    }

    private void showLargePic(Photo photo) {
        Intent intnt = new Intent(getActivity(), PhotoGalleryActivity.class);
        intnt.putExtra(Constants.KEY_CAR_ID, photoCollection.getCarId());
        intnt.putExtra(Constants.KEY_PHOTO_URL, photo.getLocalURL());
        startActivity(intnt);
    }

    private void chooseImage() {
        FlashUtil.turnOffFlash();
        if (FlashUtil.camera != null) {
            FlashUtil.camera.release();
            FlashUtil.camera = null;
        }

        Intent intent = new Intent(getActivity(), CameraLauncherActivity.class);
        intent.putExtra(Constants.KEY_CAR_ID, photoCollection.getCarId());
        intent.putExtra(Constants.KEY_DEALER_ID, photoCollection.getDealerId());
        intent.putExtra(Constants.KEY_NEED_RESULT, false);
        startActivity(intent);
    }

    private void chooseChasisImage() {
        TagDialogFragment dialogFragment = new TagDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(AppConstants.FROMMAIN, true);
        dialogFragment.setArguments(bundle);
        dialogFragment.setTargetFragment(FragmentPhotos.this, DIALOG_FRAGMENT);
        dialogFragment.show(getActivity().getSupportFragmentManager(), "dialog");
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CAPTURE_PICTURE && resultCode == Activity.RESULT_OK) {
            File main = (File) data.getSerializableExtra(AbsureCameraFragment.KEY_PIC_MAIN);
            File thumb = (File) data.getSerializableExtra(AbsureCameraFragment.KEY_PIC_THUMB);
            onImageTaken(main, thumb);
        }
        else if(requestCode == DIALOG_FRAGMENT && resultCode == Activity.RESULT_OK){
            PhotoController.getInstance().setTagname(data.getStringExtra("text"));
            chooseCamera();
        }
    }

    private void onImageTaken(File main, File thumb) {
        String caption = PhotoController.getInstance().getTagname();
        Photo photo = PhotoController.getInstance().addPhoto(Utilities.nextID(), photoCollection.getCarId(),
                photoCollection.getDealerId(), main.getPath(),
                thumb.getPath(),
                Photo.State.QUEUED.getValue(),caption,Photo.PhotoType.CUST_PHOTOS.getValue(),0);
        DataController.getInstance().insertPhoto(photo);

        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                refreshImageAdapters();
            }
        });


        /**** start photo sync manager */
        Intent photoSyncEventIntent = new Intent(getActivity(),
                PhotoSyncManager.PhotoSyncEventReceiver.class);
        photoSyncEventIntent.putExtra(Constants.KEY_PHOTO_SYNC_EVENT,
                Constants.EVENT_PHOTO_CAPTURED);
        photoSyncEventIntent.putExtra(Constants.KEY_CAR_ID, photo
                .getCollection().getCarId());
        photoSyncEventIntent.putExtra(Constants.KEY_PHOTO_URL,
                photo.getLocalURL());
        getActivity().sendBroadcast(photoSyncEventIntent);


    }


    public void alert(final String text) {
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                Toast.makeText(getActivity(), text, Toast.LENGTH_LONG).show();
            }
        });

    }


    private void refreshImageAdapters() {
        if (photoCollection != null) {
            albumListAdapter.notifyInspectionPhotoSetChanged();

			/*if(photoCollection.getAllPhotoCaption().size()>0)
				showChassisCamera(View.GONE);
			else
				showChassisCamera(View.VISIBLE);*/
        }
    }

    public static class UIEventReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent intent) {

            if (activeInstance == null) {
                return;
            }
            int event = intent.getExtras().getInt(
                    Constants.KEY_PHOTO_UPLOAD_EVENT);
            Logger.d(TAG, "onReceive() EVENT: " + Constants.getEvent(event));

            int carId = intent.getExtras().getInt(Constants.KEY_CAR_ID);
            String url = intent.getExtras().getString(Constants.KEY_PHOTO_URL);
            PhotoCollection collection = PhotoController.getInstance()
                    .getPhotoCollection(carId);
            if (collection == null) {
                return;
            }
            Photo photo = collection.get(url);
            if (photo == null) {
                return;
            }
            activeInstance.refreshImageAdapters();


        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(Constants.KEY_CAR_ID, photoCollection.getCarId());
        outState.putInt(Constants.KEY_DEALER_ID, photoCollection.getDealerId());
        super.onSaveInstanceState(outState);
    }

    private void chooseCamera(){
        FlashUtil.turnOffFlash();
        if (FlashUtil.camera != null) {
            FlashUtil.camera.release();
            FlashUtil.camera = null;
        }

        Intent intent = new Intent(getActivity(), CameraLauncherActivity.class);
        intent.putExtra(Constants.KEY_CAR_ID, photoCollection.getCarId());
        intent.putExtra(Constants.KEY_DEALER_ID, photoCollection.getDealerId());
        intent.putExtra(Constants.KEY_NEED_RESULT, true);

		/*String regno =CarDao.getInstance().getRadegistrationNumber(photoCollection.getCarId());
		if (regno != null) {
			regno = regno.replace(" ", "");
		}
		String fileName = FileUtils.createFileName(regno, photoCollection.getCarId());
		Intent intent = new Intent(getActivity(), CameraLauncherActivity.class);
		intent.putExtra(KEY_FILE_NAME, fileName+"_exterior");*/
        startActivityForResult(intent, REQUEST_CAPTURE_PICTURE);
    }

    private void showChassisCamera(int isHide) {
        albumListAdapter.setCameraChasisVisibility(isHide);
    }

    public interface OnListScrollHideListener {
        void onScrollHideViews();
        void onScrollShowViews();
    }
}