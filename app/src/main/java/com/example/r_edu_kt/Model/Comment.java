package com.example.r_edu_kt.Model;

public class Comment {
    private String comment,date,postid,publisher,commentimage,commentid;

    public Comment(){
    }

    public Comment(String comment, String date, String postid, String publisher, String commentimage, String commentid) {
        this.comment = comment;
        this.date = date;
        this.postid = postid;
        this.publisher = publisher;
        this.commentimage = commentimage;
        this.commentid = commentid;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }

    public String getPublisher() {
        return publisher;
    }

    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getCommentimage() {
        return commentimage;
    }

    public void setCommentimage(String commentimage) {
        this.commentimage = commentimage;
    }

    public String getCommentid() {
        return commentid;
    }

    public void setCommentid(String commentid) {
        this.commentid = commentid;
    }
}
