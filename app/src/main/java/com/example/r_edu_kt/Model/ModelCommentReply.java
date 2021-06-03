package com.example.r_edu_kt.Model;

public class ModelCommentReply {
    String comment;
    String publisher;
    String commentid;
    String previous_commentid;
    String postid;
    String date;
    String previous_username;
    String previous_comment;

    public ModelCommentReply() {
    }

    public ModelCommentReply(String comment, String publisher, String commentid, String previous_commentid, String postid, String date, String previous_username, String previous_comment) {
        this.comment = comment;
        this.publisher = publisher;
        this.commentid = commentid;
        this.previous_commentid = previous_commentid;
        this.postid = postid;
        this.date = date;
        this.previous_username = previous_username;
        this.previous_comment = previous_comment;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }

    public String getPrevious_commentid() {
        return previous_commentid;
    }

    public void setPrevious_commentid(String previous_commentid) {
        this.previous_commentid = previous_commentid;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPrevious_username() {
        return previous_username;
    }

    public void setPrevious_username(String previous_username) {
        this.previous_username = previous_username;
    }

    public String getPrevious_comment() {
        return previous_comment;
    }

    public void setPrevious_comment(String previous_comment) {
        this.previous_comment = previous_comment;
    }
}
