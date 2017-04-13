package com.accountabilibuddies.accountabilibuddies.fragments;


import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.accountabilibuddies.accountabilibuddies.R;
import com.accountabilibuddies.accountabilibuddies.activity.ChallengeOneOnOneActivity;
import com.accountabilibuddies.accountabilibuddies.adapter.ChallengeAdapter;
import com.accountabilibuddies.accountabilibuddies.databinding.FragmentChallengesBinding;
import com.accountabilibuddies.accountabilibuddies.model.Challenge;
import com.accountabilibuddies.accountabilibuddies.network.APIClient;
import com.accountabilibuddies.accountabilibuddies.util.ItemClickSupport;

import java.util.ArrayList;
import java.util.List;

public abstract class ChallengesFragment extends Fragment {

    protected FragmentChallengesBinding binding;
    protected APIClient client;
    protected ArrayList<Challenge> mChallengeList;
    protected ChallengeAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;

    public ChallengesFragment() {
        // Required empty public constructor
    }

    APIClient.GetChallengeListListener listener = new APIClient.GetChallengeListListener(){

        @Override
        public void onSuccess(List<Challenge> challengeList) {
            if (challengeList != null) {
                mChallengeList.clear();
                mChallengeList.addAll(challengeList);
                mAdapter.notifyDataSetChanged();
            }
            binding.swipeContainer.setRefreshing(false);
        }

        @Override
        public void onFailure(String error_message) {
            binding.swipeContainer.setRefreshing(false);
            Snackbar.make(binding.getRoot(), error_message, Snackbar.LENGTH_LONG).show();
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()),
                R.layout.fragment_challenges, container, false);

        client = APIClient.getClient();

        mChallengeList = new ArrayList<>();
        mAdapter = new ChallengeAdapter(getContext(), mChallengeList);
        binding.rVChallenges.setAdapter(mAdapter);
        binding.rVChallenges.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.ItemDecoration itemDecoration =
                new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL);
        binding.rVChallenges.addItemDecoration(itemDecoration);

        mLayoutManager = new LinearLayoutManager(getContext());
        binding.rVChallenges.setLayoutManager(mLayoutManager);

        binding.swipeContainer.setOnRefreshListener(this::getChallenges);

        ItemClickSupport.addTo(binding.rVChallenges).setOnItemClickListener((recyclerView, position, v) -> {
            Challenge challenge = mChallengeList.get(position);

            //TODO: Toggle this right now to look at that timeline
            //TODO: This needs to be changed based on the challenge type
//            Intent intent = new Intent(getActivity(), ChallengeDetailsActivity.class);
            Intent intent = new Intent(getActivity(), ChallengeOneOnOneActivity.class);
            intent.putExtra("challengeId", challenge.getObjectId());
            intent.putExtra("name", challenge.getName());
            getActivity().startActivity(intent);
        });

        getChallenges();

        return binding.getRoot();
    }

    protected abstract void getChallenges();
}
