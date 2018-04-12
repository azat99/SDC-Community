package com.example.azatk.sdccommunity;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.vk.sdk.VKScope;
import com.vk.sdk.api.VKApi;
import com.vk.sdk.api.VKApiConst;
import com.vk.sdk.api.VKError;
import com.vk.sdk.api.VKParameters;
import com.vk.sdk.api.VKRequest;
import com.vk.sdk.api.VKResponse;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class NewsFragment extends Fragment {

    private String[] scope = new String[]{VKScope.MESSAGES, VKScope.FRIENDS, VKScope.WALL};

    private RecyclerView recyclerView;
    private Adapter adapter;
    private List<Constructor> constructors;

    int a=0;

    JSONObject json_data;
    JSONObject json_data2;
    JSONObject json_data3;
    JSONObject json_data4;
    JSONArray array;

    Constructor group;

    private View mMainView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMainView = inflater.inflate(R.layout.fragment_news, container, false);

        //VKSdk.login(getActivity(),scope);

        recyclerView = (RecyclerView) mMainView.findViewById(R.id.recycler);

        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(llm);

        constructors = new ArrayList<>();

        adapter = new Adapter(getActivity(),constructors);

        recyclerView.setAdapter(adapter);

        VKParameters parameters = new VKParameters();
        parameters.put(VKApiConst.OWNER_ID, -32138817);
        parameters.put(VKApiConst.COUNT, 100);
        parameters.put("filter", "owner");
        parameters.put(VKApiConst.VERSION, "5.69");
        final VKRequest request = VKApi.wall().get(parameters);
        request.secure = false; // http, https
        request.executeWithListener(new VKRequest.VKRequestListener() {
            @Override
            public void onComplete(VKResponse response) {
                Log.d("vazat", "azatik " + response.json);
                super.onComplete(response);

                int count = 0;
                JSONArray jsonArrayItems = null;

                try {

                    JSONObject jsonObject = response.json.getJSONObject(String.valueOf("response"));

                    count = jsonObject.getInt("count");
                    jsonArrayItems = jsonObject.getJSONArray("items");

                    Log.i("qqqq", String.valueOf(jsonArrayItems.length()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (count == 0 || jsonArrayItems == null) return;

                Log.i("VKMT", "GROUPS items not null");



                for (int i = 0; i < jsonArrayItems.length(); i++) {

                    Log.v("VKMT111", "count = " + i);


                    try {

                        json_data = jsonArrayItems.getJSONObject(i);
                        json_data2 = jsonArrayItems.getJSONObject(i);
                        array = json_data2.getJSONArray("attachments");

                        json_data3 = array.getJSONObject(0);
                        json_data4 = json_data3.getJSONObject("photo");

                        group = new Constructor();
                        group.setText(json_data.getString("text"));
                        group.setThumb_src(json_data4.getString("photo_604"));
                        constructors.add(group);
                        adapter.updateList(constructors);
                        adapter.notifyDataSetChanged();



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }



                }
                for (int j = 0; j < constructors.size(); j++) {
                    System.out.println(constructors.get(j).getText());

                }
            }

            @Override
            public void attemptFailed(VKRequest request, int attemptNumber, int totalAttempts) {
                Log.d("vazat", "fail=" + request);


                super.attemptFailed(request, attemptNumber, totalAttempts);
            }

            @Override
            public void onError(VKError error) {
                Log.d("vazat", "error=" + error.errorMessage);

                super.onError(error);
            }

            @Override
            public void onProgress(VKRequest.VKProgressType progressType, long bytesLoaded, long bytesTotal) {
                super.onProgress(progressType, bytesLoaded, bytesTotal);
            }
        });


        return mMainView;

    }
}
