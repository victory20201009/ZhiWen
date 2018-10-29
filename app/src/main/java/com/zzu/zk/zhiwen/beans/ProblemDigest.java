package com.zzu.zk.zhiwen.beans;

public class ProblemDigest {
    public Integer id;
    public String belongToUserHead_url;
    public String belongToUser;
    public String labels;
    public String digest_pic_url;
    public String pro_title;
    public String pro_digest;
    public String time;
    public String num_of_discussion;
    public String q_belt_subject;
    public Integer belt_uid;

    public ProblemDigest(String belongToUserHead_url,
                         String belongToUser, String labels,
                         String digest_pic_url, String pro_title,
                         String pro_digest, String time,
                         String num_of_discussion, String q_belt_subject) {
        this.belongToUserHead_url = belongToUserHead_url;
        this.belongToUser = belongToUser;
        this.labels = labels;
        this.digest_pic_url = digest_pic_url;
        this.pro_title = pro_title;
        this.pro_digest = pro_digest;
        this.time = time;
        this.num_of_discussion = num_of_discussion;
        this.q_belt_subject = q_belt_subject;
    }

    public ProblemDigest() {
    }
}
