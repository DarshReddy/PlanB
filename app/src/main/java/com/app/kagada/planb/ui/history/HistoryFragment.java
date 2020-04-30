package com.app.kagada.planb.ui.history;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kagada.planb.R;
import com.app.kagada.planb.activities.MainActivity;
import com.app.kagada.planb.networks.APIClient;
import com.app.kagada.planb.networks.APIInterface;
import com.app.kagada.planb.networks.MyVisitsResponse;
import com.app.kagada.planb.networks.RestaurantResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryFragment extends Fragment implements HistoryAdapter.OnRestClicked {
    private com.app.kagada.planb.networks.APIInterface APIInterface = APIClient.getClient().create(APIInterface.class);
    private RecyclerView mVisitRecycler;
    private ProgressBar mProgress;
    private TextView mFetch;
    private HistoryAdapter mAdapter;
    private List<MyVisitsResponse.Visit> visitList = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_history, container, false);
        mVisitRecycler = root.findViewById(R.id.history_recycler);
        mProgress = root.findViewById(R.id.history_progress);
        mFetch = root.findViewById(R.id.history_fetching);
        mVisitRecycler.setHasFixedSize(true);
        mVisitRecycler.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        return root;
    }

    public void onViewCreated(final View v, Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        APIInterface.myVisits().enqueue(new Callback<MyVisitsResponse>() {
            @Override
            public void onResponse(Call<MyVisitsResponse> call, Response<MyVisitsResponse> response) {
                if (response.code() == 200) {
                    assert response.body() != null;
                    visitList = response.body().getVisits();
                    mAdapter = new HistoryAdapter(visitList, HistoryFragment.this);
                    mVisitRecycler.setAdapter(mAdapter);
                    mProgress.setVisibility(View.GONE);
                    if (visitList.size() == 0)
                        mFetch.setText(R.string.emptyhistory);
                    else
                        mFetch.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Call<MyVisitsResponse> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }

    @Override
    public void OnRestClicked(final int position) {
        Map<String, String> params = new HashMap<>();
        params.put("Url", visitList.get(position).getRest().getUrl());
        params.put("Img_url", visitList.get(position).getRest().getImgUrl());
        params.put("Locality", visitList.get(position).getRest().getLocality());
        params.put("Avg_cost", visitList.get(position).getRest().getAvgCost().toString());
        params.put("Name", visitList.get(position).getRest().getName());
        params.put("Cuisines", visitList.get(position).getRest().getCuisines());
        params.put("resID", visitList.get(position).getRest().getResID());
        params.put("Rating", String.valueOf(visitList.get(position).getRest().getRating()));
        APIInterface.postRst(params).enqueue(new Callback<RestaurantResponse.NearbyRestaurant>() {
            @Override
            public void onResponse(Call<RestaurantResponse.NearbyRestaurant> call, Response<RestaurantResponse.NearbyRestaurant> response) {
                if (response.code() == 201 || response.code() == 400) {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.putExtra("call_frag", 2);
                    intent.putExtra("resID", visitList.get(position).getRest().getResID());

                    startActivity(intent);
                }
            }

            @Override
            public void onFailure(Call<RestaurantResponse.NearbyRestaurant> call, Throwable t) {
                Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                call.cancel();
            }
        });
    }
}