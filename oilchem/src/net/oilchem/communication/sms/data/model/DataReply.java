package net.oilchem.communication.sms.data.model;

import java.io.Serializable;
import java.util.List;

/**
 * Created by luowei on 2014/5/5.
 */
public class DataReply extends OilResponseData {

    private Reply reply;
    private List<Reply> replies;

    public Reply getReply() {
        return reply;
    }

    public void setReply(Reply reply) {
        this.reply = reply;
    }

    public List<Reply> getReplies() {
        return replies;
    }

    public void setReplies(List<Reply> replies) {
        this.replies = replies;
    }

    public static class Reply  implements Serializable,Comparable<Reply>{
        String msgId;
        String username;
        String reply;
        String replyTime;

        public String getMsgId() {
            return msgId;
        }

        public void setMsgId(String msgId) {
            this.msgId = msgId;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getReply() {
            return reply;
        }

        public void setReply(String reply) {
            this.reply = reply;
        }

        public String getReplyTime() {
            return replyTime;
        }

        public void setReplyTime(String replyTime) {
            this.replyTime = replyTime;
        }

        @Override
        public int compareTo(Reply reply) {
            if(reply !=null && this.getReplyTime()!=null && reply.getReplyTime()!=null){
                return this.getReplyTime().compareTo(reply.getReplyTime());
            }
            return 0;
        }
    }
}
