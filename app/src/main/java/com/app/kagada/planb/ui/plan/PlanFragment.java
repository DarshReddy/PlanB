package com.app.kagada.planb.ui.plan;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.app.kagada.planb.activities.MainActivity;
import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.APIClient;
import com.app.kagada.planb.networks.APIInterface;
import com.app.kagada.planb.networks.RestaurantResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlanFragment extends Fragment implements PlanAdapter.OnRstClicked {

  private APIInterface APIInterface = APIClient.getClient().create(APIInterface.class);
  private RecyclerView mRstList;
  private ProgressBar mProgress;
  private TextView mFetch;
  private PlanAdapter mAdapter;
  private List<RestaurantResponse.NearbyRestaurant> mRestaurants = new ArrayList<>();

  @Override
  public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    super.onCreateView(inflater, container, savedInstanceState);
    View root = inflater.inflate(R.layout.fragment_plan, container, false);
    mRstList = root.findViewById(R.id.rst_list_view);
    mProgress = root.findViewById(R.id.rest_load);
    mFetch = root.findViewById(R.id.rest_load_text);
    mRstList.setHasFixedSize(true);
    mRstList.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    return root;
  }

  public void onViewCreated(@NotNull View v, Bundle savedInstanceState) {
      super.onViewCreated(v, savedInstanceState);

      Bundle bundle = getArguments();
      assert bundle != null;
      float lat = (float) bundle.getDouble("LAT");
      float lon = (float) bundle.getDouble("LONG");
      final int budget = Integer.parseInt(Objects.requireNonNull(bundle.getString("COST")));

      Map<String, String> params = new HashMap<>();
      params.put("lat", String.valueOf(lat));
      params.put("lon", String.valueOf(lon));

      APIInterface.getRestaurants(params).enqueue(new Callback<RestaurantResponse>() {
          @Override
          public void onResponse(@NotNull Call<RestaurantResponse> call, @NotNull Response<RestaurantResponse> response) {
              Log.d("TAG", response.code() + "");

              if (response.isSuccessful()) {
                  RestaurantResponse resource = response.body();
                  assert resource != null;
                  for (int i = 0; i < resource.getNearbyRestaurants().size(); i++) {
                      if (resource.getNearbyRestaurants().get(i).getAvgCost() <= budget)
                          mRestaurants.add(resource.getNearbyRestaurants().get(i));
                  }
                  Toast.makeText(getActivity(), "Found "+mRestaurants.size() + " matching restaurants nearby!", Toast.LENGTH_SHORT).show();
                  mAdapter = new PlanAdapter(mRestaurants, PlanFragment.this);
                  mRstList.setAdapter(mAdapter);
                  mProgress.setVisibility(View.GONE);
                  mFetch.setVisibility(View.GONE);
              }
          }

          @Override
          public void onFailure(Call<RestaurantResponse> call, Throwable t) {
              Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
              call.cancel();
          }
      });
  }

    @Override
    public void onRstClicked(final int position) {
      Map<String, String> params = new HashMap<>();
      params.put("Url", mRestaurants.get(position).getUrl());
      params.put("Img_url", mRestaurants.get(position).getImgUrl());
      params.put("Locality", mRestaurants.get(position).getLocality());
      params.put("Avg_cost", mRestaurants.get(position).getAvgCost().toString());
      params.put("Name", mRestaurants.get(position).getName());
      params.put("Cuisines", mRestaurants.get(position).getCuisines());
      params.put("resID", mRestaurants.get(position).getResID());
      params.put("Rating",String.valueOf(mRestaurants.get(position).getRating()));
      APIInterface.postRst(params).enqueue(new Callback<RestaurantResponse.NearbyRestaurant>() {
          @Override
          public void onResponse(Call<RestaurantResponse.NearbyRestaurant> call, Response<RestaurantResponse.NearbyRestaurant> response) {
              if(response.code() == 201 || response.code() == 400) {
                  Intent intent = new Intent(getContext(), MainActivity.class);
                  intent.putExtra("call_frag",2);
                  intent.putExtra("resID",mRestaurants.get(position).getResID());

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
