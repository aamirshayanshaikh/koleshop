package com.koleshop.appkoleshop.fragments.product;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ViewFlipper;

import com.koleshop.appkoleshop.common.constant.Constants;
import com.koleshop.appkoleshop.common.util.CommonUtils;
import com.koleshop.appkoleshop.common.util.KoleCacheUtil;
import com.koleshop.appkoleshop.extensions.KolClickListener;
import com.koleshop.appkoleshop.extensions.KolRecyclerTouchListener;
import com.koleshop.appkoleshop.model.ProductSelectionRequest;
import com.koleshop.appkoleshop.singletons.KoleshopSingleton;
import com.koleshop.appkoleshop.R;
import com.koleshop.appkoleshop.adapters.InventoryProductAdapter;
import com.koleshop.appkoleshop.common.util.SerializationUtil;
import com.koleshop.appkoleshop.model.genericjson.GenericJsonListInventoryProduct;
import com.koleshop.appkoleshop.services.CommonIntentService;
import com.koleshop.api.yolo.inventoryEndpoint.model.InventoryProduct;
import com.tonicartos.superslim.LayoutManager;

import org.parceler.Parcels;

import java.util.Date;
import java.util.List;

public class InventoryProductFragment extends Fragment {

    private RecyclerView recyclerView;
    private InventoryProductAdapter inventoryProductAdapter;
    private Context mContext;
    private ViewFlipper viewFlipper;
    BroadcastReceiver mBroadcastReceiverInventoryProductFragment;
    private long categoryId;
    Button buttonRetry, buttonReload;
    LayoutManager lm;
    boolean refreshProductsInsteadOfReloading;
    private final static String TAG = "InventProductFragment";
    private static final int VIEW_TYPE_HEADER = 0x01;
    private static final int VIEW_TYPE_CONTENT = 0x00;

    private static final int VF_LOADING = 0;
    private static final int VF_LOAD_FAILED = 1;
    private static final int VF_RECYCLER_VIEW = 2;
    private static final int VF_NO_PRODUCTS = 3;

    private boolean myInventory = false;

    public InventoryProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (bundle != null) {
            myInventory = bundle.getBoolean("myInventory", false);
        }
        mContext = getActivity();
        refreshProductsInsteadOfReloading = false;
        fetchProducts();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        categoryId = bundle.getLong("categoryId");
        View layout = inflater.inflate(R.layout.fragment_inventory_product, container, false);
        recyclerView = (RecyclerView) layout.findViewById(R.id.rv_inventory_product);
        viewFlipper = (ViewFlipper) layout.findViewById(R.id.view_flipper_inventory_product_fragment);
        viewFlipper.setDisplayedChild(VF_LOADING);
        buttonReload = (Button) layout.findViewById(R.id.button_reload_fragment_inventory_product);
        buttonRetry = (Button) layout.findViewById(R.id.button_retry_fragment_inventory_product);
        View.OnClickListener retryClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchProducts();
            }
        };
        buttonRetry.setOnClickListener(retryClickListener);
        buttonReload.setOnClickListener(retryClickListener);
        initializeBroadcastReceivers();
        lm = new LayoutManager(getActivity());
        recyclerView.setLayoutManager(lm);
        return layout;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        super.onResume();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        lbm.registerReceiver(mBroadcastReceiverInventoryProductFragment, new IntentFilter(Constants.ACTION_FETCH_INVENTORY_PRODUCTS_SUCCESS));
        lbm.registerReceiver(mBroadcastReceiverInventoryProductFragment, new IntentFilter(Constants.ACTION_FETCH_INVENTORY_PRODUCTS_FAILED));
        lbm.registerReceiver(mBroadcastReceiverInventoryProductFragment, new IntentFilter(Constants.ACTION_UPDATE_INVENTORY_PRODUCT_SELECTION_SUCCESS));
        lbm.registerReceiver(mBroadcastReceiverInventoryProductFragment, new IntentFilter(Constants.ACTION_UPDATE_INVENTORY_PRODUCT_SELECTION_FAILURE));
        lbm.registerReceiver(mBroadcastReceiverInventoryProductFragment, new IntentFilter(Constants.ACTION_NOTIFY_PRODUCT_SELECTION_VARIETY_TO_PARENT));
        lbm.registerReceiver(mBroadcastReceiverInventoryProductFragment, new IntentFilter(Constants.ACTION_COLLAPSE_EXPANDED_PRODUCT));
        if(KoleshopSingleton.getSharedInstance().getReloadProductsCategoryId()!=null &&
                KoleshopSingleton.getSharedInstance().getReloadProductsCategoryId()==categoryId) {
            //refresh products list to show changes
            KoleshopSingleton.getSharedInstance().setReloadProductsCategoryId(0l);
            refreshProductsInsteadOfReloading = true;
            fetchProducts();
        }
    }

    private void initializeBroadcastReceivers() {
        mBroadcastReceiverInventoryProductFragment = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (isAdded()) {
                    //fetch product success
                    if (intent.getAction().equalsIgnoreCase(Constants.ACTION_FETCH_INVENTORY_PRODUCTS_SUCCESS)) {
                        long receivedCategoryId = intent.getLongExtra("catId", 0l);
                        //Log.d(TAG, "receivedCategoryId = " + receivedCategoryId + " and categoryId = " + categoryId);
                        if (receivedCategoryId == categoryId) {
                            //Log.d(TAG, "yippie...will load products now for categoryId = " + categoryId);
                            loadProducts(null);
                        } else {
                            //Log.d(TAG, "wtf is happening");
                        }

                        //fetch products failed
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_FETCH_INVENTORY_PRODUCTS_FAILED)) {
                        long receivedCategoryId = intent.getLongExtra("catId", 0l);
                        //Log.d(TAG, "FAIL...receivedCategoryId = " + receivedCategoryId + " and categoryId = " + categoryId);
                        if (receivedCategoryId == categoryId) {
                            couldNotLoadProducts();
                        }

                        //update product selection
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_UPDATE_INVENTORY_PRODUCT_SELECTION_SUCCESS)) {
                        Parcelable parcelableRequest = intent.getParcelableExtra("request");
                        ProductSelectionRequest request = Parcels.unwrap(parcelableRequest);
                        if (inventoryProductAdapter != null && inventoryProductAdapter.getPendingRequestsRandomIds() != null && inventoryProductAdapter.getPendingRequestsRandomIds().contains(request.getRandomId())) {
                            inventoryProductAdapter.updateProductSelection(request, true);
                            inventoryProductAdapter.updateProductsCache();
                        }

                        //update product selection failed
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_UPDATE_INVENTORY_PRODUCT_SELECTION_FAILURE)) {
                        Parcelable parcelableRequest = intent.getParcelableExtra("request");
                        ProductSelectionRequest request = Parcels.unwrap(parcelableRequest);
                        if (inventoryProductAdapter != null && inventoryProductAdapter.getPendingRequestsRandomIds() != null && inventoryProductAdapter.getPendingRequestsRandomIds().contains(request.getRandomId())) {
                            inventoryProductAdapter.updateProductSelection(request, false);
                        }

                        //notification from product variety view
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_NOTIFY_PRODUCT_SELECTION_VARIETY_TO_PARENT)) {
                        Long requestCategoryId = intent.getLongExtra("requestCategoryId", 0l);
                        Long varietyId = intent.getLongExtra("varietyId", 0l);
                        int position = intent.getIntExtra("position", 0);
                        boolean varietySelected = intent.getBooleanExtra("varietySelected", false);
                        if (varietyId > 0 && requestCategoryId == categoryId) {
                            inventoryProductAdapter.requestProductSelection(position, varietyId, varietySelected);
                            inventoryProductAdapter.notifyItemChanged(position);
                        } else {
                            return;
                        }
                    } else if (intent.getAction().equalsIgnoreCase(Constants.ACTION_COLLAPSE_EXPANDED_PRODUCT)) {
                        Long receivedCategoryId = intent.getLongExtra("categoryId", 0l);
                        int position = intent.getIntExtra("position", 0);
                        if (position > 0 && receivedCategoryId == categoryId) {
                            inventoryProductAdapter.collapseTheExpandedItem();
                        } else {
                            return;
                        }
                    }
                }
            }
        };
    }

    @Override
    public void onPause() {
        super.onPause();
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(mContext);
        lbm.unregisterReceiver(mBroadcastReceiverInventoryProductFragment);
    }

    private void fetchProducts() {
        //load products on a background thread
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                final List<InventoryProduct> listOfProducts = KoleCacheUtil.getCachedProducts(myInventory, categoryId);
                boolean cachedProductsAvailable = listOfProducts != null && listOfProducts.size() > 0 && Constants.KOLE_CACHE_ALLOWED;

                if (cachedProductsAvailable && (!refreshProductsInsteadOfReloading || inventoryProductAdapter==null)) {

                    //show a mandatory progress bar for 350 ms...to make smooth transitions between tabs
                    Date threadStartTimeStamp = new Date();
                    int FRAGMENT_LOAD_WAIT_TIME = 350; //in milliseconds
                    Date dateNow = new Date();
                    long timeDiff = CommonUtils.getTimeDifferenceInMillis_X_Minus_Y(dateNow, threadStartTimeStamp);
                    if (timeDiff < FRAGMENT_LOAD_WAIT_TIME) {
                        long sleepTime = FRAGMENT_LOAD_WAIT_TIME - timeDiff;
                        try {
                            Thread.sleep(sleepTime);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                    //load products list
                    FragmentActivity activity = getActivity();
                    if (activity != null) {
                        activity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loadProducts(listOfProducts);
                            }
                        });
                    }
                } else if (cachedProductsAvailable && refreshProductsInsteadOfReloading && inventoryProductAdapter!=null) {
                    inventoryProductAdapter.setProductsList(listOfProducts);
                    inventoryProductAdapter.notifyItemRangeChanged(0, inventoryProductAdapter.getItemCount());
                } else {
                    fetchProductsFromInternet();
                }
            }
        };
        Thread fetchProductThread = new Thread(runnable);
        fetchProductThread.start();
    }

    private void fetchProductsFromInternet() {
        Log.d(TAG, "will fetch products from internet for category id = " + categoryId);
        Intent intent = new Intent(mContext, CommonIntentService.class);
        intent.setAction(Constants.ACTION_FETCH_INVENTORY_PRODUCTS);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("myInventory", myInventory);
        mContext.startService(intent);
    }

    private void loadProducts(final List<InventoryProduct> listOfProducts) {
        /*recyclerView.addItemDecoration(new HorizontalDividerItemDecoration.Builder(getActivity())
                .margin(getResources().getDimensionPixelSize(R.dimen.recycler_view_left_margin),
                        getResources().getDimensionPixelSize(R.dimen.recycler_view_right_margin))
                .build());*/
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return;
        } else {
            try {
                inventoryProductAdapter = new InventoryProductAdapter(activity, categoryId, myInventory);
                recyclerView.setAdapter(inventoryProductAdapter);
                recyclerView.setHasFixedSize(true);

                //get products to load
                final List<InventoryProduct> products;
                if (listOfProducts != null && listOfProducts.size() > 0) {
                    products = listOfProducts;
                } else {
                    products = KoleCacheUtil.getCachedProducts(myInventory, categoryId);
                }

                //set recycler view click listener
                recyclerView.addOnItemTouchListener(new KolRecyclerTouchListener(getActivity(), recyclerView, new KolClickListener() {
                    @Override
                    public void onItemClick(View v, int position) {
                        //Toast.makeText(getActivity(), "product selected", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onItemLongClick(View v, int position) {
                        //Toast.makeText(getActivity(), "item clicked " + position, Toast.LENGTH_LONG).show();
                    }
                }));
                //recyclerView.setVerticalScrollBarEnabled(true); //no need of scroll bar...google play doesn't have it


                if (products != null) {
                    inventoryProductAdapter.setProductsList(products);
                    //Log.d(TAG, "will set view flipper 2 for category id =" + categoryId);
                    viewFlipper.setDisplayedChild(VF_RECYCLER_VIEW);
                } else {
                    //Log.d(TAG, "will set view flipper 3 for category id =" + categoryId );
                    viewFlipper.setDisplayedChild(VF_NO_PRODUCTS);
                }
            } catch (Exception e) {
                Log.e(TAG, "fragment loading problem", e);
            }

        }
    }

    private void couldNotLoadProducts() {
        viewFlipper.setDisplayedChild(VF_LOAD_FAILED);
    }

}
