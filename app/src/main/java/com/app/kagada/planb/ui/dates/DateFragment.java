package com.app.kagada.planb.ui.dates;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.kagada.planb.R;
import com.app.kagada.planb.networks.APIClient;
import com.app.kagada.planb.networks.APIInterface;
import com.app.kagada.planb.networks.AlarmReceiver;
import com.app.kagada.planb.networks.DateAcceptResponse;
import com.app.kagada.planb.networks.DateResponse;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DateFragment extends Fragment implements DateAdapter.DateClick {
  private RecyclerView mDates;
  private APIInterface apiInterface = APIClient.getClient().create(APIInterface.class);
  private List<DateResponse.Date> dateList = new ArrayList<>();
  private DateAdapter mAdapter;
  private ProgressBar mProgress;
  private TextView mFetch;
  private boolean is_female;

  public View onCreateView(@NonNull LayoutInflater inflater,
                           ViewGroup container, Bundle savedInstanceState) {
    View root = inflater.inflate(R.layout.fragment_dates, container, false);
    mDates = root.findViewById(R.id.date_recycler);
    mDates.setHasFixedSize(true);
    mDates.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
    mProgress = root.findViewById(R.id.date_load);
    mFetch = root.findViewById(R.id.date_load_text);
    return root;
  }

  public void onViewCreated(@NotNull View v, Bundle savedInstanceState) {
    super.onViewCreated(v,savedInstanceState);

    Bundle bundle = getArguments();
    int resID;
    if(bundle.getString("resID")!=null) {
      resID = Integer.parseInt(bundle.getString("resID"));

      Map<String, String> params = new HashMap<>();
      params.put("restaurant", String.valueOf(resID));

      apiInterface.getDates(params).enqueue(new Callback<DateResponse>() {
        @Override
        public void onResponse(Call<DateResponse> call, Response<DateResponse> response) {
          if(response.code()==200) {
            DateResponse src = response.body();
            assert src != null;
            dateList.addAll(src.getDates());
            is_female= dateList.get(0).getGirl() == null;
            mAdapter = new DateAdapter(dateList, DateFragment.this, is_female, false);
            mDates.setAdapter(mAdapter);
            mProgress.setVisibility(View.GONE);
            mFetch.setVisibility(View.GONE);
          }
          else if(response.code()==201) {
            mProgress.setVisibility(View.GONE);
            mFetch.setTextSize(24);
            mFetch.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            mFetch.setGravity(Gravity.CENTER_VERTICAL);
            mFetch.setText(R.string.date_ask);
          }
        }

        @Override
        public void onFailure(Call<DateResponse> call, Throwable t) {
          Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
          call.cancel();
        }
      });
    }
    else if (bundle.getInt("pk",0)!=0) {
      resID = bundle.getInt("pk");
      is_female = bundle.getBoolean("is_female");

      apiInterface.getDate(String.valueOf(resID)).enqueue(new Callback<DateResponse.Date>() {
        @Override
        public void onResponse(Call<DateResponse.Date> call, Response<DateResponse.Date> response) {
          DateResponse.Date date = response.body();
          assert date != null;
          callNotify(date.getId());
          dateList.clear();
          dateList.add(date);
          mAdapter = new DateAdapter(dateList, DateFragment.this, is_female, true);
          mDates.setAdapter(mAdapter);
          mProgress.setVisibility(View.GONE);
          mFetch.setVisibility(View.GONE);
        }

        @Override
        public void onFailure(Call<DateResponse.Date> call, Throwable t) {
          Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
          call.cancel(); }
      });
    }

  }

  @Override
  public void onDateClick(final int position) {
    final int dateID = dateList.get(position).getId();

    AlertDialog.Builder bd = new AlertDialog.Builder(getActivity());
    bd.setTitle("Match Date!");
    if (is_female)
      bd.setMessage("Accept to meet "+dateList.get(position).getMale().getEmail() + "?");
    else bd.setMessage("Accept to meet "+dateList.get(position).getFemale().getEmail() + "?");
    bd.setPositiveButton("Cool!",
            new DialogInterface.OnClickListener() {
              @Override
              public void onClick(final DialogInterface dialog, int which) {
                apiInterface.acceptDate(String.valueOf(dateID)).enqueue(new Callback<DateAcceptResponse>() {
                  @Override
                  public void onResponse(@NotNull Call<DateAcceptResponse> call, @NotNull Response<DateAcceptResponse> response) {
                    if (response.code()==200) {
                      dialog.dismiss();
                      callNotify(dateID);
                    }
                  }

                  @Override
                  public void onFailure(@NotNull Call<DateAcceptResponse> call, @NotNull Throwable t) {
                    Toast.makeText(getActivity(), t.getMessage(), Toast.LENGTH_SHORT).show();
                    call.cancel(); }
                });
              }
            });
    bd.setNegativeButton("No..", new DialogInterface.OnClickListener() {
      @Override
      public void onClick(DialogInterface dialog, int which) {
        dialog.dismiss();
      }
    });
    AlertDialog dialog = bd.create();
    dialog.show();
    Button btn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
    btn.setGravity(Gravity.CENTER);
  }

  private void callNotify(int dateID) {
    AlarmManager alarms = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);
    Intent intent = new Intent(getActivity(), AlarmReceiver.class);
    intent.putExtra("rated_date",dateID);
    PendingIntent operation = PendingIntent.getBroadcast(getContext(), 100, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    assert alarms != null;
    Calendar calendar = Calendar.getInstance();
    calendar.add(Calendar.HOUR, 2);
    alarms.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), operation) ;
  }
}