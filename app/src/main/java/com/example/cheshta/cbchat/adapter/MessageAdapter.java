package com.example.cheshta.cbchat.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.cheshta.cbchat.R;
import com.example.cheshta.cbchat.model.Message;
import com.google.firebase.database.DatabaseReference;

import java.util.List;

/**
 * Created by chesh on 3/29/2018.
 */

public class MessageAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;

    private Context context;
    private List<Message> messages;

    private String currentUser;

    public MessageAdapter(Context context, List<Message> messages, String currentUser) {
        this.context = context;
        this.messages = messages;
        this.currentUser = currentUser;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view;

        if (viewType == VIEW_TYPE_MESSAGE_SENT) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_sent, parent, false);
            return new SentMessageHolder(view);
        } else if (viewType == VIEW_TYPE_MESSAGE_RECEIVED) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_message_received, parent, false);
            return new ReceivedMessageHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Message message = messages.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
        }
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    @Override
    public int getItemViewType(int position) {

        Message message = messages.get(position);
        String from_user = message.getFrom();

        if(from_user.equals(currentUser)){
            return VIEW_TYPE_MESSAGE_SENT;
        }
        else{
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        SentMessageHolder(View itemView) {
            super(itemView);
            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(Message message) {

            tvMessage.setText(message.getMessage());
            tvTime.setText(DateUtils.formatDateTime(context,message.getTime(),DateUtils.FORMAT_SHOW_TIME));
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {
        TextView tvMessage, tvTime;

        ReceivedMessageHolder(View itemView) {
            super(itemView);

            tvMessage = itemView.findViewById(R.id.tvMessage);
            tvTime = itemView.findViewById(R.id.tvTime);
        }

        void bind(Message message) {

            tvMessage.setText(message.getMessage());
            tvTime.setText(DateUtils.formatDateTime(context,message.getTime(),DateUtils.FORMAT_SHOW_TIME));
        }
    }
}
