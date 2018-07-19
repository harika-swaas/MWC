package com.swaas.mwc.Fragments;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.michaelflisar.dragselectrecyclerview.DragSelectTouchListener;
import com.michaelflisar.dragselectrecyclerview.DragSelectionProcessor;
import com.swaas.mwc.API.Model.GetCategoryDocumentsRequest;
import com.swaas.mwc.API.Model.GetCategoryDocumentsResponse;
import com.swaas.mwc.API.Model.ListPinDevicesResponse;
import com.swaas.mwc.API.Service.GetCategoryDocumentsService;
import com.swaas.mwc.Adapters.DmsAdapter;
import com.swaas.mwc.Adapters.DmsAdapterList;
import com.swaas.mwc.Common.SimpleDividerItemDecoration;
import com.swaas.mwc.Dialogs.LoadingProgressDialog;
import com.swaas.mwc.Login.LoginActivity;
import com.swaas.mwc.Network.NetworkUtils;
import com.swaas.mwc.Preference.PreferenceUtils;
import com.swaas.mwc.R;
import com.swaas.mwc.Retrofit.RetrofitAPIBuilder;
import com.swaas.mwc.Utils.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

/**
 * Created by harika on 11-07-2018.
 */

public class ItemNavigationFolderFragment extends Fragment implements DmsAdapter.ItemClickListener {

    DmsAdapter mAdapter;
    DmsAdapterList mAdapterList;
    RecyclerView mRecyclerView;
    View mView;
    AlertDialog mAlertDialog;
    List<GetCategoryDocumentsResponse> mGetCategoryDocumentsResponses;
    List<GetCategoryDocumentsResponse> mSelectedDocumentList = new ArrayList<>();
    boolean mToogleGrid;
    boolean isSortByName, isSortByNewest, isSortBySize, isSortByDate;
    private DragSelectionProcessor.Mode mMode = DragSelectionProcessor.Mode.Simple;
    private DragSelectTouchListener mDragSelectTouchListener;
    private DragSelectionProcessor mDragSelectionProcessor;
    MenuItem menuItemAdd, menuItemSearch, menuItemDelete, menuItemShare, menuItemMove, menuItemMore;

    public static ItemNavigationFolderFragment newInstance() {
        ItemNavigationFolderFragment fragment = new ItemNavigationFolderFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mView =  inflater.inflate(R.layout.fragment_item_navigation_folder, container, false);

        setHasOptionsMenu(true);

        intiaizeViews();
        getBundleArguments();
        getCategoryDocuments();

        return mView;
    }

    private void intiaizeViews() {

        mRecyclerView = (RecyclerView) mView.findViewById(R.id.recycler_dms);
    }

    private void getBundleArguments() {

        Bundle myFolderBundleArgs = getArguments();
        if(myFolderBundleArgs != null) {
            mToogleGrid = myFolderBundleArgs.getBoolean(Constants.TOOGLEGRID);
            isSortByName = myFolderBundleArgs.getBoolean(Constants.SORT_BY_NAME);
            isSortByNewest = myFolderBundleArgs.getBoolean(Constants.SORT_BY_NEWEST);
            isSortBySize = myFolderBundleArgs.getBoolean(Constants.SORT_BY_SIZE);
            isSortByDate = myFolderBundleArgs.getBoolean(Constants.SORT_BY_DATE);
        } else {
            mToogleGrid = false;
        }
    }

    private void setGridAdapterToView(List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        Collections.sort(mGetCategoryDocumentsResponses, new Comparator<GetCategoryDocumentsResponse>() {
            @Override
            public int compare(GetCategoryDocumentsResponse lhs, GetCategoryDocumentsResponse rhs) {
                return lhs.getName().compareTo(rhs.getName());
            }
        });
        mRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(),3));
        mAdapter = new DmsAdapter(getCategoryDocumentsResponses,getActivity());
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.setClickListener(new DmsAdapter.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                mAdapter.toggleSelection(position);
                mSelectedDocumentList.remove(mGetCategoryDocumentsResponses.get(position));

                updateToolbarMenuItems(mSelectedDocumentList);
            }

            @Override
            public boolean onItemLongClick(View view, int position)
            {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

                mDragSelectTouchListener.startDragSelection(position);

                GetCategoryDocumentsResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);

                updateToolbarMenuItems(mSelectedDocumentList);

                return true;
            }
        });

        mDragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public HashSet<Integer> getSelection() {
                return mAdapter.getSelection();
            }

            @Override
            public boolean isSelected(int index) {
                return mAdapter.getSelection().contains(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                mAdapter.selectRange(start, end, isSelected);
            }
        })
                .withMode(mMode);
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        updateSelectionListener();
        mRecyclerView.addOnItemTouchListener(mDragSelectTouchListener);
    }

    private void updateSelectionListener()
    {
        mDragSelectionProcessor.withMode(mMode);
      //  mToolbar.setSubtitle("Mode: " + mMode.name());
    }

    private void getCategoryDocuments() {

        if (NetworkUtils.isNetworkAvailable(getActivity())) {

            Retrofit retrofitAPI = RetrofitAPIBuilder.getInstance();

            final LoadingProgressDialog transparentProgressDialog = new LoadingProgressDialog(getActivity());
            transparentProgressDialog.show();

            final GetCategoryDocumentsRequest mGetCategoryDocumentsRequest = new GetCategoryDocumentsRequest(0,"list","category","1","0");

            String request = new Gson().toJson(mGetCategoryDocumentsRequest);

            //Here the json data is add to a hash map with key data
            Map<String, String> params = new HashMap<String, String>();
            params.put("data", request);

            final GetCategoryDocumentsService mGetCategoryDocumentsService = retrofitAPI.create(GetCategoryDocumentsService.class);

            Call call = null;

            if(isSortByName == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByName(params, PreferenceUtils.getAccessToken(getActivity()));
            } else if(isSortByNewest == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByType(params, PreferenceUtils.getAccessToken(getActivity()));
            } else if(isSortBySize == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortBySize(params, PreferenceUtils.getAccessToken(getActivity()));
            } else if(isSortByDate == true) {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2SortByDate(params, PreferenceUtils.getAccessToken(getActivity()));
            } else {
                call = mGetCategoryDocumentsService.getCategoryDocumentsV2(params, PreferenceUtils.getAccessToken(getActivity()));
            }

            call.enqueue(new Callback<ListPinDevicesResponse<GetCategoryDocumentsResponse>>() {
                @Override
                public void onResponse(Response<ListPinDevicesResponse<GetCategoryDocumentsResponse>> response, Retrofit retrofit) {
                    ListPinDevicesResponse apiResponse = response.body();
                    if (apiResponse != null) {

                        transparentProgressDialog.dismiss();

                        if (apiResponse.status.getCode() instanceof Boolean) {
                            if (apiResponse.status.getCode() == Boolean.FALSE) {
                                transparentProgressDialog.dismiss();
                                mGetCategoryDocumentsResponses = response.body().getData();
                                if(mToogleGrid == false)
                                {
                                       setGridAdapterToView(mGetCategoryDocumentsResponses);
                                }
                                else
                                {
                                    setListAdapterToView(mGetCategoryDocumentsResponses);
                                }
                            }

                        } else if (apiResponse.status.getCode() instanceof Integer) {
                            transparentProgressDialog.dismiss();
                            String mMessage = apiResponse.status.getMessage().toString();

                            final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                            View view = inflater.inflate(R.layout.pin_verification_alert_layout, null);
                            builder.setView(view);
                            builder.setCancelable(false);

                            TextView txtMessage = (TextView) view.findViewById(R.id.txt_message);

                            txtMessage.setText(mMessage);

                            Button sendPinButton = (Button) view.findViewById(R.id.send_pin_button);
                            Button cancelButton = (Button) view.findViewById(R.id.cancel_button);

                            cancelButton.setVisibility(View.GONE);

                            sendPinButton.setText("OK");

                            sendPinButton.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    mAlertDialog.dismiss();
                                    startActivity(new Intent(getActivity(), LoginActivity.class));
                                    getActivity().finish();
                                }
                            });

                            mAlertDialog = builder.create();
                            mAlertDialog.show();
                        }
                    }
                }

                @Override
                public void onFailure(Throwable t) {
                    transparentProgressDialog.dismiss();
                    Log.d("PinDevice error", t.getMessage());
                }
            });
        }
    }

    private void setListAdapterToView(final List<GetCategoryDocumentsResponse> getCategoryDocumentsResponses) {

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity().getApplicationContext()));
        mAdapterList = new DmsAdapterList(getCategoryDocumentsResponses,getActivity());
        mRecyclerView.setAdapter(mAdapterList);

        mAdapterList.setClickListener(new DmsAdapterList.ItemClickListener()
        {
            @Override
            public void onItemClick(View view, int position)
            {
                mAdapterList.toggleSelection(position);

                menuItemDelete.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemMore.setVisible(false);
                menuItemSearch.setVisible(true);
                menuItemAdd.setVisible(true);

                mSelectedDocumentList.remove(mGetCategoryDocumentsResponses.get(position));

                updateToolbarMenuItems(mSelectedDocumentList);
            }

            @Override
            public boolean onItemLongClick(View view, int position)
            {
                // if one item is long pressed, we start the drag selection like following:
                // we just call this function and pass in the position of the first selected item
                // the selection processor does take care to update the positions selection mode correctly
                // and will correctly transform the touch events so that they can be directly applied to your adapter!!!
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

                mDragSelectTouchListener.startDragSelection(position);

                GetCategoryDocumentsResponse documentsResponseObj = mGetCategoryDocumentsResponses.get(position);
                mSelectedDocumentList.add(documentsResponseObj);

                updateToolbarMenuItems(mSelectedDocumentList);

                return true;
            }
        });

        mDragSelectionProcessor = new DragSelectionProcessor(new DragSelectionProcessor.ISelectionHandler() {
            @Override
            public HashSet<Integer> getSelection() {
                return mAdapterList.getSelection();
            }

            @Override
            public boolean isSelected(int index) {
                return mAdapterList.getSelection().contains(index);
            }

            @Override
            public void updateSelection(int start, int end, boolean isSelected, boolean calledFromOnStart) {
                mAdapterList.selectRange(start, end, isSelected);
            }
        })
                .withMode(mMode);
        mDragSelectTouchListener = new DragSelectTouchListener()
                .withSelectListener(mDragSelectionProcessor);
        updateSelectionListener();
        mRecyclerView.addOnItemTouchListener(mDragSelectTouchListener);
    }

    private void updateToolbarMenuItems(List<GetCategoryDocumentsResponse> mSelectedDocumentList) {

        boolean isDocument = false;
        boolean isFolder = false;

        List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
        List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();

        if(mSelectedDocumentList != null && mSelectedDocumentList.size() > 0) {

            for (GetCategoryDocumentsResponse viewListObj : mSelectedDocumentList) {

                if (viewListObj.getType().equalsIgnoreCase("category")) {
                    isFolder = true;
                    categoryDocumentsResponseFolderList.add(viewListObj);
                } else {
                    isDocument = true;
                    categoryDocumentsResponseDocumentList.add(viewListObj);
                }
            }

            if (categoryDocumentsResponseFolderList.size() == 1) {
                menuItemDelete.setVisible(true);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

            } else if (categoryDocumentsResponseFolderList.size() >= 1) {
                menuItemDelete.setVisible(false);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);
            }

            if (categoryDocumentsResponseDocumentList.size() == 1) {
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemMove.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);

            } else if (categoryDocumentsResponseDocumentList.size() >= 1) {
                menuItemDelete.setVisible(true);
                menuItemShare.setVisible(true);
                menuItemMore.setVisible(true);
                menuItemMove.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);
            }

            if (isFolder && isDocument) {
                menuItemDelete.setVisible(false);
                menuItemMove.setVisible(true);
                menuItemMore.setVisible(false);
                menuItemShare.setVisible(false);
                menuItemSearch.setVisible(false);
                menuItemAdd.setVisible(false);
            }

        } else {
            menuItemDelete.setVisible(false);
            menuItemShare.setVisible(false);
            menuItemMore.setVisible(false);
            menuItemMove.setVisible(false);
            menuItemSearch.setVisible(true);
            menuItemAdd.setVisible(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_multi_select, menu);
        menuItemAdd = menu.findItem(R.id.action_add);
        menuItemSearch = menu.findItem(R.id.action_search);
        menuItemDelete = menu.findItem(R.id.action_delete);
        menuItemShare = menu.findItem(R.id.action_share);
        menuItemMore = menu.findItem(R.id.action_more);
        menuItemMove = menu.findItem(R.id.action_move);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;

            case R.id.action_more:
                openBottomSheetForMultiSelect();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void openBottomSheetForMultiSelect(){

        View view = getActivity().getLayoutInflater().inflate(R.layout.bottom_sheet_sort, null);

        TextView clearSelection = (TextView) view.findViewById(R.id.clear_selection);
        /*TextView availableOffline = (TextView) view.findViewById(R.id.available_offline);
        TextView share = (TextView) view.findViewById(R.id.share);*/
        TextView copy = (TextView) view.findViewById(R.id.copy);
        TextView move = (TextView) view.findViewById(R.id.move);
        TextView rename = (TextView) view.findViewById(R.id.rename);
        TextView delete = (TextView) view.findViewById(R.id.delete);
        RelativeLayout availableOfflineLayout = (RelativeLayout) view.findViewById(R.id.available_offline_layout);
        RelativeLayout shareLayout = (RelativeLayout) view.findViewById(R.id.share_layout);

        final Dialog mBottomSheetDialog = new Dialog(getActivity(), R.style.MaterialDialogSheet);
        mBottomSheetDialog.setContentView(view);
        mBottomSheetDialog.setCancelable(true);
        mBottomSheetDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mBottomSheetDialog.getWindow().setGravity(Gravity.BOTTOM);
        mBottomSheetDialog.show();

        List<GetCategoryDocumentsResponse> categoryDocumentsResponseFolderList = new ArrayList<>();
        List<GetCategoryDocumentsResponse> categoryDocumentsResponseDocumentList = new ArrayList<>();

        for(GetCategoryDocumentsResponse viewListObj : mSelectedDocumentList){

            if(viewListObj.getType().equalsIgnoreCase("category")) {
                categoryDocumentsResponseFolderList.add(viewListObj);
            } else {
                categoryDocumentsResponseDocumentList.add(viewListObj);
            }
        }

        if(categoryDocumentsResponseFolderList.size() == 1){
            move.setVisibility(View.VISIBLE);
            rename.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            clearSelection.setVisibility(View.GONE);
            availableOfflineLayout.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            copy.setVisibility(View.GONE);
        }

        if(categoryDocumentsResponseDocumentList.size() == 1){
            move.setVisibility(View.VISIBLE);
            rename.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            clearSelection.setVisibility(View.GONE);
            availableOfflineLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);


        } else if(categoryDocumentsResponseDocumentList.size() >= 1){
            move.setVisibility(View.VISIBLE);
            rename.setVisibility(View.GONE);
            delete.setVisibility(View.VISIBLE);
            clearSelection.setVisibility(View.VISIBLE);
            availableOfflineLayout.setVisibility(View.VISIBLE);
            shareLayout.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);
        }

        shareLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    public void onItemClick(View view, int position) {

    }

    @Override
    public boolean onItemLongClick(View view, int position) {
        return false;
    }
}
