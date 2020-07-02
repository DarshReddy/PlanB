package com.app.kagada.planb.ui.rating;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.APIClient;
import com.app.kagada.planb.networks.APIInterface;
import com.app.kagada.planb.networks.DateAcceptResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RatingFragment extends Fragment {
  private TextView mRatingScale;
  private Button rateBtn;
  private int stars=1;
  private APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_rating, container, false);
    RatingBar mRatingBar = root.findViewById(R.id.rate_now);
    mRatingScale = root.findViewById(R.id.RatingScale);
    rateBtn = root.findViewById(R.id.rate_btn);
    mRatingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
      @Override
      public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        mRatingScale.setText(String.valueOf(v));
        switch ((int) ratingBar.getRating()) {
          case 1:
            mRatingScale.setText("Was Very bad");
            break;
          case 2:
            mRatingScale.setText("It was OK");
            stars=2;
            break;
          case 3:
            mRatingScale.setText("Good");
            stars=3;
            break;
          case 4:
            mRatingScale.setText("Great");
            stars=4;
            break;
          case 5:
            mRatingScale.setText("Awesome. I loved it");
            stars=5;
            break;
          default:
            mRatingScale.setText("");
        }
      }
    });
    return root;
  }

  public void onViewCreated(View v, Bundle savedInstanceState) {
    super.onViewCreated(v,savedInstanceState);
    rateBtn.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        Bundle bundle = getArguments();
        assert bundle != null;
        if(bundle.getInt("rated_date")!=0) {
          int ratedDate = bundle.getInt("rated_date");

          Map<String, String> params = new HashMap<>();
          params.put("rated_date",String.valueOf(ratedDate));
          params.put("stars",String.valueOf(stars));

          apiInterface.rateNow(params).enqueue(new Callback<DateAcceptResponse.Rating>() {
            @Override
            public void onResponse(Call<DateAcceptResponse.Rating> call, Response<DateAcceptResponse.Rating> response) {
              if(response.code()==200)
                Toast.makeText(getActivity(),"Rating Updated. Thanks for the feedback!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<DateAcceptResponse.Rating> call, Throwable t) {
              Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
              call.cancel();
            }
          });
        }
      }
    });
  }
}