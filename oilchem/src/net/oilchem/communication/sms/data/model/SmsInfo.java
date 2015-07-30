package net.oilchem.communication.sms.data.model;

import java.io.Serializable;

public class SmsInfo implements Serializable {
	private static final long serialVersionUID = -6277567775111488169L;

    private String msgId;
	private String groupName;
	private String ts;
	private String content;
	private String groupId;
	private String title;
    private String replies;
    private boolean isCollected;
    
    
    public boolean isCollected() {
		return isCollected;
	}

	public void setCollected(boolean isCollected) {
		this.isCollected = isCollected;
	}

	public String getMsgId() {
        return msgId;
    }

    public void setMsgId(String msgId) {
        this.msgId = msgId;
    }

    public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setTitle(String title) {
		this.groupName = title;
	}

    public String getTitle() {
        return title;
    }

    public String getTs() {
		return ts;
	}

	public void setTs(String ts) {
		this.ts = ts;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

    public String getReplies() {
        return replies;
    }

    public void setReplies(String replies) {
        this.replies = replies;
    }
}
