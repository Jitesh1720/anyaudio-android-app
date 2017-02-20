package any.audio.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;

import any.audio.Activity.AnyAudioActivity;
import any.audio.Adapters.DownloadedItemsAdapter;
import any.audio.Adapters.DownloadingAdapter;
import any.audio.Config.Constants;
import any.audio.Models.DownloadedItemModel;
import any.audio.R;
import any.audio.helpers.TaskHandler;
import any.audio.helpers.ToastMaker;

/**
 * Created by Ankit on 2/10/2017.
 */

public class DownloadedFragment extends Fragment {

    private Context context;
    private TextView emptyMessage;
    private ListView downloadedListView;
    private DownloadedItemsAdapter downloadedAdapter;
    private DownloadedItemsAdapter.DownloadedItemDeleteListener downloadedItemDeleteListener = new DownloadedItemsAdapter.DownloadedItemDeleteListener() {
        @Override
        public void onDelete(int index) {
            showConfirmationDiaoge(index);
        }
    };

    private void showConfirmationDiaoge(final int index) {

        ArrayList<DownloadedItemModel> itemModels = getDownloadedItemList();

        AlertDialog.Builder builderReDownloadAlert = new AlertDialog.Builder(context);
        builderReDownloadAlert.setTitle("Delete");
        builderReDownloadAlert.
                setMessage(itemModels.get(index).title.substring(itemModels.get(index).title.lastIndexOf('/') + 1))
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                deleteItem(index);

                                Toast.makeText(context, "Deleted", Toast.LENGTH_LONG).show();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //dismiss dialog
                                dialog.dismiss();
                                break;
                        }

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:

                                deleteItem(index);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //dismiss dialog
                                dialog.dismiss();
                                break;
                        }

                    }
                }).show();


    }

    private void deleteItem(int index) {

        ArrayList<DownloadedItemModel> newList = getDownloadedItemList();
        //deleting original files
        Log.i("DeletingFile"," path to delete "+newList.get(index));
        File _file_to_delete = new File(String.valueOf(newList.get(index).title));

        if(_file_to_delete.delete()){

            //local downloaded list
            newList.remove(index);
            downloadedAdapter.setDownloadingList(newList);
            if(newList.size()==0){
                emptyMessage.setVisibility(View.VISIBLE);
            }
            Toast.makeText(context, "Deleted ! ", Toast.LENGTH_SHORT).show();

        }else {
            Toast.makeText(context, "Cannot Delete ! ", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View fragmentView = inflater.inflate(R.layout.fragment_downloaded, container, false);
        emptyMessage = (TextView) fragmentView.findViewById(R.id.emptyDownloadedListMessage);
        downloadedListView = (ListView) fragmentView.findViewById(R.id.DownloadedListView);
        downloadedAdapter = DownloadedItemsAdapter.getInstance(getActivity());
        downloadedAdapter.setOnDownloadCancelListener(downloadedItemDeleteListener);
        downloadedAdapter.setDownloadingList(getDownloadedItemList());
        downloadedListView.setAdapter(downloadedAdapter);
        return fragmentView;

    }

    private ArrayList<DownloadedItemModel> getDownloadedItemList() {
        ArrayList<DownloadedItemModel> downloadedItemModels = new ArrayList<>();
        File dir = new File(Constants.DOWNLOAD_FILE_DIR);
        File[] _files = dir.listFiles();
        for (File f : _files) {
            String path = f.toString();
            Log.d("Downloaded", "" + path.toString());
            downloadedItemModels.add(0, new DownloadedItemModel(path));
        }

        if(downloadedItemModels.size()==0) {
            emptyMessage.setVisibility(View.VISIBLE);
        }else{
            emptyMessage.setVisibility(View.GONE);
        }

        return downloadedItemModels;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}